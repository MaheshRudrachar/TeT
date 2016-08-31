package com.teketys.templetickets.entities;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BannersResponse {

    @SerializedName("data")
    private List<Banner> records;

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