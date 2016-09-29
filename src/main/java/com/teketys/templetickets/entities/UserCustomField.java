package com.teketys.templetickets.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rudram1 on 9/27/16.
 */
public class UserCustomField {

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private String platform;
    private String device_token;
    private String gender;
}
