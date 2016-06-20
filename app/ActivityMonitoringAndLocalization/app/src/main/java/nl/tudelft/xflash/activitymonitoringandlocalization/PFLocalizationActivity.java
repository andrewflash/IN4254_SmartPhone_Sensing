package nl.tudelft.xflash.activitymonitoringandlocalization;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityMonitoring;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityType;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.WifiDBHandler;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.WifiData;
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
    private LinearAccelero accelerometer;
    private WifiManager wifiManager;
    private WiFi wifi;
    private boolean isRegisteredWifi = false;

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

    // Stride Length
    private float strideLength;

    // Timer Scheduler
    private Timer schedulerTimerWifi;
    private Timer schedulerTimerLocalization;
    private Timer schedulerTimerUpdateInfo;
    private boolean isSetSchedulerWifi = false;

    // Thread Queue
    private ExecutorService executor;

    // Monitoring
    private ActivityMonitoring activityMonitoring;
    private LocalizationMonitor localizationMonitor;

    // Activity
    private ActivityType activityType;

    // Wifi Database
    private WifiDBHandler wifiDb;

    // Buttons
    private Button btnInitialBeliefPA,btnInitialBeliefBayes,btnSenseBayes,btnClearData;

    // Button Flags
    private boolean initInitialBeliefPA = false;
    private boolean initInitialBeliefBayes = false;
    private boolean isSensing = false;
    private boolean isLoading = false;
    private boolean isBtnAfterConverged = false;

    // Text
    private TextView txtAngle;
    private TextView txtActivityPF;
    private TextView txtdX;
    private TextView txtdY;
    private TextView txtTotalStep;
    private TextView txtLocation;

    // Update View
    private ProgressDialog loading;
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

        // Wifi Data
        wifiDb = new WifiDBHandler(this.getApplicationContext());

        // Timer scheduler
        schedulerTimerLocalization = new Timer();
        schedulerTimerUpdateInfo = new Timer();
        schedulerTimerWifi = new Timer();

        // Monitoring (monitor activity and localization
        activityMonitoring = new ActivityMonitoring(getApplicationContext());
        localizationMonitor = new LocalizationMonitor(getApplicationContext(), floorLayout, N_PARTICLES);

        // Get activity type
        activityType = ActivityType.getInstance();
        curWindowSize = activityMonitoring.getWindowSize();

        // Loading
        loading = new ProgressDialog(this);

        // Init view
        initView();

        // Init buttons
        initButtons();

        // Init Sensors
        initSensors();

        // Update View in GUI
        setUpdateInfoSchedule();

        // Update localization monitoring
        setUpdateLocalizationMonitoring();

        // Init Wifi
        initWifi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // restore the sensor listeners when user resumes the application.
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister sensor listeners to prevent the activity from draining the device's battery.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            accelerometer.unregister();
            orientation.unregister();
            if(isRegisteredWifi)
                unregisterReceiver(wifi);

            schedulerTimerUpdateInfo.cancel();
            schedulerTimerWifi.cancel();
            schedulerTimerLocalization.cancel();

            schedulerTimerUpdateInfo.purge();
            schedulerTimerWifi.purge();
            schedulerTimerLocalization.purge();
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

        if(SensorType == Sensor.TYPE_LINEAR_ACCELERATION) {
            // Collect accelero data as large as WINDOW SIZE
            this.accelX.add(LinearAccelero.getLinearAcceleration()[0]);
            this.accelY.add(LinearAccelero.getLinearAcceleration()[1]);
            this.accelZ.add(LinearAccelero.getLinearAcceleration()[2]);
            this.numSample = this.numSample + 1;

            if(activityMonitoring.getActivity() == Type.WALKING) {
                this.stepSamples = this.stepSamples + 1;
                if (this.stepSamples >= activityMonitoring.getTOpt() / 2) {
                    this.stepCount = this.stepCount + 1;
                    this.stepSamples = 0;
                }
            }

            if(isLoading){
                if (this.numSample >= curWindowSize) {
                    loading.dismiss();
                    isLoading = false;
                }
            } else {
                if(this.accelX.size() >= curWindowSize && this.numSample >= ACC_SAMPLE) {
                    // Create runnable activity monitoring
                    RunUpdateActivity runUpdateActivity = new RunUpdateActivity(accelX, accelY, accelZ, activityMonitoring);

                    // Add runnable to queue
                    executor.submit(runUpdateActivity);

                    for (int j = 0; j < ACC_SAMPLE; j++) {
                        this.accelX.remove(0);
                        this.accelY.remove(0);
                        this.accelZ.remove(0);
                    }
                    this.numSample = 0;
                    curWindowSize = activityMonitoring.getWindowSize();
                }
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
        if(observable == wifi.getObservable()){
            // If it is not sensing at the moment
            if(!isSensing) {
                if (isParticleConverged) {
                    List<ScanResult> wifiResults = (List<ScanResult>) o;
                    JSONArray jsonWifiList = new JSONArray();

                    try {
                        for (ScanResult wifiRes : wifiResults) {
                            JSONObject jsonWifiData = new JSONObject();
                            jsonWifiData.put("bssid", (String) wifiRes.BSSID);
                            jsonWifiData.put("level", (int) wifiRes.level);
                            jsonWifiList.put(jsonWifiData);
                        }
                    } catch (JSONException e) {
                        Log.e(this.getClass().getSimpleName(), "JSON Wifi error: " + e.getMessage());
                    }

                    Log.d(this.getClass().getSimpleName(), "JSON data: " + jsonWifiList.toString());

                    WifiData wifiData = new WifiData(
                            localizationMonitor.getParticles().get(0).getCurrentLocation().getX(),
                            localizationMonitor.getParticles().get(0).getCurrentLocation().getY(),
                            localizationMonitor.getCellLocation(), jsonWifiList.toString());

                    AddWifiData addWifiData = new AddWifiData(wifiData);
                    addWifiData.execute();

                    Log.d(this.getClass().getSimpleName(), "Receive WiFi update");
                }
            } else {
                loading.dismiss();
                isSensing = false;
                isParticleConverged = true;
                Log.d(this.getClass().getSimpleName(), "Sensing Bayes");
            }
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
        accelerometer = new LinearAccelero(sensorManager);
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
        if(!isRegisteredWifi) {
            registerReceiver(wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifi.getObservable().addObserver(this);
            isRegisteredWifi = true;
        }
    }

    private void initButtons() {

        btnInitialBeliefPA = (Button) findViewById(R.id.btnInitialBeliefPA);
        btnInitialBeliefBayes = (Button) findViewById(R.id.btnInitialBeliefBayes);
        btnSenseBayes = (Button) findViewById(R.id.btnSenseBayes);
        btnClearData = (Button) findViewById(R.id.btnClearData);

        // Check buttons cond
        if (wifiDb.isWifiTableEmpty()) {
            btnInitialBeliefPA.setEnabled(true);
            btnSenseBayes.setEnabled(false);
            btnInitialBeliefBayes.setEnabled(false);
        }

        if(!isParticleConverged){
            btnInitialBeliefBayes.setEnabled(false);
        }

        btnInitialBeliefPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!initInitialBeliefPA) {
                    // Initial Belief start
                    isLoading = true;
                    loading.setTitle("Loading");
                    loading.setMessage("Collecting samples...");
                    loading.show();
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
                    accelX.clear();
                    accelY.clear();
                    accelZ.clear();
                    initInitialBeliefPA = false;
                    btnInitialBeliefPA.setText("INITIAL BELIEF PA");
                }
            }
        });

        // Initial Belief Bayes
        btnInitialBeliefBayes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!initInitialBeliefBayes) {
                    if (!isSetSchedulerWifi) {
                        setUpdateWifiSignal();
                        isSetSchedulerWifi = true;
                    }
                    btnInitialBeliefBayes.setText("STOP INITIAL BAYES");
                    btnSenseBayes.setEnabled(false);
                    initInitialBeliefBayes = true;
                } else {
                    schedulerTimerWifi.cancel();
                    schedulerTimerWifi.purge();
                    schedulerTimerWifi = null;
                    btnInitialBeliefBayes.setText("INITIAL BELIEF BAYES");
                    initInitialBeliefBayes = false;
                    btnSenseBayes.setEnabled(true);
                    isSetSchedulerWifi = false;
                }
            }
        });

        // Sense Bayes
        btnSenseBayes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSensing = true;
                wifiManager.startScan();
                wifi.getObservable().mySetChanged();
                loading.setTitle("Loading");
                loading.setMessage("Scanning Wifi...");
                loading.show();
            }
        });

        // Clear Data
        btnClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setTitle("Loading");
                loading.setMessage("Cleaning up wifi data");
                loading.show();
                wifiDb.clearWifiData();
                loading.dismiss();
                Toast.makeText(getApplicationContext(), (CharSequence)"Successfully clean up wifi data", Toast.LENGTH_SHORT).show();
                btnSenseBayes.setEnabled(false);
                if(!isParticleConverged)
                    btnInitialBeliefBayes.setEnabled(false);
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

        if(isParticleConverged && !isBtnAfterConverged) {
            initInitialBeliefPA = false;
            btnInitialBeliefPA.setText("INITIAL BELIEF PA");
            btnInitialBeliefPA.setEnabled(false);
            btnInitialBeliefBayes.setEnabled(true);
            btnSenseBayes.setEnabled(true);
            isBtnAfterConverged = true;
        }
    }

    public void setUpdateInfoSchedule(){
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
        schedulerTimerUpdateInfo.scheduleAtFixedRate(task,0,250);
    }

    public void setUpdateLocalizationMonitoring() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Create runnable localization
                        RunUpdateLocalization runUpdateLocalization = new RunUpdateLocalization(
                                angle, localizationMonitor, localizationView, compassGUI, stepCount,
                                getApplicationContext());

                        totalStep += stepCount;
                        stepCount = 0;

                        // Add runnable to queue
                        executor.submit(runUpdateLocalization);

                        // Update particle converged flag
                        isParticleConverged = localizationMonitor.isParticleHasConverged();
                    }
                });
            }
        };
        schedulerTimerLocalization.scheduleAtFixedRate(task,0,250);
    }

    public void setUpdateWifiSignal() {

        if(schedulerTimerWifi == null){
            schedulerTimerWifi = new Timer();
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wifiManager.startScan();
                        wifi.getObservable().mySetChanged();
                    }
                });
            }
        };
        schedulerTimerWifi.scheduleAtFixedRate(task,0,5000);
    }


    private class AddWifiData extends AsyncTask<Object, Object, Object> {
        WifiDBHandler dbConnector = new WifiDBHandler(getApplicationContext());
        WifiData wifiData_this;

        public AddWifiData(WifiData wifiData) {
            wifiData_this = wifiData;
        }

        @Override
        protected Object doInBackground(Object... params) {
            // Open the database
            dbConnector.addWifiData(wifiData_this);
            return null;
        }
    }
}
