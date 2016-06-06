package nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor;

/**
 * Created by xflash on 4-5-16.
 */
public enum Type {
    NONE{
        public Type fromStringToType(String s){
            if(NONE.toString().equals(s))
                return Type.NONE;
            else
                return null;
        }
    },
    WALKING{
        public Type fromStringToType(String s){
            if(WALKING.toString().equals(s))
                return Type.WALKING;
            else
                return null;
        }
    },
    IDLE{
        public Type fromStringToType(String s){
            if(IDLE.toString().equals(s))
                return Type.IDLE;
            else
                return null;
        }
    };

    protected abstract Type fromStringToType(String s);

    public static Type fromString(String s) {
        Type result = null;
        for(Type t : Type.values()){
            result = t.fromStringToType(s);
            if(result != null){
                return result;
            }
        }
        return result;
    }

    public static Type fromInt(int num) {
        for(Type t : Type.values()){
            if(t.ordinal() == num) {
                return t;
            }
        }
        return Type.NONE;
    }
}