package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xflash on 4-5-16.
 */
public class AcceleroDBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;
    // Database Name
    private static final String DATABASE_NAME = "acceleroData.db";
    // Contacts table name
    private static final String TABLE_ACCELERO = "accelero";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ACCX = "accx";
    private static final String KEY_ACCY = "accy";
    private static final String KEY_ACCZ = "accz";
    private static final String KEY_TIMESTAMP = "time";

    public AcceleroDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ACCELERO_TABLE = "CREATE TABLE " + TABLE_ACCELERO + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ACCX + " REAL,"
                + KEY_ACCY + " REAL," + KEY_ACCZ + " REAL," + KEY_TIMESTAMP + " INTEGER" + ")";
        db.execSQL(CREATE_ACCELERO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCELERO);
// Creating tables again
        onCreate(db);
    }

    // Adding new data
    public void addAcceleroData(AcceleroData acceleroData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACCX, acceleroData.getAccX());
        values.put(KEY_ACCY, acceleroData.getAccY());
        values.put(KEY_ACCZ, acceleroData.getAccZ());
        values.put(KEY_TIMESTAMP, System.currentTimeMillis());
        // Inserting Row
        db.insert(TABLE_ACCELERO, null, values);
        db.close(); // Closing database connection
    }

// Getting one acceleroData
    public AcceleroData getAcceleroData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACCELERO, new String[]{KEY_ID,
                        KEY_ACCX, KEY_ACCY, KEY_ACCZ, KEY_TIMESTAMP}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        AcceleroData acceleroData = new AcceleroData();
        acceleroData.setId(Integer.parseInt(cursor.getString(0)));
        acceleroData.setAccX(Float.parseFloat(cursor.getString(1)));
        acceleroData.setAccY(Float.parseFloat(cursor.getString(2)));
        acceleroData.setAccZ(Float.parseFloat(cursor.getString(3)));
        acceleroData.setTime(Long.parseLong(cursor.getString(4)));
// return acceleroData
        return acceleroData;
    }

    // Getting Some AcceleroData Based on a Certain id Range
    public List<AcceleroData> getAcceleroDataIDRange(int startId, int endId) {
        List<AcceleroData> acceleroDataList = new ArrayList<AcceleroData>();
// Select All Query
        String selectQuery = "SELECT * FROM '" + TABLE_ACCELERO + "' WHERE id BETWEEN " +
                startId + " AND " + endId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AcceleroData acceleroData = new AcceleroData();
                acceleroData.setId(Integer.parseInt(cursor.getString(0)));
                acceleroData.setAccX(Float.parseFloat(cursor.getString(1)));
                acceleroData.setAccY(Float.parseFloat(cursor.getString(2)));
                acceleroData.setAccZ(Float.parseFloat(cursor.getString(3)));
                acceleroData.setTime(Long.parseLong(cursor.getString(4)));
// Adding acceleroData to list
                acceleroDataList.add(acceleroData);
            } while (cursor.moveToNext());
        }
// return acceleroData list
        db.close();
        return acceleroDataList;
    }

    // Getting All AcceleroData
    public List<AcceleroData> getAllAcceleroData() {
        List<AcceleroData> acceleroDataList = new ArrayList<AcceleroData>();
// Select All Query
        String selectQuery = "SELECT * FROM '" + TABLE_ACCELERO + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AcceleroData acceleroData = new AcceleroData();
                acceleroData.setId(Integer.parseInt(cursor.getString(0)));
                acceleroData.setAccX(Float.parseFloat(cursor.getString(1)));
                acceleroData.setAccY(Float.parseFloat(cursor.getString(2)));
                acceleroData.setAccZ(Float.parseFloat(cursor.getString(3)));
                acceleroData.setTime(Long.parseLong(cursor.getString(4)));
// Adding acceleroData to list
                acceleroDataList.add(acceleroData);
            } while (cursor.moveToNext());
        }

// return acceleroData list
        return acceleroDataList;
    }
    // Getting AcceleroData Count
    public long getAcceleroDataCount() {
        String countQuery = "SELECT COUNT(*) FROM '" + TABLE_ACCELERO + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteStatement statement = db.compileStatement(countQuery);
        long count = statement.simpleQueryForLong();
        return count;
    }
    // Updating an acceleroData
    public int updateAcceleroData(AcceleroData acceleroData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACCX, acceleroData.getAccX());
        values.put(KEY_ACCY, acceleroData.getAccY());
        values.put(KEY_ACCZ, acceleroData.getAccZ());
        values.put(KEY_TIMESTAMP, acceleroData.getTime());

// updating row
        return db.update(TABLE_ACCELERO, values, KEY_ID + " = ?",
                new String[]{String.valueOf(acceleroData.getId())});
    }

    // Deleting an acceleroData
    public void deleteAcceleroData(AcceleroData acceleroData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCELERO, KEY_ID + " = ?",
                new String[] { String.valueOf(acceleroData.getId()) });
        db.close();
    }

    // Deleting all acceleroData
    public void clearAcceleroData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCELERO, null, null);
        db.close();
    }
}
