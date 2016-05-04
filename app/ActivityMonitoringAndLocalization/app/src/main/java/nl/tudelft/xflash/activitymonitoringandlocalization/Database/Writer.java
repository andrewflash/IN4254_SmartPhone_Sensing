package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

import android.os.Environment;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;

/**
 * Created by xflash on 4-5-16.
 */
public class Writer {
    private static final String wr_e = "Writer";

    private File file;
    private DataOutputStream fOutStream;
    private Date date;
    private Date startTime;

    public Writer(String fileName){
        file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);
        file.setReadable(true);

        this.date = new Date();
        this.startTime = new Date();

        try {
            fOutStream = new DataOutputStream(new FileOutputStream(file));
            Log.d(wr_e, file.getAbsolutePath());
        } catch(FileNotFoundException e ){
            e.printStackTrace();
        }
    }

    /**
     * Used to write data for the accelerometer
     * @param x
     * @param y
     * @param z
     * @param state
     */
    public void appendData(float x, float y, float z,  Type state) {
        try{
            date = new Date();
            long delta = date.getTime() - startTime.getTime();

            fOutStream.write((delta + " " + state.toString() + " " + x + " " + y + " " + z + "\n").getBytes());
            fOutStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // save wifi data
    public void appendWifiData(String BSSID, Integer RSSI, String cell) {
        try{
            date = new Date();
            long delta = date.getTime() - startTime.getTime();

            fOutStream.write((delta + " " + cell.toString() + " " + BSSID + " " + RSSI + "\n").getBytes());
            fOutStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Can be used to write everything in string form to a file.
     * @param msg
     */
    public void appendString(String msg){
        try{
            fOutStream.write((msg + "\n").getBytes());
            fOutStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // clear data
    public boolean clearData() {
       try {
           if(file.exists()) {
               FileOutputStream writer = new FileOutputStream(file);
               writer.write("".getBytes());
               return true;
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
       return false;
    }
}
