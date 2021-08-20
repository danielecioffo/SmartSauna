package it.unipi.dii.inginf.iot.smartsauna.coap.devices.airquality;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import java.util.concurrent.atomic.AtomicInteger;

public class AirQuality {
    private CoapClient clientCO2Sensor;
    private CoapClient clientVentilationSystem;
    private CoapObserveRelation observeCO2;

    private AtomicInteger co2Level = new AtomicInteger(300);
    private boolean ventilationOn = false;

    public void registerAirQuality(String ip) {
        System.out.print("\n[REGISTRATION] The Air Quality system: [" + ip + "] is now registered\n>");
        clientCO2Sensor = new CoapClient("coap://[" + ip + "]/air_quality/co2");
        clientVentilationSystem = new CoapClient("coap://[" + ip + "]/air_quality/ventilation");

        observeCO2 = clientCO2Sensor.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                String responseString = coapResponse.getResponseText();
                int newCO2Level;
                try {
                    newCO2Level = Integer.parseInt(responseString);
                    co2Level.set(newCO2Level);
                } catch (Exception e) {
                    System.out.print("\n[ERROR] The CO2 sensor gave non-significant data\n>");
                }

                if(!ventilationOn && co2Level.get() > 700) {
                    System.out.print("\n[AIR QUALITY] CO2 level is HIGH, the ventilation system is switched ON\n>");
                    ventilationSystemSwitch(true);
                    ventilationOn = true;
                }

                if (ventilationOn && co2Level.get()  < 700 - 0.3 * 700) {    // TODO metti parametro UPPER BOUND
                    System.out.print("\n[AIR QUALITY] CO2 level is now fine. Switch OFF the ventilation system\n>");
                    ventilationSystemSwitch(false);
                    ventilationOn = false;
                }
            }

            @Override
            public void onError() {
                System.err.print("\n[ERROR] Air Quality" + clientCO2Sensor.getURI() + "]\n>");
            }
        });
    }

    public void unregisterAirQuality(String ip) {
        if(clientCO2Sensor.getURI().equals(ip)) {
            clientCO2Sensor = null;
            clientVentilationSystem = null;
            observeCO2.proactiveCancel();
            observeCO2 = null;
        }
    }

    public int getCO2Level() {
        return co2Level.get();
    }

    private boolean ventilationSystemSwitch(boolean on) {
        if(clientVentilationSystem == null)
            return false;

        String msg = "mode=" + (on ? "ON" : "OFF");
        clientVentilationSystem.put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                if(coapResponse != null) {
                    if(!coapResponse.isSuccess())
                        System.out.print("\n[ERROR] Ventilation System: PUT request unsuccessful\n>");
                }
            }

            @Override
            public void onError() {
                System.err.print("\n[ERROR] Ventilation System " + clientVentilationSystem.getURI() + "]\n>");
            }
        }, msg, MediaTypeRegistry.TEXT_PLAIN);

        return true;
    }
}
