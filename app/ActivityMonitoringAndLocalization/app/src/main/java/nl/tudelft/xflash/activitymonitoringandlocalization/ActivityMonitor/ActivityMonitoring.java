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
    DistanceModelZee distanceModelZee;
    private Nasc nasc;
    private int stepCount;
    private float strideLength;
    private int tmin = 40;
    private int tmax = 100;
    private boolean finished = true;
    private Type oldState = Type.NONE;

    public ActivityMonitoring(Context ctx, DistanceModelZee distanceModelZee) {
        this.distanceModelZee = distanceModelZee;
        activityList = ActivityType.getInstance();
        nasc = new Nasc(tmin, tmax);
    }

    private void updateStepCount() {
        if(getActivity() == Type.WALKING) {
            this.stepCount = getWindowSize() / (nasc.gettOpt()/2); // num samples = window size
        }
        else {
            this.stepCount = 0;
        }
        distanceModelZee.setStepCount(this.stepCount);
    }

    private void updateStrideLength() {
        this.strideLength = 0.5f; // in meter;
        distanceModelZee.setStrideLength(this.strideLength);
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

        Log.d(this.getClass().getSimpleName(), "stdevAcc is " + stdevAcc);
        Log.d(this.getClass().getSimpleName(), "maxNAC is " + maxNAC);

        if(stdevAcc < 0.03) {
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
        this.finished = false;
        nasc.setAccelerations(x, y, z);
        nasc.calculateMaxNACandTopt(this.tmin, this.tmax);

        this.tmin = nasc.gettMin();
        this.tmax = nasc.gettMax();

        Type label = updateState();
        activityList.addType(label);

        updateStepCount();
        updateStrideLength();
        this.finished = true;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getStepCount(){
        return stepCount;
    }
}
