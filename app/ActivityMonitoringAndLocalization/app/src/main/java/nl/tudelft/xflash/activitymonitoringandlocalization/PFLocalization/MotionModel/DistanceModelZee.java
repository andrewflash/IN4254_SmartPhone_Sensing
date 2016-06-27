package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.MotionModel;

import android.app.Activity;
import android.util.Log;

import java.io.Console;
import java.util.Random;

import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.FloorLayout;

/**
 * Created by tritronik on 5/26/2016.
 */
public class DistanceModelZee {
    private Random rand;
    private FloorLayout floorLayout;

    public DistanceModelZee(FloorLayout floorLayout)
    {
        this.floorLayout = floorLayout;
        rand = new Random();
    }

    // Estimate distance (dx and dy)
    public float[] getDistance(float alpha, int stepCount, float strideLength) {
        //Gaussian distribution of mean alpha and stdev alphaDeviation
        //float alphaDeviation = 0.8859f;   // in radians
        float alphaDeviation = 0.2f;   // in radians
        float alphaMean = -0.1f;    // in radians

        // Add gaussian noise to the angle
        float alphaNoise = alpha + (float)Math.toRadians(floorLayout.getNorthAngle())
                + alphaMean + (float)Math.toRadians(90);

        Log.d(this.getClass().getSimpleName(), "alphaNoise: " + alphaNoise);

        if ((5.5f < alphaNoise) && (alphaNoise < 7.0f)) {
            alphaNoise = 6.28f; // 360 deg, ok
        } else if ((10.15f < alphaNoise) && (alphaNoise < 11.8f)) {
            alphaNoise = 10.99f; // 630 deg, ok
        } else if ((7.0f < alphaNoise) && (alphaNoise < 8.63f)) {
            alphaNoise = 7.85f; // 450 deg, ok
        } else if ((8.63f < alphaNoise) && (alphaNoise < 10.15f)) {
            alphaNoise = 9.42f; // 540 deg
        }

        alphaNoise = alphaNoise + (float) rand.nextGaussian()*alphaDeviation;

        float randerr = (float) (Math.random() * (0.2) -0.1)*strideLength;

        // Caluclate the dx/dy based on the window size and alpha
        float dx = stepCount*(strideLength+randerr) * (float) Math.cos(alphaNoise);
        float dy = stepCount*(strideLength+randerr) * (float) Math.sin(alphaNoise);
        float[] out = {dx,dy};

        return out;
    }

}
