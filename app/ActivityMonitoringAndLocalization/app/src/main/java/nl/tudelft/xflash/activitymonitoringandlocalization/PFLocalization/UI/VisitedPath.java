package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.Location;

/**
 * Created by xflash on 15-6-16.
 */
public class VisitedPath {
    private Path pathVisited;
    private ArrayList<Float> dx;
    private ArrayList<Float> dy;
    private float offsetX;
    private float offsetY;
    private Matrix scaleMatrix;
    private static VisitedPath singleton = null;

    private Paint visitedColor;
    private ArrayList<Float> pathX;
    private ArrayList<Float> pathY;

    private VisitedPath(){
        dx = new ArrayList<Float>();
        dy = new ArrayList<Float>();

        pathX = new ArrayList<Float>();
        pathY = new ArrayList<>();

        pathVisited = new Path();

        scaleMatrix = new Matrix();

        visitedColor = new Paint();
        visitedColor.setStrokeWidth(3);
        visitedColor.setColor(Color.BLUE);
        visitedColor.setStyle(Paint.Style.STROKE);
    }

    // singleton
    public static VisitedPath getInstance(){
        if(singleton == null){
            singleton = new VisitedPath();
        }

        return singleton;
    }

    public void setDx(float dx){
        this.dx.add(dx);
    }

    public void setDy(float dy){
        this.dy.add(dy);
    }

    public void setPathVisited(Location convLoc) {
        pathVisited.reset();
        pathX.clear();
        pathY.clear();

        // Obtain user's visited path
        if(convLoc != null) {
            float x = convLoc.getX();
            float y = convLoc.getY();

            // Convergence location = start location
            pathVisited.moveTo(convLoc.getX(), convLoc.getY());

            pathX.add(convLoc.getX());
            pathY.add(convLoc.getY());

            // Integrate dx to get total length of visited path
            for(int i = dx.size()-1; i >= 0; i-- ){
                float dx = this.dx.get(i);
                float dy = this.dy.get(i);

                x = x - dx;
                y = y - dy;

                // Create an arraylist with all the positions
                pathX.add(x);
                pathY.add(y);

                // Draw path
                pathVisited.lineTo(x, y);
            }

            // Transform to GUI
            this.transform();
        }
    }

    public void setPath(Location convLoc){
        float x = convLoc.getX();
        float y = convLoc.getY();

        pathVisited.lineTo(x, y);
        // Transform to GUI
        this.transform();
    }

    public void setOffset(float offsetX, float offSetY){
        this.offsetX = offsetX;
        this.offsetY = offSetY;
    }

    public void initTransform(Matrix scaleMatrix, float offSetX, float offSetY){
        this.scaleMatrix.set(scaleMatrix);
        this.offsetX = offSetX;
        this.offsetY = offSetY;
    }

    public void transform(){
        pathVisited.transform(scaleMatrix);
        pathVisited.offset(offsetX, offsetY);
    }

    public void reset(){
        dx.clear();
        dy.clear();
        pathX.clear();
        pathY.clear();
        pathVisited.reset();
    }

    public void draw(Canvas canvas){
        canvas.drawPath(pathVisited, visitedColor);
    }

    public ArrayList<Float> getPathX(){return this.pathX;}
    public ArrayList<Float> getPathY(){return this.pathY;}
}
