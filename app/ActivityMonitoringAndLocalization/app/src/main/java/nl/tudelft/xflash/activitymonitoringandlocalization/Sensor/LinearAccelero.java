package nl.tudelft.xflash.activitymonitoringandlocalization.Sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by xflash on 4-5-16.
 */
public class LinearAccelero extends AbstractSensor {

    private static float[] linear_acceleration = {0f,0f,0f};
    private static final float alpha = 0.1f;      // Low-pass filter

    public LinearAccelero(SensorManager sm){
        super(sm);
        type = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if(type != null){
            Log.d("Linear_Accelerometer", "Sensor is initialized.");
            sensorAvailable = true;
        }
        else{
            Log.d("Linear_Accelerometer", "Sensor is not available.");
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
        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            linear_acceleration[0] = (1.0f - alpha)*linear_acceleration[0] + alpha*event.values[0];
            linear_acceleration[1] = (1.0f - alpha)*linear_acceleration[1] + alpha*event.values[1];
            linear_acceleration[2] = (1.0f - alpha)*linear_acceleration[2] + alpha*event.values[2];

            this.notifyObserver(Sensor.TYPE_LINEAR_ACCELERATION);
        }
    }

    public static float[] getLinearAcceleration() { return linear_acceleration; }
}