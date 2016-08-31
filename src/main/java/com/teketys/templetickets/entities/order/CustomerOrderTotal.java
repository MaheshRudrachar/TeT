package com.teketys.templetickets.entities.order;

/**
 * Created by rudram1 on 8/31/16.
 */
public class CustomerOrderTotal {
    private String order_total_id;
    private String order_id;
    private String code;
    private String title;
    private String value;

    public String getOrder_total_id() {
        return order_total_id;
    }

    public void setOrder_total_id(String order_total_id) {
        this.order_total_id = order_total_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
