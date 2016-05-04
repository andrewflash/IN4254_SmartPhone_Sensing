package nl.tudelft.xflash.activitymonitoringandlocalization;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Localization extends AppCompatActivity {

    private int WINDOW_SIZE;            // set window size for Wifi data
    private final int SCAN_INTERVAL = 5000;

    private WifiManager wifiManager;
    private WifiInfo wifiInfo;

    private TextView txtRssi;
    //Button btnRssi;

    //private long timeStart;
    //private long numSamples = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_menu);

        txtRssi = (TextView) findViewById(R.id.txtRSSI);
        //btnRssi = (Button) findViewById(R.id.btnRSSI);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

                wifiInfo = wifiManager.getConnectionInfo();
                txtRssi.setText("\n\tSSID = " + wifiInfo.getSSID()
                        + "\n\tRSSI = " + wifiInfo.getRssi()
                        + "\n\tLocal Time = " + System.currentTimeMillis());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkStateReceiver = new NetworkStateReceiver(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        this.registerReceiver(networkStateReceiver, new IntentFilter("android.net.wifi.SCAN_RESULTS"));
        networkStateReceiver.registerListener(this);
    }


}