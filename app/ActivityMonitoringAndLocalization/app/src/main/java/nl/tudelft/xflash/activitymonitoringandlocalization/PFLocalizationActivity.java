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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityMonitoring;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityType;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.MotionModel.DistanceModelZee;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.FloorLayout;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.LocalizationMonitor;
//import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.RunUpdate;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.RunUpdateActivity;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.RunUpdateLocalization;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.CompassGUI;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.LocalizationMap;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.Accelerometer;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.LinearAccelero;
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

    // Windows size of accelerometer and orientation sensor
    public static final int SAMPLING_RATE_ACC = 20000; // 50 Hz (1/20000 us)
    public static final int SAMPLING_RATE_ORIENTATION = 20000; // 50 Hz (1/20000 us)
    private int curWindowSize;

    // Sample size of accelerometer
    private static final int ACC_SAMPLE = 10;
    private int numSample = 0;

    // Particle converged flag
    private boolean isParticleConverged;

    // Total Step
    private int totalStep;
    private int stepSamples;
    private int stepCount;

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
    private TextView txtLocation;

    // Update View
    public Handler mHandler;
    DecimalFormat d = new DecimalFormat("#.##");
    DecimalFormat di = new DecimalFormat("#");

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
        activityMonitoring = new ActivityMonitoring(getApplicationContext());
        localizationMonitor = new LocalizationMonitor(getApplicationContext(), floorLayout, N_PARTICLES);

        // Get activity type
        activityType = ActivityType.getInstance();

        // Init view
        initView();

        // Init buttons
        initButtons();

        // Init Sensors
        initSensors();

        // Update View in GUI
        setUpdateInfoSchedule();
        //mHandler.post(updateInfoViewTask);

        setUpdateLocalizationMonitoring();
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

        if(SensorType == Sensor.TYPE_ACCELEROMETER) {

            // Collect accelero data as large as WINDOW SIZE
            this.accelX.add(Accelerometer.getGravity()[0]);
            this.accelY.add(Accelerometer.getGravity()[1]);
            this.accelZ.add(Accelerometer.getGravity()[2]);
            this.numSample = this.numSample + 1;

            if(activityMonitoring.getActivity() == Type.WALKING) {
                this.stepSamples = this.stepSamples + 1;
                if (this.stepSamples >= activityMonitoring.getTOpt() / 2) {
                    this.stepCount = this.stepCount + 1;
                    this.stepSamples = 0;
                }
            }

            if (this.accelX.size() >= activityMonitoring.getWindowSize() && this.numSample >= ACC_SAMPLE) {
                for (int j=0; j<ACC_SAMPLE; j++) {
                    this.accelX.remove(0);
                    this.accelY.remove(0);
                    this.accelZ.remove(0);
                }
                this.numSample = 0;

                // Create runnable activity monitoring
                RunUpdateActivity runUpdateActivity = new RunUpdateActivity(accelX, accelY, accelZ, activityMonitoring);

                // Add runnable to queue
                executor.submit(runUpdateActivity);
            }
        } else if(SensorType == Sensor.TYPE_ROTATION_VECTOR) {
            float prevAngle = angle;
            angle = RotationSensor.getAngleRad();
            // Prevent spike
            if(Math.abs(angle - prevAngle) > Math.toRadians(300)){
                angle = prevAngle;
            }
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
        localizationView = new LocalizationMap(this, floorLayout.getPath(),
                floorLayout.getCellNames(),floorLayout.getCellRectList(),
                localizationMonitor.getParticles(), screenSize.x, screenSize.y);
        localizationView.clearParticles();

        // Initialize compass view
        compassGUI = new CompassGUI(this,100,100);

        // Add localization view to Android GUI
        localizationLayout = (LinearLayout)findViewById(R.id.imgFloorPlan);
        localizationLayout.addView(localizationView);

        // Add compass to Android GUI
        compassLayout = (LinearLayout)findViewById(R.id.imgCompass);
        compassLayout.addView(compassGUI);

        // Text View
        //mHandler = new Handler();
        txtAngle = (TextView)findViewById(R.id.txtOrienAngle);

        txtActivityPF = (TextView)findViewById(R.id.txtActivityPF);

        txtdX = (TextView)findViewById(R.id.txtdX);
        txtdY = (TextView)findViewById(R.id.txtdY);
        txtTotalStep = (TextView)findViewById(R.id.txtTotalStep);
        txtLocation = (TextView)findViewById(R.id.txtCellLocation);
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

                    setUpdateLocalizationMonitoring();

                    localizationMonitor.reset();
                    btnInitialBeliefPA.setText("STOP INITIAL BELIEF");

                    localizationView.setParticles(localizationMonitor.getParticles());
                    localizationView.reset();
                    localizationView.invalidate();
                    compassGUI.invalidate();
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
        txtAngle.setText(di.format(Math.toDegrees(angle)) + '\u00B0');
        txtTotalStep.setText(di.format(totalStep));
        txtdX.setText(d.format(localizationMonitor.getMovement()[0]) + " m");
        txtdY.setText(d.format(localizationMonitor.getMovement()[1]) + " m");
        txtActivityPF.setText(activityMonitoring.getActivity().toString());
        txtLocation.setText(localizationMonitor.getCellLocation());

        if(isParticleConverged) {
            initInitialBeliefPA = false;
            btnInitialBeliefPA.setText("INITIAL BELIEF PA");
            btnInitialBeliefPA.setEnabled(false);
            btnInitialBeliefPA.setClickable(false);
        }

        //this.compassGUI.invalidate();
    }

    public void setUpdateInfoSchedule(){
        Timer schedulerTimer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateInfoView();
                    }
                });
            }
        };
        schedulerTimer.scheduleAtFixedRate(task,0,250);
    }


    public void setUpdateLocalizationMonitoring() {
        Timer schedulerTimer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //ArrayList<Integer> stepCountList = activityMonitoring.getStepCountList();
                        //int totalStepCurrent = 0;

//                        for(int step : stepCountList){
//                            totalStepCurrent += step;
//                            totalStep += step;
//                        }

                        activityMonitoring.clearStepCountList();

                        // Create runnable localization
                        RunUpdateLocalization runUpdateLocalization = new RunUpdateLocalization(
                                angle, localizationMonitor, localizationView, compassGUI, stepCount);

                        totalStep += stepCount;
                        stepCount = 0;

                        // Add runnable to queue
                        executor.submit(runUpdateLocalization);

                        // Update particle converged flag
                        isParticleConverged = localizationMonitor.isParticleHasConverged();

                        // Scan Wifi while user is walking
                        if (activityType.getLast() == Type.WALKING) {
                            //wifiManager.startScan();
                        }
                    }
                });
            }
        };
        schedulerTimer.scheduleAtFixedRate(task,0,250);
    }

}
