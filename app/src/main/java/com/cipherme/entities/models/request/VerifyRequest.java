package com.cipherme.entities.models.request;

public final class VerifyRequest {
    final String qr;
    final String gpe;

    public VerifyRequest(String qr, String gpe) {
        this.qr = qr;
        this.gpe = gpe;
    }
}
