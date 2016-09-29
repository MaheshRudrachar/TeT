package com.teketys.templetickets.entities;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;
import com.teketys.templetickets.entities.cart.Cart;
import com.teketys.templetickets.entities.wishlist.WishlistItem;

import java.util.Date;
import java.util.List;

public class User {

    private long customer_id;
    private long customer_group_id;
    private long store_id;

    public UserCustomField getUserCustomField() {
        return userCustomField;
    }

    public void setUserCustomField(UserCustomField userCustomField) {
        this.userCustomField = userCustomField;
    }

    @SerializedName("account_custom_field")
    private UserCustomField userCustomField;

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    private String address_id;
    private String firstname;
    private String lastname;
    private String email;
    private String telephone;
    private String fax;

    private String password;

    private long newsletter;
    private UserAddress address;
    private long status;

    private String city;

    private String postcode;

    @SerializedName("fb_id")
    private String fbId;

    private String access_token;
    private String Country;

    private String Region;

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    private String address_1;
    private String address_2;

    private String provider;

    public User() {
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public String getPostalcode() {
        return postcode;
    }

    public void setPostalcode(String postalcode) {
        this.postcode = postalcode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(long customer_id) {
        this.customer_id = customer_id;
    }

    public long getCustomer_group_id() {
        return customer_group_id;
    }

    public void setCustomer_group_id(long customer_group_id) {
        this.customer_group_id = customer_group_id;
    }

    public long getStore_id() {
        return store_id;
    }

    public void setStore_id(long store_id) {
        this.store_id = store_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public long getNewsletter() {
        return newsletter;
    }

    public void setNewsletter(long newsletter) {
        this.newsletter = newsletter;
    }

    public UserAddress getAddress() {
        return address;
    }

    public void setAddress(UserAddress address) {
        this.address = address;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    @Override
    public String toString() {
        return "User{" +
                "customer_id=" + customer_id +
                ", customer_group_id=" + customer_group_id +
                ", store_id=" + store_id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", fax='" + fax + '\'' +
                ", password='" + password + '\'' +
                ", newsletter=" + newsletter +
                ", address='" + address + '\'' +
                ", status=" + status +
                ", city='" + city + '\'' +
                ", postalcode='" + postcode + '\'' +
                ", fbId='" + fbId + '\'' +
                ", access_token='" + access_token + '\'' +
                ", Country='" + Country + '\'' +
                ", Region='" + Region + '\'' +
                ", provider='" + provider + '\'' +
                ", addressID='" + address_id + '\'' +
                '}';
    }
}
