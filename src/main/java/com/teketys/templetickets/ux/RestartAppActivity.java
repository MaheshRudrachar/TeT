/**
 * Created by rudram1 on 8/25/16.
 */

package com.teketys.templetickets.ux;

import android.app.Activity;
import android.content.Intent;

import timber.log.Timber;

/**
 * Calling this activity cause restart application and new start from Splash activity.
 * It is used, when user change active/selected shop during lifetime.
 */
public class RestartAppActivity extends Activity {
    private static String TAG = RestartAppActivity.class.getSimpleName();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.tag(TAG);
        Timber.d("---------- onShopChange - finish old instances -----------");
        finish();
    }

    protected void onResume() {
        super.onResume();
        Timber.tag(TAG);
        Timber.d("---------- onShopChange starting new instance. -----------");
        startActivityForResult(new Intent(this, SplashActivity.class), 0);
    }
}
