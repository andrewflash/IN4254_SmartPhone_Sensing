package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

import android.content.Context;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;

/**
 * Created by xflash on 4-5-16.
 */
public abstract class AbstractReader {

    protected String fileName;
    protected DataInputStream fInpStream = null;

    protected ArrayList<Type> allStates;
    protected ArrayList<Float> allX;

    protected AbstractReader(Context ctx){
        allStates = new ArrayList<>();
        allX = new ArrayList<>();
    }

    public boolean available(){
        try {
            return (fInpStream.available() > 0);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    public void readData(){
        String str = "";
        String[] split;
        try {
            while(this.available()) {
                str = fInpStream.readLine();
                if (str == null) {
                    return;
                }
                split = str.split(" ");
                allStates.add(Type.fromString(split[0]));               // Label
                allX.add(Float.parseFloat(split[1]));
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public int size(){
        return allStates.size();
    }

    public void empty(){
        allStates.clear();
        allX.clear();
    }

    public void emptyStates(){
        allStates.clear();
    }
    public void emptyX(){
        allX.clear();
    }


    public ArrayList<Type> getAllStates(){ return allStates;}
    public ArrayList<Float> getAllX(){return allX;}

    public List<Type> getSubListStates(int windowSize){
        int size = allStates.size();
        return allStates.subList(size-windowSize,size);
    }
    public List<Float> getSubListX(int windowSize){
        int size = allX.size();
        return allX.subList(size-windowSize,size);
    }
}
