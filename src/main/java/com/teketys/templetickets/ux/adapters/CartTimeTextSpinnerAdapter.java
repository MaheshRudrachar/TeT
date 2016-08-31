package com.teketys.templetickets.ux.adapters;


/**
 * Created by rudram1 on 8/25/16.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teketys.templetickets.R;
import com.teketys.templetickets.entities.product.ProductColor;
import com.teketys.templetickets.entities.product.ProductTime;
import com.teketys.templetickets.views.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by rudram1 on 8/30/16.
 */
public class CartTimeTextSpinnerAdapter extends ArrayAdapter<ProductTime> {
    private static final int layoutID = R.layout.spinner_item_simple_text;
    private final LayoutInflater layoutInflater;

    private List<ProductTime> productTimeList;

    /**
     * Creates an adapter for time selection.
     *
     * @param context activity context.
     */

    public CartTimeTextSpinnerAdapter(Context context) {
        super(context, layoutID);
        this.productTimeList = new ArrayList<>();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return productTimeList.size();
    }

    public ProductTime getItem(int position) {
        return productTimeList.get(position);
    }

    public long getItemId(int position) {
        return productTimeList.get(position).getProduct_option_value_id();
    }

    public void setProductTimeList(List<ProductTime> productTime) {
        if (productTime != null) {
            this.productTimeList.addAll(productTime);
            notifyDataSetChanged();
        } else {
            Timber.e("Trying set null time list in %s", this.getClass().getSimpleName());
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
//        Timber.e("getView Position: " + position + ". ConvertView: " + convertView);
        View v = convertView;
        ListItemHolder holder;

        if (v == null) {
            v = layoutInflater.inflate(layoutID, parent, false);
            holder = new ListItemHolder();
            holder.text = (TextView) v.findViewById(R.id.text);
            v.setTag(holder);
        } else {
            holder = (ListItemHolder) v.getTag();
        }

        if (productTimeList.get(position) != null && productTimeList.get(position).getName() != null) {
            holder.text.setText(productTimeList.get(position).getName());
        } else {
            Timber.e("Received null productTime in %s", this.getClass().getSimpleName());
        }
        return v;
    }

    static class ListItemHolder {
        TextView text;
    }
}
