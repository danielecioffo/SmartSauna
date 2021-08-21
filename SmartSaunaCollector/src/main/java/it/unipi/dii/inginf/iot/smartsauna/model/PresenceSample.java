package it.unipi.dii.inginf.iot.smartsauna.model;

public class PresenceSample {
    private int quantity;

    public PresenceSample(int quantity) {
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
}
