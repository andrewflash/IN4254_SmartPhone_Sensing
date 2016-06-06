package nl.tudelft.xflash.activitymonitoringandlocalization.Misc;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xflash on 4-5-16.
 */
public class ArrayOperations {
    public static float maximumValue(ArrayList<Float> in){
        if(in.isEmpty())
            return 0;
        float max = in.get(0);
        for(Float f:in){
            if(f>max)
                max = f;
        }
        return max;
    }
    public static float maximumValueFrom(int i,float[] in){
        float max = in[i];
        for(int index = i;index<in.length;index++){
            if(in[index]>max)
                max = in[index];
        }
        return max;
    }
    public static int indexFirstMaximumFromInt(int i,int[] in){
        int max = in[i];
        int indexOut = i;
        for(int index = i;index<in.length;index++){
            if(in[index]>max){
                max = in[index];
                indexOut = index;
            }
        }
        return indexOut;
    }
    public static int indexFirstMaximumFrom(int i,List<Float> in){
        float max = in.get(i);
        int maxIndex = i;
        for(int index = i;index<in.size();index++){
            if(in.get(index)>max) {
                max = in.get(index);
                maxIndex = index;
            }
        }
        return maxIndex;
    }
    public static int indexFirstMinimumFrom(int i,List<Double> in){
        double min = in.get(i);
        int minIndex = i;
        for(int index = i;index<in.size();index++){
            if(in.get(index)<min) {
                min = in.get(index);
                minIndex = index;
            }
        }
        return minIndex;
    }
    public static float sum(List<Float> in){
        float s = 0 ;
        for(Float f : in){
            s += f;
        }
        return s;
    }

    public static float mean(List<Float> in){
        return sum(in)/in.size();
    }

    public static float standardDeviation(List<Float> in){
        float mean = ArrayOperations.sum(in)/in.size();

        double SD = 0;

        for (int i = 0;i<in.size();i++){
            SD = SD + Math.pow(in.get(i)-mean,2.0);
        }

        SD = SD/in.size();
        SD = Math.sqrt(SD);
        return (float) SD;
    }

    public static float[] matrixMultiplication3x3(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }
}