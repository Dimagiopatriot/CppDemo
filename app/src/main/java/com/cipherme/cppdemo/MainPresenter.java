package com.cipherme.cppdemo;

import android.text.TextUtils;

import com.cipherme.api.GetKeyAuthApi;
import com.cipherme.api.VerifyApi;
import com.cipherme.api.headers.HeadersProvider;
import com.cipherme.arch.BasePresenter;
import com.cipherme.arch.MainScreenContractHolder;
import com.cipherme.entities.models.request.AuthRequest;
import com.cipherme.entities.models.request.GetKeyRequest;
import com.cipherme.entities.models.request.VerifyRequest;
import com.cipherme.entities.models.response.present.Auth;
import com.cipherme.entities.models.response.present.GetKey;
import com.cipherme.util.RxUtil;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Retrofit;

public class MainPresenter extends BasePresenter<MainScreenContractHolder.MainView> implements
        MainScreenContractHolder.MainScreenContract {

    MainScreenContractHolder.MainView mView;
    CompositeDisposable mDisposable;
    GetKeyAuthApi mGetKeyApi;
    VerifyApi mVerifyApi;

    public MainPresenter(Retrofit retrofit, MainScreenContractHolder.MainView mView) {
        this.mView = mView;
        mDisposable = new CompositeDisposable();
        mGetKeyApi = retrofit.create(GetKeyAuthApi.class);
        mVerifyApi = retrofit.create(VerifyApi.class);
    }

    @Override
    public void getKey(String uuid) {
        final Map<String, String> headers = new HashMap<>(3);

        headers.put(HeadersProvider.Key.VERSION.getValue(), "2.0");
        headers.put(HeadersProvider.Key.ACCEPT_LANGUAGE.getValue(), "ru");
        headers.put(HeadersProvider.Key.CONTENT_TYPE.getValue(), "application/json");

        mDisposable.add(
                RxUtil.buildRxRequest(mGetKeyApi
                        .getAuthKey(headers, new GetKeyRequest(uuid)))
                        .subscribe(this::getKeyResultStrategy, error -> mView.onFailure(error)));
    }

    @Override
    public void auth(String authKey) {
        final Map<String, String> headers = new HashMap<>(4);

        headers.put(HeadersProvider.Key.VERSION.getValue(), "2.0");
        headers.put(HeadersProvider.Key.ACCEPT_LANGUAGE.getValue(), "ru");
        headers.put(HeadersProvider.Key.CONTENT_TYPE.getValue(), "application/json");

        mDisposable.add(
                RxUtil.buildRxRequest(mGetKeyApi
                        .getToken(headers, new AuthRequest(authKey)))
                        .subscribe(this::authResultStrategy, error -> mView.onFailure(error)));
    }

    @Override
    public void verify(String gpe, String qr, String token) {
        final Map<String, String> headers = new HashMap<>(4);

        headers.put(HeadersProvider.Key.VERSION.getValue(), "2.0");
        headers.put(HeadersProvider.Key.ACCEPT_LANGUAGE.getValue(), "ru");
        headers.put(HeadersProvider.Key.CONTENT_TYPE.getValue(), "application/json");
        headers.put(HeadersProvider.Key.AUTH_TOKEN.getValue(), TextUtils.isEmpty(token) ? "" : token);

        mDisposable.add(RxUtil.buildRxRequest(mVerifyApi
                        .getVerifiedResult(headers, new VerifyRequest(qr, gpe)))
                        .subscribe(verify -> mView.onSuccessfulVerify(verify), error -> mView.onFailure(error)));
    }

    @Override
    public void detachView() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        super.detachView();
    }

    private void authResultStrategy(Auth auth) {
        if (auth == null) {
            mView.onFailure("Auth null");
            return;
        }
        if (auth.getResult() != null) {
            mView.onSuccessfulAuth(auth.getResult().getToken());
        }
        else if (auth.getError() != null){
            mView.onFailure(auth.getError().getDescription());
        }
    }

    private void getKeyResultStrategy(GetKey getKey) {
        if (getKey == null) {
            mView.onFailure("Get key NULL");
            return;
        }
        if (getKey.getResult() != null) {
            mView.onSuccessfulGetKey(getKey);
        }
        else if (getKey.getError() != null){
            mView.onFailure(getKey.getError().getDescription());
        }
    }
}
