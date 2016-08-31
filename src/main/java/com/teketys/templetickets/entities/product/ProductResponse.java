package com.teketys.templetickets.entities.product;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rudram1 on 8/23/16.
 */
public class ProductResponse {

    @SerializedName("data")
    private Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
