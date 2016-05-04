package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

/**
 * Created by xflash on 4-5-16.
 */

public enum ActivityList {
    WALKING(2), STANDING(1);
    private int value;

    private ActivityList(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}