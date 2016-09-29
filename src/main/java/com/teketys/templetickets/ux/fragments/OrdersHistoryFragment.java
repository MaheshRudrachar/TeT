package com.teketys.templetickets.ux.fragments;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.teketys.templetickets.entities.Metadata;
import com.teketys.templetickets.entities.User;
import com.teketys.templetickets.entities.order.CustomerOrder;
import com.teketys.templetickets.entities.order.CustomerOrderRespose;
import com.teketys.templetickets.entities.order.Order;
import com.teketys.templetickets.entities.order.OrderResponse;
import com.teketys.templetickets.interfaces.OrdersRecyclerInterface;
import com.teketys.templetickets.utils.EndlessRecyclerScrollListener;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.utils.RecyclerMarginDecorator;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.ux.MainActivity;
import com.teketys.templetickets.ux.adapters.OrdersHistoryRecyclerAdapter;
import com.teketys.templetickets.ux.dialogs.LoginDialogFragment;
import com.teketys.templetickets.ux.dialogs.LoginExpiredDialogFragment;

import org.json.JSONObject;

import timber.log.Timber;

/**
 * Fragment shows the user's order history.
 */
public class OrdersHistoryFragment extends Fragment {

    private ProgressDialog progressDialog;

    // Fields referencing complex screen layouts.
    private View empty;
    private View content;

    /**
     * Request metadata containing urls for endlessScroll.
     */
    private Metadata ordersMetadata;

    private OrdersHistoryRecyclerAdapter ordersHistoryRecyclerAdapter;
    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;

    /**
     * Field for recovering scroll position.
     */
    private RecyclerView ordersRecycler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Order_history));

        View view = inflater.inflate(R.layout.fragment_orders_history, container, false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        empty = view.findViewById(R.id.order_history_empty);
        content = view.findViewById(R.id.order_history_content);

        prepareOrdersHistoryRecycler(view);

        loadOrders(null);
        return view;
    }

    public static OrdersHistoryFragment newInstance(String url) {
        Bundle args = new Bundle();
        OrdersHistoryFragment fragment = new OrdersHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Prepare content recycler. Create custom adapter and endless scroll.
     *
     * @param view root fragment view.
     */
    private void prepareOrdersHistoryRecycler(View view) {
        ordersRecycler = (RecyclerView) view.findViewById(R.id.orders_history_recycler);
        ordersHistoryRecyclerAdapter = new OrdersHistoryRecyclerAdapter(new OrdersRecyclerInterface() {
            @Override
            public void onOrderSelected(View v, CustomerOrder order) {
                Activity activity = getActivity();
                if (activity instanceof MainActivity) ((MainActivity) activity).onOrderSelected(order);
            }
        });
        ordersRecycler.setAdapter(ordersHistoryRecyclerAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ordersRecycler.getContext());
        ordersRecycler.setLayoutManager(layoutManager);
        ordersRecycler.setItemAnimator(new DefaultItemAnimator());
        ordersRecycler.setHasFixedSize(true);
        ordersRecycler.addItemDecoration(new RecyclerMarginDecorator(getResources().getDimensionPixelSize(R.dimen.base_margin)));

        endlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                /*if (ordersMetadata != null && ordersMetadata.getLinks() != null && ordersMetadata.getLinks().getNext() != null) {
                    loadOrders(ordersMetadata.getLinks().getNext());
                } else {*/
                    Timber.d("CustomLoadMoreDataFromApi NO MORE DATA");
                //}
            }
        };
        ordersRecycler.addOnScrollListener(endlessRecyclerScrollListener);
    }

    /**
     * Endless content loader. Should be used after views inflated.
     *
     * @param url null for fresh load. Otherwise use URLs from response metadata.
     */
    public void loadOrders(String url) {
        User user = SettingsMy.getActiveUser();
        if (user != null) {
            progressDialog.show();
            if (url == null) {
                ordersHistoryRecyclerAdapter.clear();
                url = String.format(EndPoints.ORDERS);
            }

           GsonRequest<CustomerOrderRespose> userOrderRequest = new GsonRequest<>(Request.Method.GET, url, null, CustomerOrderRespose.class,
                    new Response.Listener<CustomerOrderRespose>() {
                        @Override
                        public void onResponse(@NonNull CustomerOrderRespose response) {
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
                                    if(response.getCustomerRecord() != null) {
                                        if (response.getCustomerRecord().getCustomerOrders() != null && response.getCustomerRecord().getCustomerOrders().size() > 0) {
                                            Timber.d("response %s", response.getCustomerRecord().getCustomerOrders().get(0).toString());

                                            ordersHistoryRecyclerAdapter.addOrders(response.getCustomerRecord().getCustomerOrders());

                                            if (ordersHistoryRecyclerAdapter.getItemCount() > 0) {
                                                empty.setVisibility(View.GONE);
                                                content.setVisibility(View.VISIBLE);
                                            } else {
                                                empty.setVisibility(View.VISIBLE);
                                                content.setVisibility(View.GONE);
                                            }
                                            if (progressDialog != null) progressDialog.cancel();
                                        } else {
                                            Timber.d("No orders exists....");
                                            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.No_orders_found), MsgUtils.ToastLength.SHORT);
                                            if (progressDialog != null) progressDialog.cancel();
                                        }
                                    }
                                    else {
                                        Timber.d("No orders exists....");
                                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.No_orders_found), MsgUtils.ToastLength.SHORT);
                                        if (progressDialog != null) progressDialog.cancel();
                                    }
                                }
                            }
                            else
                                Timber.d("Null response during loadOrders....");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null) progressDialog.cancel();
                    MsgUtils.logAndShowErrorMessage(null, error);
                }
            });
            userOrderRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            userOrderRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(userOrderRequest, CONST.ORDERS_HISTORY_REQUESTS_TAG);

        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
        }
    }

    @Override
    public void onStop() {
        if (progressDialog != null) {
            // Hide progress dialog if exist.
            if (progressDialog.isShowing() && endlessRecyclerScrollListener != null) {
                // Fragment stopped during loading data. Allow new loading on return.
                endlessRecyclerScrollListener.resetLoading();
            }
            progressDialog.cancel();
        }
        MyApplication.getInstance().cancelPendingRequests(CONST.ORDERS_HISTORY_REQUESTS_TAG);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (ordersRecycler != null) ordersRecycler.clearOnScrollListeners();
        super.onDestroyView();
    }
}
