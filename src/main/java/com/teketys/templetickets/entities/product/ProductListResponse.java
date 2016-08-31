package com.teketys.templetickets.entities.product;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.teketys.templetickets.entities.Metadata;

public class ProductListResponse {

    //private Metadata metadata;

    @SerializedName("data")
    private List<Product> products;

    /*public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }*/

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "ProductListResponse{" +
                ", products=" + products +
                '}';
    }
}
