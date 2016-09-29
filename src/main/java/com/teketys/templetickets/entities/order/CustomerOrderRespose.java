package com.teketys.templetickets.entities.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rudram1 on 8/27/16.
 */

public class CustomerOrderRespose {

    private String statusCode;
    private String statusText;

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

    public CustomerRecord getCustomerRecord() {
        return customerRecord;
    }

    public void setCustomerRecord(CustomerRecord customerRecord) {
        this.customerRecord = customerRecord;
    }

    @SerializedName("data")
    private CustomerRecord customerRecord;

    public CustomerOrderRespose() {

    }
}
