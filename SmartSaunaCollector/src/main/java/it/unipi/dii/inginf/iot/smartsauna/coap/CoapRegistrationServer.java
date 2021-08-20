package it.unipi.dii.inginf.iot.smartsauna.coap;

import it.unipi.dii.inginf.iot.smartsauna.LightColor;
import it.unipi.dii.inginf.iot.smartsauna.coap.devices.CoapDevicesHandler;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class CoapRegistrationServer extends CoapServer {
    private final static CoapDevicesHandler coapDevicesHandler = CoapDevicesHandler.getInstance();

    public CoapRegistrationServer() throws SocketException {
        this.add(new CoapRegistrationResource());
    }

    // GET measures from sensors
    public int getCO2Level() {
        return coapDevicesHandler.getCO2Level();
    }

    // SET
    public void setLightColor(LightColor lightColor) {
        coapDevicesHandler.setLightColor(lightColor);
    }

    class CoapRegistrationResource extends CoapResource {
        public CoapRegistrationResource() {
            super("registration");
        }

        @Override
        public void handlePOST(CoapExchange exchange) {
            String deviceType = exchange.getRequestText();
            String ip = exchange.getSourceAddress().getHostAddress();
            boolean success = true;

            switch (deviceType) {
                case "co2_sensor":
                    coapDevicesHandler.registerAirQuality(ip);
                    break;
                case "light":
                    coapDevicesHandler.registerLight(ip);
                    break;
                case "presence_sensor":
                    coapDevicesHandler.registerPresenceSensor(ip);
                    break;
                default:
                    success = false;
                    break;
            }

            if (success)
                exchange.respond(CoAP.ResponseCode.CREATED, "Success".getBytes(StandardCharsets.UTF_8));
            else
                exchange.respond(CoAP.ResponseCode.NOT_ACCEPTABLE, "Unsuccessful".getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public void handleDELETE(CoapExchange exchange) {
            String[] request = exchange.getRequestText().split("-");
            String ip = request[0];
            String deviceType = request[1];
            boolean success = true;;

            switch (deviceType) {
                case "co2_sensor":
                    coapDevicesHandler.unregisterAirQuality(ip);
                    break;
                case "light":
                    coapDevicesHandler.unregisterLight(ip);
                    break;
                case "presence_sensor":
                    coapDevicesHandler.unregisterPresenceSensor(ip);
                    break;
                default:
                    success = false;
                    break;
            }

            if(success)
                exchange.respond(CoAP.ResponseCode.DELETED, "Cancellation Completed!".getBytes(StandardCharsets.UTF_8));
            else
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "Cancellation not allowed!".getBytes(StandardCharsets.UTF_8));
        }
    }
}
