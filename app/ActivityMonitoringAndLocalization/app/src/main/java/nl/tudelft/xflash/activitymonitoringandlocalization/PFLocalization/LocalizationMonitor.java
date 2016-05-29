package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityType;

/**
 * Created by xflash on 29-5-16.
 */
public class LocalizationMonitor {

    private ActivityType activityList;
    private ParticleFilter pf;
    private FloorLayout floorLayout;
    //private WalkedPath walkedPath;
    private int WINDOW_SIZE_ACC;
    private int WINDOW_SIZE_ORIENTATION;

    private float angle = 0.0f;

    public LocalizationMonitor(Context context, FloorLayout floorLayout, int nParticles) {
        // Initialize floor layout
        this.floorLayout = floorLayout;

        this.WINDOW_SIZE_ORIENTATION = 20;

        // Initialize particle filter with nParticles.
        pf = new ParticleFilter(nParticles, floorLayout);
    }

    public void initialBelief(ArrayList<ArrayList<Integer>> rssiData){
        //pf.initialBelief(rssiData);
        activityList.empty();
    }

    public void reset(){
        //WalkedPath.getInstance().reset();
        pf.resetParticleFilter();
        //activityList.empty();
    }

    public FloorLayout getFloorLayout(){
        return this.floorLayout;
    }

    public ArrayList<Particle> getParticles(){
        return pf.getParticles();
    }

    public float getAngle(){
        return this.angle;
    }

    public boolean update(ArrayList<Float> orientationX, ArrayList<Float> orientationY,
                          ArrayList<Float> orientationZ, float time){
        //Type activity = activityList.getType(activityList.size() - 1);

        //if (activity == Type.WALK || activity == Type.IDLE ) {
            angle = 0f;
            // Average angle per window size
            for (int i = 0; i < WINDOW_SIZE_ORIENTATION; i++) {
                float[] orientation = {orientationX.get(i), orientationY.get(i), orientationZ.get(i)};
                //float orienDegree = (float)Math.toDegrees(orientation[0]);
                //angle +=  (Math.toDegrees(orientation[0]) < 0)?(360+orienDegree):orienDegree;
                angle += orientation[0];
            }
            angle = angle/WINDOW_SIZE_ORIENTATION;

            // If activity Type update the movement of partcicles
            //if(activity == Type.WALK){
                //pf.movement(angle, time);
                // Log.i("BP TEST", "x=" + pf.bestParticle().getCurrentLocation().getX() + "y=" + pf.bestParticle().getCurrentLocation().getY());
            //}

            return true;
        //}
        //return false;
    }

//    public Location particleConvergent(){
//        return pf.converged(3f);
//    }
}
