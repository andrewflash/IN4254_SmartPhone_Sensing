package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
    private ArrayList<RectF> cellRectList;
    private ArrayList<String> cellNames;
    private Particle convParticle = null;

    private final float size = 0.98f;       // Add padding 0.02
    private float scale;
    private float offsetX;
    private float offsetY;
    private float width, height;
    private VisitedPath visitedPath;

    private Matrix scaleMatrix;
    private Paint particlePaint, wallPaint, convPaint, cellPaint, cellTextPaint;

    // Pan and zoom
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private float MIN_ZOOM = 1f;
    private float MAX_ZOOM = 5f;
    private PointF mid = new PointF();
    private final static int NONE = 0;
    private int mode ;
    private float startX = 0f;
    private float startY = 0f;
    private float translateX = 0f;
    private float translateY = 0f;
    private float previousTranslateX = 0f;
    private float previousTranslateY = 0f;
    private boolean dragged = false;

    public LocalizationMap(Context context, Path floorLayout,
                           ArrayList<String> cellNames, ArrayList<RectF> cellRectList,
                           ArrayList<Particle> particles, float width, float height) {
        super(context);

        this.wall = floorLayout;
        this.width = width;
        this.height = height;

        this.particles = new CopyOnWriteArrayList<Particle>(particles);
        this.visitedPath = VisitedPath.getInstance();
        this.cellRectList = cellRectList;
        this.cellNames = cellNames;

        // Create wall, initialize scaling matrix
        scaleMatrix = new Matrix();
        RectF rectF = new RectF();
        this.wall.computeBounds(rectF, true);

        // Scale the floor layout depending on screen size.
        this.scale = (size * width) / rectF.width();
        scaleMatrix.setScale(scale, scale, rectF.left, rectF.top);
        this.wall.transform(scaleMatrix);

        this.offsetX = (width - width * size) / 2;
        this.offsetY = (height - height * size) / 2;

        // Offset of X and Y
        this.wall.offset(offsetX, offsetY);
        visitedPath.initTransform(scaleMatrix, offsetX, offsetY);

        // Paint
        particlePaint = new Paint();
        particlePaint.setStrokeWidth(4);
        particlePaint.setColor(Color.RED);
        particlePaint.setStrokeCap(Paint.Cap.ROUND);

        wallPaint = new Paint();
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(3);

        cellPaint = new Paint();
        cellPaint.setStyle(Paint.Style.STROKE);
        cellPaint.setColor(Color.BLACK);
        cellPaint.setStrokeWidth(1);
        cellPaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));

        cellTextPaint = new Paint();
        cellTextPaint.setTextAlign(Paint.Align.CENTER);
        cellTextPaint.setColor(Color.BLACK);
        cellTextPaint.setTextSize(20f);
        cellTextPaint.setTypeface(Typeface.MONOSPACE);
        cellTextPaint.setTextScaleX(-1f);

        convPaint = new Paint();
        convPaint.setStyle(Paint.Style.STROKE);
        convPaint.setColor(Color.BLUE);
        convPaint.setStrokeWidth(20);
        convPaint.setStrokeCap(Paint.Cap.ROUND);

        // Pan and zoom
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    // Set particle color
    public void setColor(int color) {
        particlePaint.setColor(color);
    }

    // Reset view
    public void reset() {
        convParticle = null;
        particlePaint.setColor(Color.RED);
    }

    // Clear particles
    public void clearParticles(){
        particles.clear();
    }

    // Set particles (draw)
    public void setParticles(ArrayList<Particle> newParticles) {
        particles = new CopyOnWriteArrayList<Particle>(newParticles);
    }

    // Set converged particle location
    public void setConvLocation(Particle convParticle) {
        this.convParticle = convParticle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.translate(translateX / mScaleFactor , translateY / mScaleFactor);

        super.onDraw(canvas);

        // Draw cell area
        for (int i=0; i<cellNames.size(); i++){
            canvas.drawRect(cellRectList.get(i).left*scale + offsetX,cellRectList.get(i).top*scale + offsetY,
                    cellRectList.get(i).right*scale + offsetX, cellRectList.get(i).bottom*scale + offsetY,cellPaint);
            canvas.drawText(cellNames.get(i),
                    cellRectList.get(i).left*scale + offsetX + cellRectList.get(i).width()*scale/2,
                    cellRectList.get(i).top*scale + offsetY + cellRectList.get(i).height()*scale/2,
                    cellTextPaint);
        }

        // Draw walls
        canvas.drawPath(wall, wallPaint);

        // Draw converged particle
        if (convParticle != null) {
            canvas.drawPoint(convParticle.getCurrentLocation().getX() * scale + offsetX,
                    convParticle.getCurrentLocation().getY() * scale + offsetY, convPaint);
            visitedPath.draw(canvas);
        } else {
            // Draw particles
            for (Particle p : this.particles) {
                canvas.drawPoint(p.getCurrentLocation().getX() * scale +
                        offsetX, p.getCurrentLocation().getY() * scale + offsetY, particlePaint);
            }
        }

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int ZOOM = 2;
        int DRAG = 1;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                mode = DRAG;

                //We assign the current X and Y coordinate of the finger to startX and startY minus the previously translated
                //amount for each coordinates This works even when we are translating the first time because the initial
                //values for these two variables is zero.
                startX = event.getX() - previousTranslateX;
                startY = event.getY() - previousTranslateY;

                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == ZOOM)
                    break;

                translateX = event.getX() - startX;
                translateY = event.getY() - startY;

                //We cannot use startX and startY directly because we have adjusted their values using the previous translation values.
                //This is why we need to add those values to startX and startY so that we can get the actual coordinates of the finger.
                double distance = Math.sqrt(Math.pow(event.getX() - (startX + previousTranslateX), 2) +
                        Math.pow(event.getY() - (startY + previousTranslateY), 2)
                );

                if(distance > 0) {
                    dragged = true;
                }

                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                midPoint(mid, event);

                mode = ZOOM;

                break;

            case MotionEvent.ACTION_UP:

                mode = NONE;
                dragged = false;

                //All fingers went up, so let's save the value of translateX and translateY into previousTranslateX and
                //previousTranslate
                previousTranslateX = translateX;
                previousTranslateY = translateY;

                break;

            case MotionEvent.ACTION_POINTER_UP:

                mode = NONE;

                //This is not strictly necessary; we save the value of translateX and translateY into previousTranslateX
                //and previousTranslateY when the second finger goes up
                previousTranslateX = translateX;
                previousTranslateY = translateY;

                break;
        }

        mScaleDetector.onTouchEvent(event);

        //We redraw the canvas only in the following cases:
        //
        // o The mode is ZOOM
        //        OR
        // o The mode is DRAG and the scale factor is not equal to 1 (meaning we have zoomed) and dragged is
        //   set to true (meaning the finger has actually moved)
        if ((mode == DRAG && mScaleFactor != 1f && dragged) || mode == ZOOM) {
            this.invalidate();
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));

            invalidate();
            return true;
        }
    }

    // calculate the mid point of the first two fingers
    private void midPoint(PointF point, MotionEvent event) {
        // ...
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void setMinZoom(float minZoom){
        this.MIN_ZOOM = minZoom;
    }
}