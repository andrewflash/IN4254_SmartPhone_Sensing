package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xflash on 4-5-16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String COMMA = ",";
    private static final String SPACE = " ";

    // Activity database
    private static final String SQL_CREATE_TABLE_ACCEL =
            "CREATE TABLE " + DatabaseModel.TableAccel.TAB_NAME +
                    " (" +
                    DatabaseModel.TableAccel._ID + SPACE +
                    "INTEGER PRIMARY KEY" + COMMA + SPACE +
                    DatabaseModel.TableAccel.COL_NAME_TRIAL + SPACE +
                    DatabaseModel.TableAccel.COL_NAME_TRIAL + COMMA + SPACE +
                    DatabaseModel.TableAccel.COL_NAME_TIMESTAMP + SPACE +
                    DatabaseModel.TableAccel.COL_TYPE_TIMESTAMP + COMMA + SPACE +
                    DatabaseModel.TableAccel.COL_NAME_ACCURACY + SPACE +
                    DatabaseModel.TableAccel.COL_TYPE_ACCURACY + COMMA + SPACE +
                    DatabaseModel.TableAccel.COL_NAME_ACTIVITY + SPACE +
                    DatabaseModel.TableAccel.COL_NAME_ACTIVITY + COMMA + SPACE +
                    DatabaseModel.TableAccel.COL_NAME_X + SPACE +
                    DatabaseModel.TableAccel.COL_TYPE_X + COMMA + SPACE +
                    DatabaseModel.TableAccel.COL_NAME_Y + SPACE +
                    DatabaseModel.TableAccel.COL_TYPE_Y + COMMA + SPACE +
                    DatabaseModel.TableAccel.COL_NAME_Z + SPACE +
                    DatabaseModel.TableAccel.COL_TYPE_Z +
                    " )";

    private static final String SQL_DELETE_TABLE_ACCEL =
            "DROP TABLE IF EXISTS " + DatabaseModel.TableAccel.TAB_NAME;

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DatabaseModel.DATABASE_NAME, null, DatabaseModel.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_ACCEL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_TABLE_ACCEL);
        db.execSQL(SQL_CREATE_TABLE_ACCEL);
    }

    //reset database
    public void reset(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_TABLE_ACCEL);
        onCreate(db);
    }
}