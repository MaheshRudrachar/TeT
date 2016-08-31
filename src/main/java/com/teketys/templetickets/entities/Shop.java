package com.teketys.templetickets.entities;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

public class Shop {

    private long store_id;
    private String store_name;
    private String store_open;
    private String store_language;

    @SerializedName("thumb")
    private String thumb;

    @SerializedName("store_geocode")
    private String store_geocode;

    public Shop() {
    }

    public long getId() {
        return store_id;
    }

    public void setId(long store_id) {
        this.store_id = store_id;
    }

    public String getName() {
        return store_name;
    }

    public void setName(String store_name) {
        this.store_name = store_name;
    }

    public String getDescription() {
        return store_open;
    }

    public void setDescription(String store_open) {
        this.store_open = store_open;
    }

    public String getLanguage() {
        return store_language;
    }

    public void setLanguage(String store_language) {
        this.store_language = store_language;
    }

    public String getFlagIcon() {
        return thumb;
    }

    public void setFlagIcon(String thumb) {
        this.thumb = thumb;
    }

    public String getGoogleUa() {
        return store_geocode;
    }

    public void setGoogleUa(String googleUa) {
        this.store_geocode = googleUa;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + store_id +
                ", name='" + store_name + '\'' +
                ", description='" + store_open + '\'' +
                ", language='" + store_language + '\'' +
                ", flagIcon='" + thumb + '\'' +
                ", googleUa='" + store_geocode + '\'' +
                '}';
    }
}
