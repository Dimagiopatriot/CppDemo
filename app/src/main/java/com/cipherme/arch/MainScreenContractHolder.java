package com.cipherme.arch;

import com.cipherme.entities.models.response.present.GetKey;
import com.cipherme.entities.models.response.present.Verify;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

public interface MainScreenContractHolder {

    interface MainView extends MVPView {

        void onSuccessfulGetKey(GetKey getKey);

        void onSuccessfulAuth(String token);

        void onSuccessfulVerify(Verify verify);

        void onSuccessfulQrGpe(String[] results, String token);

        void onShowGPE(Mat mat);

        void onFailure(Throwable throwable);

        void onFailure(String message);
    }

    interface MainScreenContract {

        void getKey(String uuid);

        void auth(String authKey);

        void prepareVerify(String token);

        void verify(String gpe, String qr, String token);

        void computeGpe();

        void setMainFrame(Mat mainFrame);
    }
}
