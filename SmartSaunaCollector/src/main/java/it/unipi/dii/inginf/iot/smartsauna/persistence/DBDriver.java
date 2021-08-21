package it.unipi.dii.inginf.iot.smartsauna.persistence;

import it.unipi.dii.inginf.iot.smartsauna.config.ConfigurationParameters;
import it.unipi.dii.inginf.iot.smartsauna.model.AirQualitySample;
import it.unipi.dii.inginf.iot.smartsauna.model.HumiditySample;
import it.unipi.dii.inginf.iot.smartsauna.model.PresenceSample;
import it.unipi.dii.inginf.iot.smartsauna.model.TemperatureSample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBDriver {
    private String databaseIp;
    private int databasePort;
    private String databaseUsername;
    private String databasePassword;
    private String databaseName;

    public DBDriver() {
        ConfigurationParameters configurationParameters = ConfigurationParameters.getInstance();
        this.databaseIp = configurationParameters.getDatabaseIp();
        this.databasePort = configurationParameters.getDatabasePort();
        this.databaseUsername = configurationParameters.getDatabaseUsername();
        this.databasePassword = configurationParameters.getDatabasePassword();
        this.databaseName = configurationParameters.getDatabaseName();
    }

    /**
     * @return the JDBC connection to be used to communicate with MySQL Database.
     *
     * @throws SQLException  in case the connection to the database fails.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://"+ databaseIp + ":" + databasePort +
                        "/" + databaseName + "?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=CET",
                databaseUsername, databasePassword);
    }

    public void insertAirQualitySample(AirQualitySample airQualitySample) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO air_quality (node, concentration) VALUES (?, ?)")
        )
        {
            statement.setInt(1, airQualitySample.getNode());
            statement.setInt(2, airQualitySample.getConcentration());
            statement.executeUpdate();
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Insert a new sample received by a humidity sensor
     * @param humiditySample    sample to be inserted
     */
    public void insertHumiditySample (HumiditySample humiditySample) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO humidity (node, percentage) VALUES (?, ?)")
        )
        {
            statement.setInt(1, humiditySample.getNode());
            statement.setFloat(2, humiditySample.getHumidity());
            statement.executeUpdate();
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void insertPresenceSample(PresenceSample presenceSample) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO presence (node, quantity) VALUES (?, ?)")
        )
        {
            statement.setInt(1, presenceSample.getNode());
            statement.setInt(2, presenceSample.getQuantity());
            statement.executeUpdate();
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void insertTemperatureSample(TemperatureSample temperatureSample) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO temperature (node, degrees) VALUES (?, ?)")
        )
        {
            statement.setInt(1, temperatureSample.getNode());
            statement.setFloat(2, temperatureSample.getTemperature());
            statement.executeUpdate();
        }
        catch (final SQLException e)
        {
            e.printStackTrace();
        }
    }
}
