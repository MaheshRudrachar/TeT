package com.teketys.templetickets.entities.order;

import com.google.gson.annotations.SerializedName;
import com.teketys.templetickets.entities.product.Product;

import java.util.List;

/**
 * Created by rudram1 on 8/31/16.
 */
public class CustomerOrderDetails {
    private String order_id;
    private String invoice_no;
    private String invoice_prefix;
    private String store_name;
    private String email;
    private String total;
    private String payment_firstname;
    private String payment_lastname;
    private String payment_address_1;
    private String payment_postcode;
    private String payment_city;
    private String payment_country;
    private String payment_method;
    private String currency_code;
    private String date_modified;

    @SerializedName("histories")
    private List<CustomerOrderHistory> histories;

    @SerializedName("products")
    private List<CustomerOrderProduct> products;

    @SerializedName("totals")
    private List<CustomerOrderTotal> totals;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getInvoice_prefix() {
        return invoice_prefix;
    }

    public void setInvoice_prefix(String invoice_prefix) {
        this.invoice_prefix = invoice_prefix;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getPayment_firstname() {
        return payment_firstname;
    }

    public void setPayment_firstname(String payment_firstname) {
        this.payment_firstname = payment_firstname;
    }

    public String getPayment_lastname() {
        return payment_lastname;
    }

    public void setPayment_lastname(String payment_lastname) {
        this.payment_lastname = payment_lastname;
    }

    public String getPayment_address_1() {
        return payment_address_1;
    }

    public void setPayment_address_1(String payment_address_1) {
        this.payment_address_1 = payment_address_1;
    }

    public String getPayment_postcode() {
        return payment_postcode;
    }

    public void setPayment_postcode(String payment_postcode) {
        this.payment_postcode = payment_postcode;
    }

    public String getPayment_city() {
        return payment_city;
    }

    public void setPayment_city(String payment_city) {
        this.payment_city = payment_city;
    }

    public String getPayment_country() {
        return payment_country;
    }

    public void setPayment_country(String payment_country) {
        this.payment_country = payment_country;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public List<CustomerOrderProduct> getProducts() {
        return products;
    }

    public void setProducts(List<CustomerOrderProduct> products) {
        this.products = products;
    }

    public List<CustomerOrderTotal> getTotals() {
        return totals;
    }

    public void setTotals(List<CustomerOrderTotal> totals) {
        this.totals = totals;
    }

    public List<CustomerOrderHistory> getHistories() {
        return histories;
    }

    public void setHistories(List<CustomerOrderHistory> histories) {
        this.histories = histories;
    }
}
