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
        System.out.println("The presence sensor: [" + ip + "] + is now registered");
        clientPresenceSensor = new CoapClient("coap://[" + ip + "]/presence");

        observePresence = clientPresenceSensor.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                String responseString = coapResponse.getResponseText();
                numberOfPeople.set(Integer.parseInt(responseString));

                if(numberOfPeople.get() > 0 && !lightOn) {
                    if(light != null) {
                        System.out.println("There are people in the sauna, the light is switched ON");
                        light.lightSwitch(true);
                        lightOn = true;
                    }
                }

                if(numberOfPeople.get() == 0 && lightOn) {
                    if(light != null) {
                        System.out.println("The sauna is empty, the light is switched OFF");
                        light.lightSwitch(false);
                        lightOn = false;
                    }
                }
            }

            @Override
            public void onError() {
                System.err.println("[ERROR: Presence sensor" + clientPresenceSensor.getURI() + "]");
            }
        });
    }

    public void addLight(Light light) {
        this.light = light;
    }

    public int getNumberOfPeople() {
        return numberOfPeople.get();
    }

}
