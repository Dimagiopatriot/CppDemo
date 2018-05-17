package com.cipherme.entities.models.detect;

import android.graphics.Bitmap;

import com.cipherme.gpe.GPEReader;
import com.cipherme.util.Utils;

import org.opencv.core.Mat;

import java.io.IOException;

public class GpeComputation {

    private static final float DELETER_MAX_WIDTH = 2.0f;
    private static final float DELETER_MIN_WIDTH = 3.2f;
    private static final float DELETER_MAX_HEIGHT = 2.0f;
    private static final float DELETER_MIN_HEIGHT = 3.2f;
    private static final float CENTER_PERCENT_X = 10.0f;
    private static final float CENTER_PECENT_Y = 10.0f;
    private static final double THRES_DELETER = 1.5d;

    private GPEReader gpeReader = new GPEReader();

    public String convertedGpeCode(Mat matRes, ComputationListener listener) {
        Mat gpeRes = new Mat();
        final boolean gpeFounded = gpeReader.findGPE(matRes, DELETER_MAX_WIDTH, DELETER_MIN_WIDTH,
                DELETER_MAX_HEIGHT, DELETER_MIN_HEIGHT, CENTER_PERCENT_X, CENTER_PECENT_Y, gpeRes, THRES_DELETER);
        if (gpeFounded) {
            try {
                listener.onGpeFounded(gpeRes);
                Bitmap bitmap = Bitmap.createBitmap(gpeRes.cols(), gpeRes.rows(), Bitmap.Config.ARGB_8888);
                org.opencv.android.Utils.matToBitmap(gpeRes, bitmap);
                return Utils.toStr(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                listener.onUnlock();
            }
        }
        else {
            listener.onUnlock();
        }
        return null;
    }

    public Double getLaplacian(Mat matRes) {
        return gpeReader.laplacianMode(matRes.nativeObj);
    }
}
