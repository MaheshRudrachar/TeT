package com.teketys.templetickets.entities.cart;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import com.teketys.templetickets.entities.product.ProductColor;
import com.teketys.templetickets.entities.product.ProductSize;

public class CartProductItemVariant {

    private String name;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String value;

    public CartProductItemVariant() {
    }

    @Override
    public String toString() {
        return "CartProductItemVariant{" +
                "name=" + name +
                ", value=" + value +
                '}';
    }
}
