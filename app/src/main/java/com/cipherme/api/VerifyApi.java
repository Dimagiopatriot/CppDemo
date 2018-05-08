package com.cipherme.api;

import com.cipherme.entities.models.request.VerifyRequest;
import com.cipherme.entities.models.response.present.Verify;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface VerifyApi {

    @POST("verify/")
    Observable<Verify> getVerifiedResult(@HeaderMap Map<String, String> headers, @Body VerifyRequest request);

}
