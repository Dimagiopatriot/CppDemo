package com.cipherme.entities.models.request;

public final class GetKeyRequest {
    final String deviceUuid;

    public GetKeyRequest(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }
}
