package com.teketys.templetickets.utils;

/**
 * Created by rudram1 on 8/25/16.
 */

import org.json.JSONException;
import org.json.JSONObject;

import com.teketys.templetickets.entities.order.Order;
import timber.log.Timber;

public class JsonUtils {

    // Server specific JSON tags
    public static final String TAG_ID = "id";
    public static final String TAG_FB_ID = "fb_id";
    public static final String TAG_FB_ACCESS_TOKEN = "access_token";
    public static final String TAG_PRODUCT_VARIANT_ID = "product_variant_id";
    public static final String TAG_IS_IN_WISHLIST = "is_in_wishlist";
    public static final String TAG_WISHLIST_PRODUCT_ID = "product_id";
    public static final String TAG_QUANTITY = "quantity";
    public static final String TAG_CODE = "code";
    public static final String TAG_PRODUCT_COUNT = "total_product_count";
    public static final String TAG_PROVIDER = "provider";

    public static final String TAG_PRODUCT_ID = "product_id";
    public static final String TAG_OPTION = "option";
    public static final String TAG_KEY = "key";


    // Server specific JSON tags - user oriented
    public static final String TAG_EMAIL = "email";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_OLD_PASSWORD = "old_password";
    public static final String TAG_NEW_PASSWORD = "new_password";
    public static final String TAG_NAME = "name";
    public static final String TAG_STREET = "street";
    public static final String TAG_HOUSE_NUMBER = "house_number";
    public static final String TAG_CITY = "city";
    public static final String TAG_REGION = "region";
    public static final String TAG_ZIP = "zip";
    public static final String TAG_PHONE = "phone";
    public static final String TAG_GENDER = "gender";
    public static final String TAG_PLATFORM = "platform";
    public static final String TAG_DEVICE_TOKEN = "device_token";
    public static final String TAG_CUSTOM_FIELD = "custom_field";

    public static final String TAG_CONFIRM = "confirm";
    public static final String TAG_FIRST_NAME = "firstname";
    public static final String TAG_LAST_NAME = "lastname";
    public static final String TAG_TELEPHONE = "telephone";
    public static final String TAG_ADDRESS1 = "address_1";
    public static final String TAG_ADDRESS2 = "address_2";
    public static final String TAG_COUNTRY = "country";
    public static final String TAG_ZONE_ID = "zone_id";
    public static final String TAG_ZONE = "zone";
    public static final String TAG_POST_CODE = "postcode";
    public static final String TAG_COMPANY = "company";
    public static final String TAG_COMPANY_ID = "company_id";
    public static final String TAG_SHIPPING_ADDRESS = "shipping_address";
    public static final String TAG_SHIPPING_METHOD = "shipping_method";
    public static final String TAG_PAYMENT_METHOD = "payment_method";
    public static final String TAG_PAYMENT_ADDRESS = "payment_address";
    public static final String TAG_AGREE = "agree";
    public static final String TAG_COMMENT = "comment";
    public static final String TAG_COUNTRY_ID = "country_id";
    public static final String TAG_ADDRESSES = "addresses";
    public static final String TAG_ADDRESS_ID = "address_id";
    public static final String TAG_EXISTING = "existing";
    public static final String TAG_DATA = "data";
    public static final String TAG_FAX = "fax";
    public static final String TAG_TAX = "tax_id";

    // ORDERS
    public static final String TAG_DATE_CREATED = "date_created";
    public static final String TAG_STATUS = "status";
    public static final String TAG_TOTAL = "total";
    public static final String TAG_SHIPPING_TYPE = "shipping_type";
    public static final String TAG_PAYMENT_TYPE = "payment_type";
    public static final String TAG_SHIPPING_NAME = "shipping_name";
    public static final String TAG_TOTAL_FORMATTED = "total_formatted";
    public static final String TAG_SHIPPING_PRICE_FORMATTED = "shipping_price_formatted";
    public static final String TAG_NOTE = "note";
    public static final String TAG_SUCCESS = "success";

    private JsonUtils() {}


    /**
     * @param order
     * @return
     * @throws JSONException
     */
    public static JSONObject createOrderJson(Order order) throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put(TAG_SHIPPING_TYPE, order.getShippingType());
        if (order.getPaymentType() == -1) {
            jo.put(TAG_PAYMENT_TYPE, null);
        } else {
            jo.put(TAG_PAYMENT_TYPE, order.getPaymentType());
        }

        jo.put(TAG_FIRST_NAME, order.getFirstName());
        jo.put(TAG_LAST_NAME, order.getLastName());
        jo.put(TAG_ADDRESS1, order.getAddress1());
        jo.put(TAG_CITY, order.getCity());
        jo.put(TAG_COUNTRY, order.getCountry());
        jo.put(TAG_REGION, order.getRegion());
        jo.put(TAG_ZIP, order.getZip());
        jo.put(TAG_EMAIL, order.getEmail());
        jo.put(TAG_PHONE, order.getPhone());

        if (order.getNote() != null) {
            jo.put(TAG_NOTE, order.getNote());
        }

        //Check Region ID
        if (order.getRegion() != null) {
            //jo.put(TAG_REGION, order.getRegion().getId());
        }

        Timber.d("JSONParser postOrder: %s", jo.toString());
        return jo;
    }

    public static JSONObject createUserAuthentication(String email, String password) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put(JsonUtils.TAG_EMAIL, email);
        jo.put(JsonUtils.TAG_PASSWORD, password);
        return jo;
    }
}
