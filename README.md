# SmartSauna
SmartSauna is a smart solution for managing a sauna that, thanks to IoT technology, allows you to automate all the processes necessary to maintain the proper environment. The full documentation can be found [here](SmartSauna_documentation.pdf).

Through the use of sensors and actuators, the application is able to automatically manage the temperature, the percentage of humidity and the concentration of CO2 in the air inside the sauna. In addition, a presence sensor that counts the number of people inside the sauna achieves a twofold objective: on the one hand, it triggers the switching on or off of an intelligent lighting system; on the other hand, it allows to limit the number of entrances so as to meet current COVID-19 regulations.

The application also gives the sauna owner a high degree of freedom as it allows him to set the thresholds as he prefers and to choose the color of the light, in order to benefit from chromotherapy. 

Data from the sensors are collected and then stored in a MySQL database. The user can then visualize them through a web interface developed using Grafana. 
