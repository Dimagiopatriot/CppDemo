package com.cipherme.entities.models.request;

import android.util.Base64;

public final class VerifyRequest {
    final String qr;
    final String gpe;

    public VerifyRequest(String qr, String gpe) {
        this.qr = Base64.encodeToString(qr.getBytes(), Base64.DEFAULT);
        this.gpe = gpe.replaceAll("\\n", "");
    }

    private String changeQr(String qr) {
        return qr.replace("-02.00", "");
    }
}
