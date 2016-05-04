package nl.tudelft.xflash.activitymonitoringandlocalization.Classification;

import java.util.ArrayList;

import nl.tudelft.xflash.activitymonitoringandlocalization.Misc.ArrayOperations;

/**
 * Created by xflash on 4-5-16.
 */
public class FeatureExtractorSD extends FeatureExtractor {

    @Override
    public FeatureSet extractFeatures(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z) {
        ArrayList<Float> magnitude = new ArrayList<>(x.size());

        for (int i = 0;i<x.size();i++){
            magnitude.add(i,(float)Math.sqrt(Math.pow(x.get(i),2.0)+Math.pow(y.get(i),2.0)+Math.pow(z.get(i),2.0)));
        }

        return new FeatureSet(ArrayOperations.standardDeviation(magnitude));
    }
}