package nl.tudelft.xflash.activitymonitoringandlocalization;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executors;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityMonitoring;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.ObserverSensor;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.AcceleroDBHandler;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.AcceleroData;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.Writer;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.Accelerometer;

public class ActivityMonActivity extends AppCompatActivity implements ObserverSensor {

    private SensorManager sensorManager;
    private Accelerometer accelerometer;

    // Sampling rate
    private static final int accelSamplingPeriodUs = 20000; // 20ms sampling period (50 Hz)

    // Flag
    private boolean initAccel = false;
    private AcceleroDBHandler dbConnector;
    // View
    private Button btnStartStanding, btnStartWalking, btnTestActivity, btnClearData;
    private TextView aX, aY, aZ;
    private TextView t;

    private ActivityMonitoring am;
    private Writer writeAccel;
    private Executors executors;
    private Type state = Type.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_menu);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        // Text view
        aX = (TextView) findViewById(R.id.txtXaxis);
        aY = (TextView) findViewById(R.id.txtYaxis);
        aZ = (TextView) findViewById(R.id.txtZaxis);
        //setup Accelerometer and buttons
        initAccelerometerAndButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(initAccel)
            accelerometer.unregister();
    }

    private void initAccelerometerAndButtons(){
        initAccel = true;

        t = (TextView) this.findViewById(R.id.txtState);
        btnStartStanding = (Button) findViewById(R.id.btnStanding);
        btnStartWalking = (Button) findViewById(R.id.btnWalking);
        btnTestActivity = (Button) findViewById(R.id.btnTestActivity);

        Resources res = this.getResources();
        String acceleroFileLocation = res.getString(R.string.accelerometer_data_file);

        writeAccel = new Writer(acceleroFileLocation);

        accelerometer = new Accelerometer(sensorManager);
        accelerometer.attach(this);

        // Create walk button, when clicked on the button state will change state to WALK.
        btnStartWalking = (Button) findViewById(R.id.btnWalking);
        btnStartWalking.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                state = Type.WALKING;
                if(initAccel) {
                    btnStartWalking.setText(R.string.btn_walking_stop);
                    accelerometer.register(accelSamplingPeriodUs);
                    initAccel = false;
                } else {
                    accelerometer.unregister();
                    btnStartWalking.setText(R.string.btn_walking);
                    initAccel = true;
                    state = Type.NONE;
                    update(0);
                }
            }
        });

        // Create standing button, when clicked on the button state will change state to STANDING.
        btnStartStanding = (Button) findViewById(R.id.btnStanding);
        btnStartStanding.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                state = Type.IDLE;
                if (initAccel) {
                    btnStartStanding.setText(R.string.btn_standing_stop);
                    accelerometer.register(accelSamplingPeriodUs);
                    initAccel = false;
                } else {
                    accelerometer.unregister();
                    btnStartStanding.setText(R.string.btn_standing);
                    initAccel = true;
                    state = Type.NONE;
                    update(0);
                }
            }
        });

        // Clear data
        btnClearData = (Button) findViewById(R.id.btnClearData);
        btnClearData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                dbConnector = new AcceleroDBHandler(ActivityMonActivity.this);
                Log.d("SPS", "#data:" + dbConnector.getAcceleroDataCount());
                CharSequence msg;
                ClearAcceleroData clearAcceleroData = new ClearAcceleroData();
                clearAcceleroData.execute();
                if (writeAccel.clearData()) {
                    msg = "Data has been cleared from DB and file";
                } else {
                    msg = "Data could not be cleared";
                }
                Context context = getApplicationContext();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAccel(float aX, float aY, float aZ) {
        this.aX.setText(Float.toString(aX));
        this.aY.setText(Float.toString(aY));
        this.aZ.setText(Float.toString(aZ));
    }

    public void update(int SensorType){
        if (Sensor.TYPE_ACCELEROMETER == SensorType) {
            float[] aData =  Accelerometer.getGravity();
            AcceleroData acceleroData = new AcceleroData();
            acceleroData.setAccX(aData[0]);
            acceleroData.setAccY(aData[1]);
            acceleroData.setAccZ(aData[2]);
            AddAcceleroData addAcceleroData = new AddAcceleroData(acceleroData);
            addAcceleroData.execute();
            writeAccel.appendData(aData[0], aData[1], aData[2], state);
            displayAccel(aData[0], aData[1], aData[2]);
        }

        t.setText(state.toString());
    }

    private class ClearAcceleroData extends AsyncTask<Object, Object, Object> {
        AcceleroDBHandler dbConnector = new AcceleroDBHandler(ActivityMonActivity.this);

        public ClearAcceleroData() { return; }

        @Override
        protected Object doInBackground(Object... params) {
            // Open the database
            dbConnector.clearAcceleroData();
            return null;
        }
    }

    private class AddAcceleroData extends AsyncTask<Object, Object, Object> {
        AcceleroDBHandler dbConnector = new AcceleroDBHandler(ActivityMonActivity.this);
        AcceleroData acceleroData_this;

        public AddAcceleroData(AcceleroData acceleroData) {
            acceleroData_this = acceleroData;
        }

        @Override
        protected Object doInBackground(Object... params) {
            // Open the database
            dbConnector.addAcceleroData(acceleroData_this);
            return null;
        }
    }

}
