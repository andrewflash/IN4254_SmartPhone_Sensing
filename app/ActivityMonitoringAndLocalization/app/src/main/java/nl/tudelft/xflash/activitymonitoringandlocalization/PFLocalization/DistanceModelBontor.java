package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import android.app.Activity;

import nl.tudelft.xflash.activitymonitoringandlocalization.Database.AcceleroDBHandler;

/**
 * Created by tritronik on 5/26/2016.
 */
public class DistanceModelBontor {
    private int tmin = 40;
    private int tmax = 100;
    private int topt = 60;
    private int numStep = 0;
    private String state = "IDLE";
    private Activity ActivityMon;

    public DistanceModelBontor(Activity activityMon)
    {
        this.ActivityMon = activityMon;
    }

    public void setNumStep(int inputNumStep) {
        this.numStep = inputNumStep;
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

    public int getNumStep() {
        return this.numStep;
    }

    public void setState(String inputState) {
        this.state = inputState;
    }

    public String getState() {
        double stdevAcc = 0.0;
        double maxNAC = 0.0;
        int tTest = this.tmin;

        // calculate stdevAcc
        // calculate maxNAC

        if(stdevAcc < 0.01) {
            this.state = "IDLE";
        }
        if(maxNAC > 0.7) {
            if (Math.abs(maxNAC-1) < 0.1) {
                setTOpt(tTest);
            }
            this.state = "WALKING";
        }
        return this.state;
    }

    public void setTOpt(int tOpt) {
        this.topt = tOpt;
    }

    public int getTOpt() {
        return this.topt;
    }
}
