package com.cipherme.entities.models.response;

import com.google.gson.annotations.SerializedName;

public final class ScanResult {

    @SerializedName("code")
    private int code;
    @SerializedName("description")
    private String description;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
