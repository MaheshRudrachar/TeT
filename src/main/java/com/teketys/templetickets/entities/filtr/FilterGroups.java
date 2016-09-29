package com.teketys.templetickets.entities.filtr;

import java.util.List;

/**
 * Created by rudram1 on 9/10/16.
 */
public class FilterGroups {

    public long getFilter_group_id() {
        return filter_group_id;
    }

    public void setFilter_group_id(long filter_group_id) {
        this.filter_group_id = filter_group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Filter> getFilter() {
        return filter;
    }

    public void setFilter(List<Filter> filter) {
        this.filter = filter;
    }

    private long filter_group_id;
    private String name;
    private List<Filter> filter;
}
