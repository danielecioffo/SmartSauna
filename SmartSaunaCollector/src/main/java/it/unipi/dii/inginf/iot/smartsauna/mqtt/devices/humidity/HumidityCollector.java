package it.unipi.dii.inginf.iot.smartsauna.mqtt.devices.humidity;

import it.unipi.dii.inginf.iot.smartsauna.config.ConfigurationParameters;
import it.unipi.dii.inginf.iot.smartsauna.model.HumiditySample;
import sun.security.krb5.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class HumidityCollector {
    public final String HUMIDITY_TOPIC = "humidity";
    public final String HUMIDIFIER_TOPIC = "humidifier";
    public final String INC = "INC";
    public final String DEC = "DEC";
    public final String OFF = "OFF";

    private Map<Integer, HumiditySample> lastHumiditySamples;
    private float lowerBoundHumidity;
    private float upperBoundHumidity;
    private String lastCommand;

    public HumidityCollector() {
        lastHumiditySamples = new HashMap<>();
        ConfigurationParameters configurationParameters = ConfigurationParameters.getInstance();
        lowerBoundHumidity = configurationParameters.getLowerBoundHumidity();
        upperBoundHumidity = configurationParameters.getUpperBoundHumidity();
        lastCommand = OFF;
    }

    public void addHumiditySample (HumiditySample humiditySample)
    {
        lastHumiditySamples.put(humiditySample.getNode(), humiditySample);
        System.out.println(humiditySample);
    }

    public HumiditySample getLastHumiditySampleOfNode (int node)
    {
        return lastHumiditySamples.get(node);
    }

    public float getAverage ()
    {
        int howMany = lastHumiditySamples.size();
        float sum = lastHumiditySamples.values().stream()
                .map(HumiditySample::getHumidity) // take only the humidity
                .reduce((float) 0, Float::sum); // sum the values
        return sum / howMany;
    }

    public float getLowerBoundHumidity() {
        return lowerBoundHumidity;
    }

    public float getUpperBoundHumidity() {
        return upperBoundHumidity;
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }
}
