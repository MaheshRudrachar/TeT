package com.teketys.templetickets.ux.dialogs;
/**
 * Created by rudram1 on 8/25/16.
 */


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.style.TtsSpan;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
import com.teketys.templetickets.entities.UserAddressResponse;
import com.teketys.templetickets.entities.UserResponse;
import com.teketys.templetickets.interfaces.LoginDialogInterface;
import com.teketys.templetickets.listeners.OnSingleClickListener;
import com.teketys.templetickets.listeners.OnTouchPasswordListener;
import com.teketys.templetickets.utils.JsonUtils;
import com.teketys.templetickets.utils.MsgUtils;
import com.teketys.templetickets.utils.Utils;
import com.teketys.templetickets.ux.MainActivity;
import timber.log.Timber;

/**
 * Dialog handles user login, registration and forgotten password function.
 */
public class LoginDialogFragment extends DialogFragment implements FacebookCallback<LoginResult> {

    public static final String MSG_RESPONSE = "response: %s";
    private CallbackManager callbackManager;
    private LoginDialogInterface loginDialogInterface;
    private ProgressDialog progressDialog;
    private FormState actualFormState = FormState.BASE;
    private LinearLayout loginBaseForm;
    private LinearLayout loginRegistrationForm;
    private LinearLayout loginEmailForm;
    private LinearLayout loginEmailForgottenForm;

    private TextInputLayout fnameInputWrapper;
    private TextInputLayout lnameInputWrapper;
    private TextInputLayout addressInputWrapper;
    private TextInputLayout cityInputWrapper;
    private TextInputLayout zipInputWrapper;
    private TextInputLayout phoneInputWrapper;
    private TextInputLayout countryInputWrapper;
    private TextInputLayout stateInputWrapper;

    private TextInputLayout loginRegistrationEmailWrapper;
    private TextInputLayout loginRegistrationPasswordWrapper;
    private RadioButton loginRegistrationGenderWoman;

    private TextInputLayout loginEmailEmailWrapper;
    private TextInputLayout loginEmailPasswordWrapper;
    private TextInputLayout loginEmailForgottenEmailWrapper;
    private static final String ACCESS_TOKEN = "access_token";

    /**
     * Creates dialog which handles user login, registration and forgotten password function.
     *
     * @param loginDialogInterface listener receiving login/registration results.
     * @return new instance of dialog.
     */
    public static LoginDialogFragment newInstance(LoginDialogInterface loginDialogInterface) {
        LoginDialogFragment frag = new LoginDialogFragment();
        frag.loginDialogInterface = loginDialogInterface;
        return frag;
    }

    public static void logoutUser(boolean generateAPIKey) {
        LoginManager fbManager = LoginManager.getInstance();
        if (fbManager != null) fbManager.logOut();

        resetUser(generateAPIKey);
    }

    private static void resetUser(final boolean generateAPIKey) {
        MainActivity.updateCartCountNotification();
        MainActivity.invalidateDrawerMenuHeader();

        JSONObject jo = new JSONObject();
        try {
            jo.put("", "");

        } catch (JSONException e) {
            Timber.e(e, "Parse new user registration exception");
            return;
        }

        if(generateAPIKey) {
            SettingsMy.setActiveUser(null);
            prepareAPIKey();
        }
        else {
            JsonRequest req = new JsonRequest(Request.Method.POST, EndPoints.USER_LOGOUT_EMAIL,
                    jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                            SettingsMy.setActiveUser(null);
                            prepareAPIKey();
                        }
                        else {
                            Timber.d("User logout on url success. Response: %s", response.toString());
                            SettingsMy.setActiveUser(null);
                        }
                    } else
                        Timber.d("Null response during resetUser....");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    MsgUtils.logErrorMessage(error);
                }
            });
            req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            req.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(req, CONST.USER_LOGOUT_TAG);

            //SettingsMy.setActiveUser(null);
        }
    }

    private static void prepareAPIKey() {
        //Start API Access Token
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("old_token", SettingsMy.getOLDToken());
        } catch (JSONException e) {
            Timber.e(e, "Exception while parsing oauth result");
            MsgUtils.showToast(null, MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.LONG);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, EndPoints.OAUTH_TOKEN, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response != null) {
                        Timber.d("response: %s", response.toString());

                        SettingsMy.setAPIToken(response.getString(ACCESS_TOKEN));
                        SettingsMy.setOLDToken(response.getString(ACCESS_TOKEN));
                    }
                    else
                        Timber.d("Null response during splash activity init....");

                } catch (JSONException e) {
                    Timber.e(e, "Exception while parsing oauth result");
                    MsgUtils.showToast(null, MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.LONG);
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MsgUtils.logErrorMessage(error);
                //Log.d("error", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                String auth = "Basic " + Base64.encodeToString(CONST.CLIENT_ID_SECRET.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        jsonObjectRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(jsonObjectRequest, CONST.OAUTH_REQUESTS_TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogFullscreen);
        progressDialog = Utils.generateProgressDialog(getActivity(), false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = d.getWindow();
            window.setLayout(width, height);
            window.setWindowAnimations(R.style.dialogFragmentAnimation);
            d.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (BuildConfig.DEBUG)
                        Timber.d("onKey: %d (Back=%d). Event:%d (Down:%d, Up:%d)", keyCode, KeyEvent.KEYCODE_BACK, event.getAction(),
                                KeyEvent.ACTION_DOWN, KeyEvent.ACTION_UP);
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        switch (actualFormState) {
                            case REGISTRATION:
                                if (event.getAction() == KeyEvent.ACTION_UP) {
                                    setVisibilityOfRegistrationForm(false);
                                }
                                return true;
                            case FORGOTTEN_PASSWORD:
                                if (event.getAction() == KeyEvent.ACTION_UP) {
                                    setVisibilityOfEmailForgottenForm(false);
                                }
                                return true;
                            case EMAIL:
                                if (event.getAction() == KeyEvent.ACTION_UP) {
                                    setVisibilityOfEmailForm(false);
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.dialog_login, container, false);
        callbackManager = CallbackManager.Factory.create();

        loginBaseForm = (LinearLayout) view.findViewById(R.id.login_base_form);
        loginRegistrationForm = (LinearLayout) view.findViewById(R.id.login_registration_form);
        loginEmailForm = (LinearLayout) view.findViewById(R.id.login_email_form);
        loginEmailForgottenForm = (LinearLayout) view.findViewById(R.id.login_email_forgotten_form);

        prepareLoginFormNavigation(view);
        prepareInputBoxes(view);
        prepareActionButtons(view);
        return view;
    }

    private void prepareInputBoxes(View view) {
        // Registration form
        fnameInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_first_name_wrapper);
        lnameInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_last_name_wrapper);
        addressInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_address_wrapper);
        phoneInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_phone_wrapper);
        cityInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_city_wrapper);
        zipInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_zip_wrapper);
        countryInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_country_wrapper);
        stateInputWrapper = (TextInputLayout) view.findViewById(R.id.account_edit_state_wrapper);

        loginRegistrationEmailWrapper = (TextInputLayout) view.findViewById(R.id.login_registration_email_wrapper);
        loginRegistrationPasswordWrapper = (TextInputLayout) view.findViewById(R.id.login_registration_password_wrapper);
        loginRegistrationGenderWoman = (RadioButton) view.findViewById(R.id.login_registration_sex_woman);
        EditText registrationPassword = loginRegistrationPasswordWrapper.getEditText();
        if (registrationPassword != null) {
            registrationPassword.setOnTouchListener(new OnTouchPasswordListener(registrationPassword));
        }

        // Login email form
        loginEmailEmailWrapper = (TextInputLayout) view.findViewById(R.id.login_email_email_wrapper);
        EditText loginEmail = loginEmailEmailWrapper.getEditText();
        if (loginEmail != null) loginEmail.setText(SettingsMy.getUserEmailHint());
        loginEmailPasswordWrapper = (TextInputLayout) view.findViewById(R.id.login_email_password_wrapper);
        EditText emailPassword = loginEmailPasswordWrapper.getEditText();
        if (emailPassword != null) {
            emailPassword.setOnTouchListener(new OnTouchPasswordListener(emailPassword));
            emailPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND || actionId == 124) {
                        invokeLoginWithEmail();
                        return true;
                    }
                    return false;
                }
            });
        }

        loginEmailForgottenEmailWrapper = (TextInputLayout) view.findViewById(R.id.login_email_forgotten_email_wrapper);
        EditText emailForgottenPassword = loginEmailForgottenEmailWrapper.getEditText();
        if (emailForgottenPassword != null)
            emailForgottenPassword.setText(SettingsMy.getUserEmailHint());

        // Simple accounts whisperer.
        Account[] accounts = AccountManager.get(getActivity()).getAccountsByType("com.google");
        String[] addresses = new String[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            addresses[i] = accounts[i].name;
            Timber.e("Sets autocompleteEmails: %s", accounts[i].name);
        }

        ArrayAdapter<String> emails = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, addresses);
        AutoCompleteTextView textView = (AutoCompleteTextView) view.findViewById(R.id.login_registration_email_text_auto);
        textView.setAdapter(emails);
    }

    private void prepareLoginFormNavigation(View view) {
        // Login email
        Button loginFormEmailButton = (Button) view.findViewById(R.id.login_form_email_btn);
        loginFormEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilityOfEmailForm(true);
            }
        });
        ImageButton closeEmailBtn = (ImageButton) view.findViewById(R.id.login_email_close_button);
        closeEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Slow to display ripple effect
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setVisibilityOfEmailForm(false);
                    }
                }, 200);
            }
        });

        // Registration
        TextView loginFormRegistrationButton = (TextView) view.findViewById(R.id.login_form_registration_btn);
        loginFormRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilityOfRegistrationForm(true);
            }
        });
        ImageButton closeRegistrationBtn = (ImageButton) view.findViewById(R.id.login_registration_close_button);
        closeRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Slow to display ripple effect
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setVisibilityOfRegistrationForm(false);
                    }
                }, 200);
            }
        });

        // Email forgotten password
        TextView loginEmailFormForgottenButton = (TextView) view.findViewById(R.id.login_email_forgotten_password);
        loginEmailFormForgottenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilityOfEmailForgottenForm(true);
            }
        });
        ImageButton closeEmailForgottenFormBtn = (ImageButton) view.findViewById(R.id.login_email_forgotten_back_button);
        closeEmailForgottenFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Slow to display ripple effect
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setVisibilityOfEmailForgottenForm(false);
                    }
                }, 200);
            }
        });
    }

    private void prepareActionButtons(View view) {
        TextView loginBaseSkip = (TextView) view.findViewById(R.id.login_form_skip);
        loginBaseSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (loginDialogInterface != null) loginDialogInterface.skipLogin();
                dismiss();
            }
        });

        // FB login
        Button fbLogin = (Button) view.findViewById(R.id.login_form_facebook);
        fbLogin.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                invokeFacebookLogin();
            }
        });

        Button emailLogin = (Button) view.findViewById(R.id.login_email_confirm);
        emailLogin.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                invokeLoginWithEmail();
            }
        });

        Button registerBtn = (Button) view.findViewById(R.id.login_registration_confirm);
        registerBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                invokeRegisterNewUser();
            }
        });

        Button resetPassword = (Button) view.findViewById(R.id.login_email_forgotten_confirm);
        resetPassword.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                invokeResetPassword();
            }
        });
    }

    private void invokeFacebookLogin() {
        LoginManager.getInstance().registerCallback(callbackManager, this);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    private void invokeRegisterNewUser() {
        hideSoftKeyboard();
        if (isRequiredFields(loginRegistrationEmailWrapper, loginRegistrationPasswordWrapper, fnameInputWrapper, lnameInputWrapper, addressInputWrapper,
                cityInputWrapper, zipInputWrapper, phoneInputWrapper, countryInputWrapper, stateInputWrapper)) {
//            SettingsMy.setUserEmailHint(loginRegistrationEmailWrapper.getText().toString());
            registerNewUser(loginRegistrationEmailWrapper.getEditText(), loginRegistrationPasswordWrapper.getEditText(), fnameInputWrapper.getEditText(), lnameInputWrapper.getEditText(),
                    addressInputWrapper.getEditText(), cityInputWrapper.getEditText(), zipInputWrapper.getEditText(), phoneInputWrapper.getEditText(),
                    countryInputWrapper.getEditText(), stateInputWrapper.getEditText());
        }
    }

    private void registerNewUser(final EditText editTextEmail, final EditText editTextPassword, final EditText editTextFName, final EditText editTextLName, final EditText editTextAddress,
                                 final EditText editTextCity, final EditText editTextZip, final EditText editTextPhone, final EditText editTextCountry, final EditText editTextState) {
        SettingsMy.setUserEmailHint(editTextEmail.getText().toString());
        String url = String.format(EndPoints.USER_REGISTER);
        progressDialog.show();

        // get selected radio button from radioGroup
        JSONObject jo = new JSONObject();
        try {
            jo.put(JsonUtils.TAG_EMAIL, editTextEmail.getText().toString().trim());
            jo.put(JsonUtils.TAG_PASSWORD, editTextPassword.getText().toString().trim());
            jo.put(JsonUtils.TAG_ADDRESS1, editTextAddress.getText().toString().trim());
            jo.put(JsonUtils.TAG_ADDRESS2, editTextAddress.getText().toString().trim());
            jo.put(JsonUtils.TAG_CITY, editTextCity.getText().toString().trim());
            jo.put(JsonUtils.TAG_COMPANY_ID, "");
            jo.put(JsonUtils.TAG_COMPANY, "");
            jo.put(JsonUtils.TAG_COUNTRY_ID, "99");
            jo.put(JsonUtils.TAG_COUNTRY, editTextCountry.getText().toString().trim());
            jo.put(JsonUtils.TAG_FAX, "");
            jo.put(JsonUtils.TAG_FIRST_NAME, editTextFName.getText().toString().trim());
            jo.put(JsonUtils.TAG_LAST_NAME, editTextLName.getText().toString().trim());
            jo.put(JsonUtils.TAG_POST_CODE, editTextZip.getText().toString().trim());
            jo.put(JsonUtils.TAG_TAX, "");
            jo.put(JsonUtils.TAG_TELEPHONE, editTextPhone.getText().toString().trim());
            jo.put(JsonUtils.TAG_ZONE_ID, "1489");
            jo.put(JsonUtils.TAG_ZONE, editTextState.getText().toString().trim());
            jo.put(JsonUtils.TAG_CONFIRM, editTextPassword.getText().toString().trim());
            jo.put(JsonUtils.TAG_AGREE, "1");


            JSONObject childObject = new JSONObject();
            JSONObject parentObject = new JSONObject();
            childObject.put(JsonUtils.TAG_GENDER, loginRegistrationGenderWoman.isChecked() ? "female" : "male");
            childObject.put(JsonUtils.TAG_PLATFORM, "");
            childObject.put(JsonUtils.TAG_DEVICE_TOKEN, "");
            parentObject.put("account", childObject);

            jo.put(JsonUtils.TAG_CUSTOM_FIELD, parentObject);

        } catch (JSONException e) {
            Timber.e(e, "Parse new user registration exception");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }
        if (BuildConfig.DEBUG) Timber.d("Register new user: %s", jo.toString());

        GsonRequest<UserResponse> registerNewUser = new GsonRequest<>(Request.Method.POST, url, jo.toString(), UserResponse.class,
                new Response.Listener<UserResponse>() {
                    @Override
                    public void onResponse(@NonNull UserResponse response) {
                        if(response != null) {
                            if(response.getStatusText() != null && response.getStatusCode() != null) {
                                if (response.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || response.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                    LoginDialogFragment.logoutUser(true);
                                    DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                    loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getWarning() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getWarning().toLowerCase().equals(CONST.RESPONSE_ERROR)) {
                                    explicitLogout(editTextEmail, editTextPassword, editTextFName, editTextLName, editTextAddress, editTextCity, editTextZip, editTextPhone, editTextCountry, editTextState);
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getAddress_1() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getAddress_1().toLowerCase().equals(CONST.RESPONSE_ERROR_ADDRESS)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_ADDRESS_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getFirstname() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getFirstname().toLowerCase().equals(CONST.RESPONSE_ERROR_FNAME)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_FNAME_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getLastname() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getLastname().toLowerCase().equals(CONST.RESPONSE_ERROR_LNAME)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_LNAME_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getEmail() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getEmail().toLowerCase().equals(CONST.RESPONSE_ERROR_EMAIL)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_EMAIL, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getTelephone() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getTelephone().toLowerCase().equals(CONST.RESPONSE_ERROR_TELEPHONE)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_TELEPHONE_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getCity() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getCity().toLowerCase().equals(CONST.RESPONSE_ERROR_CITY)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_CITY_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getPassword() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getPassword().toLowerCase().equals(CONST.RESPONSE_ERROR_PASSWORD)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_PASSWORD_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getPostcode() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getPostcode().toLowerCase().equals(CONST.RESPONSE_ERROR_POSTCODE)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_POSTCODE_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            }
                            else {
                                if (response.getUser() != null)
                                    Timber.d(MSG_RESPONSE, response.getUser().toString());

                                handleUserLogin(response.getUser(), true);
                            }
                        }
                        else {
                            Timber.d("Null response during registerNewUser....");
                            if (progressDialog != null) progressDialog.cancel();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        registerNewUser.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        registerNewUser.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(registerNewUser, CONST.LOGIN_DIALOG_REQUESTS_TAG);
    }

    private void invokeLoginWithEmail() {
        hideSoftKeyboard();
        if (isRequiredFieldsLWE(loginEmailEmailWrapper, loginEmailPasswordWrapper)) {
            logInWithEmail(loginEmailEmailWrapper.getEditText(), loginEmailPasswordWrapper.getEditText());
        }
    }

    private void logInWithEmail(final EditText editTextEmail, final EditText editTextPassword) {
        SettingsMy.setUserEmailHint(editTextEmail.getText().toString());
        String url = String.format(EndPoints.USER_LOGIN_EMAIL);
        progressDialog.show();

        JSONObject jo;
        try {
            jo = JsonUtils.createUserAuthentication(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim());
            editTextPassword.setText("");
        } catch (JSONException e) {
            Timber.e(e, "Parse logInWithEmail exception");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }
        if (BuildConfig.DEBUG) Timber.d("Login user: %s", jo.toString());

        final GsonRequest<UserResponse> userLoginEmailRequest = new GsonRequest<>(Request.Method.POST, url, jo.toString(), UserResponse.class,
                new Response.Listener<UserResponse>() {
                    @Override
                    public void onResponse(@NonNull UserResponse response) {
                        if(response != null) {
                            if(response.getStatusCode() != null && response.getStatusText() != null) {
                                if(response.getStatusCode() != null && response.getStatusText() != null) {
                                    if (response.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || response.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                        LoginDialogFragment.logoutUser(true);
                                        DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                        loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                        if (progressDialog != null) progressDialog.cancel();
                                    }
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null) {
                                if(response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getWarning().toLowerCase().equals(CONST.RESPONSE_ERROR)) {
                                    reloginWithEmail(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim());
                                }

                                if(response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getWarning().toLowerCase().contains(CONST.RESPONSE_WARNING)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INVALID_CREDENTIALS, null, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                    return;
                                }
                            }
                            else {
                                if (response.getUser() != null)
                                    Timber.d(MSG_RESPONSE, response.getUser().toString());

                                handleUserLogin(response.getUser(), false);
                            }
                        }
                        else {
                            Timber.d("Null response during logInWithEmail....");
                            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INVALID_CREDENTIALS, null, MsgUtils.ToastLength.LONG);
                            if (progressDialog != null) progressDialog.cancel();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                Timber.d(MSG_RESPONSE, error.getMessage());
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        userLoginEmailRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        userLoginEmailRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(userLoginEmailRequest, CONST.LOGIN_DIALOG_REQUESTS_TAG);
    }

    private void explicitLogout(final EditText editTextEmail, final EditText editTextPassword, final EditText editTextFName, final EditText editTextLName, final EditText editTextAddress,
                                final EditText editTextCity, final EditText editTextZip, final EditText editTextPhone, final EditText editTextCountry, final EditText editTextState)
    {
        MainActivity.updateCartCountNotification();
        MainActivity.invalidateDrawerMenuHeader();

        LoginManager fbManager = LoginManager.getInstance();
        if (fbManager != null) fbManager.logOut();

        JSONObject jo = new JSONObject();
        try {
            jo.put("", "");

        } catch (JSONException e) {
            Timber.e(e, "Parse new user registration exception");
            return;
        }

        JsonRequest req = new JsonRequest(Request.Method.POST, EndPoints.USER_LOGOUT_EMAIL,
                jo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                        LoginDialogFragment.logoutUser(true);
                        DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                        loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                        if (progressDialog != null) progressDialog.cancel();
                    }
                    else {
                        Timber.d("User logout on url success. Response: %s", response.toString());
                        SettingsMy.setActiveUser(null);
                        reRegister(editTextEmail, editTextPassword, editTextFName, editTextLName, editTextAddress, editTextCity, editTextZip, editTextPhone, editTextCountry, editTextState);
                    }
                } else {
                    Timber.d("Null response during resetUser....");
                    if (progressDialog != null) progressDialog.cancel();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logErrorMessage(error);
            }
        });
        req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        req.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(req, CONST.USER_LOGOUT_TAG);
    }

    private void reRegister(EditText editTextEmail, EditText editTextPassword, EditText editTextFName, EditText editTextLName, EditText editTextAddress,
                            EditText editTextCity, EditText editTextZip, EditText editTextPhone, EditText editTextCountry, EditText editTextState) {
        SettingsMy.setUserEmailHint(editTextEmail.getText().toString());
        String url = String.format(EndPoints.USER_REGISTER);
        progressDialog.show();

        // get selected radio button from radioGroup
        JSONObject jo = new JSONObject();
        try {
            jo.put(JsonUtils.TAG_EMAIL, editTextEmail.getText().toString().trim());
            jo.put(JsonUtils.TAG_PASSWORD, editTextPassword.getText().toString().trim());
            jo.put(JsonUtils.TAG_ADDRESS1, editTextAddress.getText().toString().trim());
            jo.put(JsonUtils.TAG_ADDRESS2, editTextAddress.getText().toString().trim());
            jo.put(JsonUtils.TAG_CITY, editTextCity.getText().toString().trim());
            jo.put(JsonUtils.TAG_COMPANY_ID, "");
            jo.put(JsonUtils.TAG_COMPANY, "");
            jo.put(JsonUtils.TAG_COUNTRY_ID, "99");
            jo.put(JsonUtils.TAG_COUNTRY, editTextCountry.getText().toString().trim());
            jo.put(JsonUtils.TAG_FAX, "");
            jo.put(JsonUtils.TAG_FIRST_NAME, editTextFName.getText().toString().trim());
            jo.put(JsonUtils.TAG_LAST_NAME, editTextLName.getText().toString().trim());
            jo.put(JsonUtils.TAG_POST_CODE, editTextZip.getText().toString().trim());
            jo.put(JsonUtils.TAG_TAX, "");
            jo.put(JsonUtils.TAG_TELEPHONE, editTextPhone.getText().toString().trim());
            jo.put(JsonUtils.TAG_ZONE_ID, "1489");
            jo.put(JsonUtils.TAG_ZONE, editTextState.getText().toString().trim());
            jo.put(JsonUtils.TAG_CONFIRM, editTextPassword.getText().toString().trim());
            jo.put(JsonUtils.TAG_AGREE, "1");


            JSONObject childObject = new JSONObject();
            JSONObject parentObject = new JSONObject();
            childObject.put(JsonUtils.TAG_GENDER, loginRegistrationGenderWoman.isChecked() ? "female" : "male");
            childObject.put(JsonUtils.TAG_PLATFORM, "");
            childObject.put(JsonUtils.TAG_DEVICE_TOKEN, "");
            parentObject.put("account", childObject);

            jo.put(JsonUtils.TAG_CUSTOM_FIELD, parentObject);

        } catch (JSONException e) {
            Timber.e(e, "Parse new user registration exception");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }
        if (BuildConfig.DEBUG) Timber.d("Register new user: %s", jo.toString());

        GsonRequest<UserResponse> registerNewUser = new GsonRequest<>(Request.Method.POST, url, jo.toString(), UserResponse.class,
                new Response.Listener<UserResponse>() {
                    @Override
                    public void onResponse(@NonNull UserResponse response) {
                        if (response != null) {
                            if (response.getStatusText() != null && response.getStatusCode() != null) {
                                if (response.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || response.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                    LoginDialogFragment.logoutUser(true);
                                    DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                    loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getAddress_1() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getAddress_1().toLowerCase().equals(CONST.RESPONSE_ERROR_ADDRESS)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_ADDRESS_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                    return;
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getFirstname() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getFirstname().toLowerCase().equals(CONST.RESPONSE_ERROR_FNAME)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_FNAME_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                    return;
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getLastname() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getLastname().toLowerCase().equals(CONST.RESPONSE_ERROR_LNAME)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_LNAME_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                    return;
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getEmail() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getEmail().toLowerCase().equals(CONST.RESPONSE_ERROR_EMAIL)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_EMAIL, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                    return;
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getTelephone() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getTelephone().toLowerCase().equals(CONST.RESPONSE_ERROR_TELEPHONE)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_TELEPHONE_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                    return;
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getCity() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getCity().toLowerCase().equals(CONST.RESPONSE_ERROR_CITY)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_CITY_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                    return;
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getPassword() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getPassword().toLowerCase().equals(CONST.RESPONSE_ERROR_PASSWORD)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_PASSWORD_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                    return;
                                }
                            }
                            else if(response.getUserWarning() != null && response.getSuccess() != null && response.getUserWarning().getPostcode() != null) {
                                if (response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getPostcode().toLowerCase().equals(CONST.RESPONSE_ERROR_POSTCODE)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, CONST.RESPONSE_ERROR_POSTCODE_REQUIRED, MsgUtils.ToastLength.LONG);
                                    if (progressDialog != null) progressDialog.cancel();
                                    return;
                                }
                            }
                            else {
                                if (response.getUser() != null)
                                    Timber.d(MSG_RESPONSE, response.getUser().toString());

                                handleUserLogin(response.getUser(), true);
                            }
                        } else {
                            Timber.d("Null response during registerNewUser....");
                            if (progressDialog != null) progressDialog.cancel();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        registerNewUser.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        registerNewUser.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(registerNewUser, CONST.LOGIN_DIALOG_REQUESTS_TAG);
    }

    private void reloginWithEmail(final String userName, final String password) {
        MainActivity.updateCartCountNotification();
        MainActivity.invalidateDrawerMenuHeader();

        LoginManager fbManager = LoginManager.getInstance();
        if (fbManager != null) fbManager.logOut();

        JSONObject jo = new JSONObject();
        try {
            jo.put("", "");

        } catch (JSONException e) {
            Timber.e(e, "Parse new user registration exception");
            return;
        }

        JsonRequest req = new JsonRequest(Request.Method.POST, EndPoints.USER_LOGOUT_EMAIL,
                jo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    if(response.toString().toLowerCase().contains(CONST.RESPONSE_CODE) || response.toString().toLowerCase().contains(CONST.RESPONSE_UNAUTHORIZED)) {
                        LoginDialogFragment.logoutUser(true);
                        DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                        loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                        if (progressDialog != null) progressDialog.cancel();
                    }
                    else {
                        Timber.d("User logout on url success. Response: %s", response.toString());
                        SettingsMy.setActiveUser(null);
                        relogin(userName, password);
                    }
                } else {
                    Timber.d("Null response during resetUser....");
                    if (progressDialog != null) progressDialog.cancel();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logErrorMessage(error);
            }
        });
        req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        req.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(req, CONST.USER_LOGOUT_TAG);
    }

    private void relogin(String userName, String password) {

        SettingsMy.setUserEmailHint(userName);
        String url = String.format(EndPoints.USER_LOGIN_EMAIL);
        //progressDialog.show();

        JSONObject jo;
        try {
            jo = JsonUtils.createUserAuthentication(userName, password);
        } catch (JSONException e) {
            Timber.e(e, "Parse logInWithEmail exception");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            if (progressDialog != null) progressDialog.cancel();
            return;
        }
        if (BuildConfig.DEBUG) Timber.d("Re-Login user after logout: %s", jo.toString());

        GsonRequest<UserResponse> userLoginEmailRequest = new GsonRequest<>(Request.Method.POST, url, jo.toString(), UserResponse.class,
                new Response.Listener<UserResponse>() {
                    @Override
                    public void onResponse(@NonNull UserResponse response) {
                        if(response != null) {
                            if(response.getStatusCode() != null && response.getStatusText() != null) {
                                if(response.getStatusCode() != null && response.getStatusText() != null) {
                                    if (response.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || response.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                        LoginDialogFragment.logoutUser(true);
                                        DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                        loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                        if (progressDialog != null) progressDialog.cancel();
                                    }
                                }
                            }
                            else if(response.getSuccess().toLowerCase().equals(CONST.RESPONSE_SUCCESS) && response.getUserWarning().getWarning().toLowerCase().contains(CONST.RESPONSE_WARNING)) {
                                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INVALID_CREDENTIALS, null, MsgUtils.ToastLength.LONG);
                                if (progressDialog != null) progressDialog.cancel();
                                return;
                            }
                            else {
                                if (response.getUser() != null)
                                    Timber.d(MSG_RESPONSE, response.getUser().toString());

                                handleUserLogin(response.getUser(), false);
                            }
                        }
                        else {
                            Timber.d("Null response during logInWithEmail....");
                            if (progressDialog != null) progressDialog.cancel();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                Timber.d(MSG_RESPONSE, error);
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        userLoginEmailRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        userLoginEmailRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(userLoginEmailRequest, CONST.LOGIN_DIALOG_REQUESTS_TAG);
    }

    private void handleUserLogin(final User user, boolean newUser) {
        if(newUser && user != null) {
            Timber.d("Registered new user!");

            SettingsMy.setActiveUser(user);
            UserAddress userAddress = new UserAddress();
            userAddress.setFirstName(user.getFirstname());
            userAddress.setLastName(user.getLastname());
            userAddress.setAddress_1(user.getAddress_1());
            userAddress.setAddress_2(user.getAddress_2());
            userAddress.setPostCode(user.getPostalcode());
            userAddress.setCountry("India");
            userAddress.setZone("Karnataka");
            userAddress.setCity(user.getCity());

            SettingsMy.getActiveUser().setAddress(userAddress);
            loadUserAddressID(user);
        }
        else if (!newUser && user != null) {
            SettingsMy.setActiveUser(user);
            loadUserAddress(user);
        }
        else {
            Timber.d("User Null during handle user login...");
            if (progressDialog != null) progressDialog.cancel();
            return;
        }
    }

    private void loadUserAddress(final User user) {
        //Load User Address
        String url = String.format(EndPoints.USER_ADDRESS, (user.getAddress_id() != null || user.getAddress_id() != "") ? Integer.parseInt(user.getAddress_id()) : 0);

        final GsonRequest<UserAddressResponse> userLoginAddressRequest = new GsonRequest<>(Request.Method.GET, url, null, UserAddressResponse.class,
                new Response.Listener<UserAddressResponse>() {
                    @Override
                    public void onResponse(@NonNull UserAddressResponse response) {
                        if(response.getUserAddress() != null) {
                            if (response.getUserAddress().getStatusCode() != null && response.getUserAddress().getStatusText() != null) {
                                if (response.getUserAddress().getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || response.getUserAddress().getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                    LoginDialogFragment.logoutUser(true);
                                    DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                    loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            } else if (response.getUserAddress() != null) {
                                Timber.d(MSG_RESPONSE, response.toString());
                                SettingsMy.getActiveUser().setAddress(response.getUserAddress());
                                if (progressDialog != null) progressDialog.cancel();
                            }
                            else {
                                Timber.d("Null user response during logInWithEmail....");
                                if (progressDialog != null) progressDialog.cancel();
                            }
                        }
                        else {
                            Timber.d("Null response during logInWithEmail....");
                            if (progressDialog != null) progressDialog.cancel();
                        }

                        // Invalidate GCM token for new registration with authorized user.
                        SettingsMy.setTokenSentToServer(false);

                        if (getActivity() instanceof MainActivity)
                            ((MainActivity) getActivity()).registerGcmOnServer();

                        MainActivity.invalidateDrawerMenuHeader();

                        if (loginDialogInterface != null) {
                            loginDialogInterface.successfulLoginOrRegistration(user);
                        } else {
                            Timber.e("Interface is null");
                            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                        }
                        dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                Timber.d(MSG_RESPONSE, error);
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        userLoginAddressRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        userLoginAddressRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(userLoginAddressRequest, CONST.LOGIN_DIALOG_REQUESTS_TAG);
    }

    private void loadUserAddressID(final User user) {
        //Load User Address
        String url = String.format(EndPoints.USER_SINGLE);

        final GsonRequest<UserResponse> userLoginAddressRequest = new GsonRequest<>(Request.Method.GET, url, null, UserResponse.class,
                new Response.Listener<UserResponse>() {
                    @Override
                    public void onResponse(@NonNull UserResponse response) {
                        if(response != null) {
                            if (response.getStatusCode() != null && response.getStatusText() != null) {
                                if (response.getStatusCode().toLowerCase().equals(CONST.RESPONSE_CODE) || response.getStatusText().toLowerCase().equals(CONST.RESPONSE_UNAUTHORIZED)) {
                                    LoginDialogFragment.logoutUser(true);
                                    DialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                    loginExpiredDialogFragment.show(getFragmentManager(), LoginExpiredDialogFragment.class.getSimpleName());
                                    if (progressDialog != null) progressDialog.cancel();
                                }
                            } else if (response.getUser() != null) {
                                Timber.d(MSG_RESPONSE, response.toString());
                                SettingsMy.getActiveUser().setAddress_id(response.getUser().getAddress_id());
                                SettingsMy.getActiveUser().setUserCustomField(response.getUser().getUserCustomField());
                                if (progressDialog != null) progressDialog.cancel();
                            }
                            else {
                                Timber.d("Null user response during logInWithEmail....");
                                if (progressDialog != null) progressDialog.cancel();
                            }
                        }
                        else {
                            Timber.d("Null response during logInWithEmail....");
                            if (progressDialog != null) progressDialog.cancel();
                        }

                        // Invalidate GCM token for new registration with authorized user.
                        SettingsMy.setTokenSentToServer(false);
                        if (getActivity() instanceof MainActivity)
                            ((MainActivity) getActivity()).registerGcmOnServer();

                        MainActivity.invalidateDrawerMenuHeader();

                        if (loginDialogInterface != null) {
                            loginDialogInterface.successfulLoginOrRegistration(user);
                        } else {
                            Timber.e("Interface is null");
                            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                        }
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Registered_new_user), MsgUtils.ToastLength.SHORT);
                        dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                Timber.d(MSG_RESPONSE, error);
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        userLoginAddressRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        userLoginAddressRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(userLoginAddressRequest, CONST.LOGIN_DIALOG_REQUESTS_TAG);
    }

    private void invokeResetPassword() {
        EditText emailForgottenPasswordEmail = loginEmailForgottenEmailWrapper.getEditText();
        if (emailForgottenPasswordEmail == null || emailForgottenPasswordEmail.getText().toString().equalsIgnoreCase("")) {
            loginEmailForgottenEmailWrapper.setErrorEnabled(true);
            loginEmailForgottenEmailWrapper.setError(getString(R.string.Required_field));
        } else {
            loginEmailForgottenEmailWrapper.setErrorEnabled(false);
            resetPassword(emailForgottenPasswordEmail);
        }
    }

    private void resetPassword(EditText emailOfForgottenPassword) {
        String url = String.format(EndPoints.USER_RESET_PASSWORD);
        progressDialog.show();

        JSONObject jo = new JSONObject();
        try {
            jo.put(JsonUtils.TAG_EMAIL, emailOfForgottenPassword.getText().toString().trim());
        } catch (JSONException e) {
            Timber.e(e, "Parse resetPassword exception");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
            return;
        }
        if (BuildConfig.DEBUG) Timber.d("Reset password email: %s", jo.toString());

        JsonRequest req = new JsonRequest(Request.Method.POST, url,
                jo, new Response.Listener<JSONObject>() {
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
                            if (response.getBoolean("success")) {
                                Timber.d("Reset password on url success. Response: %s", response.toString());
                                progressDialog.cancel();
                                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Check_your_email_we_sent_you_an_confirmation_email), MsgUtils.ToastLength.LONG);
                                setVisibilityOfEmailForgottenForm(false);
                            } else {
                                Timber.d("Reset password on url failure. Response: %s", response.toString());
                                progressDialog.cancel();

                                if(response.toString().toLowerCase().contains(CONST.RESPONSE_WARNING_FORGOTTEN)) {
                                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INVALID_EMAIL, null, MsgUtils.ToastLength.LONG);
                                    return;
                                }

                                setVisibilityOfEmailForgottenForm(false);
                            }
                        }
                    }
                    else
                        Timber.d("Null response during resetPassword....");

            } catch (JSONException e) {
                    Timber.e(e, "Parse resetPassword exception");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        req.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(req, CONST.LOGIN_DIALOG_REQUESTS_TAG);
    }

    private void hideSoftKeyboard() {
        if (getActivity() != null && getView() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    private void setVisibilityOfRegistrationForm(boolean setVisible) {
        if (setVisible) {
            actualFormState = FormState.REGISTRATION;
            loginBaseForm.setVisibility(View.INVISIBLE);
            loginRegistrationForm.setVisibility(View.VISIBLE);
        } else {
            actualFormState = FormState.BASE;
            loginBaseForm.setVisibility(View.VISIBLE);
            loginRegistrationForm.setVisibility(View.INVISIBLE);
            hideSoftKeyboard();
        }
    }

    private void setVisibilityOfEmailForm(boolean setVisible) {
        if (setVisible) {
            actualFormState = FormState.EMAIL;
            loginBaseForm.setVisibility(View.INVISIBLE);
            loginEmailForm.setVisibility(View.VISIBLE);
        } else {
            actualFormState = FormState.BASE;
            loginBaseForm.setVisibility(View.VISIBLE);
            loginEmailForm.setVisibility(View.INVISIBLE);
            hideSoftKeyboard();
        }
    }

    private void setVisibilityOfEmailForgottenForm(boolean setVisible) {
        if (setVisible) {
            actualFormState = FormState.FORGOTTEN_PASSWORD;
            loginEmailForm.setVisibility(View.INVISIBLE);
            loginEmailForgottenForm.setVisibility(View.VISIBLE);
        } else {
            actualFormState = FormState.EMAIL;
            loginEmailForm.setVisibility(View.VISIBLE);
            loginEmailForgottenForm.setVisibility(View.INVISIBLE);
        }
        hideSoftKeyboard();
    }

    private boolean isRequiredFieldsLWE(TextInputLayout emailWrapper, TextInputLayout passwordWrapper) {
        if (emailWrapper == null || passwordWrapper == null) {
            Timber.e(new RuntimeException(), "Called isRequiredFields with null parameters.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.LONG);
            return false;
        } else {
            EditText email = emailWrapper.getEditText();
            EditText password = passwordWrapper.getEditText();

            if (email == null || password == null) {
                Timber.e(new RuntimeException(), "Called isRequiredFields with null editTexts in wrappers.");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Required_fields_for_registration), MsgUtils.ToastLength.LONG);
                return false;
            } else {
                boolean isEmail = false;
                boolean isPassword = false;

                if (email.getText().toString().equalsIgnoreCase("")) {
                    emailWrapper.setErrorEnabled(true);
                    emailWrapper.setError(getString(R.string.Required_field));
                } else {
                    emailWrapper.setErrorEnabled(false);
                    isEmail = true;
                }

                if (password.getText().toString().equalsIgnoreCase("")) {
                    passwordWrapper.setErrorEnabled(true);
                    passwordWrapper.setError(getString(R.string.Required_field));
                } else {
                    passwordWrapper.setErrorEnabled(false);
                    isPassword = true;
                }

                if (isEmail && isPassword) {
                    return true;
                } else {
                    Timber.e("Some fields are required.");
                    return false;
                }
            }
        }
    }

    /**
     * Check if editTexts are valid view and if user set all required fields.
     *
     * @return true if ok.
     */
    private boolean isRequiredFields(TextInputLayout emailWrapper, TextInputLayout passwordWrapper, TextInputLayout fNameWrapper, TextInputLayout lNameWrapper,
                                     TextInputLayout addressWrapper, TextInputLayout cityWrapper, TextInputLayout zipWrapper, TextInputLayout phoneWrapper,
                                     TextInputLayout countryWrapper, TextInputLayout stateWrapper) {
        if (emailWrapper == null || passwordWrapper == null || fNameWrapper == null || lNameWrapper == null || addressWrapper == null || cityWrapper == null || zipWrapper == null ||
                phoneWrapper == null || countryWrapper == null || stateWrapper == null) {
            Timber.e(new RuntimeException(), "Called isRequiredFields with null parameters.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.LONG);
            return false;
        } else {
            EditText email = emailWrapper.getEditText();
            EditText password = passwordWrapper.getEditText();
            EditText fname = fNameWrapper.getEditText();
            EditText lname = lNameWrapper.getEditText();
            EditText phone = phoneWrapper.getEditText();
            EditText address = addressWrapper.getEditText();
            EditText zip = zipWrapper.getEditText();
            EditText city = cityWrapper.getEditText();
            EditText country = countryWrapper.getEditText();
            EditText state = stateWrapper.getEditText();

            if (email == null || password == null || fname == null || lname == null || phone == null || address == null || zip == null || city == null || country == null || state == null) {
                Timber.e(new RuntimeException(), "Called isRequiredFields with null editTexts in wrappers.");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Required_fields_for_registration), MsgUtils.ToastLength.LONG);
                return false;
            } else {
                boolean isEmail = false;
                boolean isPassword = false;
                boolean isFname = false;
                boolean isLname = false;
                boolean isPhone = false;
                boolean isAddress = false;
                boolean isZip = false;
                boolean isCity = false;
                boolean isCountry = false;
                boolean isState = false;

                if (email.getText().toString().equalsIgnoreCase("")) {
                    emailWrapper.setErrorEnabled(true);
                    emailWrapper.setError(getString(R.string.Required_field));
                } else {
                    emailWrapper.setErrorEnabled(false);
                    isEmail = true;
                }

                if (password.getText().toString().equalsIgnoreCase("")) {
                    passwordWrapper.setErrorEnabled(true);
                    passwordWrapper.setError(getString(R.string.Required_field));
                } else {
                    passwordWrapper.setErrorEnabled(false);
                    isPassword = true;
                }

                if (fname.getText().toString().equalsIgnoreCase("")) {
                    fNameWrapper.setErrorEnabled(true);
                    fNameWrapper.setError(getString(R.string.Required_field));
                } else {
                    fNameWrapper.setErrorEnabled(false);
                    isFname = true;
                }

                if (lname.getText().toString().equalsIgnoreCase("")) {
                    lNameWrapper.setErrorEnabled(true);
                    lNameWrapper.setError(getString(R.string.Required_field));
                } else {
                    lNameWrapper.setErrorEnabled(false);
                    isLname = true;
                }

                if (phone.getText().toString().equalsIgnoreCase("")) {
                    phoneWrapper.setErrorEnabled(true);
                    phoneWrapper.setError(getString(R.string.Required_field));
                } else {
                    phoneWrapper.setErrorEnabled(false);
                    isPhone = true;
                }

                if (address.getText().toString().equalsIgnoreCase("")) {
                    addressWrapper.setErrorEnabled(true);
                    addressWrapper.setError(getString(R.string.Required_field));
                } else {
                    addressWrapper.setErrorEnabled(false);
                    isAddress = true;
                }

                if (zip.getText().toString().equalsIgnoreCase("")) {
                    zipWrapper.setErrorEnabled(true);
                    zipWrapper.setError(getString(R.string.Required_field));
                } else {
                    zipWrapper.setErrorEnabled(false);
                    isZip = true;
                }

                if (city.getText().toString().equalsIgnoreCase("")) {
                    cityWrapper.setErrorEnabled(true);
                    cityWrapper.setError(getString(R.string.Required_field));
                } else {
                    cityWrapper.setErrorEnabled(false);
                    isCity = true;
                }

                if (country.getText().toString().equalsIgnoreCase("")) {
                    countryWrapper.setErrorEnabled(true);
                    countryWrapper.setError(getString(R.string.Required_field));
                } else {
                    countryWrapper.setErrorEnabled(false);
                    isCountry = true;
                }

                if (state.getText().toString().equalsIgnoreCase("")) {
                    stateWrapper.setErrorEnabled(true);
                    stateWrapper.setError(getString(R.string.Required_field));
                } else {
                    stateWrapper.setErrorEnabled(false);
                    isState = true;
                }

                if (isEmail && isPassword && isFname && isLname && isPhone && isAddress && isZip && isCity && isCountry && isState) {
                    return true;
                } else {
                    Timber.e("Some fields are required.");
                    return false;
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getInstance().getRequestQueue().cancelAll(CONST.LOGIN_DIALOG_REQUESTS_TAG);
    }

    @Override
    public void onDetach() {
        loginDialogInterface = null;
        super.onDetach();
    }

    @Override
    public void onSuccess(final LoginResult loginResult) {
        Timber.d("FB login success");
        if (loginResult == null) {
            Timber.e("Fb login succeed with null loginResult.");
            handleNonFatalError(getString(R.string.Facebook_login_failed), true);
        } else {
            Timber.d("Result: %s", loginResult.toString());
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            if (response != null && response.getError() == null) {
                                verifyUserOnApi(object, loginResult.getAccessToken());
                            } else {
                                Timber.e("Error on receiving user profile information.");
                                if (response != null && response.getError() != null) {
                                    Timber.e(new RuntimeException(), "Error: %s", response.getError().toString());
                                }
                                handleNonFatalError(getString(R.string.Receiving_facebook_profile_failed), true);
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    @Override
    public void onCancel() {
        Timber.d("Fb login canceled");
    }

    @Override
    public void onError(FacebookException e) {
        Timber.e(e, "Fb login error");
        handleNonFatalError(getString(R.string.Facebook_login_failed), false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
        else {
            Timber.d("OnActivityResult, null callbackManager object.");
        }
    }

    /**
     * Volley request that sends FB_ID and FB_ACCESS_TOKEN to API
     */
    private void verifyUserOnApi(JSONObject userProfileObject, AccessToken fbAccessToken) {

        String url = String.format(EndPoints.USER_LOGIN_SOCIAL);
        JSONObject jo = new JSONObject();
        try {
            Timber.d(MSG_RESPONSE, "********************************");
            Timber.d(MSG_RESPONSE, userProfileObject.toString());

            //jo.put(JsonUtils.TAG_EMAIL, userProfileObject.getString("email"));
            jo.put(JsonUtils.TAG_EMAIL, userProfileObject.getString("email"));
            jo.put(JsonUtils.TAG_FB_ACCESS_TOKEN, fbAccessToken.getToken());
            jo.put(JsonUtils.TAG_PROVIDER, "facebook");

            Timber.d("FB post args....%s",jo.toString());

        } catch (JSONException e) {
            Timber.e(e, "Exception while parsing fb user.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.LONG);
            return;
        }

        progressDialog.show();
        GsonRequest<UserResponse> verifyFbUser = new GsonRequest<>(Request.Method.POST, url, jo.toString(), UserResponse.class,
                new Response.Listener<UserResponse>() {
                    @Override
                    public void onResponse(@NonNull UserResponse response) {
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
                                if (response.getUser() != null)
                                    Timber.d(MSG_RESPONSE, response.getUser().toString());

                                handleUserLogin(response.getUser(), false);
                            }
                        }
                        else
                            Timber.d("Null response during verifyUserOnApi....");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
                Timber.d("error response %S", error.toString());
                LoginDialogFragment.logoutUser(false);
            }
        }, getFragmentManager(), null);
        verifyFbUser.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        verifyFbUser.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(verifyFbUser, CONST.LOGIN_DIALOG_REQUESTS_TAG);
    }

    /**
     * Handle errors, when user have identity at least.
     * Show error message to user.
     */
    private void handleNonFatalError(String message, boolean logoutFromFb) {
        if (logoutFromFb) {
            LoginDialogFragment.logoutUser(false);
        }
        if (getActivity() != null)
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, message, MsgUtils.ToastLength.LONG);
    }

    private enum FormState {
        BASE, REGISTRATION, EMAIL, FORGOTTEN_PASSWORD
    }
}
