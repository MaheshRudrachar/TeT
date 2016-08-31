package com.teketys.templetickets.entities.wishlist;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.android.gms.analytics.ecommerce.Product;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WishlistResponse {

    @SerializedName("products")
    private List<WishlistItem> products;

    public WishlistResponse() {
    }

    public WishlistResponse(List<WishlistItem> products) {

        this.products = products;
    }

    public List<WishlistItem> getProducts() {
        return products;
    }

    public void setProducts(List<WishlistItem> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "WishlistResponse{" +
                ", products=" + products +
                '}';
    }
}
