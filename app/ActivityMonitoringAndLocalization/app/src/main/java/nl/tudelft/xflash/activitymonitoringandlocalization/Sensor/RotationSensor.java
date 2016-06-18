package nl.tudelft.xflash.activitymonitoringandlocalization.Sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by xflash on 16-6-16.
 */
public class RotationSensor extends AbstractSensor {
    private static float[] orientation = {0f,0f,0f};
    private static float[] sensorValues = {0f,0f,0f,0f,0f};
    private static float angle = 0;
    private static final float alpha = 0.1f;       // Low-pass filter

    private float[] rotationMatrix;

    public RotationSensor(SensorManager sm) {
        super(sm);
        rotationMatrix = new float[16];

        type = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if(type != null){
            Log.d(this.getClass().getSimpleName(), "Sensor is initialized.");
            sensorAvailable = true;
        }
        else{
            Log.d(this.getClass().getSimpleName(), "Sensor is not available.");
            sensorAvailable = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Calculate orientation
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            for(int i=0; i < sensorValues.length; i++){
                sensorValues[i] = (1.0f - alpha)*sensorValues[i] + alpha*sensorEvent.values[i];
            }
            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorValues);
            //SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X,
            //        SensorManager.AXIS_Z, rotationMatrix);
            SensorManager.getOrientation(rotationMatrix, orientation);

            angle = ((float) (Math.toDegrees(orientation[0]))+360f) % 360f;

            this.notifyObserver(Sensor.TYPE_ROTATION_VECTOR);
        }
    }

    public static float getAngleDeg() { return angle; }

    public static float getAngleRad() { return (float)Math.toRadians(angle); }
}