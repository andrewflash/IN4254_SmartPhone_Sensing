package nl.tudelft.xflash.activitymonitoringandlocalization.Classification;

import java.util.ArrayList;

/**
 * Created by xflash on 4-5-16.
 */
public class FeatureSet {

    private ArrayList<Float> data;
    public FeatureSet(FeatureSet feat){
        this.data = feat.getData();
    }
    public FeatureSet(){this.data=new ArrayList<>();}
    public FeatureSet(ArrayList<Float> data){
        this.data = data;
    }
    public FeatureSet(Float f){
        this.data= new ArrayList<>();
        data.add(f);
    }
    public FeatureSet(int i){
        this.data= new ArrayList<>();
        data.add((float) i);
    }
    public ArrayList<Float> getData() {
        return this.data;
    }
    public void addFeature(FeatureSet feat){
        data.addAll(feat.getData());
    }
    public void addFeature(Float f){
        data.add(f);
    }
    public float distance(FeatureSet feat){
        float distance = 0;
        for (int i = 0; i < data.size() ; i++) {
            distance += Math.pow(feat.getData().get(i)-data.get(i),2);
        }
        return (float)Math.sqrt(distance);
    }
}