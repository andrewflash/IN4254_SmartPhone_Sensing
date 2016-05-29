package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xflash on 28-5-16.
 */
public class LayoutFileReader {

    private Location origin;
    private ArrayList<Location> points;
    private ArrayList<Integer> wallsIdx;
    private ArrayList<Wall> walls;
    private float northAngle;   // North angle measured form x to y in degree

    public LayoutFileReader(InputStream in) {
        origin = new Location(0,0);
        points = new ArrayList<Location>();
        wallsIdx = new ArrayList<Integer>();
        walls = new ArrayList<Wall>();
        northAngle = 0;

        try {
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            readJsonStream(reader);
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(),e.getMessage());
        }
    }

    public Location getOrigin() {
        return origin;
    }

    public ArrayList<Location> getPoints() {
        return points;
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public float getNorthAngle() {
        return northAngle;
    }

    private void readJsonStream(JsonReader reader) {
        float originX = 0, originY = 0;
        float pointX, pointY;
        int pointStartIdx = 0, pointEndIdx = 0;

        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("origins")) {
                    reader.beginArray();
                    if (reader.hasNext()) originX = (float) reader.nextDouble();
                    if (reader.hasNext()) originY = (float) reader.nextDouble();
                    reader.endArray();
                    origin.setLocation(originX, originY);
                    Log.d(getClass().getSimpleName(), "Origin: " + originX + ", " + originY);
                } else if (name.equals("points")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            pointX = (float) reader.nextDouble();
                            pointY = (float) reader.nextDouble();
                            points.add(new Location(pointX, pointY));
                            Log.d(getClass().getSimpleName(), "Point: " + pointX + ", " + pointY);
                        }
                        reader.endArray();
                    }
                    reader.endArray();
                } else if (name.equals("walls")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            pointStartIdx = reader.nextInt();
                            pointEndIdx = reader.nextInt();
                        }
                        reader.endArray();
                        walls.add(new Wall(points.get(pointStartIdx), points.get(pointEndIdx)));
                        Log.d(getClass().getSimpleName(), "Wall: [" + points.get(pointStartIdx).getX() +
                                "," + points.get(pointStartIdx).getY() + "] - [" +
                                points.get(pointEndIdx).getX() + "," + points.get(pointEndIdx).getY() + "]");
                    }
                    reader.endArray();
                } else if (name.equals("northAngle")) {
                    while (reader.hasNext()) {
                        northAngle = (float) reader.nextDouble();
                    }
                    Log.d(getClass().getSimpleName(), "NorthAngle from x (degree): " + northAngle);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
        }
    }

}