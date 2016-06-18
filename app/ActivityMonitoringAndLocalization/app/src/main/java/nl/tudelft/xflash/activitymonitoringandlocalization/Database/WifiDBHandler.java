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
public class WifiDBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;
    // Database Name
    private static final String DATABASE_NAME = "wifiData.db";
    // Contacts table name
    private static final String TABLE_WIFI = "wifi";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";
    private static final String KEY_ZONE = "zone";
    private static final String KEY_SSID_0 = "s0";
    private static final String KEY_SSID_1 = "s1";
    private static final String KEY_SSID_2 = "s2";
    private static final String KEY_SSID_3 = "s3";
    private static final String KEY_TIMESTAMP = "time";

    public WifiDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WIFI_TABLE = "CREATE TABLE " + TABLE_WIFI + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_X + " REAL,"
                + KEY_Y + " REAL," + KEY_ZONE + " STRING," + KEY_SSID_0 + " REAL,"
                + KEY_SSID_1 + " REAL," + KEY_SSID_2 + " REAL," + KEY_SSID_3 + " REAL,"
                + KEY_TIMESTAMP + " INTEGER" + ")";
        db.execSQL(CREATE_WIFI_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIFI);
// Creating tables again
        onCreate(db);
    }

    // Adding new data
    public void addWifiData(WifiData wifiData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_X, wifiData.getX());
        values.put(KEY_Y, wifiData.getY());
        values.put(KEY_ZONE, wifiData.getZone());
        values.put(KEY_SSID_0, wifiData.get_ssid_0());
        values.put(KEY_SSID_1, wifiData.get_ssid_1());
        values.put(KEY_SSID_2, wifiData.get_ssid_2());
        values.put(KEY_SSID_3, wifiData.get_ssid_3());
        values.put(KEY_TIMESTAMP, System.currentTimeMillis());
        // Inserting Row
        db.insert(TABLE_WIFI, null, values);
        db.close(); // Closing database connection
    }

// Getting one wifiData
    public WifiData getWifiData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WIFI, new String[]{KEY_ID,
                        KEY_X, KEY_Y, KEY_ZONE, KEY_SSID_0, KEY_SSID_1,
                        KEY_SSID_2, KEY_SSID_3, KEY_TIMESTAMP}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        WifiData wifiData = new WifiData();
        wifiData.setId(Integer.parseInt(cursor.getString(0)));
        wifiData.setX(Double.parseDouble(cursor.getString(1)));
        wifiData.setY(Double.parseDouble(cursor.getString(2)));
        wifiData.setZone(cursor.getString(3));
        wifiData.set_ssid_0(Double.parseDouble(cursor.getString(4)));
        wifiData.set_ssid_1(Double.parseDouble(cursor.getString(5)));
        wifiData.set_ssid_2(Double.parseDouble(cursor.getString(6)));
        wifiData.set_ssid_3(Double.parseDouble(cursor.getString(7)));
        wifiData.setTime(Long.parseLong(cursor.getString(8)));
// return wifiData
        return wifiData;
    }

    // Getting Some WifiData Based on a Certain id Range
    public List<WifiData> getWifiDataIDRange(int startId, int endId) {
        List<WifiData> wifiDataList = new ArrayList<WifiData>();
// Select All Query
        String selectQuery = "SELECT * FROM '" + TABLE_WIFI + "' WHERE id BETWEEN " +
                startId + " AND " + endId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                WifiData wifiData = new WifiData();
                wifiData.setId(Integer.parseInt(cursor.getString(0)));
                wifiData.setX(Double.parseDouble(cursor.getString(1)));
                wifiData.setY(Double.parseDouble(cursor.getString(2)));
                wifiData.setZone(cursor.getString(3));
                wifiData.set_ssid_0(Double.parseDouble(cursor.getString(4)));
                wifiData.set_ssid_1(Double.parseDouble(cursor.getString(5)));
                wifiData.set_ssid_2(Double.parseDouble(cursor.getString(6)));
                wifiData.set_ssid_3(Double.parseDouble(cursor.getString(7)));
                wifiData.setTime(Long.parseLong(cursor.getString(8)));
// Adding wifiData to list
                wifiDataList.add(wifiData);
            } while (cursor.moveToNext());
        }
// return wifiData list
        db.close();
        return wifiDataList;
    }

    // Getting All WifiData
    public List<WifiData> getAllWifiData() {
        List<WifiData> wifiDataList = new ArrayList<WifiData>();
// Select All Query
        String selectQuery = "SELECT * FROM '" + TABLE_WIFI + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                WifiData wifiData = new WifiData();
                wifiData.setId(Integer.parseInt(cursor.getString(0)));
                wifiData.setX(Double.parseDouble(cursor.getString(1)));
                wifiData.setY(Double.parseDouble(cursor.getString(2)));
                wifiData.setZone(cursor.getString(3));
                wifiData.set_ssid_0(Double.parseDouble(cursor.getString(4)));
                wifiData.set_ssid_1(Double.parseDouble(cursor.getString(5)));
                wifiData.set_ssid_2(Double.parseDouble(cursor.getString(6)));
                wifiData.set_ssid_3(Double.parseDouble(cursor.getString(7)));
                wifiData.setTime(Long.parseLong(cursor.getString(8)));
// Adding wifiData to list
                wifiDataList.add(wifiData);
            } while (cursor.moveToNext());
        }

// return wifiData list
        return wifiDataList;
    }
    // Getting WifiData Count
    public long getWifiDataCount() {
        String countQuery = "SELECT COUNT(*) FROM '" + TABLE_WIFI + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteStatement statement = db.compileStatement(countQuery);
        long count = statement.simpleQueryForLong();
        return count;
    }
    // Updating a wifiData
    public int updateWifiData(WifiData wifiData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_X, wifiData.getX());
        values.put(KEY_Y, wifiData.getY());
        values.put(KEY_ZONE, wifiData.getZone());
        values.put(KEY_SSID_0, wifiData.get_ssid_0());
        values.put(KEY_SSID_1, wifiData.get_ssid_1());
        values.put(KEY_SSID_2, wifiData.get_ssid_2());
        values.put(KEY_SSID_3, wifiData.get_ssid_3());
        values.put(KEY_TIMESTAMP, wifiData.getTime());

// updating row
        return db.update(TABLE_WIFI, values, KEY_ID + " = ?",
                new String[]{String.valueOf(wifiData.getId())});
    }

    // Deleting a wifi Data
    public void deleteWifiData(WifiData wifiData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WIFI, KEY_ID + " = ?",
                new String[] { String.valueOf(wifiData.getId()) });
        db.close();
    }

    // Deleting all wifiData
    public void clearWifiData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WIFI, null, null);
        db.close();
    }
}
