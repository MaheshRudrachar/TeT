package com.teketys.templetickets.entities.product;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rudram1 on 8/26/16.
 */
public class ProductTime {
    @SerializedName("product_option_id")
    private long product_option_id;

    private long product_option_value_id;
    private String name;

    private String image;
    private long quantity;

    public ProductTime() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ProductTime(long product_option_id) {
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
