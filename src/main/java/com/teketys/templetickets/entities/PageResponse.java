package com.teketys.templetickets.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rudram1 on 9/1/16.
 */
public class PageResponse {

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

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @SerializedName("data")
    private Page page;

}
