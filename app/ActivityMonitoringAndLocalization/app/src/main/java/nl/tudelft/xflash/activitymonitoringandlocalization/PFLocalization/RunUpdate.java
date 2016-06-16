package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import android.util.Log;

import java.util.ArrayList;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityMonitoring;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.Location;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter.Particle;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.CompassGUI;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.LocalizationMap;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.VisitedPath;

/**
 * Created by xflash on 29-5-16.
 */
public class RunUpdate implements Runnable {

    private ArrayList<Float> accelX = new ArrayList<>();
    private ArrayList<Float> accelY = new ArrayList<>();
    private ArrayList<Float> accelZ = new ArrayList<>();
    private float angle;

    private ActivityMonitoring activityMonitoring;
    private LocalizationMonitor localizationMonitor;

    private VisitedPath visitedPath;
    private Location convergedLoc;
    private boolean particleHasConverged;

    private LocalizationMap localizationMap;
    private CompassGUI compassGUI;

    private float dT;

    public RunUpdate(ArrayList<Float> accelX, ArrayList<Float> accelY, ArrayList<Float> accelZ,
                       float angle, ActivityMonitoring acMon, LocalizationMonitor locMon,
                       LocalizationMap locMap, CompassGUI compGUI, float dT)
    {
        this.accelX = (ArrayList<Float>) accelX.clone();
        this.accelY = (ArrayList<Float>) accelY.clone();
        this.accelZ = (ArrayList<Float>) accelZ.clone();
        this.angle = angle;
        this.activityMonitoring = acMon;
        this.localizationMonitor = locMon;
        this.localizationMap = locMap;
        this.compassGUI = compGUI;
        this.dT = dT;
        this.visitedPath = VisitedPath.getInstance();
        this.particleHasConverged = false;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        // Update activity monitor
        this.activityMonitoring.update(accelX, accelY, accelZ);

        // Update localization monitor
        if (this.localizationMonitor.update(angle,dT)) {
            // Check for convergence and change the color of particles
            if(!particleHasConverged) {
                convergedLoc = localizationMonitor.particleConverged();
                if (convergedLoc != null) {
                    final Particle convergeParticle = localizationMonitor.forceConverge();
                    this.localizationMap.post(new Runnable() {
                        @Override
                        public void run() {
                            localizationMap.setConvLocation(convergeParticle);
                        }
                    });
                    visitedPath.setPath(convergeParticle.getCurrentLocation());
                    localizationMonitor.setConvergedParticle(convergeParticle.getCurrentLocation());
                    particleHasConverged = true;
                    localizationMonitor.setParticleHasConverged(true);
                }
                // Set values like particles and the direction
                if(activityMonitoring.getActivity() == Type.WALKING){
                    this.localizationMap.setParticles(this.localizationMonitor.getParticles());
                }
            } else {
                // Set values like particles and the direction
                if(activityMonitoring.getActivity() == Type.WALKING){
                    final Particle convLoc = this.localizationMonitor.getParticles().get(0);
                    this.localizationMap.post(new Runnable() {
                        @Override
                        public void run() {
                            localizationMap.setConvLocation(convLoc);
                        }
                    });
                }
            }

            compassGUI.setAngle(localizationMonitor.getAngle());

            this.compassGUI.post(new Runnable() {
                public void run() {
                    compassGUI.invalidate();
                }
            });

            this.localizationMap.post(new Runnable() {
                public void run() {
                    localizationMap.invalidate();
                }
            });

        }
    }
}
