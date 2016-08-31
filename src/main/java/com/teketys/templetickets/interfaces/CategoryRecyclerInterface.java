package com.teketys.templetickets.interfaces;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.view.View;

import com.teketys.templetickets.entities.product.Product;

public interface CategoryRecyclerInterface {

    void onProductSelected(View view, Product product);

}
