package it.unipi.dii.inginf.iot.smartsauna.persistence;

import it.unipi.dii.inginf.iot.smartsauna.config.ConfigurationParameters;
import it.unipi.dii.inginf.iot.smartsauna.model.HumiditySample;

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

    public DBDriver()
    {
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
    private Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection("jdbc:mysql://"+ databaseIp + ":" + databasePort +
                        "/" + databaseName + "?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=CET",
                databaseUsername, databasePassword);
    }

    /**
     * Insert a new sample received by an humidity sensor
     * @param humiditySample    sample to be inserted
     */
    public void insertHumiditySample (HumiditySample humiditySample)
    {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO humidity (node, percentage) VALUES (?, ?)");
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
}
