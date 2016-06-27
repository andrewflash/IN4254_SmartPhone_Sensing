package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter;

import java.util.Random;

import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.Location;

/**
 * Created by xflash on 29-5-16.
 */
public class Particle {
    private Location prevLoc, currLoc;
    private float strideLength;
    private final float MAX_STRIDE = 0.75f;
    private final float MIN_STRIDE = 0.5f;
    private final float SIGMA_ANGLE_RAD = 0.01f;
    private Random rand = new Random();

    public Particle(float x, float y){
        this.currLoc = new Location(x,y);
        this.prevLoc = new Location(x,y);
        this.strideLength = (float)(Math.random() * (MAX_STRIDE - MIN_STRIDE) + MIN_STRIDE);
    }

    public Particle(Location newLoc){
        this.currLoc = new Location(newLoc);
        this.prevLoc= new Location(newLoc);
        this.strideLength = (float)(Math.random() * (MAX_STRIDE - MIN_STRIDE) + MIN_STRIDE);
    }

    public Particle(Location currLoc, Location prevLoc){
        this.currLoc = new Location(currLoc);
        this.prevLoc = new Location(prevLoc);
        this.strideLength = (float)(Math.random() * (MAX_STRIDE - MIN_STRIDE) + MIN_STRIDE);
    }

    public void updateLocation(float dx, float dy){
        prevLoc.setLocation(currLoc);
        currLoc.translate(dx, dy);
    }

    // Euclidean distance
    public float distance(Particle p){
        float distX = p.getCurrentLocation().getX() - this.currLoc.getX();
        float distY = p.getCurrentLocation().getY() - this.currLoc.getY();
        distX = distX*distX;
        distY = distY*distY;
        return (float) Math.sqrt(distX+distY);
    }

    public void setCurrentLocation(Location newLocation){
        this.currLoc = newLocation;
    }

    public Location getCurrentLocation(){
        return this.currLoc;
    }

    public float getStrideLength(){
        return this.strideLength;
    }

    public void setPreviousLocation(Location newLocation){
        this.prevLoc = newLocation;
    }

    public Location getPreviousLocation(){
        return this.prevLoc;
    }

    public Particle duplicate(){
        return new Particle(this.currLoc, this.prevLoc);
    }
}