package com.cipherme.arch;

import com.cipherme.entities.models.response.present.GetKey;
import com.cipherme.entities.models.response.present.Verify;

public interface MainScreenContractHolder {

    interface MainView extends MVPView {

        void onSuccessfulGetKey(GetKey getKey);

        void onSuccessfulAuth(String token);

        void onSuccessfulVerify(Verify verify);

        void onFailure(Throwable throwable);

        void onFailure(String message);
    }

    interface MainScreenContract {

        void getKey(String uuid);

        void auth(String authKey);

        void verify(String gpe, String qr, String token);
    }
}
