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
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
import com.teketys.templetickets.entities.product.ProductColor;
import com.teketys.templetickets.entities.product.ProductQuantity;
import com.teketys.templetickets.entities.product.ProductResponse;
import com.teketys.templetickets.entities.product.ProductSize;
import com.teketys.templetickets.entities.product.ProductVariant;
import com.teketys.templetickets.entities.product.ProductVariantValues;
import com.teketys.templetickets.interfaces.RequestListener;
import com.teketys.templetickets.utils.JsonUtils;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.ux.adapters.CartColorTextSpinnerAdapter;
import com.teketys.templetickets.ux.adapters.CartSizeSpinnerAdapter;
import com.teketys.templetickets.ux.adapters.QuantitySpinnerAdapter;
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
    private Spinner itemSizesSpinner;
    private Spinner quantitySpinner;

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
        View view = inflater.inflate(R.layout.dialog_update_cart_item, container, false);

        dialogProgress = view.findViewById(R.id.dialog_update_cart_item_progress);
        dialogContent = view.findViewById(R.id.dialog_update_cart_item_content);
        itemColorsSpinner = (Spinner) view.findViewById(R.id.dialog_update_cart_item_color_spin);
        itemSizesSpinner = (Spinner) view.findViewById(R.id.dialog_update_cart_item_size_spin);
        TextView itemName = (TextView) view.findViewById(R.id.dialog_update_cart_item_title);
        itemName.setText(cartProductItem.getName());

        View btnSave = view.findViewById(R.id.dialog_update_cart_item_save_btn);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantitySpinner != null && itemSizesSpinner != null) {
                    ProductSize productVariant = (ProductSize) itemSizesSpinner.getSelectedItem();
                    ProductQuantity productQuantity = (ProductQuantity) quantitySpinner.getSelectedItem();
                    Timber.d("Selected: %s. Quantity: %s", productVariant, productQuantity);
                    if (productVariant != null && productVariant.getName() != null && productQuantity != null) {
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

        getProductDetail(cartProductItem);
        return view;
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
                        setProgressActive(false);
                        setSpinners(response.getProduct());
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


    private void setSpinners(final Product product) {
        if (product != null && product.getVariants() != null && product.getVariants().size() > 0) {
            List<ProductColor> productColors = new ArrayList<>();

            for (ProductVariant pv : product.getVariants()) {
                if(pv.getName().toLowerCase().equals(CONST.OPTION_NAME_COLOR)) {
                    for(ProductVariantValues pvv : pv.getProductVariantValues()) {
                        ProductColor pac = new ProductColor(pvv.getProduct_option_value_id());
                        pac.setName(pvv.getName());
                        pac.setProduct_option_value_id(pvv.getProduct_option_value_id());
                        pac.setQuantity(pvv.getQuantity());
                        pac.setImage(pvv.getImage());

                        if (!productColors.contains(pac)) {
                            productColors.add(pac);
                        }
                    }
                }
            }

            if (productColors.size() > 1) {
                itemColorsSpinner.setVisibility(View.VISIBLE);
                CartColorTextSpinnerAdapter adapterColor = new CartColorTextSpinnerAdapter(getActivity(), productColors);
                itemColorsSpinner.setAdapter(adapterColor);
                //ProductColor actualItemColor = cartProductItem.getVariant().getName();

                if (productColors != null) {
                    int sizeSelection = 0;
                    for (int i = 0; i < productColors.size(); i++) {
//                    Timber.d("Compare list: " + variantSizeArrayList.get(i).getId() + " == " + cartProductItem.getVariant().getId() + " as actual");
                        for(CartProductItemVariant cpiv : cartProductItem.getVariant()) {
                            if(cpiv.getName().equals(CONST.OPTION_NAME_COLOR)) {
                                if (productColors.get(i).getName().toLowerCase().equals(cpiv.getValue())) {
                                    sizeSelection = i;
                                }
                            }
                        }

                    }
                    itemColorsSpinner.setSelection(sizeSelection);

                } else {
                    Timber.e("UpdateImagesAndSizeSpinner with null product.");
                }

                itemColorsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ProductColor productColor = (ProductColor) parent.getItemAtPosition(position);
                        if (productColor != null) {
                            Timber.d("ColorPicker selected color: %s", productColor.toString());
                            updateSizeSpinner(product, productColor);
                        } else {
                            Timber.e("Retrieved null color from spinner.");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Timber.d("Nothing selected in product colors spinner.");
                    }
                });
            } else {
                itemColorsSpinner.setVisibility(View.GONE);
                updateSizeSpinner(product, productColors.get(0));
            }

            int selectedPosition = cartProductItem.getQuantity() - 1;
            if (selectedPosition < 0) selectedPosition = 0;
            if (selectedPosition > (quantitySpinner.getCount() - 1))
                Timber.e(new RuntimeException(), "More item quantity that can be. Quantity: %d, max: %d", (selectedPosition + 1), quantitySpinner.getCount());
            else
                quantitySpinner.setSelection(selectedPosition);
        } else {
            Timber.e("Setting spinners for null product variants.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
        }
    }

    /**
     * Update size values in size adapter.
     *
     * @param product      updated product.
     * @param productColor actually selected color.
     */
    private void updateSizeSpinner(Product product, ProductColor productColor) {
        if (product != null) {
            ArrayList<ProductSize> variantSizeArrayList = new ArrayList<>();

            for (ProductVariant pv : product.getVariants()) {
                if (pv.getName().toLowerCase().equals(CONST.OPTION_NAME_SIZE)) {
                    for (ProductVariantValues sizePVV : pv.getProductVariantValues()) {
                        ProductSize ps = new ProductSize();
                        ps.setProduct_option_id(sizePVV.getProduct_option_id());
                        ps.setName(sizePVV.getName());
                        ps.setProduct_option_value_id(sizePVV.getProduct_option_value_id());
                        variantSizeArrayList.add(ps);
                    }
                }
            }

            // Show sizes
            CartSizeSpinnerAdapter adapterSize = new CartSizeSpinnerAdapter(getActivity(), variantSizeArrayList);
            itemSizesSpinner.setAdapter(adapterSize);
            // Select actual size
            if (!variantSizeArrayList.isEmpty()) {
                int sizeSelection = 0;
                for (int i = 0; i < variantSizeArrayList.size(); i++) {
//                    Timber.d("Compare list: " + variantSizeArrayList.get(i).getId() + " == " + cartProductItem.getVariant().getId() + " as actual");

                    for(CartProductItemVariant cpiv : cartProductItem.getVariant()) {
                        if(cpiv.getName().equals(CONST.OPTION_NAME_SIZE)) {
                            if (variantSizeArrayList.get(i).getName().toLowerCase().equals(cpiv.getValue())) {
                                sizeSelection = i;
                            }
                        }
                    }
                }
                itemSizesSpinner.setSelection(sizeSelection);
            }
        } else {
            Timber.e("UpdateImagesAndSizeSpinner with null product.");
        }
    }

    private void updateProductInCart(long productCartId, long newVariantId, int newQuantity) {
        User user = SettingsMy.getActiveUser();
        if (user != null) {
            JSONObject jo = new JSONObject();
            try {
                jo.put(JsonUtils.TAG_KEY, productCartId + "::");
                jo.put(JsonUtils.TAG_QUANTITY, newQuantity);
            } catch (JSONException e) {
                Timber.e(e, "Create update object exception");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }
            Timber.d("update product: %s", jo.toString());

            String url = String.format(EndPoints.CART_ITEM);

            setProgressActive(true);
            JsonRequest req = new JsonRequest(Request.Method.PUT, url, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Timber.d("Update item in cart: %s", response.toString());
                    if (requestListener != null) requestListener.requestSuccess(0);
                    setProgressActive(false);
                    dismiss();
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
