package nl.tudelft.xflash.activitymonitoringandlocalization.Classification;

import java.util.ArrayList;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;

/**
 * Created by xflash on 4-5-16.
 */
public abstract class FeatureExtractor {
    public abstract FeatureSet extractFeatures(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z);
    public LabeledFeatureSet addLabel(FeatureSet f, Type l){
        return new LabeledFeatureSet(f,l);
    }
    public static ArrayList<LabeledFeatureSet> generateDataSet(ArrayList<Type> labels,
                                                               ArrayList<Float> x,
                                                               ArrayList<Float> y,
                                                               ArrayList<Float> z,
                                                               ArrayList<FeatureExtractor> extractors,
                                                               final int stepSize){
        //Extract LabeledFeatureSet
        ArrayList<LabeledFeatureSet> train = new ArrayList<>();
        int index = 0;
        while(index < x.size()-stepSize){
            if(labels.get(index) == labels.get(index+stepSize)) {
                ArrayList<Float> xlist =  new ArrayList<>(x.subList(index, index + stepSize));
                ArrayList<Float> ylist =  new ArrayList<>(y.subList(index, index + stepSize));
                ArrayList<Float> zlist =  new ArrayList<>(z.subList(index, index + stepSize));
                FeatureSet f = new FeatureSet();
                for (FeatureExtractor ext :extractors){
                    f.addFeature(ext.extractFeatures(xlist, ylist, zlist));
                }
                train.add(new LabeledFeatureSet(f,labels.get(index)));
            }
            index = index+stepSize;
        }
        return train;
    }
}