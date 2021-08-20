package it.unipi.dii.inginf.iot.smartsauna.app;

import it.unipi.dii.inginf.iot.smartsauna.LightColor;
import it.unipi.dii.inginf.iot.smartsauna.coap.CoapRegistrationServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;

public class SmartSaunaCollector {
    public static void main(String[] args) throws SocketException {
        CoapRegistrationServer coapRegistrationServer = new CoapRegistrationServer();
        coapRegistrationServer.start();

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
                        helpFunction(parts);
                        break;
                    case "!get_humidity":
                        getHumidityFunction(coapRegistrationServer);
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
                        getAirQualityFunction(coapRegistrationServer);
                        break;
                    case "!set_air_quality":
                        setAirQualityFunction(parts);
                        break;
                    case "!set_color":
                        setColorFunction(coapRegistrationServer, parts);
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
                "8) !set_color <color> --> sets the light color (GREEN, YELLOW or RED)\n" +
                "9) !get_number_of_people --> retrieves the number of people inside the sauna\n" +
                "10) !set_max_number_of_people <number> --> sets a limit on the number of people who can enter\n" +
                "11) !exit --> terminates the program\n"
        );
    }

    private static void helpFunction(String[] parts) {
        if(parts.length != 2) {
            System.out.println("Incorrect use of the command. Please use !help <command>\n");
        } else {
            switch (parts[1]) {
                case "!help":
                case "help":
                    System.out.println("!help shows the details of the command passed as parameter.\n");
                    break;
                case "!get_humidity":
                case "get_humidity":
                    System.out.println("!get_humidity allows to retrieve the percentage value of humidity in the air inside the sauna.\n");
                    break;
                case "!set_humidity":
                case "set_humidity":
                    System.out.println("!set_humidity allows you to set the range within which the humidity level should be found inside the sauna.\n" +
                            "Two parameters are required: the lower and the upper bounds.\n");
                    break;
                case "!get_temperature":
                case "get_temperature":
                    System.out.println("!get_temperature allows to retrieve the temperature inside the sauna, expressed in degrees Celsius.\n");
                    break;
                case "!set_temperature":
                case "set_temperature":
                    System.out.println("!set_temperature allows you to set the range within which the temperature should be inside the sauna.\n" +
                            "Two parameters are required: the lower and the upper bounds.\n");
                    break;
                case "!get_air_quality":
                case "get_air_quality":
                    System.out.println("!get_air_quality allows you to retrieve the CO2 level inside the sauna, expressed in parts per million (ppm).\n");
                    break;
                case "!set_air_quality":
                case "set_air_quality":
                    System.out.println("!set_air_quality allows you to set the maximum level of CO2 that can be inside the sauna.\n" +
                            "One parameter is required: the upper bound.\n");
                    break;
                case "!set_color":
                case "set_color":
                    System.out.println("!set_color allows you to set the color of the light inside the sauna.\n" +
                            "A parameter is required, i.e. the color, which can take three values: GREEN, YELLOW or RED. \n");
                    break;
                case "!get_number_of_people":
                case "get_number_of_people":
                    System.out.println("!get_number_of_people allows you to retrieve the number of people who are inside the sauna.\n");
                    break;
                case "!set_max_number_of_people":
                case "set_max_number_of_people":
                    System.out.println("!set_max_number_of_people allows you to set the maximum number of people who can be inside the sauna at the same time.\n" +
                            "One parameter is required: the maximum number of people\n");
                    break;
                case "!exit":
                case "exit":
                    System.out.println("!exit allows you to terminate the program.\n");
                    break;
                default:
                    System.out.println("Command not recognized, try again\n");
                    break;
            }
        }
    }

    private static void getHumidityFunction(CoapRegistrationServer coapRegistrationServer) {
        // TODO leggi umidità da sensore
        int humidity = 50;
        System.out.println("The humidity level in the sauna is " + humidity + "%\n");
    }

    private static void setHumidityFunction(String[] parts) {
        if(parts.length != 3) {
            System.out.println("Incorrect use of the command. Please use !set_humidity <lower bound> <upper bound>\n");
        } else {
            int lowerBound;
            int upperBound;
            try {
                lowerBound = Integer.parseInt(parts[1]);
                upperBound = Integer.parseInt(parts[2]);
                if (upperBound < lowerBound) {
                    System.out.println("ERROR: The upper bound must be larger than the lower bound\n");
                    return;
                }
                // TODO setta nuovi bound
                System.out.println("Humidity range set correctly: [" + lowerBound + "% - " + upperBound + "%]\n");
            } catch(Exception e) {
                System.out.println("Please enter integer values\n");
            }
        }
    }

    private static void getTemperatureFunction() {
        // TODO leggi temperatura da sensore
        int temperature = 50;
        System.out.println("The temperature in the sauna is " + temperature + "°C\n");
    }

    private static void setTemperatureFunction(String[] parts) {
        if(parts.length != 3) {
            System.out.println("Incorrect use of the command. Please use !set_temperature <lower bound> <upper bound>\n");
        } else {
            int lowerBound;
            int upperBound;
            try {
                lowerBound = Integer.parseInt(parts[1]);
                upperBound = Integer.parseInt(parts[2]);
                if(upperBound < lowerBound) {
                    System.out.println("ERROR: The upper bound must be larger than the lower bound\n");
                    return;
                }
                // TODO setta nuovi bound
                System.out.println("Temperature range set correctly: [" + lowerBound + "°C - " + upperBound + "°C]\n");
            } catch (Exception e) {
                System.out.println("Please enter integer values\n");
            }
        }
    }

    private static void getAirQualityFunction(CoapRegistrationServer coapRegistrationServer) {
        int co2 = coapRegistrationServer.getCO2Level();
        System.out.println("The CO2 concentration in the sauna is " + co2 + " ppm\n");
    }

    private static void setAirQualityFunction(String[] parts) {
        if(parts.length != 2) {
            System.out.println("Incorrect use of the command. Please use !set_air_quality <upper bound>\n");
        } else {
            int upperBound;
            try {
                upperBound = Integer.parseInt(parts[1]);
                // TODO setta nuovo bound
                System.out.println("New upper bound for CO2 level set correctly: " + upperBound + " ppm\n");
            }  catch(Exception e) {
            System.out.println("Please enter an integer\n");
            }
        }
    }

    private static void setColorFunction(CoapRegistrationServer coapRegistrationServer, String[] parts) {
        if(parts.length != 2) {
            System.out.println("Incorrect use of the command. Please use !set_color <color>\n");
        } else {
            switch(parts[1]) {  // TODO imposta i colori effettivamente sulla luce
                case "GREEN":
                    coapRegistrationServer.setLightColor(LightColor.GREEN);
                    System.out.println("Light color correctly set to GREEN\n");
                    break;
                case "RED":
                    System.out.println("Light color correctly set to RED\n");
                    break;
                case "YELLOW":
                    System.out.println("Light color correctly set to YELLOW\n");
                    break;
                default:
                    System.out.println("Invalid color, please use GREEN, YELLOW or RED\n");
                    break;
            }
        }
    }

    private static void getNumberOfPeopleFunction() {
        // TODO leggi numero di persone da sensore
        int people = 10;
        System.out.println("There are " + people + " people inside the sauna\n");
    }

    private static void setMaxNumberOfPeopleFunction(String[] parts) {
        if(parts.length != 2) {
            System.out.println("Incorrect use of the command. Please use !set_max_number_of_people <number>\n");
        } else {
            int max;
            try {
                max = Integer.parseInt(parts[1]);
                // TODO imposta il nuovo bound
                System.out.println("New maximum number of people set correctly: " + max +"\n");
            } catch(Exception e) {
                System.out.println("Please enter an integer value");
            }
        }
    }
}
