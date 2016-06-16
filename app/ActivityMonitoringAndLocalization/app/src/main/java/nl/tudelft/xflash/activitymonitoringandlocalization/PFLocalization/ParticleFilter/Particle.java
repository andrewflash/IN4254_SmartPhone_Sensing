package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter;

import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.Location;

/**
 * Created by xflash on 29-5-16.
 */
public class Particle {
    private Location prevLoc, currLoc;

    public Particle(float x, float y){
        this.currLoc = new Location(x,y);
        this.prevLoc = new Location(x,y);
    }

    public Particle(Location newLoc){
        this.currLoc = new Location(newLoc);
        this.prevLoc= new Location(newLoc);
    }

    public Particle(Location currLoc, Location prevLoc){
        this.currLoc = new Location(currLoc);
        this.prevLoc = new Location(prevLoc);
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