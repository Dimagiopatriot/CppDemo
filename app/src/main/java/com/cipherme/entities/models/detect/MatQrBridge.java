package com.cipherme.entities.models.detect;

import android.text.TextUtils;

import com.cipherme.util.Utils;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MatQrBridge implements ComputationListener {

    private MatComputation matComputation = new MatComputation();
    private GpeComputation gpeComputation = new GpeComputation();

    private Mat gpeMat;
    private Mat qrCode;

    private Boolean isLocked = false;

    private String currentQrCode;
    private String newQrCode;
    private String finalGpe;
    private Map<Double, String> laplacianToGpe; // Mapping laplacian coefficients to gpe;

    public MatQrBridge() {
        laplacianToGpe = new HashMap<>();
    }

    private boolean setAndCheckIsQrChanged(String newQrCode) {
        if (!TextUtils.isEmpty(newQrCode) && !newQrCode.equals(currentQrCode)) {
            currentQrCode = newQrCode;
            clearContainers();
            return true;
        }
        return false;
    }

    private void clearContainers() {
        laplacianToGpe.clear();
    }

    public void getQrCode(Mat resImage) {
        changeLocked(true);

        Mat dest = new Mat();
        resImage.copyTo(dest);
        int c = resImage.width() > resImage.height() ? resImage.height() : resImage.width();
        int square = (int) (c);
        Rect rect = new Rect(resImage.width() / 2 - square/2, resImage.height()/2 - square / 2, square, square);
        Mat res = new Mat(dest, rect);

        matComputation.convertedQrGpe(res, this);
    }

    public boolean allowToComplete() {
        return laplacianToGpe.keySet().size() > 2;
    }

    public String[] getResults() {
        final String[] results = new String[2];
        Double key = null;
        if (!laplacianToGpe.keySet().isEmpty()) {
            key = Utils.findMaxInDoubleSet(laplacianToGpe.keySet());
        }
        if (key != null) {
            finalGpe = laplacianToGpe.get(key);
            results[0] = currentQrCode;
            results[1] = finalGpe;
        }
        return results;
    }

    public void unsubscribe() {
        clearContainers();
        laplacianToGpe = null;
        matComputation = null;
        gpeComputation = null;
        currentQrCode = null;
        finalGpe = null;
    }

    @Override
    public void onQrCodeDecoded(String qrDecoded) {
        newQrCode = qrDecoded;
    }

    @Override
    public void onQrCodeFounded(Mat qrCodeRes) {
        setAndCheckIsQrChanged(newQrCode);
        if (qrCodeRes != null) {
            Mat gpe = new Mat();
            String gpeBase64 = gpeComputation.convertedGpeCode(gpe, qrCodeRes, this);
            Double laplacian = gpeComputation.getLaplacian(gpe);
            gpeMat = gpe;
            onUnlock();
            if (laplacian != null && !TextUtils.isEmpty(gpeBase64)) {
                laplacianToGpe.put(laplacian, gpeBase64);
            }
        }
    }

    @Override
    public void onUnlock() {
        changeLocked(false);
    }

    private void changeLocked(Boolean isLocked) {
        synchronized (this.isLocked) {
            this.isLocked = isLocked;
        }
    }

    public synchronized boolean isLockedRes() {
        return isLocked == null ? false : isLocked;
    }

    public Mat getGpeMat() {
        return gpeMat;
    }

    public String getCurrentQrCode() {
        return currentQrCode;
    }
}
