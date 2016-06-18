package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

/**
 * Created by tritronik on 5/26/2016.
 */
public class WifiData {
    private int _id;

    private double x;
    private double y;
    private String zone;
    private String _ssid;
    private long _time;

    public WifiData()
    {
    }

    public WifiData(double x, double y, String zone, String _ssid)
    {
        this.x=x;
        this.y=y;
        this.zone=zone;
        this._ssid=_ssid;
    }

    public WifiData(int id, double x, double y, String zone, String _ssid, long time)
    {
        this._id=id;
        this.x=x;
        this.y=y;
        this.zone=zone;
        this._ssid=_ssid;
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

    public void set_ssid(String _ssid) {
        this._ssid=_ssid;
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

    public String get_ssid() {
        return this._ssid;
    }

    public String getZone() {
        return this.zone;
    }

    public long getTime() {
        return this._time;
    }
}
