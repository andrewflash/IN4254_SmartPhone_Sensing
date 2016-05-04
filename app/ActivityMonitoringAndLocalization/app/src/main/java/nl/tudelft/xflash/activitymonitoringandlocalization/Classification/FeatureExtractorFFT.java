package nl.tudelft.xflash.activitymonitoringandlocalization.Classification;

import java.util.ArrayList;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;
import nl.tudelft.xflash.activitymonitoringandlocalization.Misc.ArrayOperations;

/**
 * Created by xflash on 4-5-16.
 */
public class FeatureExtractorFFT extends FeatureExtractor {
    @Override
    public FeatureSet extractFeatures(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z) {
        ArrayList<Float> magnitude = new ArrayList<>(x.size());

        for (int i = 0; i < x.size(); i++) {
            magnitude.add(i, (float) Math.sqrt(Math.pow(x.get(i), 2.0) + Math.pow(y.get(i), 2.0) + Math.pow(z.get(i), 2.0)));
        }
        float[] magnitudeArray = new float[magnitude.size()];
        int i = 0;
        for (Float f : magnitude) {
            magnitudeArray[i++] = f;
        }
        FloatFFT_1D fftCalculator = new FloatFFT_1D(magnitudeArray.length);
        float[] fftArray = new float[magnitudeArray.length * 2];
        System.arraycopy(magnitudeArray, 0, fftArray, 0, magnitudeArray.length);
        fftCalculator.realForwardFull(fftArray);
        float[] fftArrayAbs = new float[magnitudeArray.length];
        for (int j = 0; j < fftArray.length; j += 2) {
            fftArrayAbs[j / 2] = (float) Math.sqrt(fftArray[j] * fftArray[j] + fftArray[j + 1] * fftArray[j + 1]);
        }
        return new FeatureSet(ArrayOperations.maximumValueFrom(1, fftArrayAbs));
    }
}