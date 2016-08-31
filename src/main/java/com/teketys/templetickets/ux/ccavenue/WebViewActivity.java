package com.teketys.templetickets.ux.ccavenue;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.teketys.templetickets.CONST;
import com.teketys.templetickets.utils.ccavenue.AvenuesParams;
import com.teketys.templetickets.utils.ccavenue.Constants;
import com.teketys.templetickets.utils.ccavenue.PaymentArgs;
import com.teketys.templetickets.utils.ccavenue.RSAUtility;
import com.teketys.templetickets.utils.ccavenue.ServiceHandler;
import com.teketys.templetickets.utils.ccavenue.ServiceUtility;
import com.teketys.templetickets.R;
import com.teketys.templetickets.ux.dialogs.OrderCreateSuccessDialogFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class WebViewActivity extends AppCompatActivity {
	private ProgressDialog dialog;
	String html, encVal;

	FragmentManager fragmentManager = null;
	private String billing_name = null;
	private String billing_address = null;
	private String billing_city = null;
	private String billing_state = null;
	private String billing_tel = null;
	private String billing_zip = null;
	private String billing_email = null;
	private String billing_amount = null;
	private String order_id = null;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_webview);
		//amount = getIntent().getStringExtra("amount");
		Intent intent = this.getIntent();
		FragmentManager fragmentManager = getFragmentManager();

		if(intent != null) {
			Bundle paymentBundle = intent.getExtras();
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

	/**
	 * Async task class to get json by making HTTP call
	 * */
	private class RenderView extends AsyncTask<PaymentArgs, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			dialog = new ProgressDialog(WebViewActivity.this);
			dialog.setMessage("Please wait...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(PaymentArgs... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();
			PaymentArgs pa = (PaymentArgs) arg0[0];

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
			    	}else if(html.indexOf("Aborted")!=-1){
			    		status = "Transaction Cancelled!";
			    	}else{
			    		status = "Status Not Known!";
			    	}
			    	//Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
			    	Intent intent = new Intent(getApplicationContext(),StatusActivity.class);
					intent.putExtra("transStatus", status);
					startActivity(intent);

					DialogFragment thankYouDF = OrderCreateSuccessDialogFragment.newInstance(true);
					thankYouDF.show(getSupportFragmentManager(), OrderCreateSuccessDialogFragment.class.getSimpleName());
			    }
			}

			final WebView webview = (WebView) findViewById(R.id.webview);
			webview.getSettings().setJavaScriptEnabled(true);
			webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
			webview.setWebViewClient(new WebViewClient(){
				@Override
	    	    public void onPageFinished(WebView view, String url) {
	    	        super.onPageFinished(webview, url);
					// Dismiss the progress dialog
					if (dialog.isShowing())
						dialog.dismiss();
	    	        if(url.indexOf("/ccavResponseHandler.php")!=-1){
	    	        	webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
	    	        }
	    	    }

	    	    @Override
	    	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	    	        Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
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
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}
} 