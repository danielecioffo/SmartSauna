#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"

#include "node-id.h"
#include "net/ipv6/simple-udp.h"
#include "net/ipv6/uip.h"
#include "net/ipv6/uip-ds6.h"
#include "net/ipv6/uip-debug.h"
#include "routing/routing.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "light"
#define LOG_LEVEL LOG_LEVEL_APP

#define INTERVAL_BETWEEN_CONNECTION_TESTS 1

extern coap_resource_t res_light_switch;
extern coap_resource_t res_light_color;
static struct etimer connectivity_timer;

/* Declare and auto-start this file's process */
PROCESS(light_server, "Light Server");
AUTOSTART_PROCESSES(&light_server);

/*---------------------------------------------------------------------------*/
static bool is_connected() {
	if(NETSTACK_ROUTING.node_is_reachable()) {
		LOG_INFO("The Border Router is reachable\n");
		return true;
  	} else {
		LOG_INFO("Waiting for connection with the Border Router\n");
	}
	return false;
}


PROCESS_THREAD(light_server, ev, data){
	PROCESS_BEGIN();
	PROCESS_PAUSE();

	LOG_INFO("Starting Light CoAP-Server\n");
	coap_activate_resource(&res_light_switch, "light/switch"); 
	coap_activate_resource(&res_light_color, "light/color"); 

	// try to connect to the border router
	etimer_set(&connectivity_timer, CLOCK_SECOND * INTERVAL_BETWEEN_CONNECTION_TESTS);
	PROCESS_WAIT_UNTIL(etimer_expired(&connectivity_timer));
	while(!is_connected()) {
		etimer_reset(&connectivity_timer);
		PROCESS_WAIT_UNTIL(etimer_expired(&connectivity_timer));
	}
  
	PROCESS_END();
}
