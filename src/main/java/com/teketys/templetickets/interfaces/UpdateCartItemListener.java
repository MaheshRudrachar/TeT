package com.teketys.templetickets.interfaces;

/**
 * Created by rudram1 on 8/25/16.
 */

public interface UpdateCartItemListener {

    void updateProductInCart(long productCartId, long newVariantId, int newQuantity);

}

