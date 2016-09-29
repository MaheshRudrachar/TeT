package com.teketys.templetickets.entities.wishlist;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.android.gms.analytics.ecommerce.Product;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WishlistResponse {

    public WishlistRecord getWishlistRecords() {
        return wishlistRecords;
    }

    private String statusCode;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    private String statusText;

    public void setWishlistRecords(WishlistRecord wishlistRecords) {
        this.wishlistRecords = wishlistRecords;
    }

    @SerializedName("data")
    private WishlistRecord wishlistRecords;


}
