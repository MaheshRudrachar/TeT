package com.teketys.templetickets.entities;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

public class Banner {

    private String title;
    private String link;

    @SerializedName("image")
    private String imageUrl;

    public Banner() {

    }

    public String getName() {
        return title;
    }

    public void setName(String title) {
        this.title = title;
    }

    public String getTarget() {
        return link;
    }

    public void setTarget(String link) {
        this.link = link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Banner{" +
                "name='" + title + '\'' +
                ", target='" + link + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
