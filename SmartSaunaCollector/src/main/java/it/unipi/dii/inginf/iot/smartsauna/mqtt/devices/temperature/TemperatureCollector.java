package it.unipi.dii.inginf.iot.smartsauna.mqtt.devices.temperature;

import it.unipi.dii.inginf.iot.smartsauna.config.ConfigurationParameters;
import it.unipi.dii.inginf.iot.smartsauna.model.HumiditySample;
import it.unipi.dii.inginf.iot.smartsauna.model.TemperatureSample;

import java.util.HashMap;
import java.util.Map;

public class TemperatureCollector {
    public final String TEMPERATURE_TOPIC = "temperature";
    public final String AC_TOPIC = "AC";
    public final String INC = "INC";
    public final String DEC = "DEC";
    public final String OFF = "OFF";

    private Map<Integer, TemperatureSample> lastTemperatureSamples;
    private float lowerBoundTemperature;
    private float upperBoundTemperature;
    private String lastCommand;

    public TemperatureCollector ()
    {
        lastTemperatureSamples = new HashMap<>();
        ConfigurationParameters configurationParameters = ConfigurationParameters.getInstance();
        lowerBoundTemperature = configurationParameters.getLowerBoundTemperature();
        upperBoundTemperature = configurationParameters.getUpperBoundTemperature();
        lastCommand = OFF;
    }

    public void addTemperatureSample (TemperatureSample temperatureSample)
    {
        lastTemperatureSamples.put(temperatureSample.getNode(), temperatureSample);
        System.out.println(temperatureSample);
    }

    public float getAverage ()
    {
        int howMany = lastTemperatureSamples.size();
        float sum = lastTemperatureSamples.values().stream()
                .map(TemperatureSample::getTemperature) // take only the temperature
                .reduce((float) 0, Float::sum); // sum the values
        return sum / howMany;
    }

    public float getLowerBoundTemperature() {
        return lowerBoundTemperature;
    }

    public void setLowerBoundTemperature(float lowerBoundTemperature) {
        this.lowerBoundTemperature = lowerBoundTemperature;
    }

    public float getUpperBoundTemperature() {
        return upperBoundTemperature;
    }

    public void setUpperBoundTemperature(float upperBoundTemperature) {
        this.upperBoundTemperature = upperBoundTemperature;
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }
}
