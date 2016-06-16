package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import android.content.Context;

import java.util.ArrayList;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityType;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.FloorLayout;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.Location;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter.Particle;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter.ParticleFilter;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.VisitedPath;

/**
 * Created by xflash on 29-5-16.
 */
public class LocalizationMonitor {

    private ActivityType activityList;
    private ParticleFilter pf;
    private FloorLayout floorLayout;

    private float angle = 0.0f;
    private float[] mov = {0,0};
    private boolean particleHasConverged;

    public LocalizationMonitor(Context context, FloorLayout floorLayout, int nParticles) {
        // Initialize floor layout
        this.floorLayout = floorLayout;

        activityList = ActivityType.getInstance();
        particleHasConverged = false;

        // Initialize particle filter with nParticles.
        pf = new ParticleFilter(nParticles, floorLayout);
    }

    // Execute initial belief
    public void initialBelief(ArrayList<ArrayList<Integer>> rssiData){
        pf.initialBelief(rssiData);
        activityList.empty();
    }

    // Reset localization
    public void reset(){
        VisitedPath.getInstance().reset();
        pf.resetParticleFilter();
        activityList.empty();
    }

    // Get floor layout
    public FloorLayout getFloorLayout(){
        return this.floorLayout;
    }

    // Get particles list
    public ArrayList<Particle> getParticles(){
        return pf.getParticles();
    }

    // Get direction
    public float getAngle(){
        return this.angle;
    }

    // Update the localization based on orientation data and timestamp
    public boolean update(float angle, float time){

        Type activity = activityList.getType(activityList.size() - 1);

        if (activity == Type.WALKING || activity == Type.IDLE ) {
            this.angle = angle;

            // If activity Type is WALKING, update the movement of particles
            if(activity == Type.WALKING) {
                if(!particleHasConverged) {
                    pf.movement(angle, time);
                } else {
                    pf.movementBest(angle, time);
                }
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

    // Set radius of convergence = 3
    public Location particleConverged(){
        return pf.converged(3f);
    }

    // Force particles become converged (1 best particle)
    public Particle forceConverge(){
        Particle bestParticle = pf.bestParticle();
        return bestParticle;
    }

    // Set converged particle (1 best particle)
    public void setConvergedParticle(Location convLoc) {
        pf.setConvergedParticle(convLoc);
    }

    // Set particle has converged flag
    public void setParticleHasConverged(boolean flag) {
        particleHasConverged = flag;
    }

    // Get flag particle has converged
    public boolean isParticleHasConverged() {
        return particleHasConverged;
    }

    // Get movement distance
    public float[] getMovement() {
        return mov;
    }
}
