package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout;

/**
 * Created by xflash on 28-5-16.
 */
public class Location {

    private float x, y;

    // First constructor, create using x and y coord.
    public Location(float x, float y){
        this.x = x;
        this.y = y;
    }

    // Second constructor, create using new location info
    public Location(Location newLocation){
        this(newLocation.getX(), newLocation.getY());
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(Location newLocation){
        this.x = newLocation.getX();
        this.y = newLocation.getY();
    }

    public boolean isEqual(Location newLocation) {
        if(newLocation.getX() == this.x && newLocation.getY() == this.y)
            return true;
        else
            return false;
    }

    public void translate(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }
}
