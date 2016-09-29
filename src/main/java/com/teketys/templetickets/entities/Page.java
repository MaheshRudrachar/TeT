package com.teketys.templetickets.entities;

/**
 * Created by rudram1 on 8/25/16.
 */

public class Page {

    private long information_id;
    private String title;
    private String description;

    private String bottom;

    public long getInformation_id() {
        return information_id;
    }

    public void setInformation_id(long information_id) {
        this.information_id = information_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }


    public Page() {
    }
}
