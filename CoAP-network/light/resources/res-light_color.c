#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"

#include "global_variables.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

static void light_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

RESOURCE(res_light_color,
         "title=\"Light Color\";rt=\"Control\"",
         NULL,
         NULL,
         light_put_handler,
         NULL);

uint8_t led = LEDS_YELLOW;

static void light_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {
	size_t len = 0;
	const char *text = NULL;
	char color[7];
	memset(color, 0, 7);

	int color_success = 1;
	
	len = coap_get_post_variable(request, "color", &text);
	if(len > 0 && len < 7) {
		memcpy(color,text,len);
		if(strncmp(color, "YELLOW", len) == 0) {
			led = LEDS_YELLOW;
		} else if(strncmp(color, "GREEN", len) == 0) {
			led = LEDS_GREEN;
		} else if(strncmp(color, "RED", len) == 0) {
		        led = LEDS_RED;
		} else {
		       	color_success = 0;
	 	}
		if(color_success) {
			LOG_INFO("Color = %s\n", color);
			if(light_on) {
				leds_set(LEDS_NUM_TO_MASK(led));
			}
		}
	} else {
		color_success = 0;
	}

	if(!color_success) {
    		coap_set_status_code(response, BAD_REQUEST_4_00);
 	}
}
