package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

import android.provider.BaseColumns;

/**
 * Created by xflash on 4-5-16.
 */
public class DatabaseModel {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "SmartPhoneSensing.db";

    // empty constructor
    public DatabaseModel() {}

    // table for activity monitoring, measurement from accelerometer
    public static abstract class TableAccel implements BaseColumns {
        // BaseColumns additions:
        // String	_COUNT	The count of rows in a directory.
        // String	_ID	The unique ID for a row.

        // Table structure:
        public static final String TAB_NAME              = "accelActivity";
        public static final String COL_NAME_TRIAL        = "trial";      // incremental standing ID
        public static final String COL_NAME_TIMESTAMP    = "timestamp";     // in nanoseconds
        public static final String COL_NAME_ACCURACY     = "accuracy";
        public static final String COL_NAME_ACTIVITY     = "activitytype";    // see MotionType-enum
        public static final String COL_NAME_X            = "x";             // x-acceleration
        public static final String COL_NAME_Y            = "y";             // y-acceleration
        public static final String COL_NAME_Z            = "z";             // z-acceleration

        // Column types
        public static final String COL_TYPE_TRIAL        = "INTEGER";   //int
        public static final String COL_TYPE_TIMESTAMP    = "INTEGER";   //long
        public static final String COL_TYPE_ACCURACY     = "INTEGER";   //int
        public static final String COL_TYPE_ACTIVITY     = "INTEGER";   //int
        public static final String COL_TYPE_X            = "REAL";      //float
        public static final String COL_TYPE_Y            = "REAL";      //float
        public static final String COL_TYPE_Z            = "REAL";      //float
    }
}
