#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "coap-engine.h"
#include "sys/log.h"
#include "dev/leds.h"
#include <time.h>

#define LOG_MODULE "presence-sensor"
#define LOG_LEVEL LOG_LEVEL_APP

static void presence_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void presence_event_handler(void);
static void presence_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

EVENT_RESOURCE(res_presence,
	"title=\"Presence sensor\";obs",
	presence_get_handler,
        NULL,
        presence_put_handler,
        NULL, 
	presence_event_handler);

static unsigned int number_of_people = 0;
static unsigned int max_number_of_people = 20;

static bool update_counter () // simulate the behavior of the real sensor
{
	bool updated = false;
	srand(time(NULL));
	int value = rand() % 10; // a number between 0 and 9
	if ((value%2) == 0) // if the value is even try to increase
	{
		if (number_of_people < max_number_of_people)
		{
			number_of_people = number_of_people + 1;
			updated = true;
		}
	}
	else // try to decrease
	{
		if (number_of_people > 0)
		{
			number_of_people = number_of_people - 1;
			updated = true;	
		}
	}

	if(updated) {
        if(number_of_people < max_number_of_people) {
            leds_set(LEDS_NUM_TO_MASK(LEDS_GREEN));
        } else {
            leds_set(LEDS_NUM_TO_MASK(LEDS_RED));
        }
	}

	return updated;
}

static void presence_event_handler(void)
{
	if (update_counter()) // if the value is changed
	{
		LOG_INFO("Number of people inside the sauna: %u\n", number_of_people);
		// Notify all the observers
    		coap_notify_observers(&res_presence);	
	}
}

static void presence_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
  	coap_set_header_content_format(response, TEXT_PLAIN);
  	coap_set_payload(response, buffer, snprintf((char *)buffer, preferred_size, "%u", (unsigned int) number_of_people));
}

static void presence_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
	size_t len = 0;
    	const uint8_t* payload = NULL;
  	if((len = coap_get_payload(request, &payload))) 
	{
		int new_value = atoi((char*)payload); // new_value >= 0 for constraint
		LOG_INFO("The maximum number of people allowed is now: %d\n", new_value);
		max_number_of_people = (unsigned int) new_value;
	}
	else
	{
		coap_set_status_code(response, BAD_REQUEST_4_00);  
	}
}
