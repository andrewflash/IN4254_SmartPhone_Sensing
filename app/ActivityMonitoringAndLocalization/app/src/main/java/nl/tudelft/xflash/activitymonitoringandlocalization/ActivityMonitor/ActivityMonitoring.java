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

    public ActivityMonitoring(Context ctx, DistanceModelZee distanceModelZee) {
        this.distanceModelZee = distanceModelZee;
        activityList = ActivityType.getInstance();
        nasc = new Nasc();
    }

    private void updateStepCount() {
        if(getActivity() == Type.WALKING) { // always walking
            this.stepCount = 250 / (nasc.gettOpt()/2);
        }
        Log.d(this.getClass().getSimpleName(), "stepCount is " + this.stepCount);
        distanceModelZee.setStepCount(this.stepCount);
    }

    private void updateStrideLength() {
        this.strideLength = 0.4f; // in meter;
        distanceModelZee.setStrideLength(this.strideLength);
    }

    private Type updateState() {
        double stdevAcc = 0.0;
        double maxNAC = 0.0;
        Type state = Type.NONE;

        // calculate stdevAcc
        stdevAcc = nasc.stdevAccelero();
        // calculate maxNAC
        maxNAC = nasc.getMaxNac();

        Log.d(this.getClass().getSimpleName(), "stdevAcc is " + stdevAcc);
        Log.d(this.getClass().getSimpleName(), "maxNAC is " + maxNAC);

        if(stdevAcc < 0.01) {
            state = Type.IDLE;
        }
        if(maxNAC > 0.7) {
            state = Type.WALKING;
        }
        return state;
    }

    // Get current activity
    public Type getActivity() {
        if (activityList.size() == 0) {
            return Type.NONE;
        }
        return activityList.getType(activityList.size() - 1);
    }

    public boolean isFinished() {
        return this.finished;
    }

    // Update activity based on acc data
    public void update(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z) {
        Log.d(this.getClass().getSimpleName(), "updating ActivityMonitoring");
        this.finished = false;

        nasc.setAccelerations(x, y, z);
        Log.d(this.getClass().getSimpleName(), "calculate MaxNAC & Opt");
        nasc.calculateMaxNACandTopt(this.tmin, this.tmax);

        Log.d(this.getClass().getSimpleName(), "update State");
        Type label = updateState();
        activityList.addType(label);

        Log.d(this.getClass().getSimpleName(), "update stepCount");
        updateStepCount();
        updateStrideLength();
        this.finished = true;
        Log.d(this.getClass().getSimpleName(), "finish ActivityMonitoring");
    }
}
