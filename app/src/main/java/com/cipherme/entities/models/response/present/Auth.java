package com.cipherme.entities.models.response.present;

import com.cipherme.entities.models.response.data.AuthData;

public final class Auth extends Response<AuthData>{

    @Override
    public AuthData getResult() {
        return data;
    }
}
