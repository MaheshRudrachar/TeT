package com.teketys.templetickets.ux.fragments;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import com.teketys.templetickets.CONST;
import com.teketys.templetickets.MyApplication;
import com.teketys.templetickets.R;
import com.teketys.templetickets.SettingsMy;
import com.teketys.templetickets.api.EndPoints;
import com.teketys.templetickets.api.GsonRequest;
import com.teketys.templetickets.entities.Shop;
import com.teketys.templetickets.entities.ShopMetaData;
import com.teketys.templetickets.entities.ShopMetaDataResponse;
import com.teketys.templetickets.entities.ShopResponse;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.ux.MainActivity;
import com.teketys.templetickets.ux.adapters.ShopSpinnerAdapter;
import com.teketys.templetickets.ux.dialogs.LicensesDialogFragment;
import com.teketys.templetickets.ux.dialogs.LoginDialogFragment;
import com.teketys.templetickets.ux.dialogs.LoginExpiredDialogFragment;
import com.teketys.templetickets.ux.dialogs.RestartDialogFragment;
import timber.log.Timber;

/**
 * Fragment shows app settings and information about used open source libraries.
 * Important is possibility of changing selected shop (if more shops exist).
 */
public class SettingsFragment extends Fragment {

    private ProgressDialog progressDialog;

    /**
     * Spinner offering all available shops.
     */
    private Spinner spinShopSelection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        MainActivity.setActionBarTitle(getString(R.string.Settings));

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        spinShopSelection = (Spinner) view.findViewById(R.id.settings_shop_selection_spinner);

        LinearLayout licensesLayout = (LinearLayout) view.findViewById(R.id.settings_licenses_layout);
        licensesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LicensesDialogFragment df = new LicensesDialogFragment();
                df.show(getFragmentManager(), LicensesDialogFragment.class.getSimpleName());
            }
        });

        requestShops();
        return view;
    }

    /**
     * Load available shops from server.
     */
    private void requestShops() {
        if (progressDialog != null) progressDialog.show();
        GsonRequest<ShopMetaDataResponse> getShopsRequest = new GsonRequest<>(Request.Method.GET, EndPoints.SHOPS, null, ShopMetaDataResponse.class,
                new Response.Listener<ShopMetaDataResponse>() {
                    @Override
                    public void onResponse(@NonNull ShopMetaDataResponse response) {
                        if(response != null) {
                            if(response.getStatusCode() != null && response.getStatusText() != null) {
                                if (response.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || response.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                    LoginDialogFragment.logoutUser(true);
                                    DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                    loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            }
                            else {
                                Timber.d("Available shops response: %s", response.toString());
                                setSpinShops(response.getShopMetaDataList());
                            }
                        }
                        else
                            Timber.d("Null response during requestShops....");

                        if (progressDialog != null) progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        getShopsRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getShopsRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getShopsRequest, CONST.SETTINGS_REQUESTS_TAG);
    }

    /**
     * Prepare spinner with shops and pre-select already selected one.
     *
     * @param shopMetaDataList list of shops received from server.
     */
    private void setSpinShops(List<ShopMetaData> shopMetaDataList) {
        //
        //Start Request specific shop details

        final List<Shop> shopList = new ArrayList<>();

        for(ShopMetaData sMetaData : shopMetaDataList) {

            String url = String.format(EndPoints.SHOPS_SINGLE, sMetaData.getId());
            GsonRequest<ShopResponse> getShopsRequest = new GsonRequest<>(Request.Method.GET, url, null, ShopResponse.class,
                    new Response.Listener<ShopResponse>() {
                        @Override
                        public void onResponse(@NonNull ShopResponse response) {
                            if(response != null) {
                                if(response.getStatusCode() != null && response.getStatusText() != null) {
                                    if (response.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || response.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                        LoginDialogFragment.logoutUser(true);
                                        DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                        loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                        if (progressDialog != null) progressDialog.cancel();
                                    }
                                }
                                else {
                                    Timber.d("Get shops response: %s", response.toString());
                                    shopList.add(response.getShop());
                                    setSpinShopDetails(shopList);
                                }
                            }
                            else
                                Timber.d("Null response during setSpinShops....");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    MsgUtils.logErrorMessage(error);
                }
            });

            getShopsRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            getShopsRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(getShopsRequest, CONST.SHOP_REQUESTS_TAG);
        }

        //End Request Specific shop details
        //
    }

    private void setSpinShopDetails(List<Shop> shops) {
        ShopSpinnerAdapter adapterLanguage = new ShopSpinnerAdapter(getActivity(), shops, false);
        spinShopSelection.setAdapter(adapterLanguage);

        int position = 0;
        for (int i = 0; i < shops.size(); i++) {
            if (shops.get(i).getId() == SettingsMy.getActualNonNullShop(getActivity()).getId()) {
                position = i;
                break;
            }
        }
        spinShopSelection.setSelection(position);
        spinShopSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Shop selectedShop = (Shop) parent.getItemAtPosition(position);
                if (selectedShop != null && selectedShop.getId() != SettingsMy.getActualNonNullShop(getActivity()).getId()) {
                    RestartDialogFragment rdf = RestartDialogFragment.newInstance(selectedShop);
                    rdf.show(getFragmentManager(), RestartDialogFragment.class.getSimpleName());
                } else {
                    Timber.e("Selected null or same shop.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Timber.d("Nothing selected");
            }
        });
    }

    @Override
    public void onStop() {
        MyApplication.getInstance().cancelPendingRequests(CONST.SETTINGS_REQUESTS_TAG);
        if (progressDialog != null) progressDialog.cancel();
        super.onStop();
    }
}
