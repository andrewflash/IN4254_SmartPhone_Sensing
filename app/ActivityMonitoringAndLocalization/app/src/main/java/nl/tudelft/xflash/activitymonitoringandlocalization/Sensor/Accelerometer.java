package nl.tudelft.xflash.activitymonitoringandlocalization.Sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by xflash on 4-5-16.
 */
public class Accelerometer extends AbstractSensor {

    private static float[] gravity = {0f,0f,0f};
    private static final float alpha = 0.8f; // alpha filter, low-pass filter

    public Accelerometer(SensorManager sm){
        super(sm);
        type = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(type != null){
            Log.d("Accelerometer", "Sensor is initialized.");
            sensorAvailable = true;
        }
        else{
            Log.d("Accelerometer", "Sensor is not available.");
            sensorAvailable = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor,int accuracy){
        // // Do something here if sensor accuracy changes
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        // Check if changed sensor is the Accelerometer.
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            gravity[0] = (1.0f - alpha)*gravity[0] + alpha*event.values[0];
            gravity[1] = (1.0f - alpha)*gravity[1] + alpha*event.values[1];
            gravity[2] = (1.0f - alpha)*gravity[2] + alpha*event.values[2];

            this.notifyObserver(Sensor.TYPE_ACCELEROMETER);
        }
    }

    public static float[] getGravity(){
        return gravity;
    }


}
