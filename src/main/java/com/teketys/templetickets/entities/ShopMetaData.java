package com.teketys.templetickets.entities;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

/**
 * Created by rudram1 on 8/11/16.
 */
public class ShopMetaData {

    private long store_id;
    private String name;

    public ShopMetaData() {
    }

    public long getId() {
        return store_id;
    }

    public void setId(long store_id) {
        this.store_id = store_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}