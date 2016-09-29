package com.teketys.templetickets.utils;

/**
 * Created by rudram1 on 9/2/16.
 */
import com.teketys.templetickets.CONST;
import com.teketys.templetickets.entities.ImageSlideShow;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONReader {

    public static List<ImageSlideShow> getHome(JSONArray jArray)
            throws JSONException {
        List<ImageSlideShow> products = new ArrayList<ImageSlideShow>();

        JSONArray jsonArray = jArray;
        ImageSlideShow product;
        for (int i = 0; i < jsonArray.length(); i++) {
            product = new ImageSlideShow();
            JSONObject productObj = jsonArray.getJSONObject(i);
            product.setTitle(productObj.getString(CONST.KEY_NAME));
            product.setLink(productObj.getString(CONST.KEY_LINK));
            product.setImage(productObj.getString(CONST.KEY_IMAGE_URL));

            products.add(product);
        }
        return products;
    }
}