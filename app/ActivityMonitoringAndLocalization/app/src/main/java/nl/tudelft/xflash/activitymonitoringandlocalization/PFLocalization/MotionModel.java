package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import android.app.Activity;

import nl.tudelft.xflash.activitymonitoringandlocalization.Classification.Nasc;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.AcceleroDBHandler;

/**
 * Created by xflash on 27-5-16.
 */
public class MotionModel {
    private int tmin = 40;
    private int tmax = 100;
    private int topt = 60;
    private int numStep = 0;
    private String state = "IDLE";
    private Activity ActivityMon;
    private Nasc nasc;

    public MotionModel(Activity activityMon)
    {
        this.ActivityMon = activityMon;
        nasc = new Nasc(this.ActivityMon);
    }

    public void setNumStep(int inputNumStep) {
        this.numStep = inputNumStep;
    }

    public int getNumStep() {
        return this.numStep;
    }

    public void updateNumStep() {
        AcceleroDBHandler dbAccelero = new AcceleroDBHandler(ActivityMon.getApplicationContext());
        long numSamples = dbAccelero.getAcceleroDataCount();
        while(this.state == "WALKING") {
            if ((dbAccelero.getAcceleroDataCount() - numSamples) >= (this.topt/2)) {
                this.numStep++;
                numSamples = dbAccelero.getAcceleroDataCount();
            }
        }
    }

    public void setState(String inputState) {
        this.state = inputState;
    }

    public String getState() {
        double stdevAcc = 0.0;
        double maxNAC = 0.0;
        int tTest = this.tmin;
        int m = 0;

        // calculate stdevAcc for tOpt samples
        stdevAcc = nasc.stdevAccelero(m,this.topt);
        // calculate maxNAC and potential tOpt
        nasc.calculateMaxNACandTopt(m, this.tmin, this.tmax);
        maxNAC = nasc.getMaxNac();
        tTest = nasc.gettOpt();

        if(stdevAcc < 0.01) {
            this.state = "IDLE";
        }
        if(maxNAC > 0.7) {
            if (Math.abs(maxNAC-1) < 0.1) {
                if ((tTest>this.tmin) && (tTest<this.tmax)) {
                    setTOpt(tTest);
                }
            }
            this.state = "WALKING";
        }
        return this.state;
    }

    private void setTOpt(int tOpt) {
        this.topt = tOpt;
        this.tmin = tOpt - 10;
        this.tmax = tOpt + 10;
    }
}