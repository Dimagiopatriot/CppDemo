package com.cipherme.entities.models.response.present;

import com.cipherme.entities.models.response.data.GetKeyData;

public final class GetKey extends Response<GetKeyData> {

    @Override
    public GetKeyData getResult() {
        return data;
    }
}
