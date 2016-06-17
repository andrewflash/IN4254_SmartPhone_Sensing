package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

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
public class RunUpdateLocalization implements Runnable {

    private float angle;

    private ActivityMonitoring activityMonitoring;
    private LocalizationMonitor localizationMonitor;

    private VisitedPath visitedPath;
    private Location convergedLoc;
    private boolean particleHasConverged;

    private LocalizationMap localizationMap;
    private CompassGUI compassGUI;

    private float dT;

    public RunUpdateLocalization(float angle, ActivityMonitoring acMon, LocalizationMonitor locMon,
                                 LocalizationMap locMap, CompassGUI compGUI, float dT)
    {
        this.angle = angle;
        this.localizationMonitor = locMon;
        this.localizationMap = locMap;
        this.compassGUI = compGUI;
        this.dT = dT;
        this.visitedPath = VisitedPath.getInstance();
        this.particleHasConverged = false;
        this.activityMonitoring = acMon;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        // Update localization monitor
        if (this.localizationMonitor.update(angle,dT)) {
            // Check for convergence of particles
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
                // Set values of particles and direction
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
