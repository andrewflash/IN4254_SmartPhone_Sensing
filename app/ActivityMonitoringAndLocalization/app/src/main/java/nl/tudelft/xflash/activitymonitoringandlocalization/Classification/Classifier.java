package nl.tudelft.xflash.activitymonitoringandlocalization.Classification;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;

/**
 * Created by xflash on 4-5-16.
 */
public abstract class Classifier {

    public abstract void addTrainingData(List<LabeledFeatureSet> trainingDataSet);
    public abstract Type classify(FeatureSet inputData);
    public abstract void clearTrainingData();
    public abstract List<LabeledFeatureSet> getAllTrainingData();

    public void retrain(List<LabeledFeatureSet> trainingDataSet) {
        clearTrainingData();
        addTrainingData(trainingDataSet);
    }
    public List<Type> classifyList(List<FeatureSet> inputList) {
        List<Type> labelList = new ArrayList<>();
        for (FeatureSet set : inputList) {
            labelList.add(classify(set));
        }
        return labelList;
    }
    public float test(List<LabeledFeatureSet> testDataSet) {
        int correct=0;
        for (LabeledFeatureSet featSet : testDataSet ){
            Type label = classify(featSet.getFeatureSet());
            if(label.equals(featSet.getLabel())){
                correct++;
            }
        }
        return (float) correct/testDataSet.size();
    }
    public static ArrayList<LabeledFeatureSet> extractLabeledData(ArrayList<LabeledFeatureSet> data, Type a){
        ArrayList<LabeledFeatureSet> out = new ArrayList<>();
        for(LabeledFeatureSet f : data){
            if (f.getLabel().equals(a)){
                out.add(f);
            }
        }
        return out;
    }
    public  List<Type> getLabelList(){
        ArrayList<Type> out = new ArrayList<>();
        for(LabeledFeatureSet f : getAllTrainingData()){
            if (!out.contains(f.getLabel())){
                out.add(f.getLabel());
            }
        }
        return out;
    }

    public float leaveOneOut(){
        ArrayList<LabeledFeatureSet> save = new ArrayList<>(getAllTrainingData());
        int correct = 0;
        ArrayList<LabeledFeatureSet> all = new ArrayList<>();
        clearTrainingData();
        for (LabeledFeatureSet f:save){
            all.addAll(save);
            all.remove(f);
            retrain(all);
            if(classify(f).equals(f.getLabel())){
                correct++;
            }
            all.clear();
        }
        retrain(save);
        return correct/(float)save.size();
    }
}
