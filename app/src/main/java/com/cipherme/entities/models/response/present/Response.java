package com.cipherme.entities.models.response.present;

import com.cipherme.entities.models.response.Error;
import com.google.gson.annotations.SerializedName;

public abstract class Response<T> {

    @SerializedName("data")
    protected T data;

    @SerializedName("error")
    private Error error;

    public Error getError() {
        return error;
    }

    abstract public T getResult();
}
