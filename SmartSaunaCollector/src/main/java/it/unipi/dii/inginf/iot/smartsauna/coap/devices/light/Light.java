package it.unipi.dii.inginf.iot.smartsauna.coap.devices.light;

import it.unipi.dii.inginf.iot.smartsauna.LightColor;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class Light {
    private CoapClient clientLightSwitch;
    private CoapClient clientLightColor;

    public void registerLight(String ip) {
        System.out.println("[REGISTRATION] The light: [" + ip + "] is now registered");
        clientLightSwitch = new CoapClient("coap://[" + ip + "]/light/switch");
        clientLightColor = new CoapClient("coap://[" + ip + "]/light/color");
    }

    public void unregisterLight(String ip) {
        if(clientLightColor.getURI().equals(ip)) {
            clientLightColor = null;
        }

        if(clientLightSwitch.getURI().equals(ip)) {
            clientLightSwitch = null;
        }
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
                        System.out.println("[ERROR]Light Switch: PUT request unsuccessful");
                }
            }

            @Override
            public void onError() {
                System.err.println("[ERROR] Light Switch " + clientLightSwitch.getURI() + "]");
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
                    System.out.println("[ERROR] Light Color: PUT request unsuccessful");
            }

            @Override
            public void onError() {
                System.err.println("[ERROR] Light Color " + clientLightColor.getURI() + "]");
            }
        }, msg, MediaTypeRegistry.TEXT_PLAIN);

        return true;
    }
}
