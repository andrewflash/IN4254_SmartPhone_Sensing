package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

import android.content.ContentValues;

/**
 * Created by xflash on 4-5-16.
 */
public class RecordActivity {

    private int trial;
    private long timestamp;
    private int accuracy;
    private int activityType;
    private float x;
    private float y;
    private float z;

    // empty constructor
    public RecordActivity() {
    }

    // constructor with all data
    public RecordActivity(int trial, long timestamp, int accuracy, int activityType, float x, float y, float z) {
        this.accuracy = accuracy;
        this.activityType = activityType;
        this.timestamp = timestamp;
        this.trial = trial;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
