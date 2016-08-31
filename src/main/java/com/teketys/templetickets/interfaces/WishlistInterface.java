package com.teketys.templetickets.interfaces;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.view.View;

import com.teketys.templetickets.entities.wishlist.WishlistItem;

public interface WishlistInterface {

    void onWishlistItemSelected(View view, WishlistItem wishlistItem);

    void onRemoveItemFromWishList(View caller, WishlistItem wishlistItem, int adapterPosition);
}
