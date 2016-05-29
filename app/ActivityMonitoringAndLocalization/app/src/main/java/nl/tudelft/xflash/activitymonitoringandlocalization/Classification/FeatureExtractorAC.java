package nl.tudelft.xflash.activitymonitoringandlocalization.Classification;

import java.util.ArrayList;

import nl.tudelft.xflash.activitymonitoringandlocalization.Misc.ArrayOperations;

/**
 * Created by xflash on 29-5-16.
 */
public class FeatureExtractorAC extends FeatureExtractor {

    @Override
    public FeatureSet extractFeatures(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z){
        ArrayList<Float> magnitude = new ArrayList<>(x.size());

        for (int i = 0;i<x.size();i++){
            magnitude.add(i,(float)Math.sqrt(Math.pow(x.get(i),2.0)+Math.pow(y.get(i),2.0)+Math.pow(z.get(i),2.0)));
        }
        float mean = new FeatureExtractorMean().extractFeatures(x,y,z).getData().get(0);
        float sigma = new FeatureExtractorSD().extractFeatures(x,y,z).getData().get(0);
        ArrayList<Float> out = new ArrayList<>(magnitude.size());
        for (int i = 0; i < magnitude.size(); i++) {
            out.add((float)0);
        }
        for (int i = 0; i < out.size(); i++) {
            for (int j = 0; j < magnitude.size()-i; j++) {
                out.set(i,out.get(i)+(magnitude.get(j)-mean)*(magnitude.get(i+j)-mean));
            }
            out.set(i,out.get(i)/(magnitude.size()-i)/sigma/sigma);
        }
        return new FeatureSet(ArrayOperations.standardDeviation(out));
    }
}

