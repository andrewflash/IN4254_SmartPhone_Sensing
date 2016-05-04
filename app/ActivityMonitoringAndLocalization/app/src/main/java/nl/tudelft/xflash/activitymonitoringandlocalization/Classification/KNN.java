package nl.tudelft.xflash.activitymonitoringandlocalization.Classification;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;
import nl.tudelft.xflash.activitymonitoringandlocalization.Misc.ArrayOperations;

/**
 * Created by xflash on 4-5-16.
 */
public class KNN extends Classifier {
    private int k;
    private List<LabeledFeatureSet> data;
    private ArrayList<Float> covariances;

    public KNN(int k,List<LabeledFeatureSet> data){
        this.k = k;
        this.data = data;
        this.covariances=new ArrayList<>();
        this.updateCovariances();
    }

    @Override
    public void addTrainingData(List<LabeledFeatureSet> trainingDataSet) {
        this.data.addAll(trainingDataSet);
        this.updateCovariances();
    }
    @Override
    public void clearTrainingData(){
        this.data.clear();
        this.covariances.clear();
    }

    @Override
    public List<LabeledFeatureSet> getAllTrainingData() {
        return this.data;
    }

    @Override
    public Type classify(FeatureSet inputData) {
        //Construct mapping of distances to inputData
        TreeMap<Float,ArrayList<Type>> distanceMapping = new TreeMap<>();
        for(LabeledFeatureSet known : this.data){
            float d = mahaDistance(inputData, known);
            if(!distanceMapping.containsKey(d)){
                distanceMapping.put(d, new ArrayList<Type>());
            }
            distanceMapping.get(d).add(known.getLabel());
        }
        //Find K closest labels to inputData, and count how many times each label is present
        List<Type> kClosestLabels = new ArrayList<>();
        List<Integer> count = new ArrayList<>();
        int neighbor = 0;
        while (neighbor < k) {
            ArrayList<Type> labelListAtDistance = distanceMapping.pollFirstEntry().getValue();
            for (Type label : labelListAtDistance) {
                if (kClosestLabels.contains(label)) {
                    int index = kClosestLabels.indexOf(label);
                    count.set(index, count.get(index) + 1);
                } else {
                    kClosestLabels.add(label);
                    count.add(1);
                }
                neighbor++;
                if (neighbor == k){
                    break;
                }
            }
        }
        //Find index of most frequent label
        int max = count.get(0);
        int maxIndex = 0;
        for (int i = 1; i < count.size(); i++) {
            if(count.get(i) > max) {
                max = count.get(i);
                maxIndex = i;
            }
        }
        return kClosestLabels.get(maxIndex);
    }
    private void updateCovariances(){
        covariances.clear();
        //Calculate covariances for Mahalanobis distance
        if(!data.isEmpty()){
            ArrayList<Float> calculateCovariance = new ArrayList<>();
            for (int i = 0; i < data.get(0).getData().size(); i++) {
                for (int j = 0; j < data.size(); j++) {
                    calculateCovariance.add(data.get(j).getData().get(i));
                }
                covariances.add(ArrayOperations.standardDeviation(calculateCovariance));
            }
        }
    }
    public float mahaDistance(FeatureSet f1, FeatureSet f2){
        if(f1.getData().size() != f2.getData().size()){
            return Float.NaN;
        }
        float distance = 0;
        for (int i = 0; i < f1.getData().size(); i++){
            float x = (f1.getData().get(i) - f2.getData().get(i))/covariances.get(i);
            distance += x*x;
        }
        return (float) Math.sqrt(distance);
    }
}