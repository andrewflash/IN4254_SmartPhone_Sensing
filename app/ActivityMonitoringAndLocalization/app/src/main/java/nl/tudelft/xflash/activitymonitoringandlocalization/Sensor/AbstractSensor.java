package nl.tudelft.xflash.activitymonitoringandlocalization.Sensor;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by xflash on 4-5-16.
 */
public abstract class AbstractSensor implements SensorEventListener {

    protected Sensor type;
    protected SensorManager sm;
    protected ArrayList<ObserverSensor> observerSensorList;
    protected boolean sensorAvailable = false;
    protected boolean isStabilizing = false;
    protected static final int MAX_SAMPLE_STABLE = 100;
    protected int numSamples = 0;

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
    public void register(int samplingPeriodUs){
        if(sensorAvailable) {
            //sm.registerListener(this, type, SensorManager.SENSOR_DELAY_FASTEST);
            isStabilizing = false;
            sm.registerListener(this, type, samplingPeriodUs);
        }
    }

    /**
     * Unregister the event listener for certain sensor.
     */
    public void unregister(){
        sm.unregisterListener(this);
        isStabilizing = false;
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
        if(!isStabilizing){
            numSamples++;
            if(numSamples >= MAX_SAMPLE_STABLE) {
                numSamples = 0;
                isStabilizing = true;
            }
            return;
        }

        for(ObserverSensor obs: observerSensorList){
            obs.update(SensorType);
        }
    }
}