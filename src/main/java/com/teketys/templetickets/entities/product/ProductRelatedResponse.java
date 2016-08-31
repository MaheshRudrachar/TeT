package com.teketys.templetickets.entities.product;

import com.google.gson.annotations.SerializedName;
import com.teketys.templetickets.entities.wishlist.WishlistItem;

import java.util.List;

/**
 * Created by rudram1 on 8/19/16.
 */
public class ProductRelatedResponse {
    @SerializedName("data")
    private List<Product> items;

    public ProductRelatedResponse() {
    }

    public ProductRelatedResponse(List<Product> items) {

        this.items = items;
    }

    public List<Product> getItems() {
        return items;
    }

    public void setItems(List<Product> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "ProductRelatedResponse{" +
                ", items=" + items +
                '}';
    }
}
