package nl.tudelft.xflash.activitymonitoringandlocalization;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityMonitoring;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ObserverSensor;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.Writer;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.Accelerometer;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.AbstractSensor;

public class ActivityMonActivity extends AppCompatActivity implements ObserverSensor {

    private SensorManager sensorManager;
    private Accelerometer accelerometer;

    // Flag
    private boolean initAccel = false;

    // View
    private Button btnStartStanding, btnStartWalking, btnTestActivity, btnClearData;
    private TextView aX, aY, aZ;
    private TextView t;

    //private DatabaseAPI dbAPI;
    //private ActivityList acType;

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
                    accelerometer.register();
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
                state = Type.STANDING;
                if (initAccel) {
                    btnStartStanding.setText(R.string.btn_standing_stop);
                    accelerometer.register();
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
                CharSequence msg;
                Context context = getApplicationContext();
                if (writeAccel.clearData()) {
                    msg = "Data has been cleared";

                } else {
                    msg = "Data could not be cleared";
                }
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
            writeAccel.appendData(aData[0], aData[1], aData[2], state);
            displayAccel(aData[0], aData[1], aData[2]);
        }

        t.setText(state.toString());
    }

}