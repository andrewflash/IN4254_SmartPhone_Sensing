package nl.tudelft.xflash.activitymonitoringandlocalization;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityMonitoring;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.LocalizationMonitor;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.OrientationFusion;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.RunUpdate;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.CompassGUI;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.LocalizationMap;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.WiFi;

/**
 * Created by xflash on 27-5-16.
 */
public class PFLocalizationActivity extends AppCompatActivity implements Observer {

    private OrientationFusion orientation;
    private SensorManager sensorManager;
    private float[] orientationResults;

    private LocalizationMap localizationView;
    private CompassGUI compassGUI;
    private FloorLayout floorLayout;
    private LinearLayout localizationLayout;
    private LinearLayout compassLayout;

    // Particles
    private static final int N_PARTICLES = 1000;

    // Sensors
    private WifiManager wifiManager;
    private WiFi wifi;
    private float[] accel;

    // Accelerometer and Orientation data
    private ArrayList<Float> accelX;
    private ArrayList<Float> accelY;
    private ArrayList<Float> accelZ;
    private ArrayList<Float> orienX;
    private ArrayList<Float> orienY;
    private ArrayList<Float> orienZ;

    // Windows size of accelerometer and orientation sensor
    public int WINDOW_SIZE_ACC;
    public int WINDOW_SIZE_ORIENTATION;

    // Timing for calculating window
    private long startTime;

    // Thread Queue
    private ExecutorService executor;

    // Monitoring
    private ActivityMonitoring activityMonitoring;
    private LocalizationMonitor localizationMonitor;

    // Buttons
    private Button btnInitialBeliefPA,btnInitialBeliefBayes,btnSenseBayes;

    // Button Flags
    private boolean initInitialBeliefPA = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Window Size
        WINDOW_SIZE_ACC = 20;
        WINDOW_SIZE_ORIENTATION = 20;

        executor = Executors.newSingleThreadExecutor();

        // Create floor layout
        floorLayout = new FloorLayout(getResources().openRawResource(R.raw.floor9th));
        floorLayout.generateLayout();

        // Monitoring
        activityMonitoring = new ActivityMonitoring(getApplicationContext());
        localizationMonitor = new LocalizationMonitor(getApplicationContext(), floorLayout, N_PARTICLES);

        // Init sensors
        initSensors();

        // Init view
        initView();

        // Init buttons
        initButtons();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // restore the sensor listeners when user resumes the application.
        orientation.initListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister sensor listeners to prevent the activity from draining the device's battery.
        //orientation.unregisterListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifi.clear();
        orientation.unregisterListeners();
        try {
            unregisterReceiver(wifi);
        }
        catch (Exception e){
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // unregister sensor listeners to prevent the activity from draining the device's battery.
        //orientation.unregisterListeners();
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable == orientation) {
            if(this.accelX.size() == 0 && this.orienX.size() == 0){
                startTime = System.currentTimeMillis();
            }

            orientationResults = (float[]) orientation.getOrientationResults();

            if(accelX.size() <= WINDOW_SIZE_ACC) {
                this.accelX.add(orientation.getAccel()[0]);
                this.accelY.add(orientation.getAccel()[1]);
                this.accelZ.add(orientation.getAccel()[2]);
            }

            if(orienX.size() <= WINDOW_SIZE_ORIENTATION) {
                this.orienX.add(orientation.getOrientationResults()[0]);
                this.orienY.add(orientation.getOrientationResults()[1]);
                this.orienZ.add(orientation.getOrientationResults()[2]);
            }

            if(this.accelX.size() >= WINDOW_SIZE_ACC && this.orienX.size() >= WINDOW_SIZE_ORIENTATION) {
                float dT = (float)(Double.valueOf(System.currentTimeMillis() - startTime)/1000d);

                // Create runnable
                RunUpdate runUpdate = new RunUpdate(accelX,accelY,accelZ,orienX,orienY,orienZ,
                        activityMonitoring,localizationMonitor,localizationView, compassGUI, dT);

                // Add runnable to queue
                executor.submit(runUpdate);

                // Clear sensor data
                accelX.clear();
                accelY.clear();
                accelZ.clear();
                orienX.clear();
                orienY.clear();
                orienZ.clear();
            }

        } else if(observable == wifi.getObservable()) {
            localizationMonitor.initialBelief(wifi.getRSSI());
            localizationView.setParticles(localizationMonitor.getParticles());
            localizationView.reset();
            localizationView.post(new Runnable() {
                @Override
                public void run() {
                    localizationView.invalidate();
                }
            });
            compassGUI.post(new Runnable() {
                @Override
                public void run() {
                    compassGUI.invalidate();
                }
            });
            btnInitialBeliefPA.setText("INITIAL BELIEF");
        }
    }

    public void initView() {
        // Get layout and set the layout to horizontal
        setContentView(R.layout.localization_advance_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Get screen size
        Point screenSize = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screenSize);

        // Initialize localization view in landscape mode
        localizationView = new LocalizationMap(this, floorLayout.getPath(), localizationMonitor.getParticles(), screenSize.x, screenSize.y);

        // Initialize compass view
        compassGUI = new CompassGUI(this,100,100);

        // Add localization view to Android GUI
        localizationLayout = (LinearLayout)findViewById(R.id.imgFloorPlan);
        localizationLayout.addView(localizationView);

        // Add compass to Android GUI
        compassLayout = (LinearLayout)findViewById(R.id.imgCompass);
        compassLayout.addView(compassGUI);
    }

    private void initSensors(){
        // Init sensors data
        accelX = new ArrayList<>();
        accelY = new ArrayList<>();
        accelZ = new ArrayList<>();

        orienX= new ArrayList<>();
        orienY = new ArrayList<>();
        orienZ = new ArrayList<>();

        // Manage sensors
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        orientation = new OrientationFusion(sensorManager);
        orientation.addObserver(this);
        orientation.initListeners();

        //Init wifi
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        wifi = new WiFi(wifiManager);
        registerReceiver(wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.getObservable().addObserver(this);
    }

    private void initButtons() {
        btnInitialBeliefPA = (Button) findViewById(R.id.btnInitialBeliefPA);
        btnInitialBeliefPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!initInitialBeliefPA) {
                    if (wifi.getWifiPoints().isEmpty()) { //|| WalkedPath.getInstance().getPathX().isEmpty()){
                        localizationMonitor.reset();
                    } else {
                        registerReceiver(wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                        wifi.getObservable().mySetChanged();
                        wifiManager.startScan();
                        btnInitialBeliefPA.setText("Scanning...");
                        initInitialBeliefPA = true;
                    }
                    localizationView.setParticles(localizationMonitor.getParticles());
                    localizationView.reset();
                    localizationView.post(new Runnable() {
                        @Override
                        public void run() {
                            localizationView.invalidate();
                        }
                    });
                    compassGUI.post(new Runnable() {
                        @Override
                        public void run() {
                            compassGUI.invalidate();
                        }
                    });
                } else {
                    orientation.unregisterListeners();
                    try {
                        unregisterReceiver(wifi);
                    }
                    catch (Exception e){}
                    initInitialBeliefPA = false;
                    btnInitialBeliefPA.setText("INITIAL BELIEF");
                }
            }
        });

        btnInitialBeliefBayes = (Button) findViewById(R.id.btnInitialBeliefBayes);
        btnInitialBeliefBayes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                accelerometer.unregister();
//                magnetometer.unregister();
//                try {
//                    unregisterReceiver(wifiReceiver);
//                }
//                catch (Exception e){}
            }
        });

        btnSenseBayes = (Button) findViewById(R.id.btnSenseBayes);
        btnSenseBayes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localizationView.invalidate();
                compassGUI.invalidate();
                compassGUI.setAngle(orientationResults[0]);
//                accelerometer.register();
//                magnetometer.register();
//                registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            }
        });

    }
}