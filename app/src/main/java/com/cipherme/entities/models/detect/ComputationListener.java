package com.cipherme.entities.models.detect;

import org.opencv.core.Mat;

public interface ComputationListener extends UnlockResource{

    void onQrCodeFounded(Mat qrCodeRes);
    void onQrCodeDecoded(String qrDecoded);
}
