package nl.tudelft.xflash.activitymonitoringandlocalization.Classification;

import nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor.Type;

/**
 * Created by xflash on 4-5-16.
 */
public class LabeledFeatureSet extends FeatureSet{
    private FeatureSet feat;
    private Type label;
    public LabeledFeatureSet(FeatureSet feat,Type lab){
        super(feat);
        this.feat = feat;
        this.label = lab;
    }
    public Type getLabel() {
        return label;
    }
    public FeatureSet getFeatureSet(){
        return this.feat;
    }

    public void setLabel(Type label) {
        this.label = label;
    }
}