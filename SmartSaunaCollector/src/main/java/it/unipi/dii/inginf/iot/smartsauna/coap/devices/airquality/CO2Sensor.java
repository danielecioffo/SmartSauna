package it.unipi.dii.inginf.iot.smartsauna.coap.devices.airquality;

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

    public void registerCO2Sensor(String ip) {
        System.out.println("The CO2 sensor: [" + ip + "] + is now registered");
        clientCO2Sensor = new CoapClient("coap://[" + ip + "]/air-quality/co2");

        observeCO2 = clientCO2Sensor.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                String responseString = coapResponse.getResponseText();
                co2Level.set(Integer.getInteger(responseString));

                if(!ventilationOn && co2Level.get() > 700) {    // TODO metti parametro UPPER BOUND
                    if(ventilationSystem != null) {
                        System.out.println("CO2 level is HIGH, the ventilation system is switched ON");
                        ventilationSystem.ventilationSystemSwitch(true);
                        ventilationOn = true;
                    }
                }

                if (ventilationOn && co2Level.get()  < 700 - 0.3 * 700) {    // TODO metti parametro UPPER BOUND
                    if (ventilationSystem != null) {
                        System.out.println("CO2 level is now fine. Switch OFF the ventilation system");
                        ventilationSystem.ventilationSystemSwitch(false);
                        ventilationOn = false;
                    }
                }
            }

            @Override
            public void onError() {
                System.err.println("[ERROR: CO2 sensor" + clientCO2Sensor.getURI() + "]");
            }
        });
    }

    public void unregisterCO2Sensor(String ip) {
        if(clientCO2Sensor.getURI().equals(ip)) {
            clientCO2Sensor = null;
            observeCO2.proactiveCancel();
            observeCO2 = null;
        }
    }

    public void addVentilationSystem(VentilationSystem ventilationSystem) {
        this.ventilationSystem = ventilationSystem;
    }

    public int getCO2Level() {
        return co2Level.get();
    }
}
