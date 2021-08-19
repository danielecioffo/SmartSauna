package it.unipi.dii.inginf.iot.smartsauna.mqtt;

import org.eclipse.paho.client.mqttv3.*;

import java.util.HashMap;
import java.util.Map;

public class MQTTHandler implements MqttCallback {

    private final String BROKER = "tcp://127.0.0.1:1883";
    private final String CLIENT_ID = "SmartSaunaCollector";
    private final String HUMIDITY_TOPIC = "humidity";
    private final String HUMIDIFIER_TOPIC = "humidifier";
    private final int SECONDS_TO_WAIT_FOR_RECONNECTION = 5;
    private final int MAX_RECONNECTION_ITERATIONS = 10;

    private MqttClient mqttClient = null;
    private Map<Integer, Float> lastHumiditySamples = new HashMap<>();

    public MQTTHandler ()
    {
        do {
            try {
                mqttClient = new MqttClient(BROKER, CLIENT_ID);
                System.out.println("Connecting to the broker: " + BROKER);

                mqttClient.setCallback( this );
                mqttClient.connect();

                mqttClient.subscribe(HUMIDITY_TOPIC);
                System.out.println("Subscribed to: " + HUMIDITY_TOPIC);

            }
            catch(MqttException me)
            {
                System.out.println("I could not connect, Retrying ...");
            }
        }while(!mqttClient.isConnected());
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
                mqttClient.connect();
                mqttClient.subscribe(HUMIDITY_TOPIC);
            }
            catch (MqttException | InterruptedException e)
            {
                e.printStackTrace();
            }
        } while (!this.mqttClient.isConnected());
        System.out.println("Connection with the Broker restored!");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
