package it.unipi.dii.inginf.iot.smartsauna.coap.devices.presence;

import it.unipi.dii.inginf.iot.smartsauna.coap.devices.light.Light;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

import java.util.concurrent.atomic.AtomicInteger;

public class PresenceSensor {
    // TODO inserisci max number of people
    private CoapClient clientPresenceSensor;
    private CoapObserveRelation observePresence;

    private Light light;
    private AtomicInteger numberOfPeople = new AtomicInteger(0);
    private boolean lightOn = false;

    public void registerPresenceSensor(String ip) {
        System.out.println("\n[REGISTRATION] The presence sensor [" + ip + "] is now registered\n>");
        clientPresenceSensor = new CoapClient("coap://[" + ip + "]/presence");

        observePresence = clientPresenceSensor.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                String responseString = coapResponse.getResponseText();
                int newNumberOfPeople;
                try {
                    newNumberOfPeople = Integer.parseInt(responseString);
                    numberOfPeople.set(newNumberOfPeople);
                } catch(Exception e) {
                    System.out.println("\n[ERROR] The presence sensor gave non-significant data\n>");
                }

                if(numberOfPeople.get() > 0 && !lightOn) {
                    if(light != null) {
                        System.out.println("\n[PRESENCE] There are people in the sauna, the light is switched ON\n>");
                        light.lightSwitch(true);
                        lightOn = true;
                    }
                }

                if(numberOfPeople.get() == 0 && lightOn) {
                    if(light != null) {
                        System.out.println("\n[PRESENCE] The sauna is empty, the light is switched OFF\n>");
                        light.lightSwitch(false);
                        lightOn = false;
                    }
                }
            }

            @Override
            public void onError() {
                System.err.println("\n[ERROR] Presence sensor" + clientPresenceSensor.getURI() + "]\n>");
            }
        });
    }

    public void unregisterPresenceSensor(String ip) {
        if(clientPresenceSensor.getURI().equals(ip)) {
            clientPresenceSensor = null;
            observePresence.proactiveCancel();
            observePresence = null;
        }
    }

    public void addLight(Light light) {
        this.light = light;
    }

    public int getNumberOfPeople() {
        return numberOfPeople.get();
    }
}
