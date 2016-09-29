package com.teketys.templetickets.entities.filtr;

import java.util.List;

/**
 * Created by rudram1 on 9/11/16.
 */
public class FilterTypePuja extends Filter {
    /**
     * Currently selected value
     */
    private transient FilterValuePuja selectedValue = null;

    private List<FilterValuePuja> values;

    public FilterTypePuja() {
    }

    public FilterValuePuja getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(FilterValuePuja selectedValue) {
        this.selectedValue = selectedValue;
    }

    public List<FilterValuePuja> getValues() {
        return values;
    }

    public void setValues(List<FilterValuePuja> values) {
        this.values = values;
    }
}
