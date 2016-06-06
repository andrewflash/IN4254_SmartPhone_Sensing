package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout.FloorLayout;

/**
 * Created by xflash on 29-5-16.
 */
public class CompassView {
    private Point compassDir;
    private Point compassPlac;
    private int compassRadius;
    private Paint circlePaint, compassPaint;

    public CompassView(int width, int height, float angle, int radius){
        compassPlac = new Point((width/2), (height/2));
        compassDir = new Point();
        compassRadius = radius;
        setAngle(0f);

        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStrokeWidth(3);

        compassPaint = new Paint();
        compassPaint.setStyle(Paint.Style.STROKE);
        compassPaint.setColor(Color.RED);
        compassPaint.setStrokeWidth(20);
    }

    public void draw(Canvas canvas){
        // Draw Compass circle
        canvas.drawCircle(compassPlac.x, compassPlac.y, compassRadius, circlePaint);

        // Draw Direction of the compass
        canvas.drawLine(compassPlac.x, compassPlac.y, compassDir.x, compassDir.y, compassPaint);

    }

    public void setAngle(float angle){
        compassDir.set( compassPlac.x + (int)(compassRadius*Math.cos(-angle + Math.toRadians(FloorLayout.getNorthAngle()))),
                compassPlac.y + (int)(compassRadius*Math.sin(-angle + Math.toRadians(FloorLayout.getNorthAngle()))));
    }
}
