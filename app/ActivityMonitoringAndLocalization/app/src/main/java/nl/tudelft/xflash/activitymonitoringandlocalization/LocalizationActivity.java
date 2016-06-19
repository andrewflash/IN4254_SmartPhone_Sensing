package nl.tudelft.xflash.activitymonitoringandlocalization;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import nl.tudelft.xflash.activitymonitoringandlocalization.Database.Writer;
import nl.tudelft.xflash.activitymonitoringandlocalization.Sensor.WiFi;

public class LocalizationActivity extends AppCompatActivity implements Observer {

    // WiFi
    private WifiManager wifiManager;
    private WiFi wifiReceiver;

    // Writer
    private Writer writeAccel;

    // Flag
    private boolean initWifi = false;

    // View
    private TextView txtLocalTime;
    private Button btnScan, btnClearData;
    private Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b10;
    private TextView lstWifi;
    private Resources res;
    private Observable obs;

    private String cellType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localization_menu);

        txtLocalTime = (TextView) findViewById(R.id.txtLocalTime);
        //btnRssi = (Button) findViewById(R.id.btnRSSI);

        // Init View
        res = this.getResources();
        lstWifi = (TextView) findViewById(R.id.lstWifi);
        cellType = "NONE";

        // Init Wifi and buttons
        initWifiAndButtons();
    }

    private void initWifiAndButtons(){
        String wifiFileLocation = res.getString(R.string.wifi_data_file);
        writeAccel = new Writer(wifiFileLocation);

        //Init wifi
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiReceiver = new WiFi(wifiManager);
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiReceiver.getObservable().addObserver(this);

        // Init buttons
        btnScan = (Button) findViewById(R.id.btnScan);
        b1 = (Button) findViewById(R.id.btnCell1);
        b2 = (Button) findViewById(R.id.btnCell2);
        b3 = (Button) findViewById(R.id.btnCell3);
        b4 = (Button) findViewById(R.id.btnCell4);
        b5 = (Button) findViewById(R.id.btnCell5);
        b6 = (Button) findViewById(R.id.btnCell6);
        b7 = (Button) findViewById(R.id.btnCell7);
        b8 = (Button) findViewById(R.id.btnCell8);
        b9 = (Button) findViewById(R.id.btnCell9);
        b10 = (Button) findViewById(R.id.btnCell10);

        btnScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cellType = "NONE";
                wifiReceiver.getObservable().mySetChanged();
                wifiManager.startScan();
            }
        });

        // Clear data
        btnClearData = (Button) findViewById(R.id.btnClearData);
        btnClearData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                CharSequence msg;
                Context context = getApplicationContext();
                if (writeAccel.clearData()) {
                    msg = "Data has been cleared";
                } else {
                    msg = "Data could not be cleared";
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });

        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cellType = "CELL_0";
                wifiReceiver.getObservable().mySetChanged();
                wifiManager.startScan();
                CharSequence msg = "Wifi signal on Cell 0 has been scanned.";
                Context context = getApplicationContext();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });

        b2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cellType = "CELL_1";
                wifiReceiver.getObservable().mySetChanged();
                wifiManager.startScan();
                CharSequence msg = "Wifi signal on Cell 1 has been scanned.";
                Context context = getApplicationContext();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });

        b3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cellType = "CELL_2";
                wifiReceiver.getObservable().mySetChanged();
                wifiManager.startScan();
                CharSequence msg = "Wifi signal on Cell 2 has been scanned.";
                Context context = getApplicationContext();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });

        b4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cellType = "CELL_3";
                wifiReceiver.getObservable().mySetChanged();
                wifiManager.startScan();
                CharSequence msg = "Wifi signal on Cell 3 has been scanned.";
                Context context = getApplicationContext();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });

        b5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cellType = "CELL_4";
                wifiReceiver.getObservable().mySetChanged();
                wifiManager.startScan();
                CharSequence msg = "Wifi signal on Cell 4 has been scanned.";
                Context context = getApplicationContext();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });

        b6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cellType = "CELL_5";
                wifiReceiver.getObservable().mySetChanged();
                wifiManager.startScan();
                CharSequence msg = "Wifi signal on Cell 5 has been scanned.";
                Context context = getApplicationContext();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });

        b7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cellType = "CELL_6";
                wifiReceiver.getObservable().mySetChanged();
                wifiManager.startScan();
                CharSequence msg = "Wifi signal on Cell 6 has been scanned.";
                Context context = getApplicationContext();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });

        b8.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cellType = "CELL_7";
                wifiReceiver.getObservable().mySetChanged();
                wifiManager.startScan();
                CharSequence msg = "Wifi signal on Cell 7 has been scanned.";
                Context context = getApplicationContext();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });

        b9.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cellType = "CELL_8";
                wifiReceiver.getObservable().mySetChanged();
                wifiManager.startScan();
                CharSequence msg = "Wifi signal on Cell 8 has been scanned.";
                Context context = getApplicationContext();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });

        b10.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                cellType = "CELL_9";
                wifiReceiver.getObservable().mySetChanged();
                wifiManager.startScan();
                CharSequence msg = "Wifi signal on Cell 9 has been scanned.";
                Context context = getApplicationContext();
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(wifiReceiver);
        } catch (Exception e) {

        }
    }


    @Override
    public void update(Observable observable, Object data) {
        String temp = "";

        temp = "BSSID\t\t\t\t\t\t\t\t\t\tRSSI\n";
        List<ScanResult> wifiList = wifiManager.getScanResults();
        for(ScanResult iScan : wifiList) {
            temp = temp + iScan.BSSID + "\t\t\t\t" + iScan.level + "\n";
            writeAccel.appendWifiData(iScan.BSSID,iScan.level,cellType);
        }
        lstWifi.setText(temp);
   }
}