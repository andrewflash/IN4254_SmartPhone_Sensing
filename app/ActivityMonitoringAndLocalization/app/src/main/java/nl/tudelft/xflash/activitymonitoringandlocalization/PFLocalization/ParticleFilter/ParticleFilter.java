package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter;

import android.net.wifi.ScanResult;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.tudelft.xflash.activitymonitoringandlocalization.Database.WifiData;
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

    private ArrayList<Float> dx,dy;
    private DistanceModelZee distanceModelZee;
    private float[] mov = {0,0};

    public ParticleFilter(final int n, FloorLayout floorLayout){
        this.particles = new ArrayList<>(n);
        this.N_PARTICLES = n;
        this.floorLayout = floorLayout;
        this.generateParticles(N_PARTICLES);
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
            mov = distanceModelZee.getDistance(alpha,stepCount,p.getStrideLength());
            p.updateLocation(mov[0], mov[1]);
            if(floorLayout.detectCollision(p)){
                collisionParticles.add(p);
                mov[0] = 0;
                mov[1] = 0;
            }
            else if (!floorLayout.isParticleInside(p)){
                collisionParticles.add(p);
                mov[0] = 0;
                mov[1] = 0;
            }
            else{
                dx.add(mov[0]);
                dy.add(mov[1]);
            }
        }

        // Get instance of visited path
        VisitedPath visitedPath = VisitedPath.getInstance();

        // If 90% of particles have died than don't update the particleList
        // avoid trap and ensure convergence
        if(collisionParticles.size() > dupParticles.size() * 0.9f){
            // As it converge, we do not need to add movement dx and dy
            visitedPath.setDx(0f);
            visitedPath.setDy(0f);
            return;
        }

        if(this.dx.size() > 0) {
            visitedPath.setDx(ArrayOperations.mean(this.dx));
            visitedPath.setDy(ArrayOperations.mean(this.dy));
        } else {
            visitedPath.setDx(0);
            visitedPath.setDy(0);
        }
        // Clear dx and dy list
        dx.clear();
        dy.clear();

        // Remove collided particle from the list
        dupParticles.removeAll(collisionParticles);

        // Clear out all the particles
        particles.clear();

        // Recreate particle (excluding the collided particles)
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

        // 1-ratio (particleTemp), randomized the rest particle locations
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
            mov = distanceModelZee.getDistance(alpha,stepCount,p.getStrideLength());
            p.updateLocation(mov[0], mov[1]);
            // Check particle collision with walls
            if(floorLayout.detectCollision(p) || !floorLayout.isParticleInside(p)) {
                mov[0] = 0;
                mov[1] = 0;
                p.setCurrentLocation(p.getPreviousLocation());
            } else {
                dx.add(mov[0]);
                dy.add(mov[1]);
            }
        }

        // New movement (not yet converged), then update the path
        if(this.dx.size() > 0) {
            visitedPath.setDx(ArrayOperations.mean(this.dx));
            visitedPath.setDy(ArrayOperations.mean(this.dy));
        } else {
            visitedPath.setDx(0);
            visitedPath.setDy(0);
        }

        // Clear dx and dy list
        dx.clear();
        dy.clear();
    }

    // Initial Belief PF
    public boolean initialBeliefBayesKNN(List<ScanResult> curWifi, List<WifiData> wifiData) {
        // If no rssiData yet, return
        if (wifiData.isEmpty() || curWifi.isEmpty()){
            return false;
        }

        // Current Wifi Data
        ArrayList<String> curBssidList = new ArrayList<>();
        ArrayList<Integer> curLevelList = new ArrayList<>();
        for (ScanResult s : curWifi){
            curBssidList.add(s.BSSID);
            curLevelList.add(s.level);
        }

        // RSSI Wifi distances
        ArrayList<Double> rssiDistances = new ArrayList<>();

        // Calculate RSSI distances
        try {
            for (WifiData wifi : wifiData) {
                float distance = 0;
                float totalDistance = 0;
                String jsonWifi = wifi.get_ssid();
                JSONArray jsonWifiArray = new JSONArray(jsonWifi);
                for (int i=0; i<jsonWifiArray.length(); i++){
                    JSONObject jsonWifiData = jsonWifiArray.getJSONObject(i);
                    String bssid = jsonWifiData.getString("bssid");
                    int level = jsonWifiData.getInt("level");
                    for(int j=0; j<curBssidList.size();j++) {
                        if(curBssidList.get(j).equals(bssid)){
                            float diff = (Math.abs(WifiData.normalizeRssi(level)
                                    - WifiData.normalizeRssi(curLevelList.get(j))));
                            distance += diff;
                        } else {    // wifi not found in database
                            distance += 1;
                        }
                        totalDistance += 1;
                    }
                }
                rssiDistances.add((double)distance/totalDistance);
            }
            Log.d(this.getClass().getSimpleName(), rssiDistances.toString());
        } catch (JSONException e) {
            Log.e(this.getClass().getSimpleName(), "JSON Wifi error: " + e.getMessage());
        }

        // Find best RSSI point
        int bestRssiIndex = ArrayOperations.indexFirstMinimumFrom(0, rssiDistances);
        if(rssiDistances.get(bestRssiIndex) > 0.98) {
            return false;
        }

        float x0 = (float)wifiData.get(bestRssiIndex).getX();
        float y0 = (float)wifiData.get(bestRssiIndex).getY();

        Log.d(this.getClass().getSimpleName(), "x0: " + x0 + " y0: " + y0);
        setConvergedParticle(new Location(x0,y0));

        return true;
    }

    // Return converged particle location, approximate using average and stdev
    public Location converged(float r, float rs){
        float xavg = 0f;
        float yavg = 0f;
        float xstdev = 0f;
        float ystdev = 0f;
        float sstdev = 0f;

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
        sstdev = sstdev/particles.size();
        sstdev = (float)Math.sqrt(sstdev);

        if (xstdev < r && ystdev < r && sstdev < rs){
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