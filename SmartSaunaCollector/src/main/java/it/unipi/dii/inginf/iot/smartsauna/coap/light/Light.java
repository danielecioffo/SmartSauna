package it.unipi.dii.inginf.iot.smartsauna.coap.light;

import it.unipi.dii.inginf.iot.smartsauna.LightColor;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class Light {
    private CoapClient clientLightSwitch;
    private CoapClient clientLightColor;

    public void addLight(String ip) {
        System.out.println("The light: [" + ip + "] + is now registered");
        clientLightSwitch = new CoapClient("coap://[" + ip + "]/light/switch");
        clientLightColor = new CoapClient("coap://[" + ip + "]/light/color");
    }

    public boolean lightSwitch(boolean on) {
        if(clientLightSwitch == null)
            return false;

        String msg = "mode=" + (on ? "ON" : "OFF");
        clientLightSwitch.put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                if(coapResponse != null) {
                    if(!coapResponse.isSuccess())
                        System.out.println("Light Switch: PUT request unsuccessful");
                }
            }

            @Override
            public void onError() {
                System.err.println("[ERROR: Light Switch " + clientLightSwitch.getURI() + "]");
            }
        }, msg, MediaTypeRegistry.TEXT_PLAIN);

        return true;
    }

    public boolean changeLightColor(LightColor color) {
        if(clientLightColor == null)
            return false;

        String msg = "color=" + color.name();
        clientLightColor.put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                if(!coapResponse.isSuccess())
                    System.out.println("Light Color: PUT request unsuccessful");
            }

            @Override
            public void onError() {
                System.err.println("[ERROR: Light Color " + clientLightColor.getURI() + "]");
            }
        }, msg, MediaTypeRegistry.TEXT_PLAIN);

        return true;
    }
}
