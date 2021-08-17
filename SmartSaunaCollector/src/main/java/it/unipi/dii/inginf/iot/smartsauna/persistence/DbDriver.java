package it.unipi.dii.inginf.iot.smartsauna.persistence;

import it.unipi.dii.inginf.iot.smartsauna.config.ConfigurationParameters;

public class DbDriver {
    private String databaseIp;
    private int databasePort;
    private String databaseUsername;
    private String databasePassword;
    private String databaseName;

    public DbDriver ()
    {
        ConfigurationParameters configurationParameters = ConfigurationParameters.getInstance();
        this.databaseIp = configurationParameters.getDatabaseIp();
        this.databasePort = configurationParameters.getDatabasePort();
        this.databaseUsername = configurationParameters.getDatabaseUsername();
        this.databasePassword = configurationParameters.getDatabasePassword();
        this.databaseName = configurationParameters.getDatabaseName();
    }
}
