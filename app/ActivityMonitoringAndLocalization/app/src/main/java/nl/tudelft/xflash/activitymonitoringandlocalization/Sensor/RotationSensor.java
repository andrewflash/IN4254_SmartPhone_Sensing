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
    private static float angle = 0;

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
            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
            //SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X,
            //        SensorManager.AXIS_Z, rotationMatrix);
            SensorManager.getOrientation(rotationMatrix, orientation);

            angle = ((float) (Math.toDegrees(orientation[0]))+360f) % 360f; //important
            //angle = (float)Math.toDegrees(orientation[0]);

            this.notifyObserver(Sensor.TYPE_ROTATION_VECTOR);
        }
    }

    public static float[] getOrientationDeg(){
        float[] orientationDegree = {((float)Math.toDegrees(orientation[0]) + 360f) % 360f,
                (float)Math.toDegrees(orientation[1]),
                (float)Math.toDegrees(orientation[2])};
        return orientationDegree;
    }

    public static float[] getOrientationRad(){
        float[] orientationRad = {(float)(orientation[0] + Math.PI*2) % (float)(Math.PI*2),
                orientation[1],orientation[2]};
        return orientationRad;
    }

    public static float getAngleDeg() { return angle; }

    public static float getAngleRad() { return (float)Math.toRadians(angle); }
}