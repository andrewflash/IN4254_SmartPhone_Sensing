package nl.tudelft.xflash.activitymonitoringandlocalization.Sensor;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

import nl.tudelft.xflash.activitymonitoringandlocalization.Monitoring.ObserverSensor;

/**
 * Created by xflash on 4-5-16.
 */
public abstract class AbstractSensor implements SensorEventListener {

    protected Sensor type;
    protected SensorManager sm;
    protected ArrayList<ObserverSensor> observerSensorList;
    protected boolean sensorAvailable = false;

    public AbstractSensor(SensorManager sm){
        this.sm = sm;
        this.observerSensorList = new ArrayList<>();
    }

    /**
     * Returns a true if the sensor was initialised well.
     * False if the sensor is not available.
     */
    public boolean available(){
        return this.sensorAvailable;
    }

    /**
     * Register the event listener for certain sensor.
     */
    public void register(){
        if(sensorAvailable) {
            sm.registerListener(this, type, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    /**
     * Unregister the event listener for certain sensor.
     */
    public void unregister(){
        sm.unregisterListener(this);
    }

    /**
     * Add class that needs to be notified when something happens.
     * @param obs
     */
    public void attach(ObserverSensor obs){
        this.observerSensorList.add(obs);
    }

    /**
     * Remove class that needs to be notified when something happens.
     * @param obs
     */
    public void detach(ObserverSensor obs){
        this.observerSensorList.remove(obs);
    }

    /**
     * Notify all classes in the ObserverList.
     */
    public void notifyObserver(int SensorType){
        for(ObserverSensor obs: observerSensorList){
            obs.update(SensorType);
        }
    }
}