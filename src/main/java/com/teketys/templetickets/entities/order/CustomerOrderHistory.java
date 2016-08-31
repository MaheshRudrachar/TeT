package com.teketys.templetickets.entities.order;

/**
 * Created by rudram1 on 8/31/16.
 */
public class CustomerOrderHistory {
    private String date_added;
    private String status;
    private String comment;

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
