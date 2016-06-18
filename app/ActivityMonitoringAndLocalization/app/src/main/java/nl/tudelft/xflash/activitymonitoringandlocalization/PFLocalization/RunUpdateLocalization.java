package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityMonitoring;
import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.WifiDBHandler;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.WifiData;
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

    private LocalizationMonitor localizationMonitor;

    private VisitedPath visitedPath;
    private Location convergedLoc;
    private boolean particleHasConverged;

    private LocalizationMap localizationMap;
    private CompassGUI compassGUI;

    private Context context;

    private int stepCount;

    public RunUpdateLocalization(float angle, LocalizationMonitor locMon,
                                 LocalizationMap locMap, CompassGUI compGUI, int stepCount, Context context)
    {
        this.angle = angle;
        this.localizationMonitor = locMon;
        this.localizationMap = locMap;
        this.compassGUI = compGUI;
        this.stepCount = stepCount;
        this.visitedPath = VisitedPath.getInstance();
        this.particleHasConverged = false;
        this.context = context;
    }

    public Context getAppContext() {
        return this.context;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        // Update localization monitor
        if (this.localizationMonitor.update(angle,stepCount)) {
            // Check for convergence of particles
            if(!particleHasConverged) {
                convergedLoc = localizationMonitor.particleConverged();
                if (convergedLoc != null) {
                    final Particle convergeParticle = localizationMonitor.forceConverge();
                    visitedPath.setPathVisited(convergeParticle.getCurrentLocation());
                    this.localizationMap.post(new Runnable() {
                        @Override
                        public void run() {
                            localizationMap.setConvLocation(convergeParticle);
                        }
                    });
                    localizationMonitor.setConvergedParticle(convergeParticle.getCurrentLocation());

                    // storing RSSI values to database
                    WifiData wifiData = new WifiData();
                    wifiData.setX(convergeParticle.getCurrentLocation().getX());
                    wifiData.setY(convergeParticle.getCurrentLocation().getX());
                    wifiData.setZone();
                    wifiData.set_ssid_0();
                    wifiData.set_ssid_1();
                    wifiData.set_ssid_2();
                    wifiData.set_ssid_3();

                    AddWifiData addWifiData = new AddWifiData(wifiData);
                    addWifiData.execute();

                    particleHasConverged = true;
                    localizationMonitor.setParticleHasConverged(true);
                }
                // Set values of particles and direction
                if(stepCount != 0){
                    this.localizationMap.setParticles(this.localizationMonitor.getParticles());
                }
            }

            localizationMap.post(new Runnable() {
                @Override
                public void run() {
                    localizationMap.invalidate();
                }
            });
        }

        compassGUI.setAngle(angle);

        compassGUI.post(new Runnable() {
            @Override
            public void run() {
                compassGUI.invalidate();
            }
        });
    }

    private class ClearWifiData extends AsyncTask<Object, Object, Object> {
        WifiDBHandler dbConnector = new WifiDBHandler(getAppContext());

        public ClearWifiData() { return; }

        @Override
        protected Object doInBackground(Object... params) {
            // Open the database
            dbConnector.clearWifiData();
            return null;
        }
    }

    private class AddWifiData extends AsyncTask<Object, Object, Object> {
        WifiDBHandler dbConnector = new WifiDBHandler(getAppContext());
        WifiData wifiData_this;

        public AddWifiData(WifiData wifiData) {
            wifiData_this = wifiData;
        }

        @Override
        protected Object doInBackground(Object... params) {
            // Open the database
            dbConnector.addWifiData(wifiData_this);
            return null;
        }
    }

}