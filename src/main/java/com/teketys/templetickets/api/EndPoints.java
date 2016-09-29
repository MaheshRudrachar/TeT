package com.teketys.templetickets.api;

/**
 * Created by rudram1 on 7/25/16.
 */


import com.teketys.templetickets.CONST;

public class EndPoints {

    /**
     * Base server url.
     */
    private static final String API_URL                 = "http://ec2-52-54-27-19.compute-1.amazonaws.com/api/rest/";    // staging
    private static final String API_FQ_URL              = "http://ec2-52-54-27-19.compute-1.amazonaws.com/index.php?route=feed/rest_api/";    // staging
    private static final String API_SAM_URL             = "http://ec2-52-54-27-19.compute-1.amazonaws.com/assets/index_e.html";
    public static final String OAUTH_TOKEN              = API_URL.concat("oauth2/token/client_credentials");
    public static final String SHOPS                    = API_URL.concat("stores");
    public static final String SHOPS_SINGLE             = API_URL.concat("stores/%d");
    public static final String NAVIGATION_DRAWER        = API_URL.concat("categories/parent/0/level/6");
    public static final String NAVIGATION_SINGLE        = API_URL.concat("categories/%d");
    public static final String BANNERS                  = API_URL.concat("banners/9");
    public static final String SLIDER                   = API_URL.concat("banners/7");
    public static final String PAGES                    = API_URL.concat("information");
    public static final String PAGES_SINGLE             = API_URL.concat("information/%d");
    public static final String PAGES_TERMS_AND_COND     = API_URL.concat("information/5");
    public static final String PAGES_SLOKAS             = API_URL.concat("information/7");
    public static final String PRODUCTS                 = API_URL.concat("products/category/%d/limit/4/page/%d");
    public static final String PRODUCTS_SINGLE          = API_URL.concat("products/%d");
    public static final String PRODUCTS_SEARCH          = API_URL.concat("products/search/%s/limit/4/page/%d");
    public static final String PRODUCTS_SINGLE_RELATED  = API_URL.concat("related/%d");
    public static final String PRODUCTS_FILTER          = API_URL.concat("products&category=%d&filters=%d"); //products&category=63&filters=1 - product filters
    public static final String USER_REGISTER            = API_URL.concat("register");
    public static final String USER_LOGIN_EMAIL         = API_URL.concat("login");
    public static final String USER_LOGOUT_EMAIL        = API_URL.concat("logout");
    public static final String USER_LOGIN_SOCIAL        = API_URL.concat("sociallogin");
    public static final String USER_RESET_PASSWORD      = API_URL.concat("forgotten");
    public static final String USER_SINGLE              = API_URL.concat("account");
    public static final String USER_ADDRESS             = API_URL.concat("account/address/%d");
    public static final String USER_CHANGE_PASSWORD     = API_URL.concat("account/password");
    public static final String CART                     = API_URL.concat("cart");
    public static final String CART_ITEM                = API_URL.concat("cart/update");
    public static final String CART_DELETE              = API_URL.concat("cart");
    public static final String SHIPPING_METHODS         = API_URL.concat("shippingmethods");
    public static final String SHIPPING_ADDRESS         = API_URL.concat("shippingaddress");
    public static final String PAYMENT_METHODS          = API_URL.concat("paymentmethods");
    public static final String PAYMENT_ADDRESS          = API_URL.concat("paymentaddress");
    public static final String PAYMENT_PAY              = API_URL.concat("pay");
    public static final String PAYMENT_CONFIRM          = API_URL.concat("confirm");
    public static final String ORDERS                   = API_URL.concat("customerorders");
    public static final String ORDERS_SINGLE            = API_URL.concat("customerorders/%d");
    public static final String BRANCHES                 = API_URL.concat("%d/branches");
    public static final String WISHLIST                 = API_URL.concat("wishlist");
    public static final String WISHLIST_SINGLE          = API_URL.concat("wishlist/%d");
    public static final String REGISTER_NOTIFICATION    = API_URL.concat("account");
    public static final String SLOKAS_AND_MANTRAS       = API_SAM_URL;


    // Notifications parameters
    public static final String NOTIFICATION_LINK        = "link";
    public static final String NOTIFICATION_MESSAGE     = "message";
    public static final String NOTIFICATION_TITLE       = "title";
    public static final String NOTIFICATION_IMAGE_URL   = "image_url";
    public static final String NOTIFICATION_SHOP_ID     = "shop_id";
    public static final String NOTIFICATION_UTM         = "utm";

    private EndPoints() {}
}
