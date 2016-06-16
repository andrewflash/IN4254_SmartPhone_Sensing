package nl.tudelft.xflash.activitymonitoringandlocalization.Sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;

/**
 * Created by xflash on 4-5-16.
 */
public class WiFi extends BroadcastReceiver {
    private WifiManager wifi;
    private ArrayList<String> wifiPoints;
    private ArrayList<ArrayList<Integer>> RSSI;
    private WifiObservable o;
    private Comparator<ScanResult> comparator;
    private static final int MAX_AP = 10;

    public WiFi(WifiManager wifi) {
        super();
        this.wifi = wifi;
        wifiPoints = new ArrayList<>();
        RSSI = new ArrayList<>();
        o = new WifiObservable();

        comparator = new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return (lhs.level <rhs.level ? -1 : (lhs.level==rhs.level ? 0 : 1));
            }
        };
    }

    // RSSI list (AP1: RSSI-1,RSSI-2,... ; AP2: RSSI-1,RSSI-2,... )
    public ArrayList<ArrayList<Integer>> getRSSI() {
        return this.RSSI;
    }

    public ArrayList<String> getWifiPoints() {
        return this.wifiPoints;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        List<ScanResult> results = wifi.getScanResults();
        try {
            Collections.sort(results, comparator);

            ArrayList<Integer> out = new ArrayList<>();
            // Initialize wifiPoints with 0
            for (int j = 0; j < wifiPoints.size(); j++) {
                out.add(0);
            }
            // Extract info from scan results
            for (int n = 0; n < results.size(); n++) {
                //if(n >= MAX_AP) break;

                // Search for BSSID in wifiPoints to check if we already know the wifi
                int i = wifiPoints.lastIndexOf(results.get(n).BSSID);
                if (i >= 0) {
                    //already know this wifi point, just add RSSI to the existing wifi point
                    out.set(i, results.get(n).level);
                } else {
                    //add new wifi point
                    wifiPoints.add(results.get(n).BSSID);
                    out.add(results.get(n).level);
                    for (ArrayList<Integer> t : RSSI) {
                        t.add(0);
                    }
                }
            }
            Log.d(this.getClass().getSimpleName(),"WifiPoints: " + wifiPoints.toString());
            Log.d(this.getClass().getSimpleName(),"WifiRSSI: " + out.toString());

            RSSI.add(out);
            o.notifyObservers();
        } catch (Exception e) {
            Log.i(this.getClass().getSimpleName(), "exception " + e.toString());
        }
    }

    public void clear() {
        this.RSSI.clear();
        this.wifiPoints.clear();
    }

    public WifiObservable getObservable() {
        return o;
    }

    public class WifiObservable extends Observable {
        public WifiObservable() {
            super();
        }

        public void mySetChanged() {
            this.setChanged();
        }
    }
}