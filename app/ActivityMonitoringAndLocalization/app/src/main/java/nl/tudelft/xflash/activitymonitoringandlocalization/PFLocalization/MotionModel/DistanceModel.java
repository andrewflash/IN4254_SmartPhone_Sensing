package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.MotionModel;

import java.util.Random;

import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.FloorLayout;

/**
 * Created by xflash on 27-5-16.
 */
public class DistanceModel {

    private Random rand;
    private FloorLayout floorLayout;
    private final float VELOCITY = 1.2f;

    public DistanceModel(FloorLayout floorLayout){
        this.floorLayout = floorLayout;
        rand = new Random();
    }

    // Estimate distance (dx and dy)
    public float[] getDistance(float alpha, float time) {
        //Gaussian distribution of mean 1 and stdev 0.2 m/s
        float v = VELOCITY + (float) rand.nextGaussian()*VELOCITY/10f;

        // Gaussian distribution of mean alpha and stdev alphaDeviation
        float alphaDeviation = 0.05f;   // in radians

        // Add gaussian noise to the angle and also add horizontal placement angle pi/2
        float alphaNoise = alpha + (float)Math.toRadians(floorLayout.getNorthAngle()) +
                (float) rand.nextGaussian()*alphaDeviation + (float)Math.PI/2;

        // Caluclate the dx/dy based on the window size and alpha
        float dx = v*time * (float) Math.cos(alphaNoise);
        float dy = v*time * (float) Math.sin(alphaNoise);
        float[] out = {dx,-dy};

        return out;
    }
}
