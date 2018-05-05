package com.cipherme.entities.models.response.data;

import com.google.gson.annotations.SerializedName;

public final class AuthData {

    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }
}
