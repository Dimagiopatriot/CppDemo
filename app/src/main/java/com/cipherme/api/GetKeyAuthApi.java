package com.cipherme.api;

import com.cipherme.entities.models.request.AuthRequest;
import com.cipherme.entities.models.request.GetKeyRequest;
import com.cipherme.entities.models.response.present.Auth;
import com.cipherme.entities.models.response.present.GetKey;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface GetKeyAuthApi {

    @POST("get-key/")
    Observable<GetKey> getAuthKey(@HeaderMap Map<String, String> headers, @Body GetKeyRequest request);

    @POST("auth/")
    Observable<Auth> getToken(@HeaderMap Map<String, String> headers, @Body AuthRequest request);

}
