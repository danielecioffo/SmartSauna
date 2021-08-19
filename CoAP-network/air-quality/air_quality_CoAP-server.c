#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "contiki.h"
#include "coap-engine.h"
#include "sys/etimer.h"
#include "dev/leds.h"

#include "node-id.h"
#include "net/ipv6/simple-udp.h"
#include "net/ipv6/uip.h"
#include "net/ipv6/uip-ds6.h"
#include "net/ipv6/uip-debug.h"
#include "routing/routing.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

#define INTERVAL_BETWEEN_SIMULATIONS 3
#define INTERVAL_BETWEEN_CONNECTION_TESTS 1

extern coap_resource_t res_ventilation_system;
extern coap_resource_t res_co2_sensor;
static struct etimer simulation_timer;
static struct etimer connectivity_timer;

/* Declare and auto-start this file's process */
PROCESS(air_quality_server, "Air Quality Server");
AUTOSTART_PROCESSES(&air_quality_server);

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

PROCESS_THREAD(air_quality_server, ev, data){
	PROCESS_BEGIN();
	leds_set(LEDS_NUM_TO_MASK(LEDS_RED));
	PROCESS_PAUSE();

	LOG_INFO("Starting Air Quality CoAP-Server\n");
	coap_activate_resource(&res_ventilation_system, "air_quality/ventilation"); 
	coap_activate_resource(&res_co2_sensor, "air_quality/co2");

	// try to connect to the border router
	etimer_set(&connectivity_timer, CLOCK_SECOND * INTERVAL_BETWEEN_CONNECTION_TESTS);
	PROCESS_WAIT_UNTIL(etimer_expired(&connectivity_timer));
	while(!is_connected()) {
		etimer_reset(&connectivity_timer);
		PROCESS_WAIT_UNTIL(etimer_expired(&connectivity_timer));
	}

	etimer_set(&simulation_timer, CLOCK_SECOND * INTERVAL_BETWEEN_SIMULATIONS); // every three seconds
	while(1) {
		PROCESS_WAIT_EVENT();
		if(ev == PROCESS_EVENT_TIMER && data == &simulation_timer) {
			res_co2_sensor.trigger();	
			etimer_set(&simulation_timer, CLOCK_SECOND * INTERVAL_BETWEEN_SIMULATIONS);
		}
	}        

	PROCESS_END();
}
