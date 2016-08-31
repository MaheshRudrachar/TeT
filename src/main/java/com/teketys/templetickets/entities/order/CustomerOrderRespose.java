package com.teketys.templetickets.entities.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rudram1 on 8/27/16.
 */

public class CustomerOrderRespose {

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
