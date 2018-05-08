package com.cipherme.entities.models.response.present;

import com.cipherme.entities.models.response.data.VerifyData;

public final class Verify extends Response<VerifyData> {

    @Override
    public VerifyData getResult() {
        return data;
    }

    @Override
    public String toString() {
        return "Verify{" +
                "data=" + data == null ? "" : data.toString() +
                '}';
    }
}
