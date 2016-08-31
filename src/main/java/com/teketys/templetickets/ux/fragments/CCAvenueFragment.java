package com.teketys.templetickets.ux.fragments;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.teketys.templetickets.CONST;
import com.teketys.templetickets.R;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.utils.ccavenue.AvenuesParams;
import com.teketys.templetickets.utils.ccavenue.Constants;
import com.teketys.templetickets.utils.ccavenue.PaymentArgs;
import com.teketys.templetickets.utils.ccavenue.RSAUtility;
import com.teketys.templetickets.utils.ccavenue.ServiceHandler;
import com.teketys.templetickets.utils.ccavenue.ServiceUtility;
import com.teketys.templetickets.ux.ccavenue.StatusActivity;
import com.teketys.templetickets.ux.dialogs.OrderCreateSuccessDialogFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class CCAvenueFragment extends Fragment {

    private ProgressDialog dialog;
    String html, encVal;

    private String billing_name = null;
    private String billing_address = null;
    private String billing_city = null;
    private String billing_state = null;
    private String billing_tel = null;
    private String billing_zip = null;
    private String billing_email = null;
    private String billing_amount = null;

    private String order_id = null;
    private WebView webview;

    public static class PaymentParams {
        private String billing_name;
        private String billing_address;
        private String billing_city;
        private String billing_state;
        private String billing_tel;
        private String billing_zip;
        private String billing_email;
        private String billing_amount;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //setContentView(R.layout.activity_webview);
        //amount = getIntent().getStringExtra("amount");

        // Calling async task to get display content

        if(getArguments() != null) {
            Bundle paymentBundle = getArguments();
            PaymentArgs paymentArgs = (PaymentArgs) paymentBundle.getSerializable(CONST.ORDERS_CREATE_FRAGMENT_TAG);

            this.billing_name = paymentArgs.getBillingName();
            this.billing_address = paymentArgs.getBillingAddress();
            this.billing_city = paymentArgs.getBillingCity();
            this.billing_state = paymentArgs.getBillingRegion();
            this.billing_tel = paymentArgs.getBillingPhone();
            this.billing_zip = paymentArgs.getBillingZip();
            this.billing_email = paymentArgs.getBillingEmail();
            //TODO get actual amount Mahesh
            //this.billing_amount = paymentArgs.getBillingAmount();
            this.order_id = paymentArgs.getBillingOrderId();

            new RenderView().execute(paymentArgs);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        /*if(getArguments() != null) {

            Bundle paymentBundle = getArguments();
            PaymentArgs paymentArgs = (PaymentArgs) paymentBundle.getSerializable(CONST.ORDERS_CREATE_FRAGMENT_TAG);

            this.billing_name = paymentArgs.getBillingName();
            this.billing_address = paymentArgs.getBillingAddress();
            this.billing_city = paymentArgs.getBillingCity();
            this.billing_state = paymentArgs.getBillingRegion();
            this.billing_tel = paymentArgs.getBillingPhone();
            this.billing_zip = paymentArgs.getBillingZip();
            this.billing_email = paymentArgs.getBillingEmail();
            //TODO get actual amount Mahesh
            //this.billing_amount = paymentArgs.getBillingAmount();
            this.billing_amount = "1";
            this.order_id = paymentArgs.getBillingOrderId();

        }*/

        View view = inflater.inflate(R.layout.activity_webview, container, false);
        webview = (WebView) view.findViewById(R.id.webview);

        return view;
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class RenderView extends AsyncTask<PaymentArgs, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

            dialog = Utils.generateProgressDialog(getActivity(), false);

            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(PaymentArgs... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            PaymentArgs pa = (PaymentArgs) arg0[0];

            //Integer randomNum = ServiceUtility.randInt(0, 999);
            //order_id = randomNum.toString();

            String result = pa.getBillingOrderId();

            Timber.d("response: payment params get %s", pa.getBillingOrderId() + " "+ pa.getBillingAddress() + " "+ pa.getBillingCity() + " "+ pa.getBillingPhone());

            // Making a request to url and getting response
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(AvenuesParams.ACCESS_CODE, CONST.ACCESS_CODE));
            params.add(new BasicNameValuePair(AvenuesParams.ORDER_ID, pa.getBillingOrderId()));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_COUNTRY, CONST.BILLING_COUNTRY));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_NAME, pa.getBillingName()));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_ADDRESS, pa.getBillingAddress()));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_CITY, pa.getBillingCity()));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_STATE, pa.getBillingRegion()));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_TEL, pa.getBillingPhone()));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_ZIP, pa.getBillingZip()));
            params.add(new BasicNameValuePair(AvenuesParams.BILLING_EMAIL, pa.getBillingEmail()));

            String vResponse = sh.makeServiceCall(CONST.RSA_URL, ServiceHandler.POST, params);
            System.out.println(vResponse);
            if(!ServiceUtility.chkNull(vResponse).equals("")
                    && ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR")==-1){
                StringBuffer vEncVal = new StringBuffer("");
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, "1"));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, CONST.CURRENCY));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_COUNTRY, CONST.BILLING_COUNTRY));

                encVal = RSAUtility.encrypt(vEncVal.substring(0,vEncVal.length()-1), vResponse);
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            class MyJavaScriptInterface
            {
                @JavascriptInterface
                public void processHTML(String html)
                {
                    // process the html as needed by the app
                    String status = null;
                    if(html.indexOf("Failure")!=-1){
                        status = "Transaction Declined!";
                    }else if(html.indexOf("Success")!=-1){
                        status = "Transaction Successful!";
                        DialogFragment thankYouDF = OrderCreateSuccessDialogFragment.newInstance(true);
                        thankYouDF.show(getFragmentManager(), OrderCreateSuccessDialogFragment.class.getSimpleName());
                    }else if(html.indexOf("Aborted")!=-1){
                        status = "Transaction Cancelled!";
                    }else{
                        status = "Status Not Known!";
                    }
                    //Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(),StatusActivity.class);
                    intent.putExtra("transStatus", status);
                    startActivity(intent);
                }
            }

            webview.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(webview, url);
                    // Dismiss the progress dialog
                    if (dialog.isShowing())
                        dialog.dismiss();
                    if(url.indexOf("/ccavResponseHandler.php")!=-1){
                        webview.getSettings().setJavaScriptEnabled(true);
                        webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
                        webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(getContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                }
            });

			/* An instance of this class will be registered as a JavaScript interface */
            StringBuffer params = new StringBuffer();
            params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE,CONST.ACCESS_CODE));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID,CONST.MERCHANT_CODE));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_ID,result));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL,CONST.REDIRECT_URL));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL,CONST.CANCEL_URL));
            params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL, URLEncoder.encode(encVal)));

            String vPostParams = params.substring(0,params.length()-1);
            try {
                webview.postUrl(Constants.TRANS_URL, EncodingUtils.getBytes(vPostParams, "UTF-8"));
            } catch (Exception e) {
                showToast("Exception occured while opening webview.");
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getContext(), "Toast: " + msg, Toast.LENGTH_LONG).show();
    }}