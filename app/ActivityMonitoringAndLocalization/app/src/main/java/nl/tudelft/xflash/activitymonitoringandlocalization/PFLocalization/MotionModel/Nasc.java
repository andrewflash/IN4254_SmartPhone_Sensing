package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.MotionModel;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.xflash.activitymonitoringandlocalization.Misc.Statistics;

/**
 * Created by tritronik on 5/25/201stdevAccelero6.
 */
public class Nasc {
    private double maxNac;
    private int tOpt;
    private int tMin;
    private int tMax;
    private ArrayList<Float> arrayListX;
    private ArrayList<Float> arrayListY;
    private ArrayList<Float> arrayListZ;

    public Nasc(int tMin, int tMax) {
        this.tOpt = tMin;
        this.tMin = tMin;
        this.tMax = tMax;
    }

    public void setAccelerations(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z){
        this.arrayListX = x;
        this.arrayListY = y;
        this.arrayListZ = z;
    }

    // samplesX, samplesY, samplesZ have length t+t-1
    public double normalizedAutoCorrelation(int t) {
        int k = 0;
        double a, b;
        double mu1, mu2, stdev1, stdev2;
        double nac;
        double upper = 0.0;
        double[] arrAcceleroData;
        arrAcceleroData = new double[t+t-1];

        // get mean from m to m+t-1
//        Log.d(this.getClass().getSimpleName(), "running normalizeAcceleroList t: " + Integer.toString(t-1));
        arrAcceleroData = normalizeAcceleroList(this.arrayListX.subList(0, t-1),
                this.arrayListY.subList(0, t-1), this.arrayListZ.subList(0, t-1));
//        Log.d(this.getClass().getSimpleName(), "finished running normalizeAcceleroList");
        Statistics stats = new Statistics(arrAcceleroData);
        mu1 = stats.getMean();
        // get stdev from m to m+t-1
        stdev1 = stats.getStdDev();

        // get mean from m+t to m+t+t-1
//        Log.d(this.getClass().getSimpleName(), "running normalizeAcceleroList t+t: " + Integer.toString(t+t-1));
        arrAcceleroData = normalizeAcceleroList(this.arrayListX.subList(t, t+t-1),
                this.arrayListY.subList(t, t+t-1), this.arrayListZ.subList(t, t+t-1));
//        Log.d(this.getClass().getSimpleName(), "finish normalizeAcceleroList t+t: " + Integer.toString(t+t-1));
        stats = new Statistics(arrAcceleroData);
        mu2 = stats.getMean();
        // get stdev from m+t to m+t+t-1
        stdev2 = stats.getStdDev();

//        Log.d(this.getClass().getSimpleName(), "for loop");
        for (k = 0; k < t; k++) {
            // get accelerometer m+k
            a = normalizeAcceleroData(this.arrayListX.get(k), this.arrayListY.get(k), this.arrayListZ.get(k));
            // get accelerometer m+k+t
            b = normalizeAcceleroData(this.arrayListX.get(k+t), this.arrayListY.get(k+t), this.arrayListZ.get(k+t));
            upper = upper + (a-mu1)*(b-mu2);
        }
//        Log.d(this.getClass().getSimpleName(), "finish for loop");

        // calculate nac: get the normalization value of the upper elements, divide by the lower elements
        nac = upper / (t*stdev1*stdev2);

        return nac;
    }

    public void calculateMaxNACandTopt(int tmin, int tmax) {
        int i;
        int j = 0;
        int maxIndex = 0;
        int tOptimal;
        double tempNAC;
        double[] arrNAC;
        arrNAC = new double[tmax-tmin+1];

        for (i=tmin; i<=tmax; i++) {
            tempNAC = normalizedAutoCorrelation(i);
            if (!Double.isInfinite(tempNAC)) {
                arrNAC[j] = tempNAC;
                j++;
            }
        }

        for (i = 0; i < arrNAC.length; i++){
            double newnumber = arrNAC[i];
            if ((newnumber > arrNAC[maxIndex])){
                maxIndex = i;
            }
        }
        tOptimal = maxIndex+tmin;

        Statistics stats = new Statistics(arrNAC);
        this.maxNac = stats.getMax();

        if (tOptimal<50) {
            tOptimal = 50;
        } else if (tOptimal>90) {
            tOptimal = 90;
        }

        this.tOpt = tOptimal;
        this.tMin = tOptimal-10;
        this.tMax = tOptimal+10;
    }

    public double getMaxNac() {
        return this.maxNac;
    }

    public int gettMin() {
        return this.tMin;
    }

    public int gettMax() {
        return this.tMax;
    }

    public int gettOpt() {
        return this.tOpt;
    }

    private double normalizeAcceleroData(float acceleroDataX, float acceleroDataY, float acceleroDataZ) {
        double normAcceleroData;
        normAcceleroData = Math.sqrt(Math.pow(acceleroDataX ,2) + Math.pow(acceleroDataY, 2)
                + Math.pow(acceleroDataZ,2));
        return normAcceleroData;
    }

    private double[] normalizeAcceleroList(List<Float> acceleroDataX,
                                           List<Float> acceleroDataY,
                                           List<Float> acceleroDataZ) {
        int length = acceleroDataX.size();
        double[] arrNormAcceleroData;
        arrNormAcceleroData = new double[length];
        double powSum = 0;
        int i = 0;
        for (i=0; i<length; i++) {
            powSum = Math.pow(acceleroDataX.get(i), 2) +
                    Math.pow(acceleroDataY.get(i), 2) +
                    Math.pow(acceleroDataZ.get(i), 2);
            arrNormAcceleroData[i] = Math.sqrt(powSum);
        }
        return arrNormAcceleroData;
    }

    public double stdevAccelero() {
        double stdev = 0.0;
        double[] arrAcceleroData;
        arrAcceleroData = new double[this.arrayListX.size()];

        // get stdev from m to m+topt-1
        arrAcceleroData = normalizeAcceleroList(this.arrayListX.subList(0, this.tOpt-1),
                this.arrayListY.subList(0, this.tOpt-1), this.arrayListZ.subList(0, this.tOpt-1));
        Statistics stats = new Statistics(arrAcceleroData);
        stdev = stats.getStdDev();

        return stdev;
    }

}
