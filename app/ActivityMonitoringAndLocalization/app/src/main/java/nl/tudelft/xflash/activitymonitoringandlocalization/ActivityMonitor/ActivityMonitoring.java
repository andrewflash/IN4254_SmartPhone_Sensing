package nl.tudelft.xflash.activitymonitoringandlocalization.ActivityMonitor;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;

import nl.tudelft.xflash.activitymonitoringandlocalization.Classification.FeatureExtractor;
import nl.tudelft.xflash.activitymonitoringandlocalization.Classification.FeatureExtractorSD;
import nl.tudelft.xflash.activitymonitoringandlocalization.Classification.FeatureSet;
import nl.tudelft.xflash.activitymonitoringandlocalization.Classification.KNN;
import nl.tudelft.xflash.activitymonitoringandlocalization.Classification.LabeledFeatureSet;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.AbstractReader;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.Reader;
import nl.tudelft.xflash.activitymonitoringandlocalization.Database.ReaderTest;
import nl.tudelft.xflash.activitymonitoringandlocalization.R;

/**
 * Created by xflash on 4-5-16.
 */
public class ActivityMonitoring {
    // This instance keeps track of the activities performed
    ActivityType activityList;

    // Readers
    private AbstractReader SDReader;
    //    private AbstractReader MagReader;
//    private AbstractReader ACReader;
    private AbstractReader FFTReader;

    // Initialise the Feature type!
    ArrayList<FeatureExtractor> extractor;

    // k-Nearest Neighbors
    private KNN knn;
    private final int K = 5;

//    //Try to estimate speed of walking
//    private float speed;

    public ActivityMonitoring(Context ctx) {

        Resources res = ctx.getResources();

        // initialise the readers to train kNN
        SDReader = new Reader(ctx, res.getString(R.string.activity_file_feature_sd));
        //SDReader = new ReaderTest(ctx, R.raw.stdfeature);
//        MagReader = new ReaderTest(ctx, R.raw.maxfeature);
//        ACReader = new ReaderTest(ctx, R.raw.acfeature);
        FFTReader = new Reader(ctx, res.getString(R.string.activity_file_feature_fft));
        //FFTReader = new ReaderTest(ctx, R.raw.fftfeature);

        // Choose which features you want
        extractor = new ArrayList<>();
        extractor.add(new FeatureExtractorSD());
        //extractor.add(new FeatureExtractorMag());
//        extractor.add(new FeatureExtractorAC());
//        extractor.add(new FeatureExtractorFFT());

        activityList = ActivityType.getInstance();

        ArrayList<Float> SDList = new ArrayList<>();
//        ArrayList<Float> MagList = new ArrayList<>();
//        ArrayList<Float> ACList = new ArrayList<>();
        ArrayList<Float> FFTList = new ArrayList<>();
        ArrayList<Type> labelsList = new ArrayList<>();

        // Get all data from the trainingData file in resources
        SDReader.readData();
//        MagReader.readData();
//        ACReader.readData();
        FFTReader.readData();
        SDList = SDReader.getAllX();
//         MagList = MagReader.getAllX();
//        ACList = ACReader.getAllX();
        FFTList = FFTReader.getAllX();
        labelsList = SDReader.getAllStates();


        ArrayList<LabeledFeatureSet> train = new ArrayList<>();
        for (int i = 0; i < labelsList.size(); i++) {
            FeatureSet f = new FeatureSet();
            f.addFeature(SDList.get(i));
//            f.addFeature(MagList.get(i));
//            f.addFeature(ACList.get(i));
            f.addFeature(FFTList.get(i));
            train.add(new LabeledFeatureSet(f, labelsList.get(i)));
        }
        knn = new KNN(K, train);
    }

    /**
     * Return the current Activity that the user is doing.
     *
     * @return activity: Queueing, Walking, or None (to be determined activity)
     */
    public Type getActivity() {
        if (activityList.size() == 0) {
            return Type.NONE;
        }
        return activityList.getType(activityList.size() - 1);
    }

    public void update(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z) {
        // Extract features and classify them
        FeatureSet fs = new FeatureSet();
        for (FeatureExtractor ext : extractor) {
            fs.addFeature(ext.extractFeatures(x, y, z));
        }
        Type label = knn.classify(fs);
        activityList.addType(label);
    }
}