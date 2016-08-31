package com.teketys.templetickets.entities.product;

/**
 * Created by rudram1 on 8/19/16.
 */
public class ProductRelated {

    private long product_id;
    private String thumb;
    private String name;
    private String model;

    private long stock;
    private double price;
    private double price_excluding_tax;
    private boolean special_excluding_tax;
    private long minimum;
    private int rating;
    private String description;
    private boolean special;

    public ProductRelated(long product_id) {
        this.product_id = product_id;
    }

    public ProductRelated() {

    }

    public long getId() {
        return product_id;
    }

    public void setId(long product_id) {
        this.product_id = product_id;
    }

    public String getThumb() {
        return thumb;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public long getStock() {
        return stock;
    }

    public double getPrice() {
        return price;
    }

    public boolean isSpecial() {
        return special;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setStock(long stock) {
        this.stock = stock;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice_excluding_tax() {
        return price_excluding_tax;
    }

    public boolean isSpecial_excluding_tax() {
        return special_excluding_tax;
    }

    public long getMinimum() {
        return minimum;
    }

    public int getRating() {
        return rating;
    }

    public void setPrice_excluding_tax(double price_excluding_tax) {
        this.price_excluding_tax = price_excluding_tax;
    }

    public void setSpecial_excluding_tax(boolean special_excluding_tax) {
        this.special_excluding_tax = special_excluding_tax;
    }

    public void setMinimum(long minimum) {
        this.minimum = minimum;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "WishlistItem{" +
                "id=" + product_id +
                ", thumb=" + thumb +
                ", model=" + model +
                ", name=" + name +
                ", stock=" + stock +
                ", price=" + price +
                ", special=" + special +
                '}';
    }
}

