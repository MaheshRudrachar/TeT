package com.teketys.templetickets.entities.cart;

/**
 * Created by rudram1 on 9/7/16.
 */
public class CartDeleteResponse {
    private String success;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    private String total;
}
