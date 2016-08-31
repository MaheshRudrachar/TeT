package com.teketys.templetickets.entities.order;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.teketys.templetickets.entities.Region;
import com.teketys.templetickets.entities.cart.CartProductItem;

public class Order {

    private long id;

    @SerializedName("remote_id")
    private String remoteId;

    @SerializedName("date_created")
    private String dateCreated;
    private String status;
    private int total;

    @SerializedName("total_formatted")
    private String totalFormatted;

    @SerializedName("shipping_name")
    private String shippingName;

    @SerializedName("shipping_price")
    private int shippingPrice;

    @SerializedName("shipping_price_formatted")
    private String shippingPriceFormatted;
    private String currency;

    @SerializedName("shipping_type")
    private long shippingType;

    @SerializedName("payment_type")
    private long paymentType;
    private String firstName;
    private String lastName;

    private long order_id;

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    private String address1;
    private String address2;
    private String country;
    private String countryId;
    private String zoneId;

    public String getAgree() {
        return agree;
    }

    public void setAgree(String agree) {
        this.agree = agree;
    }

    private String agree;

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    private String shippingAddress;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    private String company;

    private String region;
    private String city;

    private String zip;

    @SerializedName("items")
    private List<CartProductItem> products;
    private String email;
    private String phone;
    private String note;

    private String shippingMethod;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    private String paymentMethod;


    public Order() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTotalFormatted() {
        return totalFormatted;
    }

    public void setTotalFormatted(String totalFormatted) {
        this.totalFormatted = totalFormatted;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public int getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(int shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public String getShippingPriceFormatted() {
        return shippingPriceFormatted;
    }

    public void setShippingPriceFormatted(String shippingPriceFormatted) {
        this.shippingPriceFormatted = shippingPriceFormatted;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public long getShippingType() {
        return shippingType;
    }

    public void setShippingType(long shippingType) {
        this.shippingType = shippingType;
    }

    public long getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(long paymentType) {
        this.paymentType = paymentType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public List<CartProductItem> getProducts() {
        return products;
    }

    public void setProducts(List<CartProductItem> products) {
        this.products = products;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (id != order.id) return false;
        if (total != order.total) return false;
        if (shippingPrice != order.shippingPrice) return false;
        if (shippingType != order.shippingType) return false;
        if (paymentType != order.paymentType) return false;
        if (remoteId != null ? !remoteId.equals(order.remoteId) : order.remoteId != null) return false;
        if (dateCreated != null ? !dateCreated.equals(order.dateCreated) : order.dateCreated != null) return false;
        if (status != null ? !status.equals(order.status) : order.status != null) return false;
        if (totalFormatted != null ? !totalFormatted.equals(order.totalFormatted) : order.totalFormatted != null) return false;
        if (shippingName != null ? !shippingName.equals(order.shippingName) : order.shippingName != null) return false;
        if (shippingPriceFormatted != null ? !shippingPriceFormatted.equals(order.shippingPriceFormatted) : order.shippingPriceFormatted != null)
            return false;
        if (currency != null ? !currency.equals(order.currency) : order.currency != null) return false;
        if (city != null ? !city.equals(order.city) : order.city != null) return false;
        if (region != null ? !region.equals(order.region) : order.region != null) return false;
        if (zip != null ? !zip.equals(order.zip) : order.zip != null) return false;
        if (products != null ? !products.equals(order.products) : order.products != null) return false;
        if (email != null ? !email.equals(order.email) : order.email != null) return false;
        if (phone != null ? !phone.equals(order.phone) : order.phone != null) return false;
        return !(note != null ? !note.equals(order.note) : order.note != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (remoteId != null ? remoteId.hashCode() : 0);
        result = 31 * result + (dateCreated != null ? dateCreated.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + total;
        result = 31 * result + (totalFormatted != null ? totalFormatted.hashCode() : 0);
        result = 31 * result + (shippingName != null ? shippingName.hashCode() : 0);
        result = 31 * result + shippingPrice;
        result = 31 * result + (shippingPriceFormatted != null ? shippingPriceFormatted.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (int) (shippingType ^ (shippingType >>> 32));
        result = 31 * result + (int) (paymentType ^ (paymentType >>> 32));
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (products != null ? products.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", remoteId='" + remoteId + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", status='" + status + '\'' +
                ", total=" + total +
                ", totalFormatted='" + totalFormatted + '\'' +
                ", shippingName='" + shippingName + '\'' +
                ", shippingPrice=" + shippingPrice +
                ", shippingPriceFormatted='" + shippingPriceFormatted + '\'' +
                ", currency='" + currency + '\'' +
                ", shippingType=" + shippingType +
                ", paymentType=" + paymentType +
                ", city='" + city + '\'' +
                ", region=" + region +
                ", zip='" + zip + '\'' +
                ", products=" + products +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
