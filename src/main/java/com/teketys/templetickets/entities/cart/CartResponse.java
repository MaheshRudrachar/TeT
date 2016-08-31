package com.teketys.templetickets.entities.cart;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

/**
 * Created by rudram1 on 8/24/16.
 */
public class CartResponse {

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @SerializedName("data")
    private Cart cart;

}
