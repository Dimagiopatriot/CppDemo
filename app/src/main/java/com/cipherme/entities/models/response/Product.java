package com.cipherme.entities.models.response;

import com.google.gson.annotations.SerializedName;

public final class Product {

    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("info")
    private String info;

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getInfo() {
        return info;
    }
}
