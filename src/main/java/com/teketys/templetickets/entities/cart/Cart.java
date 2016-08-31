package com.teketys.templetickets.entities.cart;

/**
 * Created by rudram1 on 8/25/16.
 */


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cart {

    @SerializedName("total_product_count")
    private int productCount;

    @SerializedName("total")
    private String totalPriceFormatted;

    private int coupon_status;
    private int voucher_status;
    private boolean reward_status;

    @SerializedName("products")
    private List<CartProductItem> items;

    @SerializedName("totals")
    private List<CartTotals> cartTotals;

    public Cart() {
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public String getTotalPriceFormatted() {
        return totalPriceFormatted;
    }

    public void setTotalPriceFormatted(String totalPriceFormatted) {
        this.totalPriceFormatted = totalPriceFormatted;
    }

    public int getCoupon_status() {
        return coupon_status;
    }

    public void setCoupon_status(int coupon_status) {
        this.coupon_status = coupon_status;
    }

    public int getVoucher_status() {
        return voucher_status;
    }

    public void setVoucher_status(int voucher_status) {
        this.voucher_status = voucher_status;
    }

    public boolean isReward_status() {
        return reward_status;
    }

    public void setReward_status(boolean reward_status) {
        this.reward_status = reward_status;
    }

    public List<CartProductItem> getItems() {
        return items;
    }

    public void setItems(List<CartProductItem> items) {
        this.items = items;
    }

    public List<CartTotals> getCartTotals() {
        return cartTotals;
    }

    public void setCartTotals(List<CartTotals> cartTotals) {
        this.cartTotals = cartTotals;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "productCount=" + productCount +
                ", totalPriceFormatted=" + totalPriceFormatted +
                ", coupon_status=" + coupon_status +
                ", voucher_status='" + voucher_status + '\'' +
                ", reward_status='" + reward_status + '\'' +
                '}';
    }
}
