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
            System.arraycopy(event.values, 0, linear_acceleration, 0, 3);

            this.notifyObserver(Sensor.TYPE_LINEAR_ACCELERATION);
        }
    }

    public static float[] getLinearAcceleration() { return linear_acceleration; }
}
