#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

static void light_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

RESOURCE(light_switch,
         "title=\"Light Switch\";rt=\"Control\"",
         NULL,
         NULL,
         light_put_handler,
         NULL);

static bool light_on = false;
static uint8_t led = LEDS_YELLOW;

static void light_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {
	size_t len = 0;
	const char *text = NULL;
	char mode[4];
	memset(mode, 0, 3);
	char color[7];
	memset(color, 0, 7);

	int mode_success = 1;
	int color_success = 1;
	
	/*
	We may have one or two parameters:
	- mode=ON or mode=OFF to turn on or turn off the light
	- color=GREEN or color=RED or color=YELLOW to change the color of the light
	*/
	
	len = coap_get_post_variable(request, "mode", &text);
	if(len > 0 && len < 4) {
		memcpy(mode, text, len);
		if(strncmp(mode, "ON", len) == 0) {
			light_on = true;
			leds_set(LEDS_NUM_TO_MASK(led));
			LOG_INFO("Light ON\n");
		} else if(strncmp(mode, "OFF", len) == 0) {
			light_on = false;
			leds_off(LEDS_ALL);
			LOG_INFO("Light OFF\n");
		} else {
			mode_success = 0;
		}
	} else {
		mode_success = 0;
	}

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

	if(!mode_success && !color_success) {
    		coap_set_status_code(response, BAD_REQUEST_4_00);
 	}
}
