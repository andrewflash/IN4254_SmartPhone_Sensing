package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import java.util.ArrayList;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityMonitoring;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.Location;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter.Particle;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.CompassGUI;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.LocalizationMap;

/**
 * Created by xflash on 29-5-16.
 */
public class RunUpdate implements Runnable {

    private ArrayList<Float> accelX = new ArrayList<>();
    private ArrayList<Float> accelY = new ArrayList<>();
    private ArrayList<Float> accelZ = new ArrayList<>();
    private ArrayList<Float> orienX = new ArrayList<>();
    private ArrayList<Float> orienY = new ArrayList<>();
    private ArrayList<Float> orienZ = new ArrayList<>();

    private ActivityMonitoring activityMonitoring;
    private LocalizationMonitor localizationMonitor;

    private LocalizationMap localizationMap;
    private CompassGUI compassGUI;

    private float dT;

    public RunUpdate(ArrayList<Float> accelX, ArrayList<Float> accelY, ArrayList<Float> accelZ,
                       ArrayList<Float> orienX, ArrayList<Float> orienY, ArrayList<Float> orienZ,
                       ActivityMonitoring acMon, LocalizationMonitor locMon,
                       LocalizationMap locMap, CompassGUI compGUI, float dT)
    {
        this.accelX = (ArrayList<Float>) accelX.clone();
        this.accelY = (ArrayList<Float>) accelY.clone();
        this.accelZ = (ArrayList<Float>) accelZ.clone();
        this.orienX = (ArrayList<Float>) orienX.clone();
        this.orienY = (ArrayList<Float>) orienY.clone();
        this.orienZ = (ArrayList<Float>) orienZ.clone();
        this.activityMonitoring = acMon;
        this.localizationMonitor = locMon;
        this.localizationMap = locMap;
        this.compassGUI = compGUI;
        this.dT = dT;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        // Update activity monitor
        this.activityMonitoring.update(accelX, accelY, accelZ);

        // Update localization monitor
        if (this.localizationMonitor.update(orienX,orienY,orienZ,dT)) {

            // Check for convergence and change the color of particles
            final Location convergedLoc = localizationMonitor.particleConverged();
            if(convergedLoc != null){
                final Particle convergeLocation = localizationMonitor.forceConverge();
                this.localizationMap.post(new Runnable() {
                    @Override
                    public void run() {
                        localizationMap.setConvLocation(convergeLocation);
                    }
                });

                //walkedPath.setPath(convergedLoc);
            };

            // Set values like particles and the direction
            if(activityMonitoring.getActivity() == Type.WALKING){
                this.localizationMap.setParticles(this.localizationMonitor.getParticles());
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
