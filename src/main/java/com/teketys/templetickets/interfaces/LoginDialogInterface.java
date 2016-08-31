package com.teketys.templetickets.interfaces;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.teketys.templetickets.entities.User;

/**
 * Interface declaring methods for login dialog.
 */
public interface LoginDialogInterface {

    void successfulLoginOrRegistration(User user);

}
