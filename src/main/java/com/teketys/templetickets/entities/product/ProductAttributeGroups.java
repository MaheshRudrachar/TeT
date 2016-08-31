package com.teketys.templetickets.entities.product;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rudram1 on 8/31/16.
 */
public class ProductAttributeGroups {
    private String attribute_group_id;
    private String name;

    public List<ProductAttributes> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(List<ProductAttributes> productAttributes) {
        this.productAttributes = productAttributes;
    }

    public String getAttribute_group_id() {
        return attribute_group_id;
    }

    public void setAttribute_group_id(String attribute_group_id) {
        this.attribute_group_id = attribute_group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("attribute")
    private List<ProductAttributes> productAttributes;
}
