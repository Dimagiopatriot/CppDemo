package com.cipherme.entities.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class Product {

    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("info")
    private List<String> info;

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public List<String> getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "Product{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", info='" + info == null ? "null" : info.toString() + '\'' +
                '}';
    }
}
