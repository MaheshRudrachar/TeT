package com.teketys.templetickets.ux.fragments;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import com.teketys.templetickets.CONST;
import com.teketys.templetickets.MyApplication;
import com.teketys.templetickets.R;
import com.teketys.templetickets.SettingsMy;
import com.teketys.templetickets.api.EndPoints;
import com.teketys.templetickets.api.GsonRequest;
import com.teketys.templetickets.api.JsonRequest;
import com.teketys.templetickets.entities.User;
import com.teketys.templetickets.entities.UserAddress;
import com.teketys.templetickets.entities.UserResponse;
import com.teketys.templetickets.listeners.OnSingleClickListener;
import com.teketys.templetickets.utils.JsonUtils;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.ux.MainActivity;
import com.teketys.templetickets.ux.dialogs.LoginDialogFragment;
import com.teketys.templetickets.ux.dialogs.LoginExpiredDialogFragment;

import timber.log.Timber;

/**
 * Fragment provides options to editing user information and password change.
 */
public class AccountEditFragment extends Fragment {

    private ProgressDialog progressDialog;

    /**
     * Indicate which fort is active.
     */
    private boolean isPasswordForm = false;

    // Account editing form
    private LinearLayout accountForm;
    private TextInputLayout fnameInputWrapper;
    private TextInputLayout lnameInputWrapper;
    private TextInputLayout addressInputWrapper;
    private TextInputLayout cityInputWrapper;
    private TextInputLayout zipInputWrapper;
    private TextInputLayout phoneInputWrapper;
    private TextInputLayout emailInputWrapper;
    private TextInputLayout countryInputWrapper;
    private TextInputLayout stateInputWrapper;

    // Password change form
    private LinearLayout passwordForm;
    private TextInputLayout currentPasswordWrapper;
    private TextInputLayout newPasswordWrapper;
    private TextInputLayout newPasswordAgainWrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Account));

        View view = inflater.inflate(R.layout.fragment_account_edit, container, false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        // Account details form
        accountForm = (LinearLayout) view.findViewById(R.id.account_edit_form);

        fnameInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_first_name_wrapper);
        lnameInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_last_name_wrapper);
        addressInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_address_wrapper);
        cityInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_city_wrapper);
        zipInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_zip_wrapper);
        phoneInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_phone_wrapper);
        emailInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_email_wrapper);
        countryInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_country_wrapper);
        stateInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_state_wrapper);

        // Password form
        passwordForm = (LinearLayout) view.findViewById(R.id.account_edit_password_form);
        currentPasswordWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_password_current_wrapper);
        newPasswordWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_password_new_wrapper);
        newPasswordAgainWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_password_new_again_wrapper);

        final Button btnChangePassword = (Button) view.findViewById(R.id.account_edit_change_form_btn);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordForm) {
                    isPasswordForm = false;
                    passwordForm.setVisibility(View.GONE);
                    accountForm.setVisibility(View.VISIBLE);
                    btnChangePassword.setText(getString(R.string.Change_password));
                } else {
                    isPasswordForm = true;
                    passwordForm.setVisibility(View.VISIBLE);
                    accountForm.setVisibility(View.GONE);
                    btnChangePassword.setText(R.string.Cancel);
                }
            }
        });

        // Fill user informations
        User activeUser = SettingsMy.getActiveUser();
        if (activeUser != null) {
            refreshScreen(activeUser);
            Timber.d("user: %s", activeUser.toString());
        } else {
            Timber.e(new RuntimeException(), "No active user. Shouldn't happen.");
        }

        Button confirmButton = (Button) view.findViewById(R.id.account_edit_confirm_button);
        confirmButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (!isPasswordForm) {
                    try {
                        User user = getUserFromView();
                        putUserAccount(user);
                    } catch (Exception e) {
                        Timber.e(e, "Update user information exception.");
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                    }
                } else {
                    changePassword();
                }
                // Remove soft keyboard
                if (getActivity().getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        super.onPause();
    }

    private User getUserFromView() {
        User user = new User();
        user.setFirstname(Utils.getTextFromInputLayout(fnameInputWrapper));
        user.setLastname(Utils.getTextFromInputLayout(lnameInputWrapper));
        user.setEmail(Utils.getTextFromInputLayout(emailInputWrapper));
        user.setTelephone(Utils.getTextFromInputLayout(phoneInputWrapper));

        UserAddress userAddress = new UserAddress();
        userAddress.setAddress_1(Utils.getTextFromInputLayout(addressInputWrapper));
        userAddress.setPostCode(Utils.getTextFromInputLayout(zipInputWrapper));
        userAddress.setCity(Utils.getTextFromInputLayout(cityInputWrapper));
        userAddress.setCountry(Utils.getTextFromInputLayout(countryInputWrapper));
        userAddress.setZone(Utils.getTextFromInputLayout(stateInputWrapper));

        user.setAddress(userAddress);

        return user;
    }

    private void refreshScreen(User user) {
        Utils.setTextToInputLayout(fnameInputWrapper, user.getFirstname());
        Utils.setTextToInputLayout(lnameInputWrapper, user.getLastname());
        Utils.setTextToInputLayout(emailInputWrapper, user.getEmail());
        Utils.setTextToInputLayout(phoneInputWrapper, user.getTelephone());

        if (user.getAddress() != null) {
            Utils.setTextToInputLayout(addressInputWrapper, user.getAddress().getAddress_1());
            Utils.setTextToInputLayout(zipInputWrapper, user.getAddress().getPostCode());
            Utils.setTextToInputLayout(cityInputWrapper, user.getAddress().getCity());
            Utils.setTextToInputLayout(countryInputWrapper, user.getAddress().getCountry());
            Utils.setTextToInputLayout(stateInputWrapper, user.getAddress().getZone());
        }
        else {
            Utils.setTextToInputLayout(addressInputWrapper, "");
            Utils.setTextToInputLayout(zipInputWrapper, "");
            Utils.setTextToInputLayout(cityInputWrapper, "");
            Utils.setTextToInputLayout(countryInputWrapper, "");
            Utils.setTextToInputLayout(stateInputWrapper, "");
        }

    }

    /**
     * Check if all input fields are filled.
     * Method highlights all unfilled input fields.
     *
     * @return true if everything is Ok.
     */
    private boolean isRequiredFields() {
        // Check and show all missing values
        String fieldRequired = getString(R.string.Required_field);
        boolean fnameCheck = Utils.checkTextInputLayoutValueRequirement(fnameInputWrapper, fieldRequired);
        boolean lnameCheck = Utils.checkTextInputLayoutValueRequirement(lnameInputWrapper, fieldRequired);
        boolean phoneCheck = Utils.checkTextInputLayoutValueRequirement(phoneInputWrapper, fieldRequired);
        boolean emailCheck = Utils.checkTextInputLayoutValueRequirement(emailInputWrapper, fieldRequired);

        boolean addressCheck = Utils.checkTextInputLayoutValueRequirement(addressInputWrapper, fieldRequired);
        boolean zipCheck = Utils.checkTextInputLayoutValueRequirement(zipInputWrapper, fieldRequired);
        boolean cityCheck = Utils.checkTextInputLayoutValueRequirement(cityInputWrapper, fieldRequired);
        boolean countryCheck = Utils.checkTextInputLayoutValueRequirement(countryInputWrapper, fieldRequired);
        boolean stateCheck = Utils.checkTextInputLayoutValueRequirement(stateInputWrapper, fieldRequired);


        return fnameCheck && lnameCheck && phoneCheck && emailCheck && addressCheck && cityCheck && zipCheck && countryCheck && stateCheck;
    }

    /**
     * Check if all input password fields are filled and entries for new password matches.
     *
     * @return true if everything is Ok.
     */
    private boolean isRequiredPasswordFields() {
        String fieldRequired = getString(R.string.Required_field);
        boolean currentCheck = Utils.checkTextInputLayoutValueRequirement(currentPasswordWrapper, fieldRequired);
        boolean newCheck = Utils.checkTextInputLayoutValueRequirement(newPasswordWrapper, fieldRequired);
        boolean newAgainCheck = Utils.checkTextInputLayoutValueRequirement(newPasswordAgainWrapper, fieldRequired);

        if (newCheck && newAgainCheck) {
            if (!Utils.getTextFromInputLayout(newPasswordWrapper).equals(Utils.getTextFromInputLayout(newPasswordAgainWrapper))) {
                Timber.d("The entries for the new password must match");
                newPasswordWrapper.setErrorEnabled(true);
                newPasswordAgainWrapper.setErrorEnabled(true);
                newPasswordWrapper.setError(getString(R.string.The_entries_must_match));
                newPasswordAgainWrapper.setError(getString(R.string.The_entries_must_match));
                return false;
            } else {
                newPasswordWrapper.setErrorEnabled(false);
                newPasswordAgainWrapper.setErrorEnabled(false);
            }
        }
        return currentCheck && newCheck && newAgainCheck;
    }

    private void putUserAccount(final User updatedUser) {
        if (isRequiredFields()) {
            User activeUser = SettingsMy.getActiveUser();
            if (activeUser != null) {
                JSONObject joUser = new JSONObject();
                try {
                    joUser.put(JsonUtils.TAG_EMAIL, updatedUser.getEmail());
                    joUser.put(JsonUtils.TAG_FIRST_NAME, updatedUser.getFirstname());
                    joUser.put(JsonUtils.TAG_LAST_NAME, updatedUser.getLastname());
                    joUser.put(JsonUtils.TAG_TELEPHONE, updatedUser.getTelephone());
                    joUser.put(JsonUtils.TAG_FAX, "");

                    JSONObject customJO = new JSONObject();
                    customJO.put(JsonUtils.TAG_GENDER, (SettingsMy.getActiveUser().getUserCustomField().getGender()) != null ? SettingsMy.getActiveUser().getUserCustomField().getGender() : "");
                    customJO.put(JsonUtils.TAG_PLATFORM, (SettingsMy.getActiveUser().getUserCustomField().getPlatform()) != null ? SettingsMy.getActiveUser().getUserCustomField().getPlatform() : "");
                    customJO.put(JsonUtils.TAG_DEVICE_TOKEN, (SettingsMy.getActiveUser().getUserCustomField().getDevice_token()) != null ? SettingsMy.getActiveUser().getUserCustomField().getDevice_token() : "");

                    joUser.put(JsonUtils.TAG_CUSTOM_FIELD, customJO);

                } catch (JSONException e) {
                    Timber.e(e, "Parse new user registration exception.");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                    return;
                }

                final String url = String.format(EndPoints.USER_SINGLE, activeUser.getCustomer_id());

                GsonRequest<UserResponse> req = new GsonRequest<>(Request.Method.POST, url, joUser.toString(), UserResponse.class,
                        new Response.Listener<UserResponse>() {
                            @Override
                            public void onResponse(@NonNull UserResponse user) {
                                if (user != null) {
                                    if (user.getStatusCode() != null && user.getStatusText() != null) {
                                        if (user.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || user.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                            LoginDialogFragment.logoutUser(true);
                                            DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                            loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                            if (progressDialog != null) progressDialog.cancel();
                                        }
                                    } else {
                                        //********TODO: Mahesh check this for user address updation after successful order placement.
                                        putUserAddress(updatedUser);
                                    }
                                } else {
                                    Timber.d("return null response during putUser");
                                    progressDialog.cancel();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog != null) progressDialog.cancel();
                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    }
                }, getFragmentManager(), "");
                req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
                req.setShouldCache(false);
                MyApplication.getInstance().addToRequestQueue(req, CONST.ACCOUNT_EDIT_REQUESTS_TAG);
            } else {
                LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
            }
        }
        else
            Timber.d("Missing required fields.");
    }

    private void putUserAddress(final User updatedUser) {

        User activeUser = SettingsMy.getActiveUser();
        if (activeUser != null) {
            JSONObject joUser = new JSONObject();
            try {

                joUser.put(JsonUtils.TAG_FIRST_NAME, updatedUser.getFirstname());
                joUser.put(JsonUtils.TAG_LAST_NAME, updatedUser.getLastname());
                joUser.put(JsonUtils.TAG_ADDRESS1, updatedUser.getAddress().getAddress_1());
                joUser.put(JsonUtils.TAG_ADDRESS2, updatedUser.getAddress().getAddress_1());
                joUser.put(JsonUtils.TAG_CITY, updatedUser.getAddress().getCity());
                joUser.put(JsonUtils.TAG_COMPANY, "");
                joUser.put(JsonUtils.TAG_COUNTRY, "INDIA");
                joUser.put(JsonUtils.TAG_COUNTRY_ID, "99");
                joUser.put(JsonUtils.TAG_ZONE, "Karnataka");
                joUser.put(JsonUtils.TAG_ZONE_ID, "1489");
                joUser.put(JsonUtils.TAG_POST_CODE, updatedUser.getAddress().getPostCode());

            } catch (JSONException e) {
                Timber.e(e, "Parse new user registration exception.");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }

            final String url = String.format(EndPoints.USER_ADDRESS, (activeUser.getAddress_id() != null || activeUser.getAddress_id() != "") ? Integer.parseInt(activeUser.getAddress_id()) : 0);

            GsonRequest<UserResponse> req = new GsonRequest<>(Request.Method.PUT, url, joUser.toString(), UserResponse.class,
                    new Response.Listener<UserResponse>() {
                        @Override
                        public void onResponse(@NonNull UserResponse user) {
                            if(user != null) {
                                if(user.getStatusCode() != null && user.getStatusText() != null) {
                                    if (user.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || user.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                        LoginDialogFragment.logoutUser(true);
                                        DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                        loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                        if (progressDialog != null) progressDialog.cancel();
                                    }
                                }
                                else {
                                    //********TODO: Mahesh check this for user address updation after successful order placement.
                                    SettingsMy.setActiveUser(user.getUser());
                                    refreshScreen(user.getUser());
                                    progressDialog.cancel();
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Ok), MsgUtils.ToastLength.SHORT);
                                    getFragmentManager().popBackStackImmediate();
                                }
                            }
                            else
                                Timber.d("return null response during putUser");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null) progressDialog.cancel();
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            }, getFragmentManager(), "");
            req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            req.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(req, CONST.ACCOUNT_EDIT_REQUESTS_TAG);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
        }
    }

    /**
     * Updates the user's password. Before the request is sent, the input fields are checked for valid values.
     */
    private void changePassword() {
        if (isRequiredPasswordFields()) {
            User user = SettingsMy.getActiveUser();
            if (user != null) {
                String url = String.format(EndPoints.USER_CHANGE_PASSWORD);

                JSONObject jo = new JSONObject();
                try {
                    jo.put(JsonUtils.TAG_PASSWORD, Utils.getTextFromInputLayout(newPasswordWrapper).trim());
                    jo.put(JsonUtils.TAG_CONFIRM, Utils.getTextFromInputLayout(newPasswordAgainWrapper).trim());

                    //jo.put(JsonUtils.TAG_OLD_PASSWORD, Utils.getTextFromInputLayout(currentPasswordWrapper).trim());
                    //jo.put(JsonUtils.TAG_NEW_PASSWORD, Utils.getTextFromInputLayout(newPasswordWrapper).trim());
                    Utils.setTextToInputLayout(currentPasswordWrapper, "");
                    Utils.setTextToInputLayout(newPasswordWrapper, "");
                    Utils.setTextToInputLayout(newPasswordAgainWrapper, "");
                } catch (JSONException e) {
                    Timber.e(e, "Parsing change password exception.");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                    return;
                }

                progressDialog.show();
                JsonRequest req = new JsonRequest(Request.Method.PUT, url, jo, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null) {
                            if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                                LoginDialogFragment.logoutUser(true);
                                DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                if (progressDialog != null) progressDialog.cancel();
                            }
                            else {
                                Timber.d("Change password successful: %s", response.toString());
                                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Ok_password_changed), MsgUtils.ToastLength.SHORT);
                                if (progressDialog != null) progressDialog.cancel();
                                getFragmentManager().popBackStackImmediate();
                            }
                        }
                        else
                            Timber.d("Null response during changePassword....");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog != null) progressDialog.cancel();
                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    }
                }, getFragmentManager(), "");

                req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
                req.setShouldCache(false);
                MyApplication.getInstance().addToRequestQueue(req, CONST.ACCOUNT_EDIT_REQUESTS_TAG);
            } else {
                LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
            }
        }
    }

    @Override
    public void onStop() {
        if (progressDialog != null) progressDialog.cancel();
        MyApplication.getInstance().cancelPendingRequests(CONST.ACCOUNT_EDIT_REQUESTS_TAG);
        super.onStop();
    }
}
