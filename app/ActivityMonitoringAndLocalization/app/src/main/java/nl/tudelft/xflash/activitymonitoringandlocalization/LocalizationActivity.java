package nl.tudelft.xflash.activitymonitoringandlocalization;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import nl.tudelft.xflash.activitymonitoringandlocalization.R;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.WiFi;

public class LocalizationActivity extends AppCompatActivity /*implements Observer*/ {
/*
    private int WINDOW_SIZE;            // set window size for Wifi data
    private final int SCAN_INTERVAL = 5000;

    private WifiManager wifiManager;
    private WiFi wifiReceiver;

    private TextView txtLocalTime;
    private Button btnScan;
    private TableLayout tlWifi;

    //private long timeStart;
    //private long numSamples = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localization_menu);

        txtLocalTime = (TextView) findViewById(R.id.txtLocalTime);
        //btnRssi = (Button) findViewById(R.id.btnRSSI);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // Turn on WIFI
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        wifiReceiver = new WiFi(wifiManager);
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiReceiver.getObservable().addObserver(this);

        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifiReceiver.getObservable().mySetChanged();
                wifiManager.startScan();
                btnScan.setText("Scanning");
                btnScan.setEnabled(false);
                btnScan.setClickable(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(wifiReceiver);
        }
        catch (Exception e){}
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void update(Observable observable, Object o) {
        ArrayList<String> lstWifiPoints = wifiReceiver.getWifiPoints();
        ArrayList<ArrayList<Integer>> lstRSSI = wifiReceiver.getRSSI();

        tlWifi = (TableLayout) findViewById(R.id.tblWifiData);

        for(int i = 0; i < lstWifiPoints.size(); i++) {
            // Create a TableRow and give it an ID
            TableRow tr = new TableRow(this);
            tr.setId(100+i);
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            // Create a TextView for WifiPoint
            TextView lblWifiData = new TextView(this);
            lblWifiData.setId(200+i);
            lblWifiData.setText(lstWifiPoints.get(i));
            lblWifiData.setTextColor(Color.BLACK);
            lblWifiData.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(lblWifiData);

            // Create a TextView for RSSI
            TextView lblRSSIData = new TextView(this);
            lblRSSIData.setId(300+i);
            lblRSSIData.setText(lstRSSI.get(i).toString());
            lblRSSIData.setTextColor(Color.BLACK);
            lblRSSIData.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(lblRSSIData);

            // Add the TableRow to the TableLayout
            tlWifi.addView(tr, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
        }

        txtLocalTime.setText(System.currentTimeMillis() + " ms");
    } */
}