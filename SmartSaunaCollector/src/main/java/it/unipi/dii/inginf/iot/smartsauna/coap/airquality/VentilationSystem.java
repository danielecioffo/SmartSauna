package it.unipi.dii.inginf.iot.smartsauna.coap.airquality;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class VentilationSystem {
    private CoapClient clientVentilationSystem;

    public void addVentilationSystem(String ip) {
        System.out.println("The ventilation system: [" + ip + "] + is now registered");
        clientVentilationSystem = new CoapClient("coap://[" + ip + "]/air-quality/ventilation");
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
