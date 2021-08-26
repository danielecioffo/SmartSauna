package it.unipi.dii.inginf.iot.smartsauna.coap.devices.airquality;

import com.google.gson.Gson;
import it.unipi.dii.inginf.iot.smartsauna.model.AirQualitySample;
import it.unipi.dii.inginf.iot.smartsauna.persistence.DBDriver;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AirQuality {
    private List<CoapClient> clientCO2SensorList = new ArrayList<>();
    private List<CoapClient> clientVentilationSystemList = new ArrayList<>();
    private List<CoapObserveRelation> observeCO2List = new ArrayList<>();

    private AtomicInteger co2Level = new AtomicInteger(300);
    private AtomicInteger upperBound = new AtomicInteger(700);
    private boolean ventilationOn = false;

    private Gson parser = new Gson();

    public void registerAirQuality(String ip) {
        System.out.print("\n[REGISTRATION] The Air Quality system: [" + ip + "] is now registered\n>");
        CoapClient newClientCO2Sensor = new CoapClient("coap://[" + ip + "]/air_quality/co2");
        clientCO2SensorList.add(newClientCO2Sensor);

        CoapClient newClientVentilationSystem = new CoapClient("coap://[" + ip + "]/air_quality/ventilation");
        clientVentilationSystemList.add(newClientVentilationSystem);

        CoapObserveRelation newObserveCO2 = newClientCO2Sensor.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                String responseString = new String(coapResponse.getPayload());
                try {
                    AirQualitySample airQualitySample = parser.fromJson(responseString, AirQualitySample.class);
                    DBDriver.getInstance().insertAirQualitySample(airQualitySample);
                    int average = (co2Level.get()*(clientCO2SensorList.size() - 1) + airQualitySample.getConcentration())/(clientCO2SensorList.size());
                    co2Level.set(average);
                } catch (Exception e) {
                    System.out.print("\n[ERROR] The CO2 sensor gave non-significant data\n>");
                }

                if(!ventilationOn && co2Level.get() > upperBound.get()) {
                    System.out.print("\n[AIR QUALITY] CO2 level is HIGH, the ventilation system is switched ON\n>");
                    for (CoapClient clientVentilationSystem: clientVentilationSystemList) {
                        ventilationSystemSwitch(clientVentilationSystem,true);
                    }
                    ventilationOn = true;
                }

                // We don't turn off the ventilation as soon as the value is lower than the upper bound,
                // but we leave a margin so that we don't have to turn on the system again right away
                if (ventilationOn && co2Level.get()  < upperBound.get()*0.7) {
                    System.out.print("\n[AIR QUALITY] CO2 level is now fine. Switch OFF the ventilation system\n>");
                    for (CoapClient clientVentilationSystem: clientVentilationSystemList) {
                        ventilationSystemSwitch(clientVentilationSystem,false);
                    }
                    ventilationOn = false;
                }
            }

            @Override
            public void onError() {
                System.err.print("\n[ERROR] Air Quality " + newClientCO2Sensor.getURI() + "]\n>");
            }
        });

        observeCO2List.add(newObserveCO2);
    }

    public void unregisterAirQuality(String ip) {
        for (int i = 0; i < clientCO2SensorList.size(); i++) {
            if (clientCO2SensorList.get(i).getURI().equals(ip)) {
                clientCO2SensorList.remove(i);
                clientVentilationSystemList.remove(i);
                observeCO2List.get(i).proactiveCancel();
                observeCO2List.remove(i);
            }
        }
    }

    public int getCO2Level() {
        return co2Level.get();
    }

    public void setUpperBound(int upperBound) {
        this.upperBound.set(upperBound);
    }

    private void ventilationSystemSwitch(CoapClient clientVentilationSystem, boolean on) {
        if(clientVentilationSystem == null)
            return;

        String msg = "mode=" + (on ? "ON" : "OFF");
        clientVentilationSystem.put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                if(coapResponse != null) {
                    if(!coapResponse.isSuccess())
                        System.out.print("\n[ERROR] Ventilation System: PUT request unsuccessful\n>");
                }
            }

            @Override
            public void onError() {
                System.err.print("\n[ERROR] Ventilation System " + clientVentilationSystem.getURI() + "]\n>");
            }
        }, msg, MediaTypeRegistry.TEXT_PLAIN);
    }
}
