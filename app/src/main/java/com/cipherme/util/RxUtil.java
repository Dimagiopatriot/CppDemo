package com.cipherme.util;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxUtil {

    public static <T> Observable<T> buildRxRequest(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
/*                .retryWhen(ExponentialBackoff.exponentialBackoffForExceptions(1, 4, TimeUnit.SECONDS, Exception.class))*/;
    }
}
