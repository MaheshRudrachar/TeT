package com.teketys.templetickets.entities.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rudram1 on 8/29/16.
 */
public class OrderConfirmResponse {

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

    public OrderConfirm getConfirm() {
        return confirm;
    }

    public void setConfirm(OrderConfirm confirm) {
        this.confirm = confirm;
    }

    @SerializedName("data")
    private OrderConfirm confirm;
}
