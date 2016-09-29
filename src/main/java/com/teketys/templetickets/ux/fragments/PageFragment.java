package com.teketys.templetickets.ux.fragments;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.teketys.templetickets.entities.Page;
import com.teketys.templetickets.entities.PageResponse;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.ux.MainActivity;
import com.teketys.templetickets.ux.dialogs.LoginDialogFragment;
import com.teketys.templetickets.ux.dialogs.LoginExpiredDialogFragment;

import java.util.regex.Matcher;

import timber.log.Timber;

/**
 * Fragment allow displaying useful information content like web page.
 * Requires input argument - id of selected page. Pages are created in TempleTickets server administration.
 */
public class PageFragment extends Fragment {

    /**
     * Name for input argument.
     */
    private static final String PAGE_ID = "page_id";
    private static final String PAGE_URL = "url";

    //private static final long TERMS_AND_CONDITIONS = -131;
    private static final long TERMS_AND_CONDITIONS = -131;

    private ProgressDialog progressDialog;

    /**
     * Reference of empty layout
     */
    private View layoutEmpty;
    /**
     * Reference of content layout
     */
    private View layoutContent;

    // Content view elements
    private TextView pageTitle;
    private WebView pageContent;

    /**
     * Create fragment instance which allow displaying useful information content like web page.
     *
     * @param pageId id of page for download and display.
     * @return new fragment instance.
     */
    public static PageFragment newInstance(long pageId) {
        Bundle args = new Bundle();
        args.putLong(PageFragment.PAGE_ID, pageId);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static PageFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(PageFragment.PAGE_URL, url);
        args.putLong(PageFragment.PAGE_ID, 0);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create fragment instance which displays Terms and Conditions defined on server.
     *
     * @return fragment instance for display.
     */
    public static PageFragment newInstance() {
        Bundle args = new Bundle();
        args.putLong(PageFragment.PAGE_ID, TERMS_AND_CONDITIONS);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        layoutEmpty = view.findViewById(R.id.page_empty);
        layoutContent = view.findViewById(R.id.page_content_layout);

        pageTitle = (TextView) view.findViewById(R.id.page_title);
        pageContent = (WebView) view.findViewById(R.id.page_content);

        // Check if fragment received some arguments.
        if (getArguments() != null && getArguments().getLong(PAGE_ID) != 0L) {
            MainActivity.setActionBarTitle(getString(R.string.app_name));
            getPage(getArguments().getLong(PAGE_ID));
        } else if(getArguments() != null && getArguments().getString(PAGE_URL) != null && getArguments().getString(PAGE_URL) != "") {
            MainActivity.setActionBarTitle(getString(R.string.slokas_and_mantras));
            handleMantrasAndSlokasPage(getArguments().getString(PAGE_URL));
        }
        else {
            Timber.e(new RuntimeException(), "Created fragment with null arguments.");
            setContentVisible(false);
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "", MsgUtils.ToastLength.LONG);
        }
        return view;
    }

    /**
     * Load page content by pageID.
     *
     * @param pageId define page to load.
     */
    private void getPage(long pageId) {
        String url;
        if (pageId == TERMS_AND_CONDITIONS) {
            url = String.format(EndPoints.PAGES_TERMS_AND_COND, SettingsMy.getActualNonNullShop(getActivity()).getId());
        } else {
            url = String.format(EndPoints.PAGES_SINGLE, pageId);
        }

        progressDialog.show();

        GsonRequest<PageResponse> getPage = new GsonRequest<>(Request.Method.GET, url, null, PageResponse.class,
                new Response.Listener<PageResponse>() {
                    @Override
                    public void onResponse(@NonNull PageResponse response) {
                        if(response != null) {
                            if(response.getStatusCode() != null && response.getStatusText() != null) {
                                if (response.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || response.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                    LoginDialogFragment.logoutUser(true);
                                    DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                    loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            }
                            else
                                handleResponse(response.getPage());
                        }
                        else
                            Timber.d("Null response during getPage....");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                setContentVisible(false);
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        getPage.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getPage.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getPage, CONST.PAGE_REQUESTS_TAG);
    }

    /**
     * Method hides progress dialog and show received content.
     *
     * @param page page data received from server.
     */
    private void handleResponse(Page page) {
        if (page != null && page.getDescription() != null && !page.getDescription().isEmpty()) {
            setContentVisible(true);
            pageTitle.setText(stripHtml(page.getTitle()));
            String data = page.getDescription(); //.replaceAll("\"", Matcher.quoteReplacement("\\\""));
            String header = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">"
                    + "<html>  <head>  <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"
                    + "</head>  <body>";
            String footer = "</body></html>";

            Timber.d("response of data*** %s", data);
            pageContent.loadData(header + stripHtml(data) + footer, "text/html; charset=UTF-8", null);
            //pageContent.loadDataWithBaseURL(null, header + data + footer, "text/html", "UTF-8", null);
        } else {
            setContentVisible(false);
        }
        // Slow disappearing of progressDialog due to slow page content processing.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) progressDialog.cancel();
            }
        }, 200);
    }

    private void handleMantrasAndSlokasPage(String url) {
        if(url != null) {
            setContentVisible(true);

            WebSettings webSetting = pageContent.getSettings();
            webSetting.setBuiltInZoomControls(true);
            webSetting.setJavaScriptEnabled(true);

            pageTitle.setText(stripHtml(CONST.SLOKAS_AND_MANTRAS_TAG));
            pageContent.setWebViewClient(new WebViewClient());
            pageContent.loadUrl(url);
        }
        else {
            setContentVisible(false);
        }

        // Slow disappearing of progressDialog due to slow page content processing.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) progressDialog.cancel();
            }
        }, 200);
    }

    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    /**
     * Display content layout or empty layout.
     *
     * @param visible true for visible content.
     */
    private void setContentVisible(boolean visible) {
        if (layoutEmpty != null && layoutContent != null) {
            if (visible) {
                layoutEmpty.setVisibility(View.GONE);
                layoutContent.setVisibility(View.VISIBLE);
            } else {
                layoutEmpty.setVisibility(View.VISIBLE);
                layoutContent.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onStop() {
        MyApplication.getInstance().cancelPendingRequests(CONST.PAGE_REQUESTS_TAG);
        if (progressDialog != null) progressDialog.cancel();
        super.onStop();
    }
}
