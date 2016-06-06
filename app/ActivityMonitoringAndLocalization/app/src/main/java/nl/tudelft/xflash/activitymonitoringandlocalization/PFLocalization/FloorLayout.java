package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by xflash on 28-5-16.
 */
public class FloorLayout {

    private Location origin;
    private ArrayList<Wall> completeWalls;
    private Path wallPath;
    private Region floorRegion;
    private int width;
    private int height;
    private static float northAngle;

    public FloorLayout(InputStream floorLayoutFile) {
        LayoutFileReader floorPlan = new LayoutFileReader(floorLayoutFile);
        Log.d(getClass().getSimpleName(), "Initializing floor layout");

        origin = floorPlan.getOrigin();
        completeWalls = floorPlan.getWalls();
        northAngle = floorPlan.getNorthAngle();
    }

    public void generateLayout() {
        wallPath = new Path();

        // Set path to the first wall
        wallPath.moveTo(completeWalls.get(0).getStartPoint().getX(), completeWalls.get(0).getStartPoint().getY());

        // Add wall from JSON readings to path (draw path)
        for(int i=0; i < completeWalls.size(); i++){
            wallPath.lineTo(completeWalls.get(i).getEndPoint().getX(),completeWalls.get(i).getEndPoint().getY());
        }

        // Create bound
        RectF rectF = new RectF();
        wallPath.computeBounds(rectF, true);
        floorRegion = new Region();
        floorRegion.setPath(wallPath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));

        width = floorRegion.getBounds().width();
        height = floorRegion.getBounds().height();
    }

    // calculate direction to find intersection, p=prevLoc, q=cur
    // http://stackoverflow.com/questions/25830932/how-to-find-if-two-line-segments-intersect-or-not-in-java
    private int direction(Location p, Location q, Location r) {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX())
                - (q.getX() - p.getX()) * (r.getY() - q.getY());

        if (val == 0.0)
            return 0;
        return (val > 0) ? 1 : 2;
    }

    // Detect collision
    // http://stackoverflow.com/questions/25830932/how-to-find-if-two-line-segments-intersect-or-not-in-java
    public boolean detectCollision(Particle p) {
        for(Wall wall : completeWalls) {
            int o1 = direction(p.getPreviousLocation(), p.getCurrentLocation(), wall.getStartPoint());
            int o2 = direction(p.getPreviousLocation(), p.getCurrentLocation(), wall.getEndPoint());
            int o3 = direction(wall.getStartPoint(), wall.getEndPoint(), p.getPreviousLocation());
            int o4 = direction(wall.getStartPoint(), wall.getEndPoint(), p.getCurrentLocation());

            if (o1 != o2 && o3 != o4) {
                return true;
            }
        }
        return false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Location getOrigin() {
        return origin;
    }

    public Path getPath(){
        return wallPath;
    }

    public static float getNorthAngle() {
        return northAngle;
    }

    // Check if particle inside the boundary of floor layout
    public boolean isParticleInside(Particle particle){
        Location loc = particle.getCurrentLocation();

        if(loc.getX() < this.width && loc.getX()>0 && loc.getY()<this.height && loc.getY()>0 ) {
            return floorRegion.contains((int) loc.getX(), (int) loc.getY());
        }
        else{
            return false;
        }

    }
}
