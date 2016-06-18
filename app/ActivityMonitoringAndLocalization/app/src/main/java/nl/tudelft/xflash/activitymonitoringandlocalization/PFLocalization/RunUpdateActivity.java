package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import android.util.Log;

import java.util.ArrayList;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.ActivityMonitoring;

/**
 * Created by xflash on 29-5-16.
 */
public class RunUpdateActivity implements Runnable {

    private ArrayList<Float> accelX = new ArrayList<>();
    private ArrayList<Float> accelY = new ArrayList<>();
    private ArrayList<Float> accelZ = new ArrayList<>();

    private ActivityMonitoring activityMonitoring;

    public RunUpdateActivity(ArrayList<Float> accelX, ArrayList<Float> accelY, ArrayList<Float> accelZ,
                             ActivityMonitoring acMon)
    {
        this.accelX = (ArrayList<Float>) accelX.clone();
        this.accelY = (ArrayList<Float>) accelY.clone();
        this.accelZ = (ArrayList<Float>) accelZ.clone();
        this.activityMonitoring = acMon;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        // Update activity monitor
        this.activityMonitoring.update(accelX, accelY, accelZ);
    }
}
