package com.teketys.templetickets;

/**
 * Created by rudram1 on 8/25/16.
 */

public class CONST {

    // TODO update this variable
    /**
     * ID used for simulate empty/null value
     */
    public static final int DEFAULT_EMPTY_ID = -131;

    // Volley requests tags
    public static final String CLIENT_ID_SECRET = "demo_oauth_client:demo_oauth_secret";
    public static final String BASE64_ENCODED = "ZGVtb19vYXV0aF9jbGllbnQ6ZGVtb19vYXV0aF9zZWNyZXQ=";
    public static final String ACCESS_CODE = "AVLS00DH50BP35SLPB";
    public static final String MERCHANT_CODE = "106036";
    public static final String CURRENCY = "INR";
    public static final String REDIRECT_URL = "http://ec2-54-152-19-19.compute-1.amazonaws.com/ccavResponseHandler.php";
    public static final String CANCEL_URL = "http://ec2-54-152-19-19.compute-1.amazonaws.com/ccavResponseHandler.php";
    public static final String RSA_URL = "http://ec2-54-152-19-19.compute-1.amazonaws.com/GetRSA.php";
    public static final String BILLING_COUNTRY = "India";

    public static final String OAUTH_REQUESTS_TAG = "oauth_requests";
    public static final String SHOP_REQUESTS_TAG = "shop_requests";
    public static final String SPLASH_REQUESTS_TAG = "splash_requests";
    public static final String DRAWER_REQUESTS_TAG = "drawer_requests";
    public static final String BANNER_REQUESTS_TAG = "banner_requests";
    public static final String CATEGORY_REQUESTS_TAG = "category_requests";
    public static final String PRODUCT_REQUESTS_TAG = "product_requests";
    public static final String LOGIN_DIALOG_REQUESTS_TAG = "login_dialog_requests";
    public static final String ACCOUNT_REQUESTS_TAG = "account_requests";
    public static final String CART_REQUESTS_TAG = "cart_requests";
    public static final String CART_DISCOUNTS_REQUESTS_TAG = "cart_discounts_requests";
    public static final String ORDER_CREATE_REQUESTS_TAG = "order_create_requests";
    public static final String DELIVERY_DIALOG_REQUESTS_TAG = "delivery_dialog_requests";
    public static final String WISHLIST_REQUESTS_TAG = "wishlist_requests";
    public static final String ACCOUNT_EDIT_REQUESTS_TAG = "account_edit_requests";
    public static final String SETTINGS_REQUESTS_TAG = "settings_requests";
    public static final String UPDATE_CART_ITEM_REQUESTS_TAG = "update_cart_item_requests";
    public static final String MAIN_ACTIVITY_REQUESTS_TAG = "main_activity_requests";
    public static final String PAGE_REQUESTS_TAG = "page_requests";
    public static final String ORDERS_HISTORY_REQUESTS_TAG = "orders_history_requests";
    public static final String ORDERS_DETAIL_REQUESTS_TAG = "orders_detail_requests";
    public static final String ORDERS_CREATE_FRAGMENT_TAG = "orders_create_fragment_tag";
    public static final String USER_LOGOUT_TAG = "logout_requests";


    // Bundle constants
    public static final String BUNDLE_PASS_TARGET = "target";
    public static final String BUNDLE_PASS_TITLE = "title";
    /**
     * Volley request unknown status code
     */
    public static final int MissingStatusCode = 9999;
    public static final String PRODUCT_KEY_ID = "product_id";

    /**
     * Possible visibility states of layout parts.
     */
    public enum VISIBLE {
        EMPTY, CONTENT, PROGRESS
    }

    public static final String OPTION_NAME_COLOR = "color";
    public static final String OPTION_NAME_SIZE = "size";
    public static final String OPTION_NAME_DATE = "adate";
    public static final String OPTION_NAME_TIME = "atime";
    public static final String OPTION_NAME_IMAGE = "image";
    public static final String OPTION_ATTRIBUTE_DAILY = "daily";
    public static final String OPTION_ATTRIBUTE_SPECIAL = "special";
    public static final String OPTION_ATTRIBUTE_BOOKING = "bookingperiod";
    public static final String OPTION_ATTRIBUTE_SPECIFIC = "specificday";
}
