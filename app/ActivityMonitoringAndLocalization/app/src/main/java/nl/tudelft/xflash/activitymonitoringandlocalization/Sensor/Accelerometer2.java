package nl.tudelft.xflash.activitymonitoringandlocalization.Sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.view.View;

import nl.tudelft.xflash.activitymonitoringandlocalization.Database.ActivityList;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.DatabaseAPI;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.RecordActivity;
import nl.tudelft.xflash.activitymonitoringandlocalization.R;

/**
 * Created by xflash on 4-5-16.
 */

public class Accelerometer2 /*implements SensorEventListener*/ {
/*
    //sampling speed
    private final int  samplingPeriodUs = 20000;   // microseconds, 20.000us = 50Hz

    //sample collection maintainence for calibration
    // samples between lower- and upper- bound are used for bias estimation.
    // With 50Hz, the linear-filter needs roughly 1000 samples to reach a steady state.
    private final int collectedSamples_lowerBound = 1000;
    private final int collectedSamples_upperBound = 1500;
    private int collectedSamples = 0;       // number of collected samples in a run

    //flags to determine operation of accelerometer
    private boolean listening  = false;  //indicates if we are already listening to the accel
    private boolean logdata    = false;  //indicates if we need to log/store the accel-data

    //track trial-number
    protected int nTrial = 0;

    //misc globals
    protected SensorManager sm;
    protected Sensor sensor;
    protected ActivityList activityType;
    protected DatabaseAPI dbAPI;
    protected Context appContext;

    public Accelerometer2(SensorManager sm, Context appContext){
        this.appContext = appContext;
        this.sm = sm;

        // init accelerometer
        sensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // start accel, warming up, do not log data at first
        this.startAccel(false);
    }

    //set database acces
    public void setDatabaseAPI(DatabaseAPI dbAPI) {
        this.dbAPI = dbAPI;
        // update run
        nTrial = dbAPI.getMaxTrials();
    }

    //update activity on change
    public void onActivityTypeChange(ActivityList acType){
        this.activityType = acType;
    }

    //get/set trial-number
    public int getTrial(){ return nTrial; }
    public void setTrial(int trial){ this.nTrial = trial; }

    //start accelerometer for retrieving data for classification
    public void startCollectingActivityData() {
        //only start if user click start button
        this.startAccel(true);
    }

    //start accelerometer
    private void startAccel(boolean logdata) {
        this.logdata = logdata;

        //new trial to collect samples
        this.collectedSamples = 0;

        //start listening
        if (!listening) {
            if (sm.registerListener(this, sensor, samplingPeriodUs)) {
                listening = true; // we are listening to accelerometer!
            }
        }
    }

    // pause logging of data
    public void pause(){
        logdata    = false;
    }

    // stop accelerometer from collecting data
    public void stopCollectingActivityData() {
        //reset flags
        listening  = false;
        logdata    = false;
        collectedSamples = 0;
    }

    public void stopAccel() {
        sm.unregisterListener(this);
    }

    public String getStatus() {
        String status = "";
        status += "Lis:" + ((listening) ? 1 : 0);
        status += " Log:" + ((logdata) ? 1 : 0);
        status += " Col:" + collectedSamples;
        return status;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        //new sample collected
        this.collectedSamples++;

        // Display to screen
        displayAccel(event.values[0],event.values[1],event.values[2]);

        //Check if data logging is enabled.
        if (logdata) {
            // Create struct for accel readings.
            RecordActivity record = new RecordActivity(
                    this.nTrial, event.timestamp, event.accuracy, this.activityType.getValue(),
                    event.values[0],
                    event.values[1],
                    event.values[2]);

            // insert data
            dbAPI.insertAccel(record);
        }
    }

    public void displayAccel(float aX, float aY, float aZ) {
        TextView currentX = (TextView) findViewById(R.id.txtXaxis);
        TextView currentY = (TextView) findViewById(R.id.txtYaxis);
        TextView currentZ = (TextView) findViewById(R.id.txtZaxis);
        currentX.setText(Float.toString(aX));
        currentY.setText(Float.toString(aY));
        currentZ.setText(Float.toString(aZ));
    }*/
}