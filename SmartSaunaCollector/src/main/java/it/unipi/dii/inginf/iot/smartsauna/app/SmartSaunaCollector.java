package it.unipi.dii.inginf.iot.smartsauna.app;

import it.unipi.dii.inginf.iot.smartsauna.config.ConfigurationParameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SmartSaunaCollector {
    public static void main(String[] args) {
        ConfigurationParameters configurationParameters = ConfigurationParameters.getInstance();
        System.out.println(configurationParameters);

        printAvailableCommands();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String command;
        String[] parts;

        while (true) {
            System.out.print("> ");
            try {
                command = bufferedReader.readLine();
                parts = command.split(" ");

                switch (parts[0]) {
                    case "!help":
                        helpFunction();
                        break;
                    case "!get_humidity":
                        getHumidityFunction();
                        break;
                    case "!set_humidity":
                        setHumidityFunction(parts);
                        break;
                    case "!get_temperature":
                        getTemperatureFunction();
                        break;
                    case "!set_temperature":
                        setTemperatureFunction(parts);
                        break;
                    case "!get_air_quality":
                        getAirQualityFunction();
                        break;
                    case "!set_air_quality":
                        setAirQualityFunction(parts);
                        break;
                    case "!set_color":
                        setColorFunction(parts);
                        break;
                    case "!get_number_of_people":
                        getNumberOfPeopleFunction();
                        break;
                    case "!set_max_number_of_people":
                        setMaxNumberOfPeopleFunction(parts);
                        break;
                    case "!exit":
                        System.out.println("Bye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Command not recognized, try again\n");
                        break;
                }
                printAvailableCommands();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printAvailableCommands() {
        System.out.println("***************************** SMART SAUNA *****************************\n" +
                "The following commands are available:\n" +
                "1) !help <command> --> shows the details of a command\n" +
                "2) !get_humidity --> recovers the last humidity measurement\n" +
                "3) !set_humidity <lower bound> <upper bound> --> sets the range within which the humidity must stay\n" +
                "4) !get_temperature --> recovers the last temperature measurement\n" +
                "5) !set_temperature <lower bound> <upper_bound> --> sets the range within which the temperature must stay\n" +
                "6) !get_air_quality --> recovers the last CO2 concentration measurement\n" +
                "7) !set_air_quality <upper bound> --> sets the limit below which the CO2 concentration must stay\n" +
                "8) !set_color <color> --> sets the light color\n" +
                "9) !get_number_of_people --> retrieves the number of people inside the sauna\n" +
                "10) !set_max_number_of_people <number> --> sets a limit on the number of people who can enter\n" +
                "11) !exit --> terminates the program\n"
        );
    }

    private static void helpFunction() {

    }

    private static void getHumidityFunction() {

    }

    private static void setHumidityFunction(String[] parts) {

    }

    private static void getTemperatureFunction() {

    }

    private static void setTemperatureFunction(String[] parts) {

    }

    private static void getAirQualityFunction() {

    }

    private static void setAirQualityFunction(String[] parts) {

    }

    private static void setColorFunction(String[] parts) {

    }

    private static void getNumberOfPeopleFunction() {

    }

    private static void setMaxNumberOfPeopleFunction(String[] parts) {

    }
}
