package com.cipherme.api;

//import com.cipherme.util.Logger;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

public class HttpLogginInterceptor implements Interceptor {

//    private static final Logger LOGGER = Logger.getLogger(HttpLogginInterceptor.class);
    private final static String TAG = "HttpLogginInterceptor";



    @Override
    public Response intercept(Chain chain) throws IOException {

        Log.d(TAG, "Method: --> " + chain.request().method());
        Log.d(TAG, "Body: --> " + bodyToString(chain.request()));
        Log.d(TAG, "Body size --> " + chain.request().toString().length());
        Log.d(TAG, "Headers: --> " + chain.request().headers());
        Log.d(TAG, "Url: --> " + chain.request().url());

        return chain.proceed(chain.request());
    }

    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
