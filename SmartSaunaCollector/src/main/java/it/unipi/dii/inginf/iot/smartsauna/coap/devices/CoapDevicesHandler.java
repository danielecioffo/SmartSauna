package it.unipi.dii.inginf.iot.smartsauna.coap.devices;

import it.unipi.dii.inginf.iot.smartsauna.LightColor;
import it.unipi.dii.inginf.iot.smartsauna.coap.devices.airquality.AirQuality;
import it.unipi.dii.inginf.iot.smartsauna.coap.devices.light.Light;
import it.unipi.dii.inginf.iot.smartsauna.coap.devices.presence.PresenceSensor;

public class CoapDevicesHandler {
    private AirQuality airQuality = new AirQuality();
    private Light light = new Light();
    private PresenceSensor presenceSensor = new PresenceSensor();

    private static CoapDevicesHandler instance = null;

    private CoapDevicesHandler() {
        presenceSensor.addLight(light);
    }
    public static CoapDevicesHandler getInstance() {
        if(instance == null)
            instance = new CoapDevicesHandler();

        return instance;
    }

    /*      REGISTER AND UNREGISTER DEVICES     */
    public void registerAirQuality(String ip) {
        airQuality.registerAirQuality(ip);
    }

    public void registerLight(String ip) {
        light.registerLight(ip);
    }

    public void registerPresenceSensor(String ip) {
        presenceSensor.registerPresenceSensor(ip);
    }

    public void unregisterAirQuality(String ip) {
        airQuality.unregisterAirQuality(ip);
    }

    public void unregisterLight(String ip) {
        light.unregisterLight(ip);
    }

    public void unregisterPresenceSensor(String ip) {
        presenceSensor.unregisterPresenceSensor(ip);
    }

    /*      GET MEASURES FROM SENSORS     */
    public int getCO2Level() {
        return airQuality.getCO2Level();
    }

    public int getNumberOfPeople() {
        return presenceSensor.getNumberOfPeople();
    }

    /*      SET     */
    public void setLightColor(LightColor lightColor) {
        light.changeLightColor(lightColor);
    }

    public void setMaxNumberOfPeople(int maxNumberOfPeople) {
        presenceSensor.setMaxNumberOfPeople(maxNumberOfPeople);
    }

    public void setCO2UpperBound(int upperBound) {
        airQuality.setUpperBound(upperBound);
    }



}
