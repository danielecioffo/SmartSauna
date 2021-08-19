package it.unipi.dii.inginf.iot.smartsauna.model;

public class HumiditySample {
    private int node; // ID of the node
    private float humidity;

    public HumiditySample(int node, float humidity) {
        this.node = node;
        this.humidity = humidity;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }
}
