package com.teketys.templetickets.entities.product;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {

    private long id;

    private long product_id;

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    private String manufacturer;
    private String model;
    private String name;

    private double price;

    @SerializedName("price_formatted")
    private String priceFormatted;

    @SerializedName("discount_price")
    private double discountPrice;

    @SerializedName("discount_price_formatted")
    private String discountPriceFormatted;

    @SerializedName("special")
    private double specialPrice;

    @SerializedName("special_formated")
    private String specialPriceFormatted;

    private String description;

    @SerializedName("original_image")
    private String mainImage;

    private String[] images;

    private String thumb;

    @SerializedName("options")
    private List<ProductVariant> variants;

    public List<ProductAttributeGroups> getAttributeGroups() {
        return attributeGroups;
    }

    public void setAttributeGroups(List<ProductAttributeGroups> attributeGroups) {
        this.attributeGroups = attributeGroups;
    }

    @SerializedName("attribute_groups")
    private List<ProductAttributeGroups> attributeGroups;

    public Product() {
    }

    public long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPriceFormatted() {
        return priceFormatted;
    }

    public void setPriceFormatted(String priceFormatted) {
        this.priceFormatted = priceFormatted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getDiscountPriceFormatted() {
        return discountPriceFormatted;
    }

    public void setDiscountPriceFormatted(String discountPriceFormatted) {
        this.discountPriceFormatted = discountPriceFormatted;
    }

    public double getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(double specialPrice) {
        this.specialPrice = specialPrice;
    }

    public String getSpecialPriceFormatted() {
        return specialPriceFormatted;
    }

    public void setSpecialPriceFormatted(String specialPriceFormatted) {
        this.specialPriceFormatted = specialPriceFormatted;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @Override
    public String toString() {
        return "Product{" +
                "product_id=" + product_id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", priceFormatted='" + priceFormatted + '\'' +
                ", description='" + description + '\'' +
                ", thumb='" + thumb + '\'' +
                '}';
    }
}

