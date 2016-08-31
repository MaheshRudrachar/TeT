package com.teketys.templetickets.entities.product;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

public class ProductSize {

    private long product_option_id;

    private long product_option_value_id;

    @SerializedName("option_id")
    private long remoteId;
    private String name;

    public ProductSize() {
    }

    public ProductSize(long product_option_id, long remoteId, String name) {
        this.product_option_id = product_option_id;
        this.remoteId = remoteId;
        this.name = name;
    }

    public long getProduct_option_id() {
        return product_option_id;
    }

    public void setProduct_option_id(long product_option_id) {
        this.product_option_id = product_option_id;
    }

    public long getProduct_option_value_id() {
        return product_option_value_id;
    }

    public void setProduct_option_value_id(long product_option_value_id) {
        this.product_option_value_id = product_option_value_id;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProductSize{" +
                "product_option_id=" + product_option_id +
                ", remoteId=" + remoteId +
                ", name='" + name + '\'' +
                '}';
    }
}

