package com.cipherme.api;

//import com.cipherme.util.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class HttpLogginInterceptor implements Interceptor {

//    private static final Logger LOGGER = Logger.getLogger(HttpLogginInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {

//        LOGGER.log("Method: --> " + chain.request().method());
//        LOGGER.log("Body: --> " + chain.request().body());
//        LOGGER.log("Headers: --> " + chain.request().headers());
//        LOGGER.log("Url: --> " + chain.request().url());

        return chain.proceed(chain.request());
    }
}
