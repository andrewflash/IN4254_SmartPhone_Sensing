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
    private static float[] linear_acceleration = {0f,0f,0f};
    private static final float alpha = 0.8f; // alpha filter

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
            float[] gravityComp = {0f,0f,0f};
            gravityComp[0] = alpha * gravityComp[0] + (1 - alpha) * event.values[0];
            gravityComp[1] = alpha * gravityComp[1] + (1 - alpha) * event.values[1];
            gravityComp[2] = alpha * gravityComp[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravityComp[0];
            linear_acceleration[1] = event.values[1] - gravityComp[1];
            linear_acceleration[2] = event.values[2] - gravityComp[2];

            // Gravity only
            System.arraycopy(event.values, 0, gravity, 0, 3);

            this.notifyObserver(Sensor.TYPE_ACCELEROMETER);
        }
    }

    public static float[] getGravity(){
        return gravity;
    }

    public static float[] getLinearAcceleration() { return linear_acceleration; }
}
