package com.teketys.templetickets.entities.wishlist;

/**
 * Created by rudram1 on 8/25/16.
 */

public class WishlistItem {

    private long product_id;

    private String thumb;
    private String name;
    private String model;

    private long stock;
    private double price;
    private boolean special;

    public WishlistItem(long product_id) {
        this.product_id = product_id;
    }

    public WishlistItem() {

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
