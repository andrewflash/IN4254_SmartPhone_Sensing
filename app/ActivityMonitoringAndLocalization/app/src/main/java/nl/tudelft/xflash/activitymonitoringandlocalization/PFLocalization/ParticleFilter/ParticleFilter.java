package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import nl.tudelft.xflash.activitymonitoringandlocalization.Misc.ArrayOperations;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.FloorLayout;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.Location;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.MotionModel.DistanceModelZee;
import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI.VisitedPath;

/**
 * Created by xflash on 29-5-16.
 */
public class ParticleFilter {
    private ArrayList<Particle> particles;
    private FloorLayout floorLayout;
    private final int N_PARTICLES;
    private final float RATIO = 9f/10;

    private Random rand;
    private ArrayList<Float> dx,dy;
    private DistanceModelZee distanceModelZee;
    private float[] mov = {0,0};

    public ParticleFilter(final int n, FloorLayout floorLayout){
        this.particles = new ArrayList<>(n);
        this.N_PARTICLES = n;
        this.floorLayout = floorLayout;
        this.generateParticles(N_PARTICLES);
        this.rand = new Random();
        this.dx = new ArrayList<>();
        this.dy = new ArrayList<>();
        this.distanceModelZee = new DistanceModelZee(this.floorLayout);
    }

    // Generate n particles
    private void generateParticles(final int n){
        int i = 0;
        int height = floorLayout.getHeight();
        int width = floorLayout.getWidth();

        while(i < n){
            Particle p = new Particle((float)(Math.random() * width), (float)(Math.random()* height));
            if(floorLayout.isParticleInside(p)){
                particles.add(p);
                i++;
            }
        }
    }

    // Reset particles, generate default number of particles
    public void resetParticleFilter(){
        particles.clear();
        this.generateParticles(N_PARTICLES);
    }

    // Clear particles
    public void clearParticles(){
        particles.clear();
    }

    public ArrayList<Particle> getParticles(){
        return this.particles;
    }


    // Motion model
    public void movement(float alpha, int stepCount){
        ArrayList<Particle> particleTemp = new ArrayList<Particle>(particles.size());
        ArrayList<Particle> dupParticles = new ArrayList<Particle>(particles.size());

        // Duplicate all particles to particleTemp
        for(Particle p : particles) {
            particleTemp.add(new Particle(p.getCurrentLocation(), p.getPreviousLocation()));
            dupParticles.add(new Particle(p.getCurrentLocation(), p.getPreviousLocation()));
        }

        ArrayList<Particle> collisionParticles = new ArrayList<>();

        // Check particle collision with walls
        for (Particle p : dupParticles){
            mov = distanceModelZee.getDistance(alpha,stepCount);
            p.updateLocation(mov[0], mov[1]);
            if(floorLayout.detectCollision(p)){
                collisionParticles.add(p);
            }
            else if (!floorLayout.isParticleInside(p)){
                collisionParticles.add(p);
            }
            else{
                dx.add(mov[0]);
                dy.add(mov[1]);
            }
        }

        // Get instance of visited path
        VisitedPath visitedPath = VisitedPath.getInstance();

        // If 95% of particles have died than don't update the particleList
        if(collisionParticles.size() > dupParticles.size() * 0.9f){
            // As it converge, we do not need to add movement dx and dy
            visitedPath.setDx(0f);
            visitedPath.setDy(0f);
            return;
        }

        // New movement (not yet converged), then update the path
        visitedPath.setDx(ArrayOperations.mean(this.dx));
        visitedPath.setDy(ArrayOperations.mean(this.dy));

        // Clear dx and dy list
        dx.clear();
        dy.clear();

        // Remove collided particle from the list
        dupParticles.removeAll(collisionParticles);

        // Clear out all the particles
        particles.clear();

        // Duplicate all new particles
        for(Particle p : dupParticles) {
            particles.add(new Particle(p.getCurrentLocation(), p.getPreviousLocation()));
        }

        int safeSize = particles.size();

        // Replace collided particles by adding new particles on top of survived particles.
        for (int i = 0; i < collisionParticles.size()*RATIO ; i++) {
            int index = new Random().nextInt(safeSize);
            particles.add(new Particle(particles.get(index).getCurrentLocation().getX(),
                    particles.get(index).getCurrentLocation().getY()));
        }

        // 1-ratio (particleTemp)
        for (int i = 0; i < collisionParticles.size()*(1-RATIO)-1 ; i++) {
            int index = new Random().nextInt(particleTemp.size());
            particles.add(new Particle(particleTemp.get(index).getPreviousLocation().getX(),
                    particleTemp.get(index).getPreviousLocation().getY()));
        }
    }

    // Motion model for best particle
    public void movementBest(float alpha, int stepCount){
        // Get instance of visited path
        VisitedPath visitedPath = VisitedPath.getInstance();

        for(Particle p : particles) {
            mov = distanceModelZee.getDistance(alpha,stepCount);
            p.updateLocation(mov[0], mov[1]);
            // Check particle collision with walls
            if(floorLayout.detectCollision(p) || !floorLayout.isParticleInside(p)) {
                mov[0] = 0;
                mov[1] = 0;
                p.setCurrentLocation(p.getPreviousLocation());
            }
            dx.add(mov[0]);
            dy.add(mov[1]);
        }

        // New movement (not yet converged), then update the path
        visitedPath.setDx(ArrayOperations.mean(this.dx));
        visitedPath.setDy(ArrayOperations.mean(this.dy));

        // Clear dx and dy list
        dx.clear();
        dy.clear();
    }

    // Initial Belief PF
    public void initialBelief(ArrayList<ArrayList<Integer>> rssiData){
        // Get instance of visited path
        ArrayList<Float> visitedPathX = VisitedPath.getInstance().getPathX();
        ArrayList<Float> visitedPathY = VisitedPath.getInstance().getPathY();

        Log.i("RSSI TEST", "pathsize " + visitedPathX.size() + " rssiSize" + rssiData.size());
        Log.i("RSSI TEST", " " + rssiData);
        // If visited path is empty and no rssiData yet, return
        if (visitedPathX.isEmpty() || visitedPathY.isEmpty() || rssiData.isEmpty())
            return;

        // RSSI Wifi distances
        ArrayList<Double> rssiDistances = new ArrayList<>();
        ArrayList<Integer> lastRssi = rssiData.get(rssiData.size()-1);
        rssiData.remove(lastRssi);

        // Calculate RSSI distances
        for (ArrayList<Integer> rssiPoint : rssiData){
            double dist = 0 ;
            for (int i = 0; i < rssiPoint.size() ; i++) {
                dist += rssiPoint.get(i) - lastRssi.get(i);
                dist = Math.abs(dist);
            }
            rssiDistances.add(dist);
        }

        Log.i("RSSI TEST", " "+ rssiDistances);

        // Find best RSSI point
        int bestRssiIndex = ArrayOperations.indexFirstMinimumFrom(0, rssiDistances);
        float x0 = visitedPathX.get(visitedPathX.size()-bestRssiIndex-1);
        float y0 = visitedPathY.get(visitedPathY.size()-bestRssiIndex-1);

        particles.clear();

        int i = 0;
        float sigma = 3f;   // stdev for generating particle

        while(i < N_PARTICLES) {
            Particle p = new Particle(x0 + (float) rand.nextGaussian() * sigma, y0 + (float) rand.nextGaussian() * sigma);
            // Check if the particle is inside floor plan.
            if (floorLayout.isParticleInside(p)) {
                particles.add(p);
                i++;
            }
        }
    }

    // Return converged particle location, approximate using average and stdev
    public Location converged(float r){
        float xavg = 0f;
        float yavg = 0f;
        float xstdev = 0f;
        float ystdev = 0f;
        for (Particle p : particles){
            xavg += p.getCurrentLocation().getX()/particles.size();
            yavg += p.getCurrentLocation().getY()/particles.size();
        }
        for (Particle p : particles){
            xstdev += (p.getCurrentLocation().getX()-xavg)*(p.getCurrentLocation().getX()-xavg);
            ystdev += (p.getCurrentLocation().getY()-yavg)*(p.getCurrentLocation().getY()-yavg);
        }

        // Normalize
        xstdev = xstdev/particles.size();
        xstdev = (float)Math.sqrt(xstdev);
        ystdev = ystdev/particles.size();
        ystdev = (float)Math.sqrt(ystdev);

        if (xstdev < r && ystdev < r){
            return new Location(xavg,yavg);
        }
        return null;
    }

    // Best particle, converged
    public Particle bestParticle(){
        int[] count = new int[particles.size()];

        for (int i = 0; i < particles.size(); i++) {
            count[i] = 0;
        }

        ArrayList<Particle> bestParticleList = new ArrayList<>();
        for(Particle p : particles){
            bestParticleList.add(new Particle(p.getCurrentLocation(), p.getPreviousLocation()));
        }

        // Check distance for each particle
        for (int i = 0; i < bestParticleList.size(); i++) {
            for (int j = 0; j < bestParticleList.size(); j++) {
                // if distance between two particle is less than 2, then
                if (bestParticleList.get(i).distance(bestParticleList.get(j)) < 2f){
                    count[i]++;
                }
            }
        }

        // Clear best particle list
        bestParticleList.clear();

        //scorePercentate = (count[ArrayOperations.indexFirstMaximumFromInt(0,count)]*100)/(float)N_INIT;
        //Log.i("BP test","score :"+ scorePercentate);
        return particles.get(ArrayOperations.indexFirstMaximumFromInt(0,count));
    }

    // Get movement of dX and dY
    public float[] getMovement() {
        return mov;
    }

    // Set converged Particle
    public void setConvergedParticle(Location convLoc) {
        particles.clear();
        particles.add(new Particle(convLoc));
    }
}