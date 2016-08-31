package com.teketys.templetickets.entities.product;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

public class ProductColor {


    @SerializedName("product_option_id")
    private long product_option_id;

    private long product_option_value_id;
    private String name;

    private String image;
    private long quantity;

    public ProductColor() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ProductColor(long product_option_id) {
        this.product_option_id = product_option_id;
    }

    public long getProduct_option_value_id() {
        return product_option_value_id;
    }

    public void setProduct_option_value_id(long product_option_value_id) {
        this.product_option_value_id = product_option_value_id;
    }

    public long getProduct_option_id() {
        return product_option_id;
    }

    public void setProduct_option_id(long product_option_id) {
        this.product_option_id = product_option_id;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProductColor{" +
                ", name='" + name + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
