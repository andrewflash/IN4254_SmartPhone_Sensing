package nl.tudelft.xflash.activitymonitoringandlocalization.Sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by xflash on 27-5-16.
 */
public class Magnetometer extends AbstractSensor {
    private static float[] magnet = {0f,0f,0f};

    public Magnetometer(SensorManager sm) {
        super(sm);
        type = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if(type != null){
            Log.d("Magnetometer", "Sensor is initialized.");
            sensorAvailable = true;
        }
        else{
            Log.d("Magnetometer", "Sensor is not available.");
            sensorAvailable = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // copy new magnetometer data into magnet array
        System.arraycopy(sensorEvent.values, 0, magnet, 0, 3);

        this.notifyObserver(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public static float[] getMagnet(){
        return magnet;
    }
}