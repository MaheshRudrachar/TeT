package com.teketys.templetickets.entities.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rudram1 on 8/31/16.
 */
public class CustomerOrderProduct {
    private String name;
    private String model;

    @SerializedName("option")
    private List<CustomerOrderVariation> options;
    private String quantity;
    private String price;
    private String total;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<CustomerOrderVariation> getOptions() {
        return options;
    }

    public void setOptions(List<CustomerOrderVariation> options) {
        this.options = options;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
