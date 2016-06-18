package nl.tudelft.xflash.activitymonitoringandlocalization.PFLocalization.FloorLayout;

/**
 * Created by xflash on 18-6-16.
 */
public class Cell {
    private Location origin;
    private float width;
    private float height;
    private String cellName;

    public Cell(String cellName, Location origin, float width, float height){
        this.origin = origin;
        this.height = height;
        this.width = width;
        this.cellName = cellName;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }
}
