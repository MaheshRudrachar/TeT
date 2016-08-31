package com.teketys.templetickets.utils;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

import com.teketys.templetickets.SettingsMy;

public class MyInstanceIDListenerService extends InstanceIDListenerService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        // Invalidate GCM token on third party server.
        SettingsMy.setTokenSentToServer(false);

        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, MyRegistrationIntentService.class);
        startService(intent);
    }
}
