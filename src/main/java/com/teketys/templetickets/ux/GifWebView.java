package com.teketys.templetickets.ux;

import android.content.Context;
import android.webkit.WebView;

/**
 * Created by rudram1 on 9/16/16.
 */
public class GifWebView extends WebView {

    public GifWebView(Context context, String path) {
        super(context);

        loadUrl(path);
    }
}
