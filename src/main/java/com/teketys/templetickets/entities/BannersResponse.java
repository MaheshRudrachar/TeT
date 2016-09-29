package com.teketys.templetickets.entities;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BannersResponse {

    @SerializedName("data")
    private List<Banner> records;

    private String statusCode;
    private String statusText;

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

    public BannersResponse() {
    }

    public List<Banner> getRecords() {
        return records;
    }

    public void setRecords(List<Banner> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "BannersResponse{" +
                ", records=" + records +
                '}';
    }
}