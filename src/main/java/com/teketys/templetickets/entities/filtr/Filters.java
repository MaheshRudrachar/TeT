package com.teketys.templetickets.entities.filtr;

/**
 * Created by rudram1 on 8/25/16.
 */

import java.util.List;


public class Filters {

    public List<FilterGroups> getFilterGroups() {
        return filterGroups;
    }

    public void setFilterGroups(List<FilterGroups> filterGroups) {
        this.filterGroups = filterGroups;
    }

    private List<FilterGroups> filterGroups;


    public Filters() {
    }
}
