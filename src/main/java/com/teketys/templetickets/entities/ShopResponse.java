package com.teketys.templetickets.entities;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopResponse {

    //Metadata metadata;

    @SerializedName("data")
    Shop shop;

    public ShopResponse() {
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Override
    public String toString() {
        return "ShopResponse{" +
                "shop=" + shop +
                '}';
    }
}
