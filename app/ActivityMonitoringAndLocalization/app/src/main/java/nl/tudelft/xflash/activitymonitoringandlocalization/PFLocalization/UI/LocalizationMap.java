package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.ParticleFilter.Particle;

/**
 * Created by xflash on 28-5-16.
 */
public class LocalizationMap extends View {

    private Path wall;
    private CopyOnWriteArrayList<Particle> particles;
    private Particle convParticle = null;

    private final float size = 0.98f;       // Add padding 0.02
    private float scale;
    private float offsetX;
    private float offsetY;
    private float width, height;
    private VisitedPath visitedPath;

    private Matrix scaleMatrix;
    private Paint particlePaint, wallPaint, convPaint;

    public LocalizationMap(Context context, Path floorLayout, ArrayList<Particle> particles, float width, float height) {
        super(context);

        this.wall = floorLayout;
        this.width = width;
        this.height = height;

        this.particles = new CopyOnWriteArrayList<Particle>(particles);
        this.visitedPath = VisitedPath.getInstance();

        // Create wall, initialize scaling matrix
        scaleMatrix = new Matrix();
        RectF rectF = new RectF();
        this.wall.computeBounds(rectF, true);

        // Scale the floor layout depending on screen size.
        this.scale = (size*width)/rectF.width();
        scaleMatrix.setScale(scale, scale, rectF.left, rectF.top);
        this.wall.transform(scaleMatrix);

        this.offsetX = (width - width*size)/2;
        this.offsetY = (height - height*size)/2;

        // Offset of X and Y
        this.wall.offset(offsetX, offsetY);
        visitedPath.initTransform(scaleMatrix, offsetX, offsetY);

        // Paint
        particlePaint = new Paint();
        particlePaint.setStrokeWidth(4);
        particlePaint.setColor(Color.RED);

        wallPaint = new Paint();
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(3);

        convPaint = new Paint();
        convPaint.setStyle(Paint.Style.STROKE);
        convPaint.setColor(Color.BLUE);
        convPaint.setStrokeWidth(20);
    }

    // Set particle color
    public void setColor(int color){
        particlePaint.setColor(color);
    }

    // Reset view
    public void reset(){
        convParticle = null;
        particlePaint.setColor(Color.RED);
    }

    // Set particles (draw)
    public void setParticles(ArrayList<Particle> newParticles) {
        particles = new CopyOnWriteArrayList<Particle>(newParticles);
        convParticle = null;
    }

    // Set converged particle location
    public void setConvLocation(Particle convParticle){
        this.convParticle = convParticle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Invert canvas (inverted in resource layout)
        //Log.d(getClass().getSimpleName(),"Scale: " + height*size/2);
        //canvas.scale(1,-1,width/2,200);

        // Draw walls
        canvas.drawPath(wall, wallPaint);

        // Draw particles
        for(Particle p : this.particles) {
            canvas.drawPoint(p.getCurrentLocation().getX()*scale +
                    offsetX, p.getCurrentLocation().getY()*scale + offsetY, particlePaint);
        }

        // Draw converged particle
        if(convParticle != null){
            canvas.drawPoint(convParticle.getCurrentLocation().getX()*scale +
                    offsetX, convParticle.getCurrentLocation().getY()*scale + offsetY,
                    convPaint);
        }

    }
}
