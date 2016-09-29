package com.teketys.templetickets.ux.fragments;

/**
 * Created by rudram1 on 8/25/16.
 */

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

import java.text.DecimalFormat;
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
import com.teketys.templetickets.entities.UserAddress;
import com.teketys.templetickets.entities.cart.Cart;
import com.teketys.templetickets.entities.cart.CartProductItem;
import com.teketys.templetickets.entities.cart.CartProductItemVariant;
import com.teketys.templetickets.entities.cart.CartResponse;
import com.teketys.templetickets.entities.cart.CartTotals;
import com.teketys.templetickets.entities.delivery.Delivery;
import com.teketys.templetickets.entities.delivery.DeliveryRequest;
import com.teketys.templetickets.entities.delivery.Payment;
import com.teketys.templetickets.entities.delivery.Shipping;
import com.teketys.templetickets.entities.order.Order;
import com.teketys.templetickets.entities.order.OrderConfirmResponse;
import com.teketys.templetickets.interfaces.PaymentDialogInterface;
import com.teketys.templetickets.listeners.OnSingleClickListener;
import com.teketys.templetickets.utils.JsonUtils;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.utils.ccavenue.PaymentArgs;
import com.teketys.templetickets.ux.MainActivity;
import com.teketys.templetickets.ux.ccavenue.WebViewActivity;
import com.teketys.templetickets.ux.dialogs.LoginDialogFragment;
import com.teketys.templetickets.ux.dialogs.LoginExpiredDialogFragment;
import com.teketys.templetickets.ux.dialogs.OrderCreateSuccessDialogFragment;
import com.teketys.templetickets.ux.dialogs.PaymentDialogFragment;

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
                    order.setZoneId("1489");
                    order.setCompany("");
                    order.setRegion(Utils.getTextFromInputLayout(regionInputWrapper));
                    order.setZip(Utils.getTextFromInputLayout(zipInputWrapper));
                    order.setEmail(Utils.getTextFromInputLayout(emailInputWrapper));
                    order.setShippingMethod((selectedShipping.getShippingMethod()) != null ? selectedShipping.getShippingMethod() : "free.free");
                    order.setShippingAddress("new");
                    order.setAgree("1");


                    if (selectedPayment != null) {
                        order.setPaymentMethod(selectedPayment.getPaymentMethod());
                    } else {
                        order.setPaymentType(-1);
                    }
                    order.setPhone(Utils.getTextFromInputLayout(phoneInputWrapper));
                    order.setNote(Utils.getTextFromInputLayout(noteInputWrapper));

                    order.setShippingMethod("free.free");

                    // Hide keyboard
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    postOrder(order);
                }
            }
        });

        //showSelectedShipping(selectedShipping);
        showSelectedPayment(selectedPayment);

        getUserCart();
        return view;
    }

    public static OrderCreateFragment newInstance(String url) {
        Bundle args = new Bundle();
        OrderCreateFragment fragment = new OrderCreateFragment();
        fragment.setArguments(args);
        return fragment;
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
            Utils.setTextToInputLayout(addressInputWrapper, user.getAddress().getAddress_1());
            Utils.setTextToInputLayout(cityInputWrapper, user.getAddress().getCity());
            Utils.setTextToInputLayout(zipInputWrapper, user.getAddress().getPostCode());
            Utils.setTextToInputLayout(countryInputWrapper, user.getAddress().getCountry());
            Utils.setTextToInputLayout(regionInputWrapper, user.getAddress().getZone());
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
            /*if (selectedShipping == null) {
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Choose_shipping_method), MsgUtils.ToastLength.SHORT);
                scrollLayout.smoothScrollTo(0, deliveryShippingLayout.getTop());
                return false;
            }*/

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

        /*deliveryShippingLayout.setOnClickListener(new View.OnClickListener() {
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
        });*/

        // Continue for payment
        selectedPayment = null;
        deliveryPaymentLayout.setVisibility(View.VISIBLE);
        selectedPaymentNameTv.setText(getString(R.string.Choose_payment_method));
        selectedPaymentPriceTv.setText("");
        deliveryPaymentLayout.performClick();

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
                //selectedShippingPriceTv.setText(getText(R.string.free));
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
                //selectedPaymentPriceTv.setText(getText(R.string.free));
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

                            if(cart != null) {
                                if(cart.getStatusCode() != null && cart.getStatusText() != null) {
                                    if (cart.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || cart.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                        LoginDialogFragment.logoutUser(true);
                                        DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                        loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                        if (progressDialog != null) progressDialog.cancel();
                                    }
                                }
                                else
                                    refreshScreenContent(cart.getCart(), user);
                            }
                            else {
                                Timber.d("returned null response during getUserCart");
                                refreshScreenContent(cart.getCart(), user);
                            }
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

        if(cart != null) {
            if (cart.getItems() != null) {
                List<CartProductItem> cartProductItems = cart.getItems();
                if (cartProductItems == null || cartProductItems.isEmpty()) {
                    Timber.e(new RuntimeException(), "Received null cart during order creation.");
                    if (getActivity() instanceof MainActivity)
                        ((MainActivity) getActivity()).onDrawerBannersSelected();
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

                        for (CartProductItemVariant cpiv : cartProductItems.get(i).getVariant()) {
                            if (cpiv.getName().toLowerCase().equals(CONST.OPTION_NAME_COLOR))
                                color = cpiv.getValue();

                            if (cpiv.getName().toLowerCase().equals(CONST.OPTION_NAME_SIZE))
                                size = cpiv.getValue();

                            if (cpiv.getName().toLowerCase().equals(CONST.OPTION_NAME_TIME))
                                time = cpiv.getValue();

                            if (cpiv.getName().toLowerCase().equals(CONST.OPTION_NAME_DATE))
                                date = cpiv.getValue();
                        }

                        if (color != null && size != null) {
                            tvItemDetails.setText(getString(R.string.format_string_division, color,
                                    size, date, time));
                        } else {
                            tvItemDetails.setText(getString(R.string.format_string_division_two, date, time));
                        }
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

                    /**
                     * Calculate the tax and arrive at the final price
                     */
                    String totalCost = null;

                    /***
                     * Calculate the total price of the puja
                     */
                    for (CartTotals cTotals : cart.getCartTotals()) {
                        if (cTotals.getTitle().toLowerCase().equals(CONST.TOTAL_PRICE)) {
                            totalCost = cTotals.getText().replace(",","");
                            break;
                        }
                    }

                    calculateTotalPujaCost(totalCost, (cart.getItems() != null && cart.getItems().size() > 0) ? cart.getItems().get(0).getCurrency() : "INR", cart.getProductCount());

                    // TODO pull to scroll could be cool here
                    //

                    DeliveryRequest deliveryReq = new DeliveryRequest();
                    Delivery delivery = new Delivery();
                    Shipping shipping = new Shipping();
                    shipping.setName("personal");
                    //shipping.setPrice(1);
                    shipping.setShippingMethod("free.free");

                    selectedShipping = shipping;

                /*Payment paymentChq = new Payment();
                paymentChq.setName("Cheque");
                paymentChq.setPaymentMethod("cheque");*/

                    Payment paymentCash = new Payment();
                    paymentCash.setName("Cash");
                    paymentCash.setPaymentMethod("cod");

                    Payment paymentCCAvenue = new Payment();
                    paymentCCAvenue.setName("Credit-Debit-NetBank");
                    paymentCCAvenue.setPaymentMethod("ccavenuepay");

                    ArrayList<Payment> payments = new ArrayList<>();
                    //payments.add(paymentChq);
                    payments.add(paymentCash);
                    payments.add(paymentCCAvenue);

                    shipping.setPayment(payments);

                    ArrayList<Shipping> shippings = new ArrayList<>();
                    shippings.add(shipping);

                    delivery.setShipping(shippings);
                    this.delivery = delivery;
                    deliveryProgressBar.setVisibility(View.GONE);
                    deliveryShippingLayout.setVisibility(View.GONE);
                }
            } else
                Timber.d("Received null cart during refresh screen content.");
        } else
            Timber.d("Received null cart during refresh screen content.");
    }

    private void calculateTotalPujaCost(String pujaCost, String currency, int productCount) {

        if(pujaCost != null && pujaCost != "") {
            double basePujaCost = Double.valueOf(pujaCost);

            //Convenience Fees
            double convenienceFees = CONST.CONVENIENCE_FEES;

            //Calculate Base Service Tax
            double serviceTax = (convenienceFees * CONST.BASE_SERVICE_TAX) / 100;

            //Calculate Swacch Bharath Cess
            double swacchBC = (convenienceFees * CONST.SWACCH_BHARATH_CESS) / 100;

            //Calculate Krishi Kalyan Cess
            double krishiKC = (convenienceFees * CONST.KRISHI_KALYAN_CESS) / 100;

            //Total Convenience Fees
            double totalConvenienceFees = (convenienceFees + serviceTax + swacchBC + krishiKC) * productCount;

            //Total Puja Cost
            double totalPujaCost = basePujaCost + totalConvenienceFees;

            DecimalFormat df = new DecimalFormat("#.##");

            StringBuffer priceBreakUpSB = new StringBuffer();
            priceBreakUpSB.append(getString(R.string.format_quantity, productCount) + "\n");
            priceBreakUpSB.append("----------------------" + "\n");
            priceBreakUpSB.append("Convenience Fees: "+ String.format("%.2f",convenienceFees) + "\n");
            priceBreakUpSB.append("Base Service Tax: "+ String.format("%.2f",serviceTax) + "\n");
            priceBreakUpSB.append("Swacch Bharat Cess: "+ String.format("%.2f",swacchBC) + "\n");
            priceBreakUpSB.append("Krishi Kalyan Cess: "+ String.format("%.2f",krishiKC) + "\n");
            priceBreakUpSB.append("----------------------" + "\n");
            priceBreakUpSB.append("Total Convenience Fees: "+ String.format("%.2f",totalConvenienceFees) + "\n");
            priceBreakUpSB.append("---------------------- "+ "\n");

            priceBreakUpSB.append("Puja Cost: "+ String.format("%.2f",basePujaCost) + "\n");
            priceBreakUpSB.append("Total Cost ("+ currency +"): " +String.format("%.2f",totalPujaCost));

            orderTotalPriceTv.setText(priceBreakUpSB.toString());

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
            //Get Payment Address - To check whether the Payment address is already set or not
            //
            String paymentAddressUrl = String.format(EndPoints.PAYMENT_ADDRESS);

            JsonRequest getPaymentAddressReq = new JsonRequest(Request.Method.GET, paymentAddressUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        if(response != null) {
                            if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                                LoginDialogFragment.logoutUser(true);
                                DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                if (progressDialog != null) progressDialog.cancel();
                            }
                            else {
                                if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                                    if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {
                                        prepareExistPaymentAddressPost(order, response.getJSONObject(JsonUtils.TAG_DATA).getJSONArray(JsonUtils.TAG_ADDRESSES).getJSONObject(0).getString(JsonUtils.TAG_ADDRESS_ID));
                                        Timber.d("response: get payment address request returned true, %s", order.toString());
                                    } else {
                                        preparePaymentAddressPost(order);
                                        Timber.d("response: get payment address request returned false");
                                    }

                                    updateUserData(user, order);
                                }
                            }
                        }
                        else {
                            Timber.d("Null response during postOrder....");
                            progressDialog.cancel();
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
            getPaymentAddressReq.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            getPaymentAddressReq.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(getPaymentAddressReq, CONST.ORDER_CREATE_REQUESTS_TAG);

        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT);
        }
    }

    //
    //POST Payment Address - with existing payment address
    //
    private void prepareExistPaymentAddressPost(final Order order, String addressID) {
        //
        //Set Payment Address
        //
        String paymentAddressUrl = String.format(EndPoints.PAYMENT_ADDRESS);
        JSONObject joExistPaymentAddressReq = new JSONObject();

        try {
            joExistPaymentAddressReq.put(JsonUtils.TAG_PAYMENT_ADDRESS, JsonUtils.TAG_EXISTING);
            joExistPaymentAddressReq.put(JsonUtils.TAG_ADDRESS_ID, addressID);

        } catch (JSONException e) {
            Timber.e(e, "Json construction exception.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }

        JsonRequest postPaymentAddressReq = new JsonRequest(Request.Method.POST, paymentAddressUrl, joExistPaymentAddressReq, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if(response != null) {
                        if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                            LoginDialogFragment.logoutUser(true);
                            DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                            loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                            if (progressDialog != null) progressDialog.cancel();
                        }
                        else {
                            if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                                if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {
                                    prepareShippingAddressGet(order);
                                    Timber.d("response: existing payment address %s", order.toString());
                                } else {
                                    Timber.d("response: existing payment address request returned false");
                                    progressDialog.cancel();
                                }
                            }
                        }
                    }
                    else
                        Timber.d("Null response during postOrder....");

                } catch (Exception e) {
                    Timber.e(e, "Existing Payment Address info parse exception");
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

    }

    //
    //POST Payment Address - with new payment address
    //
    private void preparePaymentAddressPost(final Order order) {
        JSONObject jo;
        try {
            jo = JsonUtils.createOrderJson(order);
        } catch (JSONException e) {
            Timber.e(e, "Post order Json exception.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }

        Timber.d("Prepare payment address post jo: %s", jo.toString());

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

                    if(response != null) {
                        if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                            LoginDialogFragment.logoutUser(true);
                            DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                            loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                            if (progressDialog != null) progressDialog.cancel();
                        }
                        else {
                            if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                                if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {
                                    prepareShippingAddressGet(order);
                                    Timber.d("response: payment address %s", order.toString());
                                } else {
                                    Timber.d("response: payment address request returned false");
                                    progressDialog.cancel();
                                }
                            }
                        }
                    }
                    else
                        Timber.d("Null response during postOrder....");

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
    }

    //
    //Get Shipping Address - To check whether the Shipping address is already set or not
    //
    private void prepareShippingAddressGet(final Order order) {

        //
        //Get Shipping Address
        //
        String shippingAddressUrl = String.format(EndPoints.SHIPPING_ADDRESS);

        JsonRequest postShippingAddressReq = new JsonRequest(Request.Method.GET, shippingAddressUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                        if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {
                            prepareExistShippingAddressPost(order, response.getJSONObject(JsonUtils.TAG_DATA).getJSONArray(JsonUtils.TAG_ADDRESSES).getJSONObject(0).getString(JsonUtils.TAG_ADDRESS_ID));
                            Timber.d("response: get shipping address %s", order.toString());
                        }
                        else {
                            prepareShippingAddressPost(order);
                            Timber.d("response: get shipping address request returned false");
                        }
                    }

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

    private void prepareExistShippingAddressPost(final Order order, String addressID) {
        //
        //Set Shipping Address - existing shipping address
        //
        String shippingAddressUrl = String.format(EndPoints.SHIPPING_ADDRESS);
        JSONObject joExistShippingAddressReq = new JSONObject();

        try {
            joExistShippingAddressReq.put(JsonUtils.TAG_SHIPPING_ADDRESS, JsonUtils.TAG_EXISTING);
            joExistShippingAddressReq.put(JsonUtils.TAG_ADDRESS_ID, addressID);

        } catch (JSONException e) {
            Timber.e(e, "Json construction exception.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }

        JsonRequest postShippingAddressReq = new JsonRequest(Request.Method.POST, shippingAddressUrl, joExistShippingAddressReq, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                        if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {
                            prepareShippingMethodsGet(order);
                            Timber.d("response: exist shipping address %s", order.toString());
                        }
                        else {
                            Timber.d("response: exist shipping address request returned false");
                            progressDialog.cancel();
                        }
                    }

                } catch (Exception e) {
                    Timber.e(e, "Exist Shipping Address info parse exception");
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

    private void prepareShippingAddressPost(final Order order) {

        //
        //Set Shipping Address - new shipping address
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

                    if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                        if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {
                            prepareShippingMethodsGet(order);
                            Timber.d("response: new shipping address %s", order.toString());
                        }
                        else {
                            Timber.d("response: new shipping address request returned false");
                            progressDialog.cancel();
                        }
                    }

                } catch (Exception e) {
                    Timber.e(e, "New Shipping Address info parse exception");
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
                    if(response != null) {
                        if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                            LoginDialogFragment.logoutUser(true);
                            DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                            loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                            if (progressDialog != null) progressDialog.cancel();
                        }
                        else {
                            if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                                if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {

                                    prepareShippingMethodsPost(order);

                                    Timber.d("response: shipping methods get %s", order.toString());
                                } else {
                                    Timber.d("response: shipping method get request returned false");
                                    progressDialog.cancel();
                                }
                            }
                        }
                    }
                    else
                        Timber.d("Null response during prepareShippingMethodsGet....");

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
                    if(response != null) {
                        if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                            LoginDialogFragment.logoutUser(true);
                            DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                            loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                            if (progressDialog != null) progressDialog.cancel();
                        }
                        else {
                            if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                                if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {

                                    preparePaymentMethodsGet(order);

                                    Timber.d("response: shipping methods post %s", order.toString());
                                } else {
                                    Timber.d("response: shipping method post request returned false");
                                    progressDialog.cancel();
                                }
                            }
                        }
                    }
                    else
                        Timber.d("Null response during prepareShippingMethodsPost....");

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

                    if(response != null) {
                        if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                            LoginDialogFragment.logoutUser(true);
                            DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                            loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                            if (progressDialog != null) progressDialog.cancel();
                        }
                        else {
                            if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                                if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {
                                    preparePaymentMethodsPost(order);
                                    Timber.d("response: payment methods get %s", order.toString());
                                } else {
                                    Timber.d("response: payment method get request returned false");
                                    progressDialog.cancel();
                                }
                            }
                        }
                    }
                    else
                        Timber.d("Null response during preparePaymentMethodsGet....");

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
                    if(response != null) {
                        if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                            LoginDialogFragment.logoutUser(true);
                            DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                            loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                            if (progressDialog != null) progressDialog.cancel();
                        }
                        else {
                            if (response.has(JsonUtils.TAG_SUCCESS) && !response.isNull(JsonUtils.TAG_SUCCESS)) {
                                if (response.getBoolean(JsonUtils.TAG_SUCCESS)) {

                                    prepareConfirmPost(order);
                                    Timber.d("response: payment methods post %s", order.toString());
                                } else {
                                    Timber.d("response: payment method post request returned false");
                                    progressDialog.cancel();
                                }
                            }
                        }
                    }
                    else
                        Timber.d("Null response during preparePaymentMethodsPost....");

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
                if(response != null) {
                    if(response.getStatusText() != null && response.getStatusCode() != null) {
                        if (response.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || response.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                            LoginDialogFragment.logoutUser(true);
                            DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                            loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                            if (progressDialog != null) progressDialog.cancel();
                        }
                    }
                    else {
                        if (response.getConfirm() != null) {
                            if (BuildConfig.DEBUG)
                                Timber.d("Payment Confirm Response: %s", response.getConfirm().getOrder_id());

                            preparePayRedirection(order, response.getConfirm().getOrder_id());
                            progressDialog.cancel();
                        } else {
                            Timber.d("response: payment confirm post is <null>");
                        }
                    }
                }
                else
                    Timber.d("Null response during prepareConfirmPost....");
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

        if(selectedPayment.getPaymentMethod().toLowerCase().equals(CONST.PAYMENT_METHOD)) {
            Timber.d("Payment Method CCAvenue is selected and redirecting to Payment Gateway!!!");
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtras(bundlePaymentConfirmReq);
            startActivity(intent);
        }
        else {
            //Perform the final step here without going through payment gateway!!!
            Timber.d("Payment Method is Cash, so by-passing Payment Gateway and confirmig the Order!!!");
            DialogFragment thankYouDF = OrderCreateSuccessDialogFragment.newInstance(false, false);
            thankYouDF.show(getFragmentManager(), OrderCreateSuccessDialogFragment.class.getSimpleName());
        }

        //MainActivity.updateCartCountNotification();

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

            //user.setAddress(order.getAddress1());
            UserAddress userAddress = new UserAddress();
            userAddress.setAddress_1(order.getAddress1());
            userAddress.setCity(order.getCity());
            userAddress.setCountry(order.getCountry());
            userAddress.setPostCode(order.getZip());
            userAddress.setZone(order.getRegion());
            user.setAddress(userAddress);
            user.setEmail(order.getEmail());
            user.setTelephone(order.getPhone());
            //user.setCity(order.getCity());
            //user.setPostalcode(order.getZip());
            //user.setRegion(order.getRegion());

            SettingsMy.setActiveUser(user);

        } else {
            Timber.e(new NullPointerException(), "Null user after successful order.");
        }
    }

    private void replaceFragment(Fragment newFragment, String transactionTag) {
        if (newFragment != null) {
            android.support.v4.app.FragmentManager frgManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = frgManager.beginTransaction();
            fragmentTransaction.addToBackStack(transactionTag);
            fragmentTransaction.replace(R.id.main_content_frame, newFragment).commit();
            frgManager.executePendingTransactions();
        } else {
            Timber.e(new RuntimeException(), "Replace fragments with null newFragment parameter.");
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
