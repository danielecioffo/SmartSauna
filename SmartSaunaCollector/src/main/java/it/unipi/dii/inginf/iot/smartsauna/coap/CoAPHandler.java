package it.unipi.dii.inginf.iot.smartsauna.coap;

import it.unipi.dii.inginf.iot.smartsauna.coap.devices.airquality.CO2Sensor;
import it.unipi.dii.inginf.iot.smartsauna.coap.devices.airquality.VentilationSystem;
import it.unipi.dii.inginf.iot.smartsauna.coap.devices.light.Light;
import it.unipi.dii.inginf.iot.smartsauna.coap.devices.presence.PresenceSensor;

public class CoAPHandler {
    private CO2Sensor co2Sensor = new CO2Sensor();
    private VentilationSystem ventilationSystem = new VentilationSystem();
    private Light light = new Light();
    private PresenceSensor presenceSensor = new PresenceSensor();

    private static CoAPHandler instance = null;

    private CoAPHandler() {
        co2Sensor.addVentilationSystem(ventilationSystem);
        presenceSensor.addLight(light);
    }
    public static CoAPHandler getInstance() {
        if(instance == null)
            instance = new CoAPHandler();

        return instance;
    }

    public void registerCO2Sensor(String ip) {
        co2Sensor.registerCO2Sensor(ip);
    }

    public void registerVentilationSystem(String ip) {
        ventilationSystem.registerVentilationSystem(ip);
    }

    public void registerLight(String ip) {
        light.registerLight(ip);
    }

    public void registerPresenceSensor(String ip) {
        presenceSensor.registerPresenceSensor(ip);
    }

    // TODO get e set
}
