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
    public static final String ACCESS_CODE = "AVNW00DI53BV39WNVB";
    public static final String MERCHANT_CODE = "106036";
    public static final String CURRENCY = "INR";
    public static final String REDIRECT_URL = "http://ec2-52-54-27-19.compute-1.amazonaws.com/ccavResponseHandler.php";
    public static final String CANCEL_URL = "http://ec2-52-54-27-19.compute-1.amazonaws.com/ccavResponseHandler.php";
    public static final String RSA_URL = "http://ec2-52-54-27-19.compute-1.amazonaws.com/GetRSA.php";
    public static final String BILLING_COUNTRY = "India";

    public static final String SLOKAS_AND_MANTRAS_TAG = "Slokas & Mantras";

    public static final String OAUTH_REQUESTS_TAG = "oauth_requests";
    public static final String SHOP_REQUESTS_TAG = "shop_requests";
    public static final String SPLASH_REQUESTS_TAG = "splash_requests";
    public static final String DRAWER_REQUESTS_TAG = "drawer_requests";
    public static final String BANNER_REQUESTS_TAG = "banner_requests";
    public static final String CATEGORY_REQUESTS_TAG = "category_requests";
    public static final String FILTER_REQUESTS_TAG = "filter_requests";
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

    public static final String PUJA_NAME_TAG = "Puja Name: ";
    public static final String TEMPLE_NAME_TAG = "Temple Name: ";
    public static final String PAYMENT_METHOD = "ccavenuepay";

    // Bundle constants
    public static final String BUNDLE_PASS_TARGET = "target";
    public static final String BUNDLE_PASS_TITLE = "title";
    /**
     * Volley request unknown status code
     */
    public static final int MissingStatusCode = 9999;
    public static final String PRODUCT_KEY_ID = "product_id";
    public static final String SLOKAS_TAG = "slokas";
    public static final String FILTER_PUJA_TYPE = "puja_type";
    public static final String FILTER_DEITY_GOD = "deity_god";

    /**
     * Slider TAGS
     */
    public static final String TAG_STATUS = "success";
    public static final String TAG_DATA = "data";
    public static final String TAG_PRODUCTS = "products";
    public static final String KEY_NAME = "title";
    public static final String KEY_IMAGE_URL = "image";
    public static final String KEY_LINK = "link";
    public static final String KEY_ID = "id";

    /**
     * Possible visibility states of layout parts.
     */
    public enum VISIBLE {
        EMPTY, CONTENT, PROGRESS
    }

    /**
     * Possible searches Temple or Pujas.
     */
    public enum SEARCHES {
        TEMPLE, PUJA
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

    public static final String RESPONSE_CODE = "401";
    public static final String RESPONSE_UNAUTHORIZED = "unauthorized";
    public static final String RESPONSE_SUCCESS = "false";
    public static final String RESPONSE_ERROR = "user already is logged";
    public static final String RESPONSE_ERROR_ADDRESS = "address 1 must be between 3 and 128 characters!";
    public static final String RESPONSE_ERROR_ADDRESS_REQUIRED = "Address must be minimum of 3 and maximum of 128 characters!";
    public static final String RESPONSE_ERROR_TELEPHONE = "telephone must be between 10 and 32 characters!";
    public static final String RESPONSE_ERROR_TELEPHONE_REQUIRED = "Telephone must be minimum of 10 and maximum of 32 characters!";
    public static final String RESPONSE_ERROR_PASSWORD = "password must be between 4 and 20 characters!";
    public static final String RESPONSE_ERROR_PASSWORD_REQUIRED = "Password must be minimum of 4 and maximum of 20 characters!";
    public static final String RESPONSE_ERROR_EMAIL = "e-mail address does not appear to be valid!";
    public static final String RESPONSE_ERROR_FNAME = "first name must be between 3 and 32 characters!";
    public static final String RESPONSE_ERROR_FNAME_REQUIRED = "First Name must be minimum of 3 and maximum 32 characters!";
    public static final String RESPONSE_ERROR_LNAME = "last name must be between 3 and 32 characters!";
    public static final String RESPONSE_ERROR_LNAME_REQUIRED = "Last Name must be minimum of 3 and maximum of 32 characters!";
    public static final String RESPONSE_ERROR_CITY = "city must be between 2 and 128 characters!";
    public static final String RESPONSE_ERROR_CITY_REQUIRED = "City must be minimum of 2 and maximum of 128 characters!";
    public static final String RESPONSE_ERROR_POSTCODE = "postcode must be between 2 and 6 characters!";
    public static final String RESPONSE_ERROR_POSTCODE_REQUIRED = "Postcode must be minimum of 2 and maximum of 6 characters!";

    public static final String RESPONSE_INVALID_PRODUCT = "Please Book Puja's from same Temple or Book it seperately for different Temples!!!";

    public static final String RESPONSE_WARNING = "no match for e-mail address";
    public static final String RESPONSE_WARNING_FORGOTTEN = "the e-mail address was not found in our records";

    public static final String TOTAL_PRICE = "total";
    public static final double BASE_SERVICE_TAX = 14.0;
    public static final double SWACCH_BHARATH_CESS = 0.50;
    public static final double KRISHI_KALYAN_CESS = 0.50;
    public static final double CONVENIENCE_FEES = 5.00;

}
