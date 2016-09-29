package com.teketys.templetickets.entities.wishlist;

import com.google.gson.annotations.SerializedName;
import com.teketys.templetickets.entities.product.Product;

import java.util.List;

/**
 * Created by rudram1 on 9/6/16.
 */
public class WishlistRecord {

    @SerializedName("products")
    private List<WishlistItem> products;

    public List<WishlistItem> getProducts() {
        return products;
    }

    public void setProducts(List<WishlistItem> products) {
        this.products = products;
    }
}
