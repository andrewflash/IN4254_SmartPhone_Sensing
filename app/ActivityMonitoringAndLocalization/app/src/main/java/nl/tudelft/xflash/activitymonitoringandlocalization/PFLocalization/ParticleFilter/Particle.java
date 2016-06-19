package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter;

import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.Location;

/**
 * Created by xflash on 29-5-16.
 */
public class Particle {
    private Location prevLoc, currLoc;
    private float prevStride, currStride;

    public Particle(float x, float y, float stride){
        this.currLoc = new Location(x,y);
        this.prevLoc = new Location(x,y);
        this.currStride = stride;
        this.prevStride = stride;
    }

    public Particle(Location newLoc, float stride){
        this.currLoc = new Location(newLoc);
        this.prevLoc= new Location(newLoc);
        this.currStride = stride;
        this.prevStride = stride;
    }

    public Particle(Location currLoc, Location prevLoc, float currStride, float prevStride){
        this.currLoc = new Location(currLoc);
        this.prevLoc = new Location(prevLoc);
        this.currStride = currStride;
        this.prevStride = prevStride;
    }

    public void updateLocation(float dx, float dy){
        prevLoc.setLocation(currLoc);
        currLoc.translate(dx, dy);
    }

    public void updateStride(float ds){
        prevStride = currStride;
        currStride = currStride + ds;
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

    public void setCurrentStride(float currStride){
        this.currStride = currStride;
    }

    public Location getCurrentLocation(){
        return this.currLoc;
    }

    public float getCurrentStride(){
        return this.currStride;
    }


    public void setPreviousLocation(Location newLocation){
        this.prevLoc = newLocation;
    }

    public Location getPreviousLocation(){
        return this.prevLoc;
    }

    public void setPreviousStride(float prevStride){
        this.prevStride = prevStride;
    }

    public float getPreviousStride(){
        return this.prevStride;
    }

    public Particle duplicate(){
        return new Particle(this.currLoc, this.prevLoc, this.currStride, this.prevStride);
    }
}