package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by xflash on 4-5-16.
 */
public class DatabaseAPI {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context appContext;

    //init database
    public DatabaseAPI(Context appContext) {
        this.appContext = appContext;
        //create helper
        dbHelper = new DatabaseHelper(appContext);
        //open database
        db = dbHelper.getWritableDatabase();
    }

    //close database
    public void close(){
        db.close();
    }

    //reset database
    public void reset() {
        dbHelper.reset(db);
    }

    //reset Accelerometer2 Activity
    public void resetAccelActivity() {
        db.execSQL("DELETE FROM "+ DatabaseModel.TableAccel.TAB_NAME);
    }

    //retrieve highest number of trials in database
    public int getMaxTrials() {
        int trial = 0;

        //setup query
        String query = "";
        query += "SELECT MAX(" + DatabaseModel.TableAccel.COL_NAME_TRIAL + ") as maxtrial";
        query += " FROM " + DatabaseModel.TableAccel.TAB_NAME;

        //send query to db
        Cursor c = db.rawQuery(query, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            trial = c.getInt(c.getColumnIndexOrThrow("maxtrial"));
        }
        c.close();
        return trial;
    }

    // retrieve number of records in the database
    public int getRecordCount() {
        int count = 0;

        //setup query
        String query = "";
        query += "SELECT COUNT(*) as count";
        query += " FROM " + DatabaseModel.TableAccel.TAB_NAME;

        //send query to db
        Cursor c = db.rawQuery(query, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            count = c.getInt(c.getColumnIndexOrThrow("count"));
        }
        c.close();
        return count;

    }

    //insert record of accelerometer readings for activity classification
    public long insertAccel(RecordActivity record){
        String tablename     = DatabaseModel.TableAccel.TAB_NAME;
        ContentValues values = record.toContentValues();
        return db.insert(tablename, null, values);
    }


    // Logging
    private String logname;

    //export accelerometer activity
    public void exportTableAccel() {
        //create filename
        logname = "log_accelActivity" + System.currentTimeMillis();

        //retrieve data
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseModel.TableAccel.TAB_NAME, null);

        //print column headers (same order as values!!)
        appendLog("timestamp,trial,accuracy,activityType,x,y,z");

        //iterate over data
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String text = "";
            text += c.getLong(c.getColumnIndexOrThrow(DatabaseModel.TableAccel.COL_NAME_TIMESTAMP));
            text += ",";
            text += c.getInt(c.getColumnIndexOrThrow(DatabaseModel.TableAccel.COL_NAME_TRIAL));
            text += ",";
            text += c.getInt(c.getColumnIndexOrThrow(DatabaseModel.TableAccel.COL_NAME_ACCURACY));
            text += ",";
            text += c.getInt(c.getColumnIndexOrThrow(DatabaseModel.TableAccel.COL_NAME_ACTIVITY));
            text += ",";
            text += c.getFloat(c.getColumnIndexOrThrow(DatabaseModel.TableAccel.COL_NAME_X));
            text += ",";
            text += c.getFloat(c.getColumnIndexOrThrow(DatabaseModel.TableAccel.COL_NAME_Y));
            text += ",";
            text += c.getFloat(c.getColumnIndexOrThrow(DatabaseModel.TableAccel.COL_NAME_Z));
            appendLog(text);
        }
        c.close();
    }

    public void appendLog(String text)
    {
        File logFile = new File("sdcard/" + logname + ".txt");

        if (!logFile.exists())
        {
            try
            {
                Toast toast = Toast.makeText(this.appContext, "created new log", Toast.LENGTH_SHORT);
                toast.show();
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
