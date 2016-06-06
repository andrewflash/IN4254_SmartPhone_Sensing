package nl.tudelft.xflash.activitymonitoringandlocalization.Sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by xflash on 27-5-16.
 */
public class Gyroscope extends AbstractSensor {

    // gyro raw data
    private static float[] gyroOutput = new float[3];
    private static long timestamp;

    public Gyroscope(SensorManager sm) {
        super(sm);
        type = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if(type != null){
            sensorAvailable = true;
            Log.d("Gyroscope", "Sensor is initialized.");
        }
        else{
            Log.d("Gyroscope", "Sensor is not available.");
            sensorAvailable = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do something here if sensor accuracy changes
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // copy new magnetometer data into magnet array
        System.arraycopy(sensorEvent.values, 0, gyroOutput, 0, 3);
        timestamp = sensorEvent.timestamp;

        this.notifyObserver(Sensor.TYPE_GYROSCOPE);
    }

    public static float[] getGyro(){ return gyroOutput; }

    public static long getTimestamp() { return timestamp; }
}