package nl.tudelft.xflash.activitymonitoringandlocalization.Misc;

import java.util.Arrays;

/**
 * Created by tritronik on 5/25/2016.
 */
public class Statistics
{
    double[] data;
    int size;

    public Statistics(double[] data)
    {
        this.data = data;
        size = data.length;
    }

    public double getMax() {
        // Validates input
        if (data== null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (data.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty.");
        }

        // Finds and returns max
        double max = data[0];
        for (int j = 1; j < data.length; j++) {
            if (Double.isNaN(data[j])) {
                return Double.NaN;
            }
            if (data[j] > max) {
                max = data[j];
            }
        }

        return max;
    }

    public double getMean()
    {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/size;
    }

    public double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        for(double a :data)
            temp += (mean-a)*(mean-a);
        return temp/size;
    }

    public double getStdDev()
    {
        return Math.sqrt(getVariance());
    }

    public double median()
    {
        Arrays.sort(data);

        if (data.length % 2 == 0)
        {
            return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
        }
        else
        {
            return data[data.length / 2];
        }
    }
}