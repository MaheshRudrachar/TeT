package com.teketys.templetickets.entities.filtr;

import java.util.List;

/**
 * Created by rudram1 on 9/11/16.
 */
public class FilterTypeDeity extends Filter {
    /**
     * Currently selected value
     */
    private transient FilterValueDeity selectedValue = null;

    private List<FilterValueDeity> values;

    public FilterTypeDeity() {
    }

    public FilterValueDeity getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(FilterValueDeity selectedValue) {
        this.selectedValue = selectedValue;
    }

    public List<FilterValueDeity> getValues() {
        return values;
    }

    public void setValues(List<FilterValueDeity> values) {
        this.values = values;
    }
}