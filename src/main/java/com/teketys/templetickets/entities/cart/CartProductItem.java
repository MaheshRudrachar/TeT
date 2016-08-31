package com.teketys.templetickets.entities.cart;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartProductItem {

    private long key;

    private long product_id;
    private String currency;
    private String thumb;
    private String name;
    private String model;
    private int quantity;
    private boolean stock;

    @SerializedName("price")
    private String priceFormatted;

    @SerializedName("total")
    private String totalFormatted;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isStock() {
        return stock;
    }

    public void setStock(boolean stock) {
        this.stock = stock;
    }

    public String getPriceFormatted() {
        return priceFormatted;
    }

    public void setPriceFormatted(String priceFormatted) {
        this.priceFormatted = priceFormatted;
    }

    public String getTotalFormatted() {
        return totalFormatted;
    }

    public void setTotalFormatted(String totalFormatted) {
        this.totalFormatted = totalFormatted;
    }


    public List<CartProductItemVariant> getVariant() {
        return variant;
    }

    public void setVariant(List<CartProductItemVariant> variant) {
        this.variant = variant;
    }

    @SerializedName("option")
    private List<CartProductItemVariant> variant;


    public CartProductItem() {
    }



    @Override
    public String toString() {
        return "CartProductItem{" +
                "key=" + key +
                ", thumb=" + thumb +
                ", name=" + name +
                ", model=" + model +
                ", quantity='" + quantity + '\'' +
                ", variant=" + variant +
                ", stock=" + stock +
                ", priceFormatted=" + priceFormatted +
                ", totalFormatted=" + totalFormatted +
                '}';
    }
}
