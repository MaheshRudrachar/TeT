package com.teketys.templetickets.interfaces;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.android.volley.VolleyError;

public interface RequestListener {

    void requestSuccess(long newId);

    void requestFailed(VolleyError error);
}

