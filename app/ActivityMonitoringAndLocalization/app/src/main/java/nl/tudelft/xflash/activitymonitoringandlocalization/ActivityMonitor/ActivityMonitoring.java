package nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.MotionModel.DistanceModelZee;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.MotionModel.Nasc;

/**
 * Created by xflash on 4-5-16.
 */
public class ActivityMonitoring {
    // This instance keeps track of the activities performed
    ActivityType activityList;
    private Nasc nasc;
    private int stepCount;
    private ArrayList<Integer> stepCountList;
    private float strideLength;
    private int tmin = 40;
    private int tmax = 100;
    private int tOpt = 0;
    private boolean finished = true;
    private Type oldState = Type.NONE;

    public ActivityMonitoring(Context ctx) {
        activityList = ActivityType.getInstance();
        nasc = new Nasc(tmin, tmax);
        stepCountList = new ArrayList<>();
    }

    private void updateStepCount() {
        int nStep = 0;
        if(getActivity() == Type.WALKING) { // always walking
            nStep = getWindowSize() / (nasc.gettOpt()/2); // num samples = window size
            stepCountList.add(nStep);
        }
        else {
            nStep = 0;
        }
        this.stepCount = nStep;
    }

    private void updateStrideLength() {
        this.strideLength = 0.5f; // in meter;
    }

    public int getWindowSize() {
        return this.tmax*2+10;
    }

    private Type updateState() {
        double stdevAcc = 0.0;
        double maxNAC = 0.0;
        Type state = this.oldState;

        // calculate stdevAcc
        stdevAcc = nasc.stdevAccelero();
        // calculate maxNAC
        maxNAC = nasc.getMaxNac();

        if(stdevAcc < 0.1) {
            state = Type.IDLE;
        }
        if(maxNAC > 0.7) {
            state = Type.WALKING;
        }
        this.oldState = state;
        return state;
    }

    // Get current activity
    public Type getActivity() {
        if (activityList.size() == 0) {
            return Type.NONE;
        }
        return activityList.getType(activityList.size() - 1);
    }

    // Update activity based on acc data
    public void update(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z) {
        if(this.finished) {
            this.finished = false;
            nasc.setAccelerations(x, y, z);

            nasc.calculateMaxNACandTopt(this.tmin, this.tmax);

            this.tmin = nasc.gettMin();
            this.tmax = nasc.gettMax();
            this.tOpt = nasc.gettOpt();

            Type label = this.updateState();
            activityList.addType(label);

            //updateStepCount();
            updateStrideLength();
            this.finished = true;
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public int getStepCount(){
        return stepCount;
    }

    public float getStrideLength(){
        return strideLength;
    }

    public ArrayList<Integer> getStepCountList(){
        return stepCountList;
    }

    public void clearStepCountList(){
        this.stepCountList.clear();
    }

    public int getTOpt(){
        return this.tOpt;
    }
}