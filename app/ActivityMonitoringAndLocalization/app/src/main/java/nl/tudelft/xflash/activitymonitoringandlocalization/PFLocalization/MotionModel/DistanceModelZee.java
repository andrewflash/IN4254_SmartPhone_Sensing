package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.MotionModel;

import android.app.Activity;

import java.util.Random;

import nl.tudelft.xflash.activitymonitoringandlocalization.Database.AcceleroDBHandler;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.FloorLayout;

/**
 * Created by tritronik on 5/26/2016.
 */
public class DistanceModelZee {
    private int stepCount;
    private float strideLength;
    private Random rand;
    private FloorLayout floorLayout;

    public DistanceModelZee(FloorLayout floorLayout)
    {
        this.floorLayout = floorLayout;
        rand = new Random();
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public void setStrideLength(float strideLength) {
        this.strideLength = strideLength;
    }

    // Estimate distance (dx and dy)
    public float[] getDistance(float alpha, float time) {
        //Gaussian distribution of mean alpha and stdev alphaDeviation
        float alphaDeviation = 0.05f;   // in radians

        // Add gaussian noise to the angle
        float alphaNoise = alpha + floorLayout.getNorthAngle() + (float) rand.nextGaussian()*alphaDeviation;

        // Caluclate the dx/dy based on the window size and alpha
        float dx = this.stepCount*this.strideLength * (float) Math.cos(Math.toRadians((double)alphaNoise));
        float dy = this.stepCount*this.strideLength * (float) Math.sin(Math.toRadians((double)alphaNoise));
        float[] out = {dx,dy};

        return out;
    }

}
