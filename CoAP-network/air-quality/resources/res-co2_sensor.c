#include <stdlib.h>
#include <string.h>
#include<time.h>
#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"

#include "global_variables.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "co2-sensor"
#define LOG_LEVEL LOG_LEVEL_APP

static void co2_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void co2_event_handler(void);

EVENT_RESOURCE(res_co2_sensor,
         "title=\"CO2 sensor\"; obs",
         co2_get_handler,
         NULL,
         NULL,
         NULL,
	 co2_event_handler);

static unsigned int co2_level = 300;

static bool update_co2 () { // simulate the behavior of the real sensor
	bool updated = false;
	unsigned int old_co2_level = co2_level;

	if(ventilation_on) {	// If the ventilation system is turned on, air quality improves
		co2_level = (int) (co2_level - 6);
	}
	
	srand(time(NULL));
	int value = rand() % 16; // a random rumber between 0 and 15
	co2_level = (int) (co2_level + 0.75*value);	// In any case, the CO2 level can only increase more or less rapidly
		

	if(old_co2_level != co2_level)
		updated = true;

	return updated;
}

static void co2_event_handler(void) {
	if (update_co2()) { // if the value is changed
		LOG_INFO("CO2 level: %u ppm\n", co2_level);
		// Notify all the observers
    	coap_notify_observers(&res_co2_sensor);
	}
}

static void co2_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {
  	coap_set_header_content_format(response, TEXT_PLAIN);
  	coap_set_payload(response, buffer, snprintf((char *)buffer, preferred_size, "%u", (unsigned int) co2_level));
}
