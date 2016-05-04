package nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import nl.tudelft.xflash.activitymonitoringandlocalization.Database.ActivityList;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.DatabaseAPI;
import nl.tudelft.xflash.activitymonitoringandlocalization.R;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.Accelerometer;

public class ActivityMonitoring extends AppCompatActivity {

    private SensorManager sensorManager;
    private Accelerometer accelerometer;

    private Button btnStartStanding, btnStartWalking, btnTestActivity;

    private boolean flagWalking;
    private boolean flagStanding;

    private DatabaseAPI dbAPI;
    private ActivityList acType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_menu);

        btnStartStanding = (Button) findViewById(R.id.btnStanding);
        btnStartWalking = (Button) findViewById(R.id.btnWalking);
        btnTestActivity = (Button) findViewById(R.id.btnTestActivity);

        flagStanding = false;
        flagWalking = false;

        //open db-API
        dbAPI = new DatabaseAPI(getApplicationContext());

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
    }

    private void initAccelerometerAndButtons(){
        sensorManager =(SensorManager)getSystemService(SENSOR_SERVICE);

        // Initialise the Activity Monitoring
        am = new ActivityMonitoring(this);

        executor = Executors.newSingleThreadExecutor();

        // Start accelerometer and attacht this Activity as Observer
        accelerometer = new Accelerometer(sensorManager);
        accelerometer.attach(this);

        // walking
        btnStartWalking.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!flagWalking) {
                    acType = ActivityList.WALKING;
                    btnStartWalking.setText(R.string.btn_walking_stop);
                    accelerometer.onActivityTypeChange(acType);
                    accelerometer.startCollectingActivityData();
                    flagWalking = true;
                } else {
                    btnStartWalking.setText(R.string.btn_walking);
                    accelerometer.stopCollectingActivityData();
                    dbAPI.exportTableAccel();
                    flagWalking = false;
                }
            }
        });

        // standing
        btnStartStanding.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                btnStartStanding.setEnabled(false);
                btnStartStanding.setClickable(false);
            }
        });

        btnTestActivity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });

        accelX = new ArrayList<>(WINDOW_SIZE_ACC);
        accelY = new ArrayList<>(WINDOW_SIZE_ACC);
        accelZ = new ArrayList<>(WINDOW_SIZE_ACC);
    }
}