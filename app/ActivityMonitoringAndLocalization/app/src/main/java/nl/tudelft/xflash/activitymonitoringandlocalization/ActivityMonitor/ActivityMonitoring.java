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
    private int tmin = 40;
    private int tmax = 100;
    private int tOpt = 0;
    private boolean finished = true;
    private Type oldState = Type.NONE;

    public ActivityMonitoring(Context ctx) {
        activityList = ActivityType.getInstance();
        nasc = new Nasc(tmin, tmax);
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

        if(stdevAcc < 0.3) {
            state = Type.IDLE;
        } else if ((maxNAC > 0.7) && (stdevAcc > 0.3)) {
            state = Type.WALKING;
        }

//        Log.d(this.getClass().getSimpleName(),"stdevAcc: " + stdevAcc + " :: maxNAC: " + maxNAC);
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

//        Long start = System.currentTimeMillis();
        nasc.setAccelerations(x, y, z);
        nasc.calculateMaxNACandTopt(this.tmin, this.tmax);

        this.tmin = nasc.gettMin();
        this.tmax = nasc.gettMax();
        this.tOpt = nasc.gettOpt();

        Type label = this.updateState();
        activityList.addType(label);
//        Long stop = System.currentTimeMillis();
//        Log.d(this.getClass().getSimpleName(), "activity update: " + Long.toString(stop-start));
    }

    public int getTOpt(){
        return this.tOpt;
    }
}