package com.teketys.templetickets.entities.product;

/**
 * Created by rudram1 on 8/31/16.
 */
public class ProductAttributes {

    public String getAttribute_id() {
        return attribute_id;
    }

    public void setAttribute_id(String attribute_id) {
        this.attribute_id = attribute_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String attribute_id;
    private String name;
    private String text;
}
