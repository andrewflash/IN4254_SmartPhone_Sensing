package nl.tudelft.xflash.activitymonitoringandlocalization;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ActivityMonitoring extends AppCompatActivity implements SensorEventListener {

    private int WINDOW_SIZE;        // set window size for accelerometer
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float aX = 0;
    private float aY = 0;
    private float aZ = 0;

    private TextView currentX, currentY, currentZ;

    private long timeStart;
    private long numSamples = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_menu);

        currentX = (TextView) findViewById(R.id.txtXaxis);
        currentY = (TextView) findViewById(R.id.txtYaxis);
        currentZ = (TextView) findViewById(R.id.txtZaxis);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // No accelerometer!
        }
    }

    // onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    // onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // get the the x,y,z values of the accelerometer
        aX = sensorEvent.values[0];
        aY = sensorEvent.values[1];
        aZ = sensorEvent.values[2];

        // display the current x,y,z accelerometer values
        currentX.setText(Float.toString(aX));
        currentY.setText(Float.toString(aY));
        currentZ.setText(Float.toString(aZ));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}