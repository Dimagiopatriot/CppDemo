package com.cipherme.gpe;

public class PercentCompare {

    public static boolean isEqual(double a, double b, float percent) {
        if (a == b) {
            return true;
        }
        final double[] swap = new double[] {a, b};
        GZEMathematics.maxSwap(swap);
        a = swap[0];
        b = swap[1];
        if (b > 0){
            final float c = (float) a / (float) b * 100f - 100f;
            return (percent - c) >= 0.0f;
        }
        return false;
    }
}
