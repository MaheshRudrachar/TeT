package com.teketys.templetickets.entities.order;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rudram1 on 9/7/16.
 */
public class CusromerOrderHistoryRecord {

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

    public CustomerOrderDetails getCustomerOrderDetails() {
        return customerOrderDetails;
    }

    public void setCustomerOrderDetails(CustomerOrderDetails customerOrderDetails) {
        this.customerOrderDetails = customerOrderDetails;
    }

    @SerializedName("data")
    private CustomerOrderDetails customerOrderDetails;
}
