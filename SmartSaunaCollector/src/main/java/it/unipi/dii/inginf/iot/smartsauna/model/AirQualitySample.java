package it.unipi.dii.inginf.iot.smartsauna.model;

public class AirQualitySample {
    private int node; // Node ID
    private int concentration;


    public AirQualitySample(int node, int concentration) {
        this.node = node;
        this.concentration = concentration;
    }


    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public int getConcentration() {
        return concentration;
    }

    public void setConcentration(int concentration) {
        this.concentration = concentration;
    }

    @Override
    public String toString() {
        return "AirQualitySample{" +
                "node=" + node +
                ", co2_concentration=" + concentration +
                '}';
    }
}
