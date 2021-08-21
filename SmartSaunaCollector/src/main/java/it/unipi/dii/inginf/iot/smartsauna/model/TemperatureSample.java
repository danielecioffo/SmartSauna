package it.unipi.dii.inginf.iot.smartsauna.model;

public class TemperatureSample {
    private int node; // Node ID
    private float temperature;

    public TemperatureSample(int node, float temperature) {
        this.node = node;
        this.temperature = temperature;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "TemperatureSample{" +
                "node=" + node +
                ", temperature=" + temperature +
                '}';
    }
}
