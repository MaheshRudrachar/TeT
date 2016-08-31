package com.teketys.templetickets.entities.product;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class ProductVariant {

    private long product_option_id;
    private long option_id;
    private String name;
    private String type;
    private Integer required;

    @SerializedName("option_value")
    private List<ProductVariantValues> option_value;

    //private ProductColor color;
    //private ProductSize size;

    //private String code;

    public ProductVariant() {
    }

    public ProductVariant(long id, ProductSize size) {
        this.option_id = id;

    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public Integer getRequired() {
        return this.required;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public long getProductOptionId() {
        return product_option_id;
    }

    public void setProductionOptionId(long product_option_id) {
        this.product_option_id = product_option_id;
    }

    public long getOptionId() { return option_id; }

    public void setOptionId(long option_id) {
        this.option_id = option_id;
    }

    public List<ProductVariantValues> getProductVariantValues() {
        return this.option_value;
    }

    public void setRelated(List<ProductVariantValues> option_value) {
        this.option_value = option_value;
    }
}
