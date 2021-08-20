package it.unipi.dii.inginf.iot.smartsauna.coap.devices;

import it.unipi.dii.inginf.iot.smartsauna.coap.devices.airquality.CO2Sensor;
import it.unipi.dii.inginf.iot.smartsauna.coap.devices.airquality.VentilationSystem;
import it.unipi.dii.inginf.iot.smartsauna.coap.devices.light.Light;
import it.unipi.dii.inginf.iot.smartsauna.coap.devices.presence.PresenceSensor;

public class CoapDevicesHandler {
    private CO2Sensor co2Sensor = new CO2Sensor();
    private VentilationSystem ventilationSystem = new VentilationSystem();
    private Light light = new Light();
    private PresenceSensor presenceSensor = new PresenceSensor();

    private static CoapDevicesHandler instance = null;

    private CoapDevicesHandler() {
        co2Sensor.addVentilationSystem(ventilationSystem);
        presenceSensor.addLight(light);
    }
    public static CoapDevicesHandler getInstance() {
        if(instance == null)
            instance = new CoapDevicesHandler();

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

    public void unregisterCO2Sensor(String ip) {
        co2Sensor.unregisterCO2Sensor(ip);
    }

    public void unregisterVentilationSystem(String ip) {
        ventilationSystem.unregisterVentilationSystem(ip);
    }

    public void unregisterLight(String ip) {
        light.unregisterLight(ip);
    }

    public void unregisterPresenceSensor(String ip) {
        presenceSensor.unregisterPresenceSensor(ip);
    }

    // TODO get e set
}
