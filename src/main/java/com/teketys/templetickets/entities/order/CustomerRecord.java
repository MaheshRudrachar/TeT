package com.teketys.templetickets.entities.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rudram1 on 8/31/16.
 */
public class CustomerRecord {

    @SerializedName("orders")
    private List<CustomerOrder> customerOrders;

    public List<CustomerOrder> getCustomerOrders() {
        return customerOrders;
    }

    public void setCustomerOrders(List<CustomerOrder> customerOrders) {
        this.customerOrders = customerOrders;
    }
}
