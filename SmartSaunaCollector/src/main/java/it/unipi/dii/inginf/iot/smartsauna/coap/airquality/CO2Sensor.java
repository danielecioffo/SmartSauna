package it.unipi.dii.inginf.iot.smartsauna.coap.airquality;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

import java.util.concurrent.atomic.AtomicInteger;

public class CO2Sensor {
    private CoapClient clientCO2Sensor;
    private CoapObserveRelation observeCO2;

    private VentilationSystem ventilationSystem;
    private boolean ventilationOn = false;
    private AtomicInteger co2Level = new AtomicInteger(300);

    public void addCO2Sensor(String ip) {
        System.out.println("The CO2 sensor: [" + ip + "] + is now registered");
        clientCO2Sensor = new CoapClient("coap://[" + ip + "]/air-quality/co2");

        observeCO2 = clientCO2Sensor.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                String responseString = coapResponse.getResponseText();
                co2Level.set(Integer.getInteger(responseString));

                if(ventilationSystem != null) {
                    if (co2Level.get() > 700) {   // TODO metti parametro UPPER BOUND
                        System.out.println("CO2 level is HIGH!");
                        if (ventilationOn) {
                            System.out.println("The ventilation system is already on, the CO2 concentration will soon decrease...");
                        } else {
                            ventilationSystem.ventilationSystemSwitch(true);
                            System.out.println("Switch ON the ventilation system");
                        }
                    } else if (co2Level.get()  < 700 - 0.3 * 700) {    // TODO metti parametro UPPER BOUND
                        if (ventilationOn) {
                            ventilationSystem.ventilationSystemSwitch(false);
                            System.out.println("CO2 level is now fine. Switch OFF the ventilation system");
                        }
                    }
                }
            }

            @Override
            public void onError() {
                System.err.println("[ERROR: CO2 sensor" + clientCO2Sensor.getURI() + "]");
            }
        });
    }

    public void addVentilationSystem(VentilationSystem ventilationSystem) {
        this.ventilationSystem = ventilationSystem;
    }

    public int getCO2Level() {
        return co2Level.get();
    }
}
