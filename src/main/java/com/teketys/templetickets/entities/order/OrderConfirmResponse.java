package com.teketys.templetickets.entities.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rudram1 on 8/29/16.
 */
public class OrderConfirmResponse {

    public OrderConfirm getConfirm() {
        return confirm;
    }

    public void setConfirm(OrderConfirm confirm) {
        this.confirm = confirm;
    }

    @SerializedName("data")
    private OrderConfirm confirm;
}
