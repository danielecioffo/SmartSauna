package it.unipi.dii.inginf.iot.smartsauna.model;

public class PresenceSample {
    private int node;
    private int quantity;

    public PresenceSample(int node, int quantity) {
        this.node = node;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "PresenceSample{ " +
                "quantity=" + quantity +
                '}';
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }
}
