package com.teketys.templetickets.entities.filtr;

/**
 * Created by rudram1 on 9/11/16.
 */
public class FilterValuePuja {
    private long filter_id;

    public long getFilter_id() {
        return filter_id;
    }

    public void setFilter_id(long filter_id) {
        this.filter_id = filter_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
