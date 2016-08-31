package com.teketys.templetickets.entities.order;

import com.google.gson.annotations.SerializedName;
import com.teketys.templetickets.entities.cart.CartProductItem;
import com.teketys.templetickets.entities.product.Product;

import java.util.Date;
import java.util.List;

/**
 * Created by rudram1 on 8/27/16.
 */
public class CustomerOrder {

    private long order_id;
    private String name;
    private String status;
    private String date_added;
    private int products;
    private double currency_value;
    private String total;

    private String firstName;
    private String lastName;
    private String dateCreated;
    private String totalFormatted;
    private String shippingName;
    private String shippingPriceFormatted;

    public int getProducts() {
        return products;
    }

    public void setProducts(int products) {
        this.products = products;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTotalFormatted() {
        return totalFormatted;
    }

    public void setTotalFormatted(String totalFormatted) {
        this.totalFormatted = totalFormatted;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public String getShippingPriceFormatted() {
        return shippingPriceFormatted;
    }

    public void setShippingPriceFormatted(String shippingPriceFormatted) {
        this.shippingPriceFormatted = shippingPriceFormatted;
    }

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public double getCurrency_value() {
        return currency_value;
    }

    public void setCurrency_value(double currency_value) {
        this.currency_value = currency_value;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "CustomerOrder{" +
                "order_id='" + order_id + '\'' +
                ", status='" + status + '\'' +
                ", date_added=" + date_added +
                ", products=" + products +
                ", currency_value=" + currency_value +
                ", total=" + total +
                '}';
    }
}
