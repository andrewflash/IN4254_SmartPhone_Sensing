package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

/**
 * Created by tritronik on 5/26/2016.
 */
public class WifiData {
    private int _id;

    private double x;
    private double y;
    private String zone;
    private double _ssid_0;
    private double _ssid_1;
    private double _ssid_2;
    private double _ssid_3;
    private long _time;

    public WifiData()
    {
    }

    public WifiData(double x, double y, String zone, double _ssid_0, double _ssid_1,
                    double _ssid_2, double _ssid_3)
    {
        this.x=x;
        this.y=y;
        this.zone=zone;
        this._ssid_0=_ssid_0;
        this._ssid_1=_ssid_1;
        this._ssid_2=_ssid_2;
        this._ssid_3=_ssid_3;
    }

    public WifiData(int id, double x, double y, String zone, double _ssid_0, double _ssid_1,
                    double _ssid_2, double _ssid_3, long time)
    {
        this._id=id;
        this.x=x;
        this.y=y;
        this.zone=zone;
        this._ssid_0=_ssid_0;
        this._ssid_1=_ssid_1;
        this._ssid_2=_ssid_2;
        this._ssid_3=_ssid_3;
        this._time=time;
    }

    public void setId(int id) {
        this._id = id;
    }

    public void setX(double x) {
        this.x=x;
    }

    public void setY(double y) {
        this.y=y;
    }

    public void setZone(String zone) {
        this.zone=zone;
    }

    public void set_ssid_0(double _ssid_0) {
        this._ssid_0=_ssid_0;
    }

    public void set_ssid_1(double _ssid_1) {
        this._ssid_1=_ssid_1;
    }

    public void set_ssid_2(double _ssid_2) {
        this._ssid_2=_ssid_2;
    }

    public void set_ssid_3(double _ssid_3) {
        this._ssid_3=_ssid_3;
    }

    public void setTime(long time) {
        this._time = time;
    }

    public int getId() {
        return this._id;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double get_ssid_0() {
        return this._ssid_0;
    }

    public double get_ssid_1() {
        return this._ssid_1;
    }

    public double get_ssid_2() {
        return this._ssid_2;
    }

    public double get_ssid_3() {
        return this._ssid_3;
    }

    public String getZone() {
        return this.zone;
    }

    public long getTime() {
        return this._time;
    }
}
