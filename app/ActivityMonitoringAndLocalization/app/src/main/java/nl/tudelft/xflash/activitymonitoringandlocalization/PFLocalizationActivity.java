package nl.tudelft.xflash.activitymonitoringandlocalization;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityMonitoring;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityType;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.MotionModel.DistanceModelZee;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.FloorLayout;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.LocalizationMonitor;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.RunUpdate;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.RunUpdateActivity;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.RunUpdateLocalization;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.CompassGUI;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.LocalizationMap;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.Accelerometer;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.ObserverSensor;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.RotationSensor;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.WiFi;

/**
 * Created by xflash on 27-5-16.
 */
public class PFLocalizationActivity extends AppCompatActivity implements Observer, ObserverSensor {

    // Layout
    private LocalizationMap localizationView;
    private CompassGUI compassGUI;
    private FloorLayout floorLayout;
    private LinearLayout localizationLayout;
    private LinearLayout compassLayout;

    // Particles
    private static final int N_PARTICLES = 1000;

    // Sensors
    private SensorManager sensorManager;
    private RotationSensor orientation;
    private Accelerometer accelerometer;
    private WifiManager wifiManager;
    private WiFi wifi;

    // Accelerometer and Orientation data
    private ArrayList<Float> accelX;
    private ArrayList<Float> accelY;
    private ArrayList<Float> accelZ;
    private float angle;

    // Sampling rate of accelerometer and orientation sensor
    public static final int SAMPLING_RATE_ACC = 20000; // 50 Hz (1/20000 us)
    public static final int SAMPLING_RATE_ORIENTATION = 20000; // 50 Hz (1/20000 us)

    // Sample size of accelerometer
    private int ACC_SAMPLE = 10;
    private int numSample = 0;

    // Particle converged flag
    private boolean isParticleConverged;

    // Total Step
    private int totalStep;

    // Timing for calculating window
    private long startTime;

    // Thread Queue
    private ExecutorService executor;

    // Monitoring
    private ActivityMonitoring activityMonitoring;
    private LocalizationMonitor localizationMonitor;

    // Activity
    private ActivityType activityType;

    // Buttons
    private Button btnInitialBeliefPA,btnInitialBeliefBayes,btnSenseBayes;

    // Button Flags
    private boolean initInitialBeliefPA = false;

    // Text
    private TextView txtAngle;
    private TextView txtActivityPF;
    private TextView txtdX;
    private TextView txtdY;
    private TextView txtTotalStep;

    // Update View
    public Handler mHandler;
    DecimalFormat d = new DecimalFormat("#.##");
    DecimalFormat di = new DecimalFormat("#");
    DistanceModelZee distanceModelZee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Executor
        executor = Executors.newSingleThreadExecutor();

        // Create floor layout
        floorLayout = new FloorLayout(getResources().openRawResource(R.raw.floor9th));
        floorLayout.generateLayout();

        // Initialize particle converged flag
        isParticleConverged = false;

        // Initialize total step
        totalStep = 0;

        // Monitoring (monitor activity and localization
        distanceModelZee = new DistanceModelZee(floorLayout);
        activityMonitoring = new ActivityMonitoring(getApplicationContext(), distanceModelZee);
        localizationMonitor = new LocalizationMonitor(getApplicationContext(), floorLayout, N_PARTICLES);

        // Get activity type
        activityType = ActivityType.getInstance();

        // Init view
        initView();

        // Init buttons
        initButtons();

        // Init Sensors
        initSensors();

        // Init Wifi
        //initWifi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // restore the sensor listeners when user resumes the application.
        //orientation.initListeners();
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
        try {
            //orientation.unregisterListeners();
            accelerometer.unregister();
            orientation.unregister();
            unregisterReceiver(wifi);
            wifi.clear();
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

    // ObserverSensor
    @Override
    public void update(int SensorType) {

        if(this.accelX.size() == 0){
            startTime = System.currentTimeMillis();
        }

        if(SensorType == Sensor.TYPE_ACCELEROMETER) {
            this.accelX.add(Accelerometer.getLinearAcceleration()[0]);
            this.accelY.add(Accelerometer.getLinearAcceleration()[1]);
            this.accelZ.add(Accelerometer.getLinearAcceleration()[2]);
            this.numSample = this.numSample + 1;
            if (this.accelX.size() >= activityMonitoring.getWindowSize() && this.numSample >= ACC_SAMPLE) {
                Log.d(this.getClass().getSimpleName(), "accelSize: "+accelX.size()+", " +
                        "AMWindowSize: " + activityMonitoring.getWindowSize());
                for (int j=0; j<ACC_SAMPLE; j++) {
                    this.accelX.remove(j);
                    this.accelY.remove(j);
                    this.accelZ.remove(j);
                }
                this.numSample = 0;
                // Create runnable
                RunUpdateActivity runUpdateActivity = new RunUpdateActivity(accelX, accelY, accelZ,
                        activityMonitoring);
                // Add runnable to queue
                executor.submit(runUpdateActivity);
            }
        } else if(SensorType == Sensor.TYPE_ROTATION_VECTOR) {
            angle = RotationSensor.getAngleRad();
        }

        // Update localization after collecting as much data as WINDOW SIZE
        if(this.accelX.size() >= activityMonitoring.getWindowSize()) {
            float dT = (float)(Double.valueOf(System.currentTimeMillis() - startTime)/1000d);

            // Create runnable
            RunUpdateLocalization runUpdateLocalization = new RunUpdateLocalization(angle,
                    activityMonitoring, localizationMonitor, localizationView, compassGUI, dT);

            // Update particle converged flag
            isParticleConverged = localizationMonitor.isParticleHasConverged();

            // Add runnable to queue
            executor.submit(runUpdateLocalization);

            // Scan Wifi while user is walking
            if (activityType.getLast() == Type.WALKING) {
                //wifiManager.startScan();
            }

            // Update View in GUI
            mHandler.post(updateInfoViewTask);
        }
}

    // Observer
    @Override
    public void update(Observable observable, Object o) {
        Log.d(this.getClass().getSimpleName(),"Receive WiFi update");
//        // If we receive update of wifi data
//        } else {//if(observable == wifi.getObservable()){
//            Log.d(this.getClass().getSimpleName(),"receiveWifi");
//            localizationMonitor.initialBelief(wifi.getRSSI());
//            localizationView.setParticles(localizationMonitor.getParticles());
//            localizationView.reset();
//            localizationView.post(new Runnable() {
//                @Override
//                public void run() {
//                    localizationView.invalidate();
//                }
//            });
//            compassGUI.post(new Runnable() {
//                @Override
//                public void run() {
//                    compassGUI.invalidate();
//                }
//            });
//            btnInitialBeliefPA.setText("INITIAL BELIEF PA");
//        }
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

        // Text View
        mHandler = new Handler();
        txtAngle = (TextView)findViewById(R.id.txtOrienAngle);

        txtActivityPF = (TextView)findViewById(R.id.txtActivityPF);

        txtdX = (TextView)findViewById(R.id.txtdX);
        txtdY = (TextView)findViewById(R.id.txtdY);
        txtTotalStep = (TextView)findViewById(R.id.txtTotalStep);
    }

    private void initSensors() {
        // Init sensors data
        accelX = new ArrayList<>();
        accelY = new ArrayList<>();
        accelZ = new ArrayList<>();

        // Init angle
        angle = 0f;

        // Manage sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Accelerometer
        accelerometer = new Accelerometer(sensorManager);
        accelerometer.attach(this);

        // Orientation
        orientation = new RotationSensor(sensorManager);
        orientation.attach(this);
    }

    private void initWifi() {
        //Init wifi
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        // Create WiFi observer
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
                    // Initial Belief start
                    accelerometer.register(SAMPLING_RATE_ACC);
                    orientation.register(SAMPLING_RATE_ORIENTATION);

                    localizationMonitor.reset();
                    btnInitialBeliefPA.setText("STOP INITIAL BELIEF");

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
                    initInitialBeliefPA = true;
                } else {
                    // Initial Belief stop
                    accelerometer.unregister();
                    orientation.unregister();
                    initInitialBeliefPA = false;
                    btnInitialBeliefPA.setText("INITIAL BELIEF PA");
                }
            }
        });

        // Initial Belief Bayes
        btnInitialBeliefBayes = (Button) findViewById(R.id.btnInitialBeliefBayes);
        btnInitialBeliefBayes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiManager.startScan();
                localizationMonitor.initialBelief(wifi.getRSSI());
                wifi.getObservable().mySetChanged();
            }
        });

        // Sense Bayes
        btnSenseBayes = (Button) findViewById(R.id.btnSenseBayes);
        btnSenseBayes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accelerometer.register(SAMPLING_RATE_ACC);
                orientation.register(SAMPLING_RATE_ORIENTATION);
                registerReceiver(wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            }
        });
    }

    public void updateInfoView() {
        txtAngle.setText(d.format(Math.toDegrees(angle)) + '\u00B0');
        txtTotalStep.setText(di.format(totalStep));
        txtdX.setText(d.format(localizationMonitor.getMovement()[0]) + " m");
        txtdY.setText(d.format(localizationMonitor.getMovement()[1]) + " m");
        txtActivityPF.setText(activityMonitoring.getActivity().toString());

        if(isParticleConverged) {
            initInitialBeliefPA = false;
            btnInitialBeliefPA.setText("INITIAL BELIEF PA");
        }
    }

    private Runnable updateInfoViewTask = new Runnable() {
        public void run() {
            updateInfoView();
        }
    };
}
