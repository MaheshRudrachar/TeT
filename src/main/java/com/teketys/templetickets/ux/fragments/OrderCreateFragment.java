package com.teketys.templetickets.ux.fragments;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.teketys.templetickets.BuildConfig;
import com.teketys.templetickets.CONST;
import com.teketys.templetickets.MyApplication;
import com.teketys.templetickets.R;
import com.teketys.templetickets.SettingsMy;
import com.teketys.templetickets.api.EndPoints;
import com.teketys.templetickets.api.GsonRequest;
import com.teketys.templetickets.api.JsonRequest;
import com.teketys.templetickets.entities.User;
import com.teketys.templetickets.entities.cart.Cart;
import com.teketys.templetickets.entities.cart.CartProductItem;
import com.teketys.templetickets.entities.cart.CartProductItemVariant;
import com.teketys.templetickets.entities.cart.CartResponse;
import com.teketys.templetickets.entities.delivery.Delivery;
import com.teketys.templetickets.entities.delivery.DeliveryRequest;
import com.teketys.templetickets.entities.delivery.Payment;
import com.teketys.templetickets.entities.delivery.Shipping;
import com.teketys.templetickets.entities.order.Order;
import com.teketys.templetickets.entities.order.OrderConfirmResponse;
import com.teketys.templetickets.interfaces.PaymentDialogInterface;
import com.teketys.templetickets.interfaces.ShippingDialogInterface;
import com.teketys.templetickets.listeners.OnSingleClickListener;
import com.teketys.templetickets.utils.Analytics;
import com.teketys.templetickets.utils.JsonUtils;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.utils.ccavenue.AvenuesParams;
import com.teketys.templetickets.utils.ccavenue.PaymentArgs;
import com.teketys.templetickets.ux.MainActivity;
import com.teketys.templetickets.ux.ccavenue.WebViewActivity;
import com.teketys.templetickets.ux.dialogs.LoginExpiredDialogFragment;
import com.teketys.templetickets.ux.dialogs.OrderCreateSuccessDialogFragment;
import com.teketys.templetickets.ux.dialogs.PaymentDialogFragment;
import com.teketys.templetickets.ux.dialogs.ShippingDialogFragment;
import timber.log.Timber;

/**
 * Fragment allowing the user to create order.
 */
public class OrderCreateFragment extends Fragment {

    private FragmentTabHost mTabHost;

    public static final String MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT = "loginExpiredDialogFragment";
    private ProgressDialog progressDialog;

    private ScrollView scrollLayout;
    private LinearLayout cartItemsLayout;

    private Cart cart;
    private double orderTotalPrice;
    private TextView cartItemsTotalPrice;
    private TextView orderTotalPriceTv;

    // View with user information used to create order
    private TextInputLayout firstNameInputWrapper;
    private TextInputLayout lastNameInputWrapper;
    private TextInputLayout addressInputWrapper;
    private TextInputLayout cityInputWrapper;
    private TextInputLayout countryInputWrapper;
    private TextInputLayout regionInputWrapper;
    private TextInputLayout emailInputWrapper;
    private TextInputLayout phoneInputWrapper;
    private TextInputLayout zipInputWrapper;
    private TextInputLayout noteInputWrapper;

    /*private TextInputLayout nameInputWrapper;
    private TextInputLayout streetInputWrapper;
    private TextInputLayout houseNumberInputWrapper;
    private TextInputLayout cityInputWrapper;
    private TextInputLayout zipInputWrapper;
    private TextInputLayout phoneInputWrapper;
    private TextInputLayout emailInputWrapper;
    private TextInputLayout noteInputWrapper;*/

    // Shipping and payment
    private Delivery delivery;
    private Payment selectedPayment;
    private Shipping selectedShipping;
    private ProgressBar deliveryProgressBar;
    private View deliveryShippingLayout;
    private View deliveryPaymentLayout;
    private TextView selectedShippingNameTv;
    private TextView selectedShippingPriceTv;
    private TextView selectedPaymentNameTv;
    private TextView selectedPaymentPriceTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Order_summary));

        View view = inflater.inflate(R.layout.fragment_order_create, container, false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        scrollLayout = (ScrollView) view.findViewById(R.id.order_create_scroll_layout);
        cartItemsLayout = (LinearLayout) view.findViewById(R.id.order_create_cart_items_layout);
        cartItemsTotalPrice = (TextView) view.findViewById(R.id.order_create_total_price);

        orderTotalPriceTv = (TextView) view.findViewById(R.id.order_create_summary_total_price);
        TextView termsAndConditionsTv = (TextView) view.findViewById(R.id.order_create_summary_terms_and_condition);
        termsAndConditionsTv.setText(Html.fromHtml(getString(R.string.Click_on_Order_to_allow_our_Terms_and_Conditions)));
        termsAndConditionsTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).onTermsAndConditionsSelected();
            }
        });

        prepareFields(view);
        prepareDeliveryLayout(view);

        Button finishOrder = (Button) view.findViewById(R.id.order_create_finish);
        finishOrder.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (isRequiredFieldsOk()) {
                    // Prepare data
                    Order order = new Order();
                    order.setFirstName(Utils.getTextFromInputLayout(firstNameInputWrapper));
                    order.setLastName(Utils.getTextFromInputLayout(lastNameInputWrapper));
                    order.setAddress1(Utils.getTextFromInputLayout(addressInputWrapper));
                    order.setAddress2(Utils.getTextFromInputLayout(addressInputWrapper));
                    order.setCity(Utils.getTextFromInputLayout(cityInputWrapper));
                    order.setCountry(Utils.getTextFromInputLayout(countryInputWrapper));
                    order.setCountryId("99");
                    order.setZoneId("1433");
                    order.setCompany("");
                    order.setRegion(Utils.getTextFromInputLayout(regionInputWrapper));
                    order.setZip(Utils.getTextFromInputLayout(zipInputWrapper));
                    order.setEmail(Utils.getTextFromInputLayout(emailInputWrapper));
                    order.setShippingMethod(selectedShipping.getShippingMethod());
                    order.setShippingAddress("new");
                    order.setAgree("1");


                    if (selectedPayment != null) {
                        order.setPaymentMethod(selectedPayment.getPaymentMethod());
                    } else {
                        order.setPaymentType(-1);
                    }
                    order.setPhone(Utils.getTextFromInputLayout(phoneInputWrapper));
                    order.setNote(Utils.getTextFromInputLayout(noteInputWrapper));

                    // Hide keyboard
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    postOrder(order);
                }
            }
        });

        showSelectedShipping(selectedShipping);
        showSelectedPayment(selectedPayment);

        getUserCart();
        return view;
    }

    /**
     * Prepare content views, adapters and listeners.
     *
     * @param view fragment base view.
     */
    private void prepareFields(View view) {
        firstNameInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_fname_wrapper);
        lastNameInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_lname_wrapper);
        addressInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_address_wrapper);
        cityInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_city_wrapper);
        zipInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_zip_wrapper);
        countryInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_country_wrapper);
        regionInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_region_wrapper);
        phoneInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_phone_wrapper);
        emailInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_email_wrapper);
        noteInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_note_wrapper);

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            Utils.setTextToInputLayout(firstNameInputWrapper, user.getFirstname());
            Utils.setTextToInputLayout(lastNameInputWrapper, user.getLastname());
            Utils.setTextToInputLayout(addressInputWrapper, user.getAddress());
            Utils.setTextToInputLayout(cityInputWrapper, user.getCity());
            Utils.setTextToInputLayout(zipInputWrapper, user.getPostalcode());
            Utils.setTextToInputLayout(countryInputWrapper, user.getCountry());
            Utils.setTextToInputLayout(regionInputWrapper, user.getRegion());
            Utils.setTextToInputLayout(emailInputWrapper, user.getEmail());
            Utils.setTextToInputLayout(phoneInputWrapper, user.getTelephone());
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT);
        }
    }

    /**
     * Check if all input fields are filled and also that is selected shipping and payment.
     * Method highlights all unfilled input fields.
     *
     * @return true if everything is Ok.
     */
    private boolean isRequiredFieldsOk() {
        // Check and show all missing values
        String fieldRequired = getString(R.string.Required_field);

        boolean fnameCheck = Utils.checkTextInputLayoutValueRequirement(firstNameInputWrapper, fieldRequired);
        boolean lnameCheck = Utils.checkTextInputLayoutValueRequirement(lastNameInputWrapper, fieldRequired);
        boolean addressCheck = Utils.checkTextInputLayoutValueRequirement(addressInputWrapper, fieldRequired);
        boolean countryCheck = Utils.checkTextInputLayoutValueRequirement(countryInputWrapper, fieldRequired);
        boolean regionCheck = Utils.checkTextInputLayoutValueRequirement(regionInputWrapper, fieldRequired);
        boolean cityCheck = Utils.checkTextInputLayoutValueRequirement(cityInputWrapper, fieldRequired);
        boolean zipCheck = Utils.checkTextInputLayoutValueRequirement(zipInputWrapper, fieldRequired);
        boolean phoneCheck = Utils.checkTextInputLayoutValueRequirement(phoneInputWrapper, fieldRequired);
        boolean emailCheck = Utils.checkTextInputLayoutValueRequirement(emailInputWrapper, fieldRequired);

        if (fnameCheck && lnameCheck && addressCheck && countryCheck && regionCheck && cityCheck && zipCheck && phoneCheck && emailCheck) {
            // Check if shipping and payment is selected
            if (selectedShipping == null) {
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Choose_shipping_method), MsgUtils.ToastLength.SHORT);
                scrollLayout.smoothScrollTo(0, deliveryShippingLayout.getTop());
                return false;
            }

            if (selectedPayment == null) {
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Choose_payment_method), MsgUtils.ToastLength.SHORT);
                scrollLayout.smoothScrollTo(0, deliveryShippingLayout.getTop());
                return false;
            }
            return true;
        } else {
            return false;
        }
    }


    private void prepareDeliveryLayout(View view) {
        deliveryProgressBar = (ProgressBar) view.findViewById(R.id.delivery_progress);

//        final View deliveryShippingBtn = view.findViewById(R.id.order_create_delivery_shipping_button);
//        final View deliveryPaymentBtn = view.findViewById(R.id.order_create_delivery_payment_button);

        this.deliveryShippingLayout = view.findViewById(R.id.order_create_delivery_shipping_layout);
        this.deliveryPaymentLayout = view.findViewById(R.id.order_create_delivery_payment_layout);

        selectedShippingNameTv = (TextView) view.findViewById(R.id.order_create_delivery_shipping_name);
        selectedShippingPriceTv = (TextView) view.findViewById(R.id.order_create_delivery_shipping_price);
        selectedPaymentNameTv = (TextView) view.findViewById(R.id.order_create_delivery_payment_name);
        selectedPaymentPriceTv = (TextView) view.findViewById(R.id.order_create_delivery_payment_price);

        deliveryShippingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShippingDialogFragment shippingDialogFragment = ShippingDialogFragment.newInstance(delivery, selectedShipping, new ShippingDialogInterface() {
                    @Override
                    public void onShippingSelected(Shipping shipping) {
                        // Save selected value
                        selectedShipping = shipping;

                        // Update shipping related values
                        showSelectedShipping(shipping);

                        // Continue for payment
                        selectedPayment = null;
                        selectedPaymentNameTv.setText(getString(R.string.Choose_payment_method));
                        selectedPaymentPriceTv.setText("");
                        deliveryPaymentLayout.performClick();
                    }
                });
                shippingDialogFragment.show(getFragmentManager(), ShippingDialogFragment.class.getSimpleName());
            }
        });

        deliveryPaymentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentDialogFragment paymentDialogFragment = PaymentDialogFragment.newInstance(selectedShipping, selectedPayment, new PaymentDialogInterface() {
                    @Override
                    public void onPaymentSelected(Payment payment) {
                        selectedPayment = payment;
                        showSelectedPayment(payment);
                    }
                });
                paymentDialogFragment.show(getFragmentManager(), "PaymentDialog");
            }
        });
    }

    /**
     * Show and update shipping related values.
     *
     * @param shipping values to show.
     */
    private void showSelectedShipping(Shipping shipping) {
        if (shipping != null && selectedShippingNameTv != null && selectedShippingPriceTv != null) {
            selectedShippingNameTv.setText(shipping.getName());
            if (shipping.getPrice() != 0) {
                selectedShippingPriceTv.setText(shipping.getPriceFormatted());
            } else {
                selectedShippingPriceTv.setText(getText(R.string.free));
            }

            // Set total order price
            //orderTotalPrice = shipping.getTotalPrice();
            //orderTotalPriceTv.setText(shipping.getTotalPriceFormatted());
            deliveryPaymentLayout.setVisibility(View.VISIBLE);
        } else {
            Timber.e("Showing selected shipping with null values.");
        }
    }


    /**
     * Show and update payment related values.
     *
     * @param payment values to show.
     */
    private void showSelectedPayment(Payment payment) {
        if (payment != null && selectedPaymentNameTv != null && selectedPaymentPriceTv != null) {
            selectedPaymentNameTv.setText(payment.getName());
            if (payment.getPrice() != 0) {
                selectedPaymentPriceTv.setText(payment.getPriceFormatted());
            } else {
                selectedPaymentPriceTv.setText(getText(R.string.free));
            }

            // Set total order price
            //orderTotalPrice = payment.getTotalPrice();
            //orderTotalPriceTv.setText(payment.getTotalPriceFormatted());
        } else {
            Timber.e("Showing selected payment with null values.");
        }
    }

    private void getUserCart() {
        final User user = SettingsMy.getActiveUser();
        if (user != null) {
            String url = String.format(EndPoints.CART, SettingsMy.getActualNonNullShop(getActivity()).getId());

            progressDialog.show();
            GsonRequest<CartResponse> getCart = new GsonRequest<>(Request.Method.GET, url, null, CartResponse.class,
                    new Response.Listener<CartResponse>() {
                        @Override
                        public void onResponse(@NonNull CartResponse cart) {
                            if (progressDialog != null) progressDialog.cancel();
                            refreshScreenContent(cart.getCart(), user);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null) progressDialog.cancel();
                    Timber.e("Get request cart error: %s", error.getMessage());
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).onDrawerBannersSelected();
                }
            }, getFragmentManager(), "");
            getCart.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            getCart.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(getCart, CONST.ORDER_CREATE_REQUESTS_TAG);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT);
        }
    }

    private void refreshScreenContent(@NonNull Cart cart, User user) {
        this.cart = cart;
        String color = null;
        String size = null;
        String time = null;
        String date = null;


        List<CartProductItem> cartProductItems = cart.getItems();
        if (cartProductItems == null || cartProductItems.isEmpty()) {
            Timber.e(new RuntimeException(), "Received null cart during order creation.");
            if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).onDrawerBannersSelected();
        } else {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < cartProductItems.size(); i++) {
                LinearLayout llRow = (LinearLayout) inflater.inflate(R.layout.order_create_cart_item, cartItemsLayout, false);
                TextView tvItemName = (TextView) llRow.findViewById(R.id.order_create_cart_item_name);
                tvItemName.setText(cartProductItems.get(i).getName());
                TextView tvItemPrice = (TextView) llRow.findViewById(R.id.order_create_cart_item_price);
                tvItemPrice.setText(cartProductItems.get(i).getTotalFormatted());
                TextView tvItemQuantity = (TextView) llRow.findViewById(R.id.order_create_cart_item_quantity);
                tvItemQuantity.setText(getString(R.string.format_quantity, cartProductItems.get(i).getQuantity()));
                TextView tvItemDetails = (TextView) llRow.findViewById(R.id.order_create_cart_item_details);

                for(CartProductItemVariant cpiv : cartProductItems.get(i).getVariant()) {
                    if (cpiv.getName().toLowerCase().equals(CONST.OPTION_NAME_COLOR))
                        color = cpiv.getValue();

                    if (cpiv.getName().toLowerCase().equals(CONST.OPTION_NAME_SIZE))
                        size = cpiv.getValue();

                    if (cpiv.getName().toLowerCase().equals(CONST.OPTION_NAME_TIME))
                        time = cpiv.getValue();

                    if (cpiv.getName().toLowerCase().equals(CONST.OPTION_NAME_DATE))
                        date = cpiv.getValue();
                }

                tvItemDetails.setText(getString(R.string.format_string_division, color,
                        size, date, time));
                cartItemsLayout.addView(llRow);
            }
            //TODO: check discount Mahesh
            /*if (cart.getDiscounts() != null) {
                for (int i = 0; i < cart.getDiscounts().size(); i++) {
                    LinearLayout llRow = (LinearLayout) inflater.inflate(R.layout.order_create_cart_item, cartItemsLayout, false);
                    TextView tvItemName = (TextView) llRow.findViewById(R.id.order_create_cart_item_name);
                    TextView tvItemPrice = (TextView) llRow.findViewById(R.id.order_create_cart_item_price);
                    tvItemName.setText(cart.getDiscounts().get(i).getDiscount().getName());
                    tvItemPrice.setText(cart.getDiscounts().get(i).getDiscount().getValueFormatted());
                    tvItemPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    cartItemsLayout.addView(llRow);
                }
            }*/

            cartItemsTotalPrice.setText(cart.getTotalPriceFormatted());
            orderTotalPriceTv.setText(cart.getTotalPriceFormatted());

            // TODO pull to scroll could be cool here
            //

            DeliveryRequest deliveryReq = new DeliveryRequest();
            Delivery delivery = new Delivery();
            Shipping shipping = new Shipping();
            shipping.setName("personal");
            //shipping.setPrice(1);
            shipping.setShippingMethod("free.free");

            Payment paymentChq = new Payment();
            paymentChq.setName("Cheque");
            //paymentChq.setPrice(1.0);
            paymentChq.setPaymentMethod("cheque");

            Payment paymentCCAvenue = new Payment();
            paymentCCAvenue.setName("Credit-Debit-NetBank");
            //paymentCCAvenue.setPrice(1.0);
            paymentCCAvenue.setPaymentMethod("ccavenuepay");

            ArrayList<Payment> payments = new ArrayList<>();
            payments.add(paymentChq);
            payments.add(paymentCCAvenue);

            shipping.setPayment(payments);

            ArrayList<Shipping> shippings = new ArrayList<>();
            shippings.add(shipping);

            delivery.setShipping(shippings);
            this.delivery = delivery;
            deliveryProgressBar.setVisibility(View.GONE);
            deliveryShippingLayout.setVisibility(View.VISIBLE);


            /*String url = String.format(EndPoints.CART_DELIVERY_INFO, SettingsMy.getActualNonNullShop(getActivity()).getId());

            deliveryProgressBar.setVisibility(View.VISIBLE);
            GsonRequest<DeliveryRequest> getDelivery = new GsonRequest<>(Request.Method.GET, url, null, DeliveryRequest.class,
                    new Response.Listener<DeliveryRequest>() {
                        @Override
                        public void onResponse(@NonNull DeliveryRequest deliveryResp) {
                            Timber.d("GetDelivery: %s", deliveryResp.toString());
                            delivery = deliveryResp.getDelivery();
                            deliveryProgressBar.setVisibility(View.GONE);
                            deliveryShippingLayout.setVisibility(View.VISIBLE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Timber.e("Get request cart error: %s", error.getMessage());
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);

                    deliveryProgressBar.setVisibility(View.GONE);
                    if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).onDrawerBannersSelected();
                }
            }, getFragmentManager(), user.getAccessToken());
            getDelivery.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            getDelivery.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(getDelivery, CONST.ORDER_CREATE_REQUESTS_TAG); */

        }
    }

    private void postOrder(final Order order) {
        final User user = SettingsMy.getActiveUser();
        if (user != null) {
            JSONObject jo;
            try {
                jo = JsonUtils.createOrderJson(order);
            } catch (JSONException e) {
                Timber.e(e, "Post order Json exception.");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }

            Timber.d("Post order jo: %s", jo.toString());

            progressDialog.show();

            //
            //Set Payment Address
            //
            String paymentAddressUrl = String.format(EndPoints.PAYMENT_ADDRESS);
            JSONObject joPaymentAddressReq = new JSONObject();

            try {

                joPaymentAddressReq.put(JsonUtils.TAG_FIRST_NAME, order.getFirstName());
                joPaymentAddressReq.put(JsonUtils.TAG_LAST_NAME, order.getLastName());
                joPaymentAddressReq.put(JsonUtils.TAG_ADDRESS1, order.getAddress1());
                joPaymentAddressReq.put(JsonUtils.TAG_ADDRESS2, order.getAddress1());
                joPaymentAddressReq.put(JsonUtils.TAG_CITY, order.getCity());
                joPaymentAddressReq.put(JsonUtils.TAG_COUNTRY_ID, order.getCountryId());
                joPaymentAddressReq.put(JsonUtils.TAG_ZONE, order.getZoneId());
                joPaymentAddressReq.put(JsonUtils.TAG_POST_CODE, order.getZip());
                joPaymentAddressReq.put(JsonUtils.TAG_COMPANY, order.getCompany());

            } catch (JSONException e) {
                Timber.e(e, "Json construction exception.");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }

            JsonRequest postPaymentAddressReq = new JsonRequest(Request.Method.POST, paymentAddressUrl, joPaymentAddressReq, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                            if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {

                                prepareShippingAddressPost(order);

                                Analytics.logOrderCreatedEvent(cart, order.getRemoteId(), orderTotalPrice, selectedShipping);

                                updateUserData(user, order);
                                MainActivity.updateCartCountNotification();

                                Timber.d("response: payment address %s", order.toString());
                            }
                            else {
                                Timber.d("response: payment address request returned false");
                                progressDialog.cancel();
                            }
                        }

                    } catch (Exception e) {
                        Timber.e(e, "Payment Address info parse exception");
                        progressDialog.cancel();
                        return;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    progressDialog.cancel();
                }
            }, getFragmentManager(), "");
            postPaymentAddressReq.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            postPaymentAddressReq.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(postPaymentAddressReq, CONST.ORDER_CREATE_REQUESTS_TAG);

        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT);
        }
    }

    private void prepareShippingAddressPost(final Order order) {

        //
        //Set Shipping Address
        //
        String shippingAddressUrl = String.format(EndPoints.SHIPPING_ADDRESS);
        JSONObject joShippingAddressReq = new JSONObject();

        try {

            joShippingAddressReq.put(JsonUtils.TAG_FIRST_NAME, order.getFirstName());
            joShippingAddressReq.put(JsonUtils.TAG_LAST_NAME, order.getLastName());
            joShippingAddressReq.put(JsonUtils.TAG_ADDRESS1, order.getAddress1());
            joShippingAddressReq.put(JsonUtils.TAG_ADDRESS2, order.getAddress1());
            joShippingAddressReq.put(JsonUtils.TAG_CITY, order.getCity());
            joShippingAddressReq.put(JsonUtils.TAG_COUNTRY_ID, order.getCountryId());
            joShippingAddressReq.put(JsonUtils.TAG_ZONE, order.getZoneId());
            joShippingAddressReq.put(JsonUtils.TAG_POST_CODE, order.getZip());
            joShippingAddressReq.put(JsonUtils.TAG_COMPANY, order.getCompany());
            joShippingAddressReq.put(JsonUtils.TAG_SHIPPING_ADDRESS, order.getShippingAddress());

        } catch (JSONException e) {
            Timber.e(e, "Json construction exception.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }

        JsonRequest postShippingAddressReq = new JsonRequest(Request.Method.POST, shippingAddressUrl, joShippingAddressReq, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    //if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                    //    if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {

                            prepareShippingMethodsGet(order);

                            Timber.d("response: shipping address %s", order.toString());
                    /*    }
                        else {
                            Timber.d("response: shipping address request returned false");
                            progressDialog.cancel();
                        }
                    }*/

                } catch (Exception e) {
                    Timber.e(e, "Shipping Address info parse exception");
                    progressDialog.cancel();
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
                progressDialog.cancel();
            }
        }, getFragmentManager(), "");
        postShippingAddressReq.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        postShippingAddressReq.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(postShippingAddressReq, CONST.ORDER_CREATE_REQUESTS_TAG);

    }

    private void prepareShippingMethodsGet(final Order order) {

        //
        //Get Shipping Methods
        //
        String shippingMethodsUrl = String.format(EndPoints.SHIPPING_METHODS);

        JsonRequest getShippingMethodsReq = new JsonRequest(Request.Method.GET, shippingMethodsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                        if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {

                            prepareShippingMethodsPost(order);

                            Timber.d("response: shipping methods get %s", order.toString());
                        }
                        else {
                            Timber.d("response: shipping method get request returned false");
                            progressDialog.cancel();
                        }
                    }

                } catch (Exception e) {
                    Timber.e(e, "Shipping Methods info parse exception");
                    progressDialog.cancel();
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
                progressDialog.cancel();
            }
        }, getFragmentManager(), "");
        getShippingMethodsReq.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getShippingMethodsReq.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getShippingMethodsReq, CONST.ORDER_CREATE_REQUESTS_TAG);
    }

    private void prepareShippingMethodsPost(final Order order) {
        //
        //Post Shipping Methods
        //
        String shippingMethodsUrl = String.format(EndPoints.SHIPPING_METHODS);
        JSONObject joShippingMethodReq = new JSONObject();

        try {

            joShippingMethodReq.put(JsonUtils.TAG_SHIPPING_METHOD, order.getShippingMethod());
            joShippingMethodReq.put(JsonUtils.TAG_COMMENT, (order.getNote() != "" ? order.getNote() : "Place order..."));

        } catch (JSONException e) {
            Timber.e(e, "Json construction exception.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }

        JsonRequest postShippingMethodsReq = new JsonRequest(Request.Method.POST, shippingMethodsUrl, joShippingMethodReq, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                        if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {

                            preparePaymentMethodsGet(order);

                            Timber.d("response: shipping methods post %s", order.toString());
                        }
                        else {
                            Timber.d("response: shipping method post request returned false");
                            progressDialog.cancel();
                        }
                    }

                } catch (Exception e) {
                    Timber.e(e, "Shipping Methods info parse exception");
                    progressDialog.cancel();
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
                progressDialog.cancel();
            }
        }, getFragmentManager(), "");
        postShippingMethodsReq.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        postShippingMethodsReq.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(postShippingMethodsReq, CONST.ORDER_CREATE_REQUESTS_TAG);
    }

    private void preparePaymentMethodsGet(final Order order) {
        //
        //Get Payment Methods
        //
        String paymentMethodsUrl = String.format(EndPoints.PAYMENT_METHODS);

        JsonRequest getPaymentMethodsReq = new JsonRequest(Request.Method.GET, paymentMethodsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                        if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {

                            preparePaymentMethodsPost(order);

                            Timber.d("response: payment methods get %s", order.toString());
                        }
                        else {
                            Timber.d("response: payment method get request returned false");
                            progressDialog.cancel();
                        }
                    }

                } catch (Exception e) {
                    Timber.e(e, "Payment Methods info parse exception");
                    progressDialog.cancel();
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
                progressDialog.cancel();
            }
        }, getFragmentManager(), "");
        getPaymentMethodsReq.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getPaymentMethodsReq.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getPaymentMethodsReq, CONST.ORDER_CREATE_REQUESTS_TAG);
    }

    private void preparePaymentMethodsPost(final Order order) {
        //
        //Post Payment Methods
        //
        String paymentMethodsUrl = String.format(EndPoints.PAYMENT_METHODS);
        JSONObject joPaymentMethodReq = new JSONObject();

        try {

            joPaymentMethodReq.put(JsonUtils.TAG_PAYMENT_METHOD, order.getPaymentMethod());
            joPaymentMethodReq.put(JsonUtils.TAG_AGREE, order.getAgree());
            joPaymentMethodReq.put(JsonUtils.TAG_COMMENT, (order.getNote() != "" ? order.getNote() : "Place order..."));

        } catch (JSONException e) {
            Timber.e(e, "Json construction exception.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }

        JsonRequest postPaymentMethodsReq = new JsonRequest(Request.Method.POST, paymentMethodsUrl, joPaymentMethodReq, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                        if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {

                            prepareConfirmPost(order);
                            Timber.d("response: payment methods post %s", order.toString());
                        }
                        else {
                            Timber.d("response: payment method post request returned false");
                            progressDialog.cancel();
                        }
                    }

                } catch (Exception e) {
                    Timber.e(e, "Payment Methods info parse exception");
                    progressDialog.cancel();
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
                progressDialog.cancel();
            }
        }, getFragmentManager(), "");
        postPaymentMethodsReq.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        postPaymentMethodsReq.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(postPaymentMethodsReq, CONST.ORDER_CREATE_REQUESTS_TAG);
    }

    private void prepareConfirmPost(final Order order) {
        //
        //Post Confirm
        //

        JSONObject joConfirmReq = new JSONObject();

        try {

            joConfirmReq.put("","");

        } catch (JSONException e) {
            Timber.e(e, "Json construction exception.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }

        String paymentConfirmUrl = String.format(EndPoints.PAYMENT_CONFIRM);

        GsonRequest<OrderConfirmResponse> req = new GsonRequest<>(Request.Method.POST, paymentConfirmUrl, "", OrderConfirmResponse.class, new Response.Listener<OrderConfirmResponse>() {
            @Override
            public void onResponse(OrderConfirmResponse response) {
                if(response.getConfirm() != null) {
                    if (BuildConfig.DEBUG)
                           Timber.d("Payment Confirm Response: %s", response.getConfirm().getOrder_id());

                    preparePayRedirection(order, response.getConfirm().getOrder_id());
                    progressDialog.cancel();
                    }
                    else {
                        Timber.d("response: payment confirm post is <null>");
                    }
                //progressDialog.cancel();
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
        MyApplication.getInstance().addToRequestQueue(req, CONST.ORDERS_HISTORY_REQUESTS_TAG);
    }

    private void preparePayRedirection(final Order order, String order_id) {

        Bundle bundlePaymentConfirmReq = new Bundle();

        PaymentArgs paymentArgs = new PaymentArgs();
        paymentArgs.setBillingName(order.getFirstName() +" "+ order.getLastName());
        paymentArgs.setBillingAddress(order.getAddress1());
        paymentArgs.setBillingCity(order.getCity());
        paymentArgs.setBillingRegion(order.getRegion());
        paymentArgs.setBillingPhone(order.getPhone());
        paymentArgs.setBillingZip(order.getZip());
        paymentArgs.setBillingEmail(order.getEmail());
        paymentArgs.setBillingAmount(orderTotalPriceTv.getText().toString());
        paymentArgs.setBillingOrderId(order_id);


        bundlePaymentConfirmReq.putSerializable(CONST.ORDERS_CREATE_FRAGMENT_TAG, paymentArgs);

        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtras(bundlePaymentConfirmReq);
        startActivity(intent);

        //prepareConfirmPost();

        /*Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity)
            ((MainActivity) getActivity()).startPaymentFragment(bundlePaymentConfirmReq);*/
    }

    /**
     * Update user information after successful order.
     *
     * @param user  actual user which will be updated
     * @param order order response for obtain user information
     */
    private void updateUserData(User user, Order order) {
        if (user != null) {
            if (order.getFirstName() != null && !order.getFirstName().isEmpty()) {
                user.setFirstname(order.getFirstName());
            }

            if (order.getLastName() != null && !order.getLastName().isEmpty()) {
                user.setLastname(order.getLastName());
            }

            user.setAddress(order.getAddress1());
            user.setCountry(order.getCountry());
            user.setEmail(order.getEmail());
            user.setTelephone(order.getPhone());
            user.setCity(order.getCity());
            user.setPostalcode(order.getZip());
            user.setRegion(order.getRegion());

            SettingsMy.setActiveUser(user);
        } else {
            Timber.e(new NullPointerException(), "Null user after successful order.");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getInstance().cancelPendingRequests(CONST.ORDER_CREATE_REQUESTS_TAG);
        if (progressDialog != null) progressDialog.cancel();
        if (deliveryProgressBar != null) deliveryProgressBar.setVisibility(View.GONE);
    }
}
