package com.teketys.templetickets.ux.dialogs;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.teketys.templetickets.CONST;
import com.teketys.templetickets.MyApplication;
import com.teketys.templetickets.R;
import com.teketys.templetickets.SettingsMy;
import com.teketys.templetickets.api.EndPoints;
import com.teketys.templetickets.api.GsonRequest;
import com.teketys.templetickets.api.JsonRequest;
import com.teketys.templetickets.entities.User;
import com.teketys.templetickets.entities.UserResponse;
import com.teketys.templetickets.entities.cart.CartResponse;
import com.teketys.templetickets.utils.JsonUtils;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.ux.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;


/**
 * Dialog display "Thank you" screen after order is finished.
 */
public class OrderCreateSuccessDialogFragment extends DialogFragment {

    private boolean sampleApplication = false;
    private boolean cleanActivity = false;

    private int cartCountNotificationValue = CONST.DEFAULT_EMPTY_ID;
    private ProgressDialog progressDialog;

    /**
     * Dialog display "Thank you" screen after order is finished.
     */
    public static OrderCreateSuccessDialogFragment newInstance(boolean sampleApplication, boolean killActivity) {
        OrderCreateSuccessDialogFragment orderCreateSuccessDialogFragment = new OrderCreateSuccessDialogFragment();
        orderCreateSuccessDialogFragment.sampleApplication = sampleApplication;
        orderCreateSuccessDialogFragment.cleanActivity = killActivity;
        return orderCreateSuccessDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.dialogFragmentAnimation);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    //TODO Mahesh
    //Needs to be called on successful CCAvenue Transaction
    private void prepareConfirmPut() {
        //
        //Final step to confirmation...
        //

        progressDialog.show();

        JSONObject joConfirmReq = new JSONObject();
        try {
            joConfirmReq.put("", "");
        } catch (JSONException e) {
            Timber.e(e, "Parse resetPassword exception");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }

        String paymentPayUrl = String.format(EndPoints.PAYMENT_CONFIRM);

        JsonRequest req = new JsonRequest(Request.Method.PUT, paymentPayUrl,
                joConfirmReq, new Response.Listener<JSONObject>() {
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
                        Timber.d("Payment Confirm Put info %s", response.toString());

                        /**
                         * Send the SMS message!!!
                         */

                        //********TODO: Mahesh check this for user updation after successful order placement.
                        //Update the Account information
                        updateUserAccount(SettingsMy.getActiveUser());
                    }
                }
                else {
                    Timber.d("Null response during prepareConfirmPost....");
                    if (progressDialog != null) progressDialog.cancel();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e("Payment Confirm post failed...");
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
                return;
            }
        });
        req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        req.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(req, CONST.ORDER_CREATE_REQUESTS_TAG);
    }

    private void updateUserAccount(final User updatedUser) {
        User activeUser = SettingsMy.getActiveUser();
        if (activeUser != null) {
            JSONObject joUser = new JSONObject();
            try {
                joUser.put(JsonUtils.TAG_EMAIL, updatedUser.getEmail());
                joUser.put(JsonUtils.TAG_FIRST_NAME, updatedUser.getFirstname());
                joUser.put(JsonUtils.TAG_LAST_NAME, updatedUser.getLastname());
                joUser.put(JsonUtils.TAG_TELEPHONE, updatedUser.getTelephone());
                joUser.put(JsonUtils.TAG_FAX, updatedUser.getFax());

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
                                    updateUserAddress(updatedUser);
                                }
                            }
                            else {
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

    private void updateUserAddress(final User updatedUser) {

        User activeUser = SettingsMy.getActiveUser();
        if (activeUser != null) {
            JSONObject joUser = new JSONObject();
            try {

                joUser.put(JsonUtils.TAG_FIRST_NAME, updatedUser.getFirstname());
                joUser.put(JsonUtils.TAG_LAST_NAME, updatedUser.getLastname());

                if(updatedUser.getAddress() != null) {
                    joUser.put(JsonUtils.TAG_ADDRESS1, updatedUser.getAddress().getAddress_1());
                    joUser.put(JsonUtils.TAG_ADDRESS2, updatedUser.getAddress().getAddress_1());
                    joUser.put(JsonUtils.TAG_CITY, updatedUser.getAddress().getCity());
                    joUser.put(JsonUtils.TAG_COMPANY, "");
                    joUser.put(JsonUtils.TAG_COUNTRY_ID, "99");
                    joUser.put(JsonUtils.TAG_ZONE, "1489");
                    joUser.put(JsonUtils.TAG_POST_CODE, updatedUser.getAddress().getPostCode());
                }

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
                                    updateCartCount();

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

    private void updateCartCount() {
        String url = String.format(EndPoints.CART);

        GsonRequest<CartResponse> getCartResponse = new GsonRequest<CartResponse>(Request.Method.GET, url, null, CartResponse.class, new Response.Listener<CartResponse>() {
            @Override
            public void onResponse(@NonNull CartResponse response) {
                if(response != null) {
                    if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                        LoginDialogFragment.logoutUser(true);
                        DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                        loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                        if (progressDialog != null) progressDialog.cancel();
                    }
                    else {
                        Timber.d("getCartCount: %s", response.toString());
                        if (response.getCart() != null) {
                            MainActivity.updateCartCountNotification(response.getCart().getProductCount());
                        }
                        else {
                            MainActivity.updateCartCountNotification(0);
                        }

                        if(cleanActivity)
                            getActivity().finish();
                        else
                            dismiss();

                        if (getActivity() instanceof MainActivity)
                            ((MainActivity) getActivity()).onOrdersHistory();

                        if (progressDialog != null) progressDialog.cancel();
                    }
                }
                else {
                    Timber.d("Null response during getCartResponse....");
                    MainActivity.updateCartCountNotification(0);

                    if (getActivity() instanceof MainActivity)
                        ((MainActivity) getActivity()).onOrdersHistory();

                    if(cleanActivity)
                        getActivity().finish();
                    else
                        dismiss();

                    if (progressDialog != null) progressDialog.cancel();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e(error, "Obtain cart count from response failed.");
                MainActivity.updateCartCountNotification(0);

                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).onOrdersHistory();

                if(cleanActivity)
                    getActivity().finish();
                else
                    dismiss();

                if (progressDialog != null) progressDialog.cancel();
            }
        }, getFragmentManager(), "");
        getCartResponse.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getCartResponse.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getCartResponse, CONST.MAIN_ACTIVITY_REQUESTS_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        View view = inflater.inflate(R.layout.dialog_order_create_success, container, false);

        Button okBtn = (Button) view.findViewById(R.id.order_create_success_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareConfirmPut();
                //new RenderView().execute();
            }
        });

        TextView title = (TextView) view.findViewById(R.id.order_create_success_title);
        TextView description = (TextView) view.findViewById(R.id.order_create_success_description);

        if (sampleApplication) {
            title.setText(R.string.This_is_a_sample_app);
            description.setTextColor(ContextCompat.getColor(getContext(), R.color.textSecondary));
            description.setText(R.string.Sample_app_description);
        } else {
            title.setText(R.string.Thank_you_for_your_order);
            description.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            description.setText(Html.fromHtml(getString(R.string.Wait_for_sms_or_email_order_confirmation)));
        }

        return view;
    }

    private class RenderView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            prepareConfirmPut();
            return null;
        }

        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);

            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).onOrdersHistory();

            if(cleanActivity)
                getActivity().finish();
            else
                dismiss();

            if (progressDialog != null) progressDialog.cancel();
        }
    }
}