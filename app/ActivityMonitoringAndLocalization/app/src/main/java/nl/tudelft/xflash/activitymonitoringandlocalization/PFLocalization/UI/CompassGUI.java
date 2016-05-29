package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by xflash on 29-5-16.
 */
public class CompassGUI extends View {
    private CompassView compass;

    public CompassGUI(Context context, float width, float height) {
        super(context);
        compass = new CompassView((int) width, (int) height, 0f, 50);
    }

    // Set compass angle (draw)
    public void setAngle(float angle){
        compass.setAngle(angle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the compass
        compass.draw(canvas);
    }
}
