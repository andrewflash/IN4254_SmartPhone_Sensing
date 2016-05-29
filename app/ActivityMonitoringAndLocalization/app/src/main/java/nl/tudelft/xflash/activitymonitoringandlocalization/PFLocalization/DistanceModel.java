package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import java.util.Random;

/**
 * Created by xflash on 27-5-16.
 */
public class DistanceModel {

    private Random rand;
    private FloorLayout floorLayout;
    private final float VELOCITY = 1.2f;

    public DistanceModel(FloorLayout floorLayout){
        this.floorLayout = floorLayout;
    }

    // Estimate distance (dx and dy)
    public float[] getDistance(float alpha, float time) {
        //Gaussian distribution of mean 1 and stdev 0.2 m/s
        float v = VELOCITY + (float) rand.nextGaussian()*VELOCITY/10f;

        //Gaussian distribution of mean alpha and stdev alphaDeviation
        float alphaDeviation = 20f;

        // Add gaussian noise to the angle
        float alphaNoise = alpha + floorLayout.getNorthAngle() + 90f
                + (float) rand.nextGaussian()*alphaDeviation;

        // Caluclate the dx/dy based on the window size and alpha
        float dx = v*time * (float) Math.cos(Math.toRadians((double)alphaNoise));
        float dy = v*time * (float) Math.sin(Math.toRadians((double)alphaNoise));
        float[] out = {dx,dy};
        return out;
    }
}
