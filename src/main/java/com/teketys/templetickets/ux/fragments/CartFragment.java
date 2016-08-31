package com.teketys.templetickets.ux.fragments;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.teketys.templetickets.entities.cart.Cart;
import com.teketys.templetickets.entities.cart.CartDiscountItem;
import com.teketys.templetickets.entities.cart.CartProductItem;
import com.teketys.templetickets.entities.cart.CartResponse;
import com.teketys.templetickets.interfaces.CartRecyclerInterface;
import com.teketys.templetickets.interfaces.RequestListener;
import com.teketys.templetickets.listeners.OnSingleClickListener;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.utils.RecyclerDividerDecorator;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.ux.MainActivity;
import com.teketys.templetickets.ux.adapters.CartRecyclerAdapter;
import com.teketys.templetickets.ux.dialogs.DiscountDialogFragment;
import com.teketys.templetickets.ux.dialogs.LoginExpiredDialogFragment;
import com.teketys.templetickets.ux.dialogs.UpdateCartItemDialogFragment;
import timber.log.Timber;

/**
 * Fragment handles shopping cart.
 */
public class CartFragment extends Fragment {

    private ProgressDialog progressDialog;

    private View emptyCart;
    private View cartFooter;

    private RecyclerView cartRecycler;
    private CartRecyclerAdapter cartRecyclerAdapter;

    // Footer views and variables
    private TextView cartItemCountTv;
    private TextView cartTotalPriceTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Shopping_cart));

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);
        prepareCartRecycler(view);

        emptyCart = view.findViewById(R.id.cart_empty);
        View emptyCartAction = view.findViewById(R.id.cart_empty_action);
        emptyCartAction.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                // Just open drawer menu.
                Activity activity = getActivity();
                if (activity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) activity;
                    if (mainActivity.drawerFragment != null)
                        mainActivity.drawerFragment.toggleDrawerMenu();
                }
            }
        });

        cartFooter = view.findViewById(R.id.cart_footer);
        cartItemCountTv = (TextView) view.findViewById(R.id.cart_footer_quantity);
        cartTotalPriceTv = (TextView) view.findViewById(R.id.cart_footer_price);
        view.findViewById(R.id.cart_footer_action).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                DiscountDialogFragment discountDialog = DiscountDialogFragment.newInstance(new RequestListener() {
                    @Override
                    public void requestSuccess(long newId) {
                        getCartContent();
                    }

                    @Override
                    public void requestFailed(VolleyError error) {
                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    }
                });

                if (discountDialog != null) {
                    discountDialog.show(getFragmentManager(), DiscountDialogFragment.class.getSimpleName());
                }
            }
        });

        Button order = (Button) view.findViewById(R.id.cart_order);
        order.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onOrderCreateSelected();
                }
            }
        });

        getCartContent();
        return view;
    }

    private void getCartContent() {
        User user = SettingsMy.getActiveUser();
        if (user != null) {
            String url = String.format(EndPoints.CART);

            progressDialog.show();
            GsonRequest<CartResponse> getCart = new GsonRequest<>(Request.Method.GET, url, null, CartResponse.class,
                    new Response.Listener<CartResponse>() {
                        @Override
                        public void onResponse(@NonNull CartResponse cart) {
                            if (progressDialog != null) progressDialog.cancel();

                            MainActivity.updateCartCountNotification();
                            if (cart.getCart() == null || cart.getCart().getItems().size() == 0) {
                                setCartVisibility(false);
                            } else {
                                setCartVisibility(true);
                                cartRecyclerAdapter.refreshItems(cart.getCart());

                                cartItemCountTv.setText(getString(R.string.format_quantity, cart.getCart().getProductCount()));
                                cartTotalPriceTv.setText(cart.getCart().getTotalPriceFormatted());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null) progressDialog.cancel();
                    setCartVisibility(false);
                    Timber.e("Get request cart error: %s", error.getMessage());
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            }, getFragmentManager(), "");
            getCart.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            getCart.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(getCart, CONST.CART_REQUESTS_TAG);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
        }
    }


    private void setCartVisibility(boolean visible) {
        if (visible) {
            if (emptyCart != null) emptyCart.setVisibility(View.GONE);
            if (cartRecycler != null) cartRecycler.setVisibility(View.VISIBLE);
            if (cartFooter != null) cartFooter.setVisibility(View.VISIBLE);
        } else {
            if (cartRecyclerAdapter != null) cartRecyclerAdapter.cleatCart();
            if (emptyCart != null) emptyCart.setVisibility(View.VISIBLE);
            if (cartRecycler != null) cartRecycler.setVisibility(View.GONE);
            if (cartFooter != null) cartFooter.setVisibility(View.GONE);
        }
    }

    private void prepareCartRecycler(View view) {
        this.cartRecycler = (RecyclerView) view.findViewById(R.id.cart_recycler);
        cartRecycler.addItemDecoration(new RecyclerDividerDecorator(getActivity()));
        cartRecycler.setItemAnimator(new DefaultItemAnimator());
        cartRecycler.setHasFixedSize(true);
        cartRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartRecyclerAdapter = new CartRecyclerAdapter(getActivity(), new CartRecyclerInterface() {
            @Override
            public void onProductUpdate(CartProductItem cartProductItem) {
                UpdateCartItemDialogFragment updateDialog = UpdateCartItemDialogFragment.newInstance(cartProductItem, new RequestListener() {
                    @Override
                    public void requestSuccess(long newId) {
                        getCartContent();
                    }

                    @Override
                    public void requestFailed(VolleyError error) {
                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    }
                });

                if (updateDialog != null) {
                    updateDialog.show(getFragmentManager(), UpdateCartItemDialogFragment.class.getSimpleName());
                }
            }

            @Override
            public void onProductDelete(CartProductItem cartProductItem) {
                if (cartProductItem != null)
                    deleteItemFromCart(cartProductItem.getKey(), false);
                else
                    Timber.e("Trying delete null cart item.");
            }

            @Override
            public void onDiscountDelete(CartDiscountItem cartDiscountItem) {
                if (cartDiscountItem != null)
                    deleteItemFromCart(cartDiscountItem.getId(), true);
                else
                    Timber.e("Trying delete null cart discount.");
            }

            @Override
            public void onProductSelect(long productId) {
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).onProductSelected(productId);
            }

            private void deleteItemFromCart(long id, boolean isDiscount) {
                User user = SettingsMy.getActiveUser();
                if (user != null) {

                    JSONObject jo = new JSONObject();
                    try {
                        jo.put(CONST.PRODUCT_KEY_ID, String.valueOf(id) + "::");
                        Timber.d("Json input for delete cart item %s", jo.toString());
                    } catch (JSONException e) {
                        Timber.e(e, "Parse new user registration exception");
                        return;
                    }

                    progressDialog.show();
                    JsonRequest req = new JsonRequest(Request.Method.DELETE, EndPoints.CART_DELETE, jo, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Timber.d("Delete item from cart: %s", response.toString());
                            try {

                                if(response != null) {
                                    if (Boolean.valueOf(response.getString("success"))) {
                                        getCartContent();
                                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE,
                                                getString(R.string.The_item_has_been_successfully_removed), MsgUtils.ToastLength.LONG);
                                    } else {
                                        Timber.d("JSON Response returned %s", response.toString());
                                    }
                                }
                            } catch (JSONException e) {
                                Timber.e(e, "Parse new user registration exception");
                                return;
                            }

                            if (progressDialog != null) progressDialog.cancel();
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
                    MyApplication.getInstance().addToRequestQueue(req, CONST.CART_REQUESTS_TAG);
                } else {
                    LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                    loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
                }
            }
        });
        cartRecycler.setAdapter(cartRecyclerAdapter);
    }

    @Override
    public void onStop() {
        MyApplication.getInstance().cancelPendingRequests(CONST.CART_REQUESTS_TAG);
        if (progressDialog != null) progressDialog.cancel();
        super.onStop();
    }
}
