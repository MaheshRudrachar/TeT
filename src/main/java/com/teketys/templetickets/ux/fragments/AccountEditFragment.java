package com.teketys.templetickets.ux.fragments;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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
import com.teketys.templetickets.entities.wishlist.UserResponse;
import com.teketys.templetickets.listeners.OnSingleClickListener;
import com.teketys.templetickets.utils.JsonUtils;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.ux.MainActivity;
import com.teketys.templetickets.ux.dialogs.LoginExpiredDialogFragment;

import okhttp3.internal.Util;
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
    private TextInputLayout nameInputWrapper;
    private TextInputLayout streetInputWrapper;
    private TextInputLayout houseNumberInputWrapper;
    private TextInputLayout cityInputWrapper;
    private TextInputLayout zipInputWrapper;
    private TextInputLayout phoneInputWrapper;
    private TextInputLayout emailInputWrapper;

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

        nameInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_name_wrapper);
        streetInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_street_wrapper);
        houseNumberInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_house_number_wrapper);
        cityInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_city_wrapper);
        zipInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_zip_wrapper);
        phoneInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_phone_wrapper);
        emailInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_email_wrapper);

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
                        putUser(user);
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
        user.setFirstname(Utils.getTextFromInputLayout(nameInputWrapper));
        user.setAddress(Utils.getTextFromInputLayout(houseNumberInputWrapper));
        user.setCity(Utils.getTextFromInputLayout(cityInputWrapper));
        user.setPostalcode(Utils.getTextFromInputLayout(zipInputWrapper));
        user.setTelephone(Utils.getTextFromInputLayout(phoneInputWrapper));
        return user;
    }

    private void refreshScreen(User user) {
        Utils.setTextToInputLayout(nameInputWrapper, user.getFirstname());
        Utils.setTextToInputLayout(streetInputWrapper, user.getAddress());
        Utils.setTextToInputLayout(houseNumberInputWrapper, user.getAddress());
        Utils.setTextToInputLayout(cityInputWrapper, user.getCity());
        Utils.setTextToInputLayout(zipInputWrapper, user.getPostalcode());
        Utils.setTextToInputLayout(emailInputWrapper, user.getEmail());
        Utils.setTextToInputLayout(phoneInputWrapper, user.getTelephone());
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
        boolean nameCheck = Utils.checkTextInputLayoutValueRequirement(nameInputWrapper, fieldRequired);
        boolean streetCheck = Utils.checkTextInputLayoutValueRequirement(streetInputWrapper, fieldRequired);
        boolean houseNumberCheck = Utils.checkTextInputLayoutValueRequirement(houseNumberInputWrapper, fieldRequired);
        boolean cityCheck = Utils.checkTextInputLayoutValueRequirement(cityInputWrapper, fieldRequired);
        boolean zipCheck = Utils.checkTextInputLayoutValueRequirement(zipInputWrapper, fieldRequired);
        boolean phoneCheck = Utils.checkTextInputLayoutValueRequirement(phoneInputWrapper, fieldRequired);
        boolean emailCheck = Utils.checkTextInputLayoutValueRequirement(emailInputWrapper, fieldRequired);

        return nameCheck && streetCheck && houseNumberCheck && cityCheck && zipCheck && phoneCheck && emailCheck;
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

    /**
     * Volley request for update user details.
     *
     * @param user new user data.
     */
    private void putUser(User user) {
        if (isRequiredFields()) {
            User activeUser = SettingsMy.getActiveUser();
            if (activeUser != null) {
                JSONObject joUser = new JSONObject();
                try {
                    joUser.put(JsonUtils.TAG_NAME, user.getFirstname());
                    joUser.put(JsonUtils.TAG_STREET, user.getAddress());
                    joUser.put(JsonUtils.TAG_HOUSE_NUMBER, user.getAddress());
                    joUser.put(JsonUtils.TAG_CITY, user.getCity());
                    joUser.put(JsonUtils.TAG_ZIP, user.getPostalcode());
                    joUser.put(JsonUtils.TAG_EMAIL, user.getEmail());
                    joUser.put(JsonUtils.TAG_PHONE, user.getTelephone());
                } catch (JSONException e) {
                    Timber.e(e, "Parse new user registration exception.");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                    return;
                }

                String url = String.format(EndPoints.USER_SINGLE, activeUser.getCustomer_id());

                progressDialog.show();
                GsonRequest<UserResponse> req = new GsonRequest<>(Request.Method.PUT, url, joUser.toString(), UserResponse.class,
                        new Response.Listener<UserResponse>() {
                            @Override
                            public void onResponse(@NonNull UserResponse user) {
                                SettingsMy.setActiveUser(user.getUser());
                                refreshScreen(user.getUser());
                                progressDialog.cancel();
                                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Ok), MsgUtils.ToastLength.SHORT);
                                getFragmentManager().popBackStackImmediate();
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
        } else {
            Timber.d("Missing required fields.");
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
                        Timber.d("Change password successful: %s", response.toString());
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Ok), MsgUtils.ToastLength.SHORT);
                        if (progressDialog != null) progressDialog.cancel();
                        getFragmentManager().popBackStackImmediate();
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
