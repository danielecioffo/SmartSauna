package it.unipi.dii.inginf.iot.smartsauna.mqtt;

import com.google.gson.Gson;
import it.unipi.dii.inginf.iot.smartsauna.model.HumiditySample;
import it.unipi.dii.inginf.iot.smartsauna.model.TemperatureSample;
import it.unipi.dii.inginf.iot.smartsauna.mqtt.devices.humidity.HumidityCollector;
import it.unipi.dii.inginf.iot.smartsauna.mqtt.devices.temperature.TemperatureCollector;
import org.eclipse.paho.client.mqttv3.*;

public class MQTTHandler implements MqttCallback {

    private final String BROKER = "tcp://127.0.0.1:1883";
    private final String CLIENT_ID = "SmartSaunaCollector";
    private final int SECONDS_TO_WAIT_FOR_RECONNECTION = 5;
    private final int MAX_RECONNECTION_ITERATIONS = 10;

    private MqttClient mqttClient = null;
    private Gson parser;
    private HumidityCollector humidityCollector;
    private TemperatureCollector temperatureCollector;

    public MQTTHandler ()
    {
        parser = new Gson();
        humidityCollector = new HumidityCollector();
        temperatureCollector = new TemperatureCollector();
        do {
            try {
                mqttClient = new MqttClient(BROKER, CLIENT_ID);
                System.out.println("Connecting to the broker: " + BROKER);
                mqttClient.setCallback( this );
                connectToBroker();
            }
            catch(MqttException me)
            {
                System.out.println("I could not connect, Retrying ...");
            }
        }while(!mqttClient.isConnected());
    }

    private void connectToBroker () throws MqttException {
        mqttClient.connect();
        mqttClient.subscribe(humidityCollector.HUMIDITY_TOPIC);
        System.out.println("Subscribed to: " + humidityCollector.HUMIDITY_TOPIC);
        mqttClient.subscribe(temperatureCollector.TEMPERATURE_TOPIC);
        System.out.println("Subscribed to: " + temperatureCollector.TEMPERATURE_TOPIC);
    }

    /**
     * Function used to publish a message
     * @param topic     topic of the message
     * @param message   message to send
     */
    public void publishMessage (final String topic, final String message)
    {
        try
        {
            mqttClient.publish(topic, new MqttMessage(message.getBytes()));
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection with the Broker lost!");
        // We have lost the connection, we have to try to reconnect after waiting some time
        // At each iteration we increase the time waited
        int iter = 0;
        do {
            iter++; // first iteration iter=1
            if (iter > MAX_RECONNECTION_ITERATIONS)
            {
                System.err.println("Reconnection with the broker not possible!");
                System.exit(-1);
            }
            try
            {
                Thread.sleep(SECONDS_TO_WAIT_FOR_RECONNECTION * 1000 * iter);
                System.out.println("New attempt to connect to the broker...");
                connectToBroker();
            }
            catch (MqttException | InterruptedException e)
            {
                e.printStackTrace();
            }
        } while (!this.mqttClient.isConnected());
        System.out.println("Connection with the Broker restored!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String payload = new String(mqttMessage.getPayload());
        if (topic.equals(humidityCollector.HUMIDITY_TOPIC))
        {
            HumiditySample humiditySample = parser.fromJson(payload, HumiditySample.class);
            humidityCollector.addHumiditySample(humiditySample);
            float newAverage = humidityCollector.getAverage();
            if (newAverage < humidityCollector.getLowerBoundHumidity())
            {
                if (!humidityCollector.getLastCommand().equals(humidityCollector.INC))
                {
                    System.out.println("Average level of Humidity too low, increase it");
                    publishMessage(humidityCollector.HUMIDIFIER_TOPIC, humidityCollector.INC);
                    humidityCollector.setLastCommand(humidityCollector.INC);
                }
                else
                    System.out.println("Average level of Humidity too low, but is increasing");
            }
            else if (newAverage > humidityCollector.getUpperBoundHumidity())
            {
                if (!humidityCollector.getLastCommand().equals(humidityCollector.DEC))
                {
                    System.out.println("Average level of Humidity too high, decrease it");
                    publishMessage(humidityCollector.HUMIDIFIER_TOPIC, humidityCollector.DEC);
                    humidityCollector.setLastCommand(humidityCollector.DEC);
                }
                else
                    System.out.println("Average level of Humidity too high, but is decreasing");
            }
            else
            {
                if (!humidityCollector.getLastCommand().equals(humidityCollector.OFF))
                {
                    System.out.println("Correct average humidity level, switch off the humidifier/dehumidifier");
                    publishMessage(humidityCollector.HUMIDIFIER_TOPIC, humidityCollector.OFF);
                    humidityCollector.setLastCommand(humidityCollector.OFF);
                }
                else
                {
                    System.out.println("Correct average humidity level");
                }
            }
        }
        else if (topic.equals(temperatureCollector.TEMPERATURE_TOPIC))
        {
            TemperatureSample temperatureSample = parser.fromJson(payload, TemperatureSample.class);
            temperatureCollector.addTemperatureSample(temperatureSample);
            float newAverage = temperatureCollector.getAverage();
            if (newAverage < temperatureCollector.getLowerBoundTemperature())
            {
                if (!temperatureCollector.getLastCommand().equals(humidityCollector.INC))
                {
                    System.out.println("Average level of Temperature too low, increase it");
                    publishMessage(temperatureCollector.AC_TOPIC, temperatureCollector.INC);
                    temperatureCollector.setLastCommand(temperatureCollector.INC);
                }
                else
                    System.out.println("Average level of Temperature too low, but is increasing");
            }
            else if (newAverage > temperatureCollector.getUpperBoundTemperature())
            {
                if (!temperatureCollector.getLastCommand().equals(humidityCollector.DEC))
                {
                    System.out.println("Average level of Temperature too high, decrease it");
                    publishMessage(temperatureCollector.AC_TOPIC, temperatureCollector.DEC);
                    temperatureCollector.setLastCommand(temperatureCollector.DEC);
                }
                else
                    System.out.println("Average level of Temperature too high, but is decreasing");
            }
            else
            {
                if (!temperatureCollector.getLastCommand().equals(humidityCollector.OFF))
                {
                    System.out.println("Correct average temperature level, switch off the AC");
                    publishMessage(temperatureCollector.AC_TOPIC, temperatureCollector.OFF);
                    temperatureCollector.setLastCommand(temperatureCollector.OFF);
                }
                else
                    System.out.println("Correct average Temperature level");
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("Message correctly delivered");
    }

    public HumidityCollector getHumidityCollector() {
        return humidityCollector;
    }

    public TemperatureCollector getTemperatureCollector() {
        return temperatureCollector;
    }
}
