package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

/**
 * Created by xflash on 28-5-16.
 */
public class Wall {
    private Location startPoint, endPoint;

    public Wall(Location startPoint, Location endPoint){
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Wall(float startX, float startY, float endX, float endY) {
        this(new Location(startX, startY), new Location(endX, endY));
    }

    public Location getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Location endPoint) {
        this.endPoint = endPoint;
    }

    public Location getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Location startPoint) {
        this.startPoint = startPoint;
    }
}