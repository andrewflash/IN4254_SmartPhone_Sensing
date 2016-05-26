package nl.tudelft.xflash.activitymonitoringandlocalization.Classification;

import android.app.Activity;
import java.util.List;

import nl.tudelft.xflash.activitymonitoringandlocalization.Database.AcceleroDBHandler;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.AcceleroData;
import nl.tudelft.xflash.activitymonitoringandlocalization.Misc.Statistics;

/**
 * Created by tritronik on 5/25/2016.
 */
public class Nasc {
    Activity ActivityMon;
    public Nasc(Activity activityMon)
    {
        this.ActivityMon = activityMon;
    }

    public double normalizedAutoCorrelation(int m, int t) {
        int k = 0;
        double a;
        double mu1, mu2, stdev1, stdev2;
        double nac;
        double firstElm = 0.0;
        double secondElm = 0.0;
        double[] arrAcceleroData;
        AcceleroDBHandler dbAccelero = new AcceleroDBHandler(ActivityMon.getApplicationContext());
        AcceleroData acceleroData;
        List<AcceleroData> listAcceleroData;

        // get mean from m to m+t-1
        listAcceleroData = dbAccelero.getAcceleroDataIDRange(m, m+t-1);
        arrAcceleroData = normalizeAcceleroList(listAcceleroData);
        Statistics stats1 = new Statistics(arrAcceleroData);
        mu1 = stats1.getMean();

        // get mean from m+t to m+t+t-1
        listAcceleroData = dbAccelero.getAcceleroDataIDRange(m+t, m+t+t-1);
        arrAcceleroData = normalizeAcceleroList(listAcceleroData);
        Statistics stats2 = new Statistics(arrAcceleroData);
        mu2 = stats2.getMean();

        for (k = 0; k < t; k++) {
            // get accelerometer m+k
            acceleroData = dbAccelero.getAcceleroData(m+k);
            a = normalizeAcceleroData(acceleroData);
            firstElm = firstElm + (a-mu1);

            // get accelerometer m+k+t
            acceleroData = dbAccelero.getAcceleroData(m+k+t);
            a = normalizeAcceleroData(acceleroData);
            secondElm = secondElm + (a-mu2);
        }
        // get stdev from m to m+t-1
        stdev1 = stats1.getStdDev();
        // get stdev from m+t to m+t+t-1
        stdev2 = stats2.getStdDev();
        // calculate nac: get the normalization value of the upper elements, divide by the lower elements
        nac = Math.sqrt(Math.pow(firstElm, 2)+Math.pow(secondElm, 2)) / (t*stdev1*stdev2);
        return nac;
    }

    public double maxNormalizedAutoCorrelation(int m, int tmin, int tmax) {
        int i;
        int j = 0;
        double arrNAC[] = {};
        for (i=tmin; i<=tmax; i++) {
            arrNAC[j] = normalizedAutoCorrelation(m, i);
            j++;
        }
        Statistics stats = new Statistics(arrNAC);

        return stats.getMax();
    }

    private double normalizeAcceleroData(AcceleroData acceleroData) {
        double normAcceleroData;
        normAcceleroData = Math.sqrt(Math.pow(acceleroData.getAccX(),2) +
                Math.pow(acceleroData.getAccY(),2) + Math.pow(acceleroData.getAccZ(),2));
        return normAcceleroData;
    }

    private double[] normalizeAcceleroList(List<AcceleroData> listAcceleroData) {
        double arrNormAcceleroData[] = {};
        double normAcceleroData;
        int i = 0;
        for (AcceleroData element : listAcceleroData) {
            normAcceleroData = Math.sqrt(Math.pow(element.getAccX(), 2) +
                    Math.pow(element.getAccY(), 2) + Math.pow(element.getAccZ(), 2));
            arrNormAcceleroData[i] = normAcceleroData;
        }
        return arrNormAcceleroData;
    }

}
