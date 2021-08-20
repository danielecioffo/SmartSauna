package it.unipi.dii.inginf.iot.smartsauna.coap.devices.airquality;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import java.util.concurrent.atomic.AtomicInteger;

public class AirQuality {
    private CoapClient clientCO2Sensor;
    private CoapObserveRelation observeCO2;
    private CoapClient clientVentilationSystem;

    private boolean ventilationOn = false;
    private AtomicInteger co2Level = new AtomicInteger(300);

    public void registerAirQuality(String ip) {
        System.out.println("The Air Quality system: [" + ip + "] is now registered");
        clientCO2Sensor = new CoapClient("coap://[" + ip + "]/air-quality/co2");
        clientVentilationSystem = new CoapClient("coap://[" + ip + "]/air-quality/ventilation");

        observeCO2 = clientCO2Sensor.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                String responseString = coapResponse.getResponseText();
                co2Level.set(Integer.getInteger(responseString));

                if(!ventilationOn && co2Level.get() > 700) {    // TODO metti parametro UPPER BOUND
                    System.out.println("CO2 level is HIGH, the ventilation system is switched ON");
                        ventilationSystemSwitch(true);
                        ventilationOn = true;
                }

                if (ventilationOn && co2Level.get()  < 700 - 0.3 * 700) {    // TODO metti parametro UPPER BOUND
                    System.out.println("CO2 level is now fine. Switch OFF the ventilation system");
                    ventilationSystemSwitch(false);
                    ventilationOn = false;
                }
            }

            @Override
            public void onError() {
                System.err.println("[ERROR: CO2 sensor" + clientCO2Sensor.getURI() + "]");
            }
        });
    }

    public void unregisterAirQuality(String ip) {
        if(clientCO2Sensor.getURI().equals(ip)) {
            clientCO2Sensor = null;
            observeCO2.proactiveCancel();
            observeCO2 = null;
            clientVentilationSystem = null;
        }
    }

    public boolean ventilationSystemSwitch(boolean on) {
        if(clientVentilationSystem == null)
            return false;

        String msg = "mode=" + (on ? "ON" : "OFF");
        clientVentilationSystem.put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                if(coapResponse != null) {
                    if(!coapResponse.isSuccess())
                        System.out.println("Ventilation System: PUT request unsuccessful");
                }
            }

            @Override
            public void onError() {
                System.err.println("[ERROR: Ventilation System " + clientVentilationSystem.getURI() + "]");
            }
        }, msg, MediaTypeRegistry.TEXT_PLAIN);

        return true;
    }
}
