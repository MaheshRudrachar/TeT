package com.teketys.templetickets.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rudram1 on 9/26/16.
 */
public class UserAddressResponse {
    public UserAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(UserAddress userAddresse) {
        this.userAddress = userAddress;
    }

    @SerializedName("data")
    private UserAddress userAddress;
}
