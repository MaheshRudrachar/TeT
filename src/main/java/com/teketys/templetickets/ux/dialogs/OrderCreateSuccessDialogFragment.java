package com.teketys.templetickets.ux.dialogs;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.teketys.templetickets.CONST;
import com.teketys.templetickets.MyApplication;
import com.teketys.templetickets.R;
import com.teketys.templetickets.api.EndPoints;
import com.teketys.templetickets.api.GsonRequest;
import com.teketys.templetickets.api.JsonRequest;
import com.teketys.templetickets.utils.JsonUtils;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.ux.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;


/**
 * Dialog display "Thank you" screen after order is finished.
 */
public class OrderCreateSuccessDialogFragment extends DialogFragment {

    private boolean sampleApplication = false;

    /**
     * Dialog display "Thank you" screen after order is finished.
     */
    public static OrderCreateSuccessDialogFragment newInstance(boolean sampleApplication) {
        OrderCreateSuccessDialogFragment orderCreateSuccessDialogFragment = new OrderCreateSuccessDialogFragment();
        orderCreateSuccessDialogFragment.sampleApplication = sampleApplication;
        return orderCreateSuccessDialogFragment;
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
    public void onDestroyView() {
        super.onDestroyView();

        prepareConfirmPost();
    }

    //TODO Mahesh
    //Needs to be called on successful CCAvenue Transaction
    private void prepareConfirmPost() {
        //
        //Get Pay
        //

        JSONObject joConfirmReq = new JSONObject();
        try {
            joConfirmReq.put("", "");
        } catch (JSONException e) {
            Timber.e(e, "Parse resetPassword exception");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }

        String paymentPayUrl = String.format(EndPoints.PAYMENT_CONFIRM);

        JsonRequest req = new JsonRequest(Request.Method.POST, paymentPayUrl,
                joConfirmReq, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Timber.d("Payment Confirm Post info %s", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e("Payment Confirm post failed...");
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
                return;
            }
        });
        req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        req.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(req, CONST.ORDER_CREATE_REQUESTS_TAG);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.dialog_order_create_success, container, false);

        Button okBtn = (Button) view.findViewById(R.id.order_create_success_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).onDrawerBannersSelected();
                dismiss();
            }
        });

        TextView title = (TextView) view.findViewById(R.id.order_create_success_title);
        TextView description = (TextView) view.findViewById(R.id.order_create_success_description);

        if (sampleApplication) {
            title.setText(R.string.This_is_a_sample_app);
            description.setTextColor(ContextCompat.getColor(getContext(), R.color.textSecondary));
            description.setText(R.string.Sample_app_description);
        } else {
            title.setText(R.string.Thank_you_for_your_order);
            description.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            description.setText(Html.fromHtml(getString(R.string.Wait_for_sms_or_email_order_confirmation)));
        }

        return view;
    }
}