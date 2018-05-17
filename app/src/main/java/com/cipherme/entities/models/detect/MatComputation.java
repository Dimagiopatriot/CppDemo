package com.cipherme.entities.models.detect;

import android.graphics.Bitmap;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.common.HybridBinarizer;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class MatComputation {

    private LuminanceSource source;
    private HybridBinarizer hybridBinarizer;
    private BinaryBitmap binaryBitmap;
    private Reader reader = new MultiFormatReader();

    public void convertedQrGpe(Mat mat, ComputationListener listener) {
        Mat qrCode = new Mat(1000, 1000, CvType.CV_8UC3);
        final int qrCodeFounded = 0; /*calcQR(mat.nativeObj, qrCode.nativeObj);*/
        if (qrCodeFounded == 0) {
            Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, bitmap);
            try {
                listener.onQrCodeDecoded(decodeQr(bitmap));
                listener.onQrCodeFounded(mat);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                listener.onUnlock();
            }
        } else {
            listener.onUnlock();
        }
    }

    private String decodeQr(Bitmap bitmap) throws Exception {
        clearReferences();
        int[] bmpArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(bmpArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
                bitmap.getHeight());
        source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), bmpArray);
        hybridBinarizer = new HybridBinarizer(source);
        binaryBitmap = new BinaryBitmap(hybridBinarizer);
        return reader.decode(binaryBitmap).getText();
    }

    private void clearReferences() {
        source = null;
        hybridBinarizer = null;
        binaryBitmap = null;
    }

    private static native int calcQR(long matRes, long matQr);
}
