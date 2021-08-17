package it.unipi.dii.inginf.iot.smartsauna.app;

import it.unipi.dii.inginf.iot.smartsauna.config.ConfigurationParameters;

public class SmartSaunaCollector {
    public static void main(String[] args) {
        ConfigurationParameters configurationParameters = ConfigurationParameters.getInstance();
        System.out.println(configurationParameters);
    }
}
