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
        int m = arrayListX.size()-t-t;

        // get mean from m to m+t-1
//        arrAcceleroData = magnitudeAcceleroList(this.arrayListX.subList(0, t),
//                this.arrayListY.subList(0, t), this.arrayListZ.subList(0, t));
        arrAcceleroData = AcceleroListToDouble(this.arrayListX.subList(m,m+t-1));
        Statistics stats = new Statistics(arrAcceleroData);
        mu1 = stats.getMean();

        // get stdev from m to m+t-1
        stdev1 = stats.getStdDev();

        // get mean from m+t to m+t+t-1
//        arrAcceleroData = magnitudeAcceleroList(this.arrayListX.subList(t, t+t),
//                this.arrayListY.subList(t, t+t), this.arrayListZ.subList(t, t+t));
        arrAcceleroData = AcceleroListToDouble(this.arrayListX.subList(m+t,m+t+t-1));
        stats = new Statistics(arrAcceleroData);
        mu2 = stats.getMean();

        // get stdev from m+t to m+t+t-1
        stdev2 = stats.getStdDev();

//        Log.d(this.getClass().getSimpleName(), "for loop");
        for (k = m; k < m+t; k++) {
            // get accelerometer m+k
//            a = magnitudeAcceleroData(this.arrayListX.get(k), this.arrayListY.get(k), this.arrayListZ.get(k));
            a = this.arrayListX.get(k);
            // get accelerometer m+k+t
//            b = magnitudeAcceleroData(this.arrayListX.get(k+t), this.arrayListY.get(k+t), this.arrayListZ.get(k+t));
            b = this.arrayListX.get(k+t);
            upper = upper + (a-mu1)*(b-mu2);
        }

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

    private double magnitudeAcceleroData(float acceleroDataX, float acceleroDataY, float acceleroDataZ) {
        double normAcceleroData;
        normAcceleroData = Math.sqrt(Math.pow(acceleroDataX ,2) + Math.pow(acceleroDataY, 2)
                + Math.pow(acceleroDataZ,2));
        return normAcceleroData;
    }

    private double[] magnitudeAcceleroList(List<Float> acceleroDataX,
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

    public ArrayList<Float> magnitudeAcceleroArrayList(List<Float> acceleroDataX,
                                                       List<Float> acceleroDataY,
                                                       List<Float> acceleroDataZ) {

        ArrayList<Float> normAccel = new ArrayList<>();

        for(int i=0;i<acceleroDataX.size();i++){
            normAccel.add((float)Math.sqrt(Math.pow(acceleroDataX.get(i),2) +
                    Math.pow(acceleroDataY.get(i),2) +
                    Math.pow(acceleroDataZ.get(i),2)));
        }

        return normAccel;
    }

    public double[] AcceleroListToDouble(List<Float> acceleroData){
        int length = acceleroData.size();
        double[] arrAcceleroData = new double[length];

        for (int i=0; i<length; i++) {
            arrAcceleroData[i] = acceleroData.get(i);
        }
        return arrAcceleroData;
    }

    public double stdevAccelero() {
        double stdev = 0.0;
        double[] arrAcceleroData;
        arrAcceleroData = new double[this.arrayListX.size()];
        int m = arrayListX.size()-this.tOpt-this.tOpt;

        // get stdev from m to m+topt-1
        arrAcceleroData = magnitudeAcceleroList(
                this.arrayListX.subList(m, m+this.tOpt-1),
                this.arrayListY.subList(m, m+this.tOpt-1),
                this.arrayListZ.subList(m, m+this.tOpt-1));
        Statistics stats = new Statistics(arrAcceleroData);
        stdev = stats.getStdDev();

        return stdev;
    }

}
