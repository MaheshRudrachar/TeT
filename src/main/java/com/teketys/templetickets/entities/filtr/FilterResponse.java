package com.teketys.templetickets.entities.filtr;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rudram1 on 9/10/16.
 */
public class FilterResponse {

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    private String statusCode;

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

    private String statusText;

    @SerializedName("data")
    private Filters filters;
}
