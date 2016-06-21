package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityType;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.WifiData;
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
    public boolean initialBeliefBayesKNN(List<ScanResult> curWifi, List<WifiData> wifiData){
        boolean b = pf.initialBeliefBayesKNN(curWifi, wifiData);
        activityList.empty();
        return b;
    }

    // Reset localization
    public void reset(){
        VisitedPath.getInstance().reset();
        pf.resetParticleFilter();
        activityList.empty();
    }

    // Get particles list
    public ArrayList<Particle> getParticles(){
        return pf.getParticles();
    }

    // Get direction
    public float getAngle(){
        return this.angle;
    }

    // Update the localization based on orientation data
    public boolean update(float angle) {

        //Type activity = activityList.getType(activityList.size() - 1);

        this.angle = angle;

        if (!particleHasConverged) {
            pf.movement(angle);
        } else {
            pf.movementBest(angle);
        }
        mov = pf.getMovement();
        return true;
    }

    // Set radius of convergence = 3, radius of converge of stride = 0.1
    public Location particleConverged(){
        return pf.converged(3f, 0.1f);
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

    // Get cell name from current location
    public String getCellLocation(){
        String cellName = "NONE";
        if(isParticleHasConverged()){
            cellName = floorLayout.getCellNameFromLocation(pf.getParticles().get(0).getCurrentLocation());
        }
        return cellName;
    }
}
