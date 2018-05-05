package com.cipherme.entities.models.response.data;

import com.google.gson.annotations.SerializedName;

public final class GetKeyData {

    @SerializedName("auth_key")
    private String authKey;

    public String getAuthKey() {
        return authKey;
    }
}
