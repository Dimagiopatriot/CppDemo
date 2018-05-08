package com.cipherme.entities.models.response.data;

import com.cipherme.entities.models.response.Product;
import com.cipherme.entities.models.response.ScanResult;
import com.google.gson.annotations.SerializedName;

public final class VerifyData {

    @SerializedName("scanResult")
    private ScanResult scanResult;

    @SerializedName("product")
    private Product product;

    public ScanResult getScanResult() {
        return scanResult;
    }

    public Product getProduct() {
        return product;
    }

    @Override
    public String toString() {
        return "VerifyData{" +
                "scanResult=" + scanResult == null ? "scan: null" : scanResult.toString() +
                ", product=" + product == null ? "product: null" : product.toString() +
                '}';
    }
}
