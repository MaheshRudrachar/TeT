package com.teketys.templetickets.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rudram1 on 8/23/16.
 */
public class UserResponse {

    @SerializedName("data")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
