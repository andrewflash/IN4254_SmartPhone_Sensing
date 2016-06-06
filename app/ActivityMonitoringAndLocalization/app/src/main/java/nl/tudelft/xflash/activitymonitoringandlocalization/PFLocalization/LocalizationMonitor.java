package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import android.content.Context;

import java.util.ArrayList;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityType;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.FloorLayout;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.Location;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter.Particle;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter.ParticleFilter;

/**
 * Created by xflash on 29-5-16.
 */
public class LocalizationMonitor {

    private ActivityType activityList;
    private ParticleFilter pf;
    private FloorLayout floorLayout;
    //private WalkedPath walkedPath;

    private float angle = 0.0f;
    private float[] mov = {0,0};

    public LocalizationMonitor(Context context, FloorLayout floorLayout, int nParticles) {
        // Initialize floor layout
        this.floorLayout = floorLayout;

        activityList = ActivityType.getInstance();

        // Initialize particle filter with nParticles.
        pf = new ParticleFilter(nParticles, floorLayout);
    }

    public void initialBelief(ArrayList<ArrayList<Integer>> rssiData){
        pf.initialBelief(rssiData);
        activityList.empty();
    }

    public void reset(){
        //WalkedPath.getInstance().reset();
        pf.resetParticleFilter();
        activityList.empty();
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

        Type activity = activityList.getType(activityList.size() - 1);

        if (activity == Type.WALKING || activity == Type.IDLE ) {
            angle = 0f;
            // Average angle per window size
            for (int i = 0; i < orientationX.size(); i++) {
                float[] orientation = {orientationX.get(i), orientationY.get(i), orientationZ.get(i)};
                angle += orientation[0]/orientationX.size();
            }

            // If activity Type is WALKING, update the movement of particles
            if(activity == Type.WALKING) {
                pf.movement(angle, time);
                mov = pf.getMovement();
            }

            // If idle clear mov
            if(activity == Type.IDLE) {
                // Clear mov
                mov[0] = 0;
                mov[1] = 0;
            }

            return true;
        }

        return false;
    }

    public Location particleConverged(){
        return pf.converged(3f);
    }

    public Particle forceConverge(){
        //WalkedPath walkedPath = WalkedPath.getInstance();
        Particle bestParticle = pf.bestParticle();
        //walkedPath.setPath(bestParticle.getCurrentLocation());
        return bestParticle;
    }

    public float[] getMovement() {
        return mov;
    }
}
