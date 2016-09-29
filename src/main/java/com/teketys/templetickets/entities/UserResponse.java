package com.teketys.templetickets.entities;

import com.google.gson.annotations.SerializedName;
import com.teketys.templetickets.entities.User;

/**
 * Created by rudram1 on 8/23/16.
 */
public class UserResponse {

    @SerializedName("data")
    private User user;

    public UserWarning getUserWarning() {
        return userWarning;
    }

    public void setUserWarning(UserWarning userWarning) {
        this.userWarning = userWarning;
    }

    @SerializedName("error")
    private UserWarning userWarning;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    private String statusCode;
    private String statusText;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    private String success;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
