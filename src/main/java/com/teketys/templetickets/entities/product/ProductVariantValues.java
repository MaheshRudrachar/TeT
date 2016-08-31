package com.teketys.templetickets.entities.product;

/**
 * Created by rudram1 on 8/17/16.
 */
public class ProductVariantValues {
    private String image;

    private long product_option_id;
    private long product_option_value_id;
    private String name;
    private Integer quantity;
    private boolean price;

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(boolean price) {
        this.price = price;
    }

    public void setPrice_excluding_tax(boolean price_excluding_tax) {
        this.price_excluding_tax = price_excluding_tax;
    }

    public long getProduct_option_id() {
        return product_option_id;
    }

    public void setProduct_option_id(long product_option_id) {
        this.product_option_id = product_option_id;
    }

    public long getProduct_option_value_id() {
        return product_option_value_id;
    }

    public void setProduct_option_value_id(long product_option_value_id) {
        this.product_option_value_id = product_option_value_id;
    }

    public void setPrice_formated(boolean price_formated) {
        this.price_formated = price_formated;
    }

    private boolean price_excluding_tax;

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public boolean isPrice() {
        return price;
    }

    public boolean isPrice_excluding_tax() {
        return price_excluding_tax;
    }

    public boolean isPrice_formated() {
        return price_formated;
    }

    private boolean price_formated;

}
