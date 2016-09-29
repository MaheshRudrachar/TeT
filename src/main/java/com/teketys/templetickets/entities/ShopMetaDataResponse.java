package com.teketys.templetickets.entities;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rudram1 on 8/11/16.
 */
public class ShopMetaDataResponse {

    //Metadata metadata;

    @SerializedName("data")
    List<ShopMetaData> shopMetaDataList;

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

    public ShopMetaDataResponse() {
    }

    public List<ShopMetaData> getShopMetaDataList() {
        return shopMetaDataList;
    }

    public void setShopMetaDataList(List<ShopMetaData> shopMeteDataList) {
        this.shopMetaDataList = shopMetaDataList;
    }

    @Override
    public String toString() {
        return "ShopResponse{" +
                "shopMetaDataList=" + shopMetaDataList +
                '}';
    }
}
