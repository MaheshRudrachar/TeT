package com.teketys.templetickets.entities.order;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.teketys.templetickets.entities.Metadata;

public class OrderResponse {

    @SerializedName("data")
    private List<Order> orders;

    public OrderResponse() {
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderResponse that = (OrderResponse) o;

        return getOrders() != null ? getOrders().equals(that.getOrders()) : that.getOrders() == null;
    }

    @Override
    public int hashCode() {
        return getOrders() != null ? getOrders().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "orders=" + orders +
                '}';
    }
}
