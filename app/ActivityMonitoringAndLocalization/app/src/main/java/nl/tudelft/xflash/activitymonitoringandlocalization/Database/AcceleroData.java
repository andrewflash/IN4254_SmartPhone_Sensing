package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

/**
 * Created by tritronik on 5/26/2016.
 */
public class AcceleroData {
    private int _id;
    private float _accx;
    private float _accy;
    private float _accz;
    private long _time;

    public AcceleroData()
    {
    }

    public AcceleroData(float accx, float accy, float accz)
    {
        this._accx=accx;
        this._accy=accy;
        this._accz=accz;
    }

    public AcceleroData(int id, float accx, float accy, float accz, long time)
    {
        this._id=id;
        this._accx=accx;
        this._accy=accy;
        this._accz=accz;
        this._time=time;
    }

    public void setId(int id) {
        this._id = id;
    }

    public void setAccX(float accx) {
        this._accx=accx;
    }

    public void setAccY(float accy) {
        this._accy=accy;
    }

    public void setAccZ(float accz) {
        this._accz=accz;
    }

    public void setTime(long time) {
        this._time = time;
    }

    public int getId() {
        return this._id;
    }

    public float getAccX() {
        return this._accx;
    }

    public float getAccY() {
        return this._accy;
    }

    public float getAccZ() {
        return this._accz;
    }

    public long getTime() {
        return this._time;
    }
}
