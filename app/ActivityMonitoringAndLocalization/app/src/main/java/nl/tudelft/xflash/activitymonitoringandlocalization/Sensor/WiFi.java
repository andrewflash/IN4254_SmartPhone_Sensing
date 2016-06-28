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
    private List<ScanResult> wifiResults;

    private WifiObservable o;
    private Comparator<ScanResult> comparator;
    private static final int MAX_AP = 30;

    public WiFi(WifiManager wifi) {
        super();
        this.wifi = wifi;
        o = new WifiObservable();

        comparator = new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return (lhs.level <rhs.level ? -1 : (lhs.level==rhs.level ? 0 : 1));
            }
        };
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        List<ScanResult> results = wifi.getScanResults();

        try {
            Collections.sort(results, comparator);

            if(results.size() > MAX_AP) {
                wifiResults = results.subList(0, MAX_AP);
            } else {
                wifiResults = results;
            }

            o.notifyObservers(wifiResults);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "exception " + e.toString());
        }
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