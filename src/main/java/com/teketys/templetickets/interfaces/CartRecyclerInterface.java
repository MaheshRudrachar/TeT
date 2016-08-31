package com.teketys.templetickets.interfaces;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.teketys.templetickets.entities.cart.CartDiscountItem;
import com.teketys.templetickets.entities.cart.CartProductItem;

public interface CartRecyclerInterface {

    void onProductUpdate(CartProductItem cartProductItem);

    void onProductDelete(CartProductItem cartProductItem);

    void onDiscountDelete(CartDiscountItem cartDiscountItem);

    void onProductSelect(long productId);

}
