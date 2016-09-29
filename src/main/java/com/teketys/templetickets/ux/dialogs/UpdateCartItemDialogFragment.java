package com.teketys.templetickets.ux.dialogs;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.teketys.templetickets.entities.cart.CartProductItem;
import com.teketys.templetickets.entities.cart.CartProductItemVariant;
import com.teketys.templetickets.entities.product.Product;
import com.teketys.templetickets.entities.product.ProductAttributeGroups;
import com.teketys.templetickets.entities.product.ProductAttributes;
import com.teketys.templetickets.entities.product.ProductColor;
import com.teketys.templetickets.entities.product.ProductDate;
import com.teketys.templetickets.entities.product.ProductQuantity;
import com.teketys.templetickets.entities.product.ProductResponse;
import com.teketys.templetickets.entities.product.ProductSize;
import com.teketys.templetickets.entities.product.ProductTime;
import com.teketys.templetickets.entities.product.ProductVariant;
import com.teketys.templetickets.entities.product.ProductVariantValues;
import com.teketys.templetickets.interfaces.RequestListener;
import com.teketys.templetickets.utils.JsonUtils;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.ux.adapters.CartColorTextSpinnerAdapter;
import com.teketys.templetickets.ux.adapters.CartSizeSpinnerAdapter;
import com.teketys.templetickets.ux.adapters.CartTimeTextSpinnerAdapter;
import com.teketys.templetickets.ux.adapters.QuantitySpinnerAdapter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import timber.log.Timber;

/**
 * Dialog handles update items in the shopping cart.
 */
public class UpdateCartItemDialogFragment extends DialogFragment {

    /**
     * Defined max product quantity.
     */
    private static final int QUANTITY_MAX = 15;

    private CartProductItem cartProductItem;

    private RequestListener requestListener;

    private View dialogProgress;
    private View dialogContent;
    private Spinner itemColorsSpinner;
    private Spinner timeSpinner;
    private Spinner quantitySpinner;
    private Button bookingDateButton;
    private TextView bookingDateText;

    private Product selectedProduct;
    private ProductTime selectedProductTime;

    /**
     * Creates dialog which handles update items in the shopping cart
     *
     * @param cartProductItem item in the cart, which should be updated.
     * @param requestListener listener receiving update request results.
     * @return new instance of dialog.
     */
    public static UpdateCartItemDialogFragment newInstance(CartProductItem cartProductItem, RequestListener requestListener) {
        if (cartProductItem == null) {
            Timber.e(new RuntimeException(), "Created UpdateCartItemDialogFragment with null parameters.");
            return null;
        }
        UpdateCartItemDialogFragment updateCartItemDialogFragment = new UpdateCartItemDialogFragment();
        updateCartItemDialogFragment.cartProductItem = cartProductItem;
        updateCartItemDialogFragment.requestListener = requestListener;
        return updateCartItemDialogFragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        final View view = inflater.inflate(R.layout.dialog_update_cart_item, container, false);

        dialogProgress = view.findViewById(R.id.dialog_update_cart_item_progress);
        dialogContent = view.findViewById(R.id.dialog_update_cart_item_content);
        //itemColorsSpinner = (Spinner) view.findViewById(R.id.dialog_update_cart_item_color_spin);
        bookingDateButton = (Button) view.findViewById(R.id.dialog_update_cart_item_date_picker);
        bookingDateText = (TextView) view.findViewById(R.id.dialog_update_cart_item_date_text);

        timeSpinner = (Spinner) view.findViewById(R.id.dialog_update_cart_item_time_spin);
        TextView itemName = (TextView) view.findViewById(R.id.dialog_update_cart_item_title);
        itemName.setText(cartProductItem.getName());

        //Booking Calendar Option
        bookingDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Initialize a new date picker dialog fragment
                //DialogFragment dFragment = new OrderDateSelectFragment();

                // Show the date picker dialog fragment
                //dFragment.show(getFragmentManager(), "Date Picker");

                final Calendar now = Calendar.getInstance();
                final DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
                                now.set(i, i1, i2);
                                bookingDateText.setText(SimpleDateFormat.getDateInstance().format(now.getTime()));
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setThemeDark(true);
                dpd.vibrate(true);
                dpd.dismissOnPause(true);
                //dpd.setAccentColor(Color.parseColor("#9C27B0"));
                dpd.setTitle("Booking Date");

                //***********************
                //Limit Dates
                //***********************

                dpd.setMinDate(now);

                /*ArrayList<Calendar> lDates = new ArrayList();
                for (int i = 0; i <= 2; i++) {
                    Calendar date = Calendar.getInstance();
                    date.add(Calendar.MONTH, i);
                    lDates.add(date);
                }

                Calendar[] lCalendarArray = lDates.toArray(new Calendar[lDates.size()]);
                dpd.setSelectableDays(lCalendarArray);*/

                //***********************
                //Disable Advanced Booking Dates
                //***********************

                int advancedBookingDays = 0;

                for(ProductAttributeGroups pag : selectedProduct.getAttributeGroups()) {
                    for(ProductAttributes pa : pag.getProductAttributes()) {
                        if (pa.getName().toLowerCase().equals(CONST.OPTION_ATTRIBUTE_BOOKING)) {
                            if (pa.getText() != null || pa.getText() != "")
                                advancedBookingDays = Integer.valueOf(pa.getText());
                            else
                                advancedBookingDays = 1;
                        }
                    }
                }

                ArrayList<Calendar> bDates = new ArrayList();
                for (int i = 0; i < advancedBookingDays; i++) {
                    Calendar date = Calendar.getInstance();
                    date.add(Calendar.DAY_OF_MONTH, i+1);
                    bDates.add(date);
                }

                Calendar[] calendarArray = bDates.toArray(new Calendar[bDates.size()]);
                dpd.setDisabledDays(calendarArray);

                dpd.show(getActivity().getFragmentManager(), "Date Picker");

                //if(bookingDateText.getText() != null || bookingDateText.getText() != "")
                    //upateTimeSpinner(selectedProduct);
            }
        });


        View btnSave = view.findViewById(R.id.dialog_update_cart_item_save_btn);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (quantitySpinner != null && timeSpinner != null && bookingDateText != null) {
                if (quantitySpinner != null) {
                    ProductTime productVariant = (ProductTime) timeSpinner.getSelectedItem();
                    ProductQuantity productQuantity = (ProductQuantity) quantitySpinner.getSelectedItem();

                    Timber.d("Selected: %s. Quantity: %s", productVariant, productQuantity);
                    if (productVariant != null && productVariant.getName() != null && productQuantity != null && bookingDateText != null && bookingDateText.getText() != "") {
                        updateProductInCart(cartProductItem.getKey(), productVariant.getProduct_option_value_id(), productQuantity.getQuantity());
                    } else {
                        Timber.e(new RuntimeException(), "Cannot obtain info about edited cart item.");
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Internal_error_reload_cart_please), MsgUtils.ToastLength.SHORT);
                        dismiss();
                    }
                } else {
                    Timber.e(new NullPointerException(), "Null spinners in editing item in cart");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Internal_error_reload_cart_please), MsgUtils.ToastLength.SHORT);
                    dismiss();
                }
            }
        });

        View btnCancel = view.findViewById(R.id.dialog_update_cart_item_cancel_btn);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Set item quantity
        QuantitySpinnerAdapter adapterQuantity = new QuantitySpinnerAdapter(getActivity(), getQuantities());
        quantitySpinner = (Spinner) view.findViewById(R.id.dialog_update_cart_item_quantity_spin);
        quantitySpinner.setAdapter(adapterQuantity);

        //prepareTimeSpinner(view);
        getProductDetail(cartProductItem);
        return view;
    }

    private void prepareTimeSpinner(View view) {

        ArrayList<ProductTime> productTimes = new ArrayList<>();
        timeSpinner.setVisibility(View.VISIBLE);

        ProductTime pt = new ProductTime();
        pt.setProduct_option_id(CONST.DEFAULT_EMPTY_ID);
        pt.setProduct_option_value_id(CONST.DEFAULT_EMPTY_ID);
        pt.setName(getString(R.string.Select_time));

        productTimes.add(0, pt);
        CartTimeTextSpinnerAdapter cartTimeTextSpinnerAdapter = new CartTimeTextSpinnerAdapter(getActivity());
        cartTimeTextSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cartTimeTextSpinnerAdapter.setProductTimeList(productTimes);
        timeSpinner.setAdapter(cartTimeTextSpinnerAdapter);
    }

    // Prepare quantity spinner layout
    private List<ProductQuantity> getQuantities() {
        List<ProductQuantity> quantities = new ArrayList<>();
        for (int i = 1; i <= QUANTITY_MAX; i++) {
            ProductQuantity q = new ProductQuantity(i, i + "x");
            quantities.add(q);
        }
        return quantities;
    }

    //TODO chech this.... Mahesh
    private void getProductDetail(CartProductItem cartProductItem) {
        String url = String.format(EndPoints.PRODUCTS_SINGLE, cartProductItem.getProduct_id());

        setProgressActive(true);

        GsonRequest<ProductResponse> getProductRequest = new GsonRequest<>(Request.Method.GET, url, null, ProductResponse.class,
                new Response.Listener<ProductResponse>() {
                    @Override
                    public void onResponse(@NonNull ProductResponse response) {
                        if(response != null) {
                            if(response.getStatusCode() != null && response.getStatusText() != null) {
                                if (response.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || response.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                    LoginDialogFragment.logoutUser(true);
                                    DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                    loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                }
                            }
                            else {
                                setProgressActive(false);
                                selectedProduct = response.getProduct();
                            }
                        }
                        else
                            Timber.d("Null response during getProductDetail....");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setProgressActive(false);
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        getProductRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getProductRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getProductRequest, CONST.UPDATE_CART_ITEM_REQUESTS_TAG);
    }


    private void upateTimeSpinner(Product product) {
        //**************************************
        // Set Time Spinner
        //**************************************
        List<ProductTime> productTimes = new ArrayList<>();

        for (ProductVariant pv : product.getVariants()) {
            if(pv.getName().toLowerCase().equals(CONST.OPTION_NAME_TIME)) {

                for(ProductVariantValues pvv : pv.getProductVariantValues()) {
                    ProductTime pat = new ProductTime(pvv.getProduct_option_value_id());
                    pat.setName(pvv.getName());
                    pat.setProduct_option_value_id(pvv.getProduct_option_value_id());
                    pat.setQuantity(pvv.getQuantity());
                    pat.setImage(pvv.getImage());

                    if (!productTimes.contains(pat)) {
                        productTimes.add(pat);
                    }
                }
            }
        }

        if (productTimes.size() > 1) {
            timeSpinner.setVisibility(View.VISIBLE);

            ProductTime pt = new ProductTime();
            pt.setProduct_option_id(CONST.DEFAULT_EMPTY_ID);
            pt.setProduct_option_value_id(CONST.DEFAULT_EMPTY_ID);
            pt.setName(getString(R.string.Select_time));

            productTimes.add(0, pt);

            CartTimeTextSpinnerAdapter timeSpinnerAdapter = new CartTimeTextSpinnerAdapter(getActivity());
            timeSpinnerAdapter.setProductTimeList(productTimes);
            timeSpinner.setAdapter(timeSpinnerAdapter);
            timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ProductTime productTime = (ProductTime) parent.getItemAtPosition(position);
                    if (productTime != null) {
                        selectedProductTime = productTime;
                        Timber.d("TimePicker selected time: %s", productTime.toString());
                    } else {
                        selectedProductTime = null;
                        Timber.e("Retrieved null time from spinner.");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedProductTime = null;
                    Timber.d("Nothing selected in product time spinner.");
                }
            });
        } else {
            timeSpinner.setVisibility(View.GONE);
            selectedProductTime = null;
            Timber.e("Setting timers spinners for null product variants.");
                /*for (ProductVariant pv : product.getVariants()) {
                    if (pv.getName().toLowerCase().equals(CONST.OPTION_NAME_TIME)) {
                        timeSpinner.setVisibility(View.GONE);
                        updateDateAndTimeSpinner(productTimes.get(0));
                    }
                }*/
        }
    }

    private void updateProductInCart(long productCartId, long newVariantId, int newQuantity) {
        User user = SettingsMy.getActiveUser();
        long timeID = 0;
        long dateID = 0;

        if (user != null) {
            // get selected radio button from radioGroup
            JSONObject jo = new JSONObject();

            try {
                JSONObject optionSize = new JSONObject();

                /*for (ProductVariant pv : selectedProduct.getVariants()) {
                    if(pv.getName().toLowerCase().equals(CONST.OPTION_NAME_TIME)) {
                        timeID = pv.getProductOptionId();
                    }

                    if(pv.getName().toLowerCase().equals(CONST.OPTION_NAME_DATE)) {
                        dateID = pv.getProductOptionId();
                    }
                }

                optionSize.put(String.valueOf(timeID), selectedProductTime.getProduct_option_value_id());
                String selectedDate = bookingDateText.getText().toString();
                if(selectedDate != "") {
                    optionSize.put(String.valueOf(dateID), selectedDate);
                }
                else {
                    //Error Message TODO Mahesh
                }*/

                jo.put(JsonUtils.TAG_KEY, productCartId + "::");
                //jo.put(JsonUtils.TAG_OPTION, optionSize);
                jo.put(JsonUtils.TAG_QUANTITY, newQuantity);

                if (BuildConfig.DEBUG) Timber.d("json input: %s", jo.toString());

            } catch (JSONException e) {
                Timber.e(e, "Create json add product to cart exception");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }

            /*JSONObject jo = new JSONObject();
            try {
                jo.put(JsonUtils.TAG_KEY, productCartId + "::");
                jo.put(JsonUtils.TAG_QUANTITY, newQuantity);
            } catch (JSONException e) {
                Timber.e(e, "Create update object exception");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }*/

            Timber.d("update product: %s", jo.toString());

            String url = String.format(EndPoints.CART_ITEM);

            setProgressActive(true);
            JsonRequest req = new JsonRequest(Request.Method.PUT, url, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(response!= null) {
                        if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                            LoginDialogFragment.logoutUser(true);
                            DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                            loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                        }
                        else {
                            Timber.d("Update item in cart: %s", response.toString());
                            if (requestListener != null) requestListener.requestSuccess(0);
                            setProgressActive(false);
                            dismiss();
                        }
                    }
                    else
                        Timber.d("Null response during updateProductInCart....");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setProgressActive(false);
                    if (requestListener != null) requestListener.requestFailed(error);
                    dismiss();
                }
            }, getFragmentManager(), "");
            req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            req.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(req, CONST.UPDATE_CART_ITEM_REQUESTS_TAG);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
        }
    }


    private void setProgressActive(boolean active) {
        if (active) {
            dialogProgress.setVisibility(View.VISIBLE);
            dialogContent.setVisibility(View.INVISIBLE);
        } else {
            dialogProgress.setVisibility(View.GONE);
            dialogContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        MyApplication.getInstance().getRequestQueue().cancelAll(CONST.UPDATE_CART_ITEM_REQUESTS_TAG);
        super.onStop();
    }
}
