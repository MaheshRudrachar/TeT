package com.teketys.templetickets.entities.drawerMenu;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rudram1 on 9/1/16.
 */
public class DrawerItemPageResponse {

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

    public List<DrawerItemPage> getPages() {
        return pages;
    }

    public void setPages(List<DrawerItemPage> pages) {
        this.pages = pages;
    }

    @SerializedName("data")
    private List<DrawerItemPage> pages;
}
