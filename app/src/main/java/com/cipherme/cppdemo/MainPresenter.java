package com.cipherme.cppdemo;

import android.text.TextUtils;
import android.util.Log;

import com.cipherme.api.GetKeyAuthApi;
import com.cipherme.api.VerifyApi;
import com.cipherme.api.headers.HeadersProvider;
import com.cipherme.arch.BasePresenter;
import com.cipherme.arch.MainScreenContractHolder;
import com.cipherme.entities.models.detect.MatQrBridge;
import com.cipherme.entities.models.request.AuthRequest;
import com.cipherme.entities.models.request.GetKeyRequest;
import com.cipherme.entities.models.request.VerifyRequest;
import com.cipherme.entities.models.response.present.Auth;
import com.cipherme.entities.models.response.present.GetKey;
import com.cipherme.entities.models.response.present.Verify;
import com.cipherme.util.RxUtil;
import com.cipherme.util.Utils;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainPresenter extends BasePresenter<MainScreenContractHolder.MainView> implements
        MainScreenContractHolder.MainScreenContract {

    MainScreenContractHolder.MainView mView;
    CompositeDisposable mDisposable;
    GetKeyAuthApi mGetKeyApi;
    VerifyApi mVerifyApi;
    MatQrBridge mMatQrBridge;

    Observable<Mat> gpeObservable;
    ObservableEmitter<Mat> frameObservableEmitter;
    Disposable frameDisposable;

    public MainPresenter(Retrofit retrofit, MainScreenContractHolder.MainView mView) {
        this.mView = mView;
        mDisposable = new CompositeDisposable();
        mGetKeyApi = retrofit.create(GetKeyAuthApi.class);
        mVerifyApi = retrofit.create(VerifyApi.class);
        mMatQrBridge = new MatQrBridge();
    }

    @Override
    public void getKey(String uuid) {
        final Map<String, String> headers = Utils.baseHeaders();

        mDisposable.add(
                RxUtil.buildRxRequest(mGetKeyApi
                        .getAuthKey(headers, new GetKeyRequest(uuid)))
                        .subscribe(this::getKeyResultStrategy, error -> mView.onFailure(error)));
    }

    @Override
    public void auth(String authKey) {
        final Map<String, String> headers = Utils.baseHeaders();

        mDisposable.add(
                RxUtil.buildRxRequest(mGetKeyApi
                        .getToken(headers, new AuthRequest(authKey)))
                        .subscribe(this::authResultStrategy, error -> mView.onFailure(error)));
    }

    @Override
    public void prepareVerify(String token) {

        gpeObservable = Observable.create(emitter -> frameObservableEmitter = emitter);

        mDisposable.add(gpeObservable
                .observeOn(Schedulers.computation())
                .doOnNext(mat -> {
                    mMatQrBridge.getQrCode(mat);
                    mView.onShowGPE(mMatQrBridge.getGpeMat());
                })
                .takeUntil(mat -> (mMatQrBridge.allowToComplete() && !TextUtils.isEmpty(mMatQrBridge.getCurrentQrCode())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> {}, error -> mView.onFailure(error), () -> {
                    final String[] res = mMatQrBridge.getResults();
                    mView.onSuccessfulQrGpe(res, token);
                }));
    }

    @Override
    public void verify(String gpe, String qr, String token) {
        final Map<String, String> headers = Utils.baseHeaders();
        headers.put(HeadersProvider.Key.AUTH_TOKEN.getValue(), TextUtils.isEmpty(token) ? "" : token);

        if (!TextUtils.isEmpty(qr) && !TextUtils.isEmpty(gpe)) {
            mDisposable.add(RxUtil.buildRxRequest(mVerifyApi
                    .getVerifiedResult(headers, new VerifyRequest(qr, gpe)))
                    .subscribe(this::verifyResultStrategy, error -> mView.onFailure(error)));
        }
        else {
            mView.onFailure("gpe or qr is null");
        }
    }

    @Override
    public void computeGpe(final CameraBridgeViewBase.CvCameraViewFrame frame) {
        if (frame == null) {
            frameObservableEmitter.onComplete();
            return;
        }
        if (frameObservableEmitter != null && !frameObservableEmitter.isDisposed()) {
            frameObservableEmitter.onNext(frame.rgba());
        }
    }

    @Override
    public void detachView() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mMatQrBridge.unsubscribe();
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

    private void verifyResultStrategy(Verify verify) {
        if (verify == null) {
            mView.onFailure("Verify NULL");
            return;
        }
        if (verify.getResult() != null) {
            mView.onSuccessfulVerify(verify);
        }
        else if (verify.getError() != null){
            mView.onFailure(verify.getError().getDescription());
        }
    }
}
