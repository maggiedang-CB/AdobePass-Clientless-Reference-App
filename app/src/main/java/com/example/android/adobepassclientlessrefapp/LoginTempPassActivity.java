package com.example.android.adobepassclientlessrefapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;
import com.example.android.adobepassclientlessrefapp.utils.DeviceUtils;
import com.nbcsports.leapsdk.authentication.adobepass.AdobeClientlessService;
import com.nbcsports.leapsdk.authentication.adobepass.config.AdobeConfig;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Temporary Pass allow users to authorize compatible media assets for a set amount of time.
 * The requestorIds from both the Temp Pass Login and Media Info data must match in order to
 * have a successful authorization.
 * <p>
 * TempPassActivity can tell the user the status of the temp pass (Off or On), the current
 * temp pass id, and a section to enter a temp pass id to reset or login.
 * </p>
 * The Temp Pass Ids are determined from this section of the adobe config json:
 * {@code
 *			"passes": [{
 * 				"requestorID": "NBCOlympics",
 * 				"shortTempPassId": "TempPass-ShortTTL",
 * 				"longTempPassId": "TempPass-LongTTL"
 *                        }, {
 * 				"requestorID": "nbcentertainment",
 * 				"shortTempPassId": "TempPass-ShortTTL",
 * 				"longTempPassId": "TempPass-LongTTL"
 *            }, {
 * 				"requestorID": "telemundo",
 * 				"shortTempPassId": "TempPass-ShortTTL",
 * 				"longTempPassId": "TempPass-LongTTL"
 *            }, {
 * 				"requestorID": "nbcsports",
 * 				"shortTempPassId": "TempPass-Sports-10min",
 * 				"longTempPassId": ""
 *            }, {
 * 				"requestorID": "golf",
 * 				"shortTempPassId": "TempPass-Golf-10min",
 * 				"longTempPassId": ""
 *            }]
 * }
 */
public class LoginTempPassActivity extends AbstractActivity {

    public static String TAG = "LoginTempPassActivity";

    @BindView(R.id.temppass_status)
    TextView tvTempPassStatus;
    @BindView(R.id.tempPass_Id)
    TextView tvTempPassId;

    @BindView(R.id.logintemppass_tempPassId)
    EditText etTempPassId;
    @BindView(R.id.btn_logintemppass_ok)
    Button loginButton;
    @BindView(R.id.btn_logintemppass_back)
    Button backButton;
    @BindView(R.id.btn_logintemppass_reset)
    Button resetButton;

    SharedPreferences sharedPreferences;
    AdobeConfig adobeConfig;
    AdobeClientlessService adobeClientless;

    // For Resetting Temp Pass
    private String apiKey = "WEwV2lPkLvFCSBF83D5viGGwa2mI8y4s";
    private String token = "uEBlZUHnAjCPP3yGYRbu3KJfEXJD";
    private static String PREQUAL_URL = "mgmt.prequal.auth-staging.adobe.com";
    private static String RELEASE_URL = "mgmt.auth.adobe.com";
    private static String RESET_SERVICE = "/reset-tempass/v2.1/reset";
    private OkHttpClient client;

    enum LoginStatus {
        TEMPPASS_ID
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logintemppass_layout);
        ButterKnife.bind(this);

        // set listeners
        backButton.setOnClickListener(backButtonListener());
        loginButton.setOnClickListener(loginListener);
        resetButton.setOnClickListener(resetListener);

        this.adobeConfig = getAdobeConfigFromJson();
        this.adobeClientless = new AdobeClientlessService(this, adobeConfig, DeviceUtils.getDeviceInfo());
        this.client = new OkHttpClient();

        loadSavedInfo();
    }

    /**
     * Coming back to the temp pass login screen should show the temp pass status and current
     * temp pass id if possible.
     */
    private void loadSavedInfo() {
        sharedPreferences = getSharedPreferences();
        String mvpdKey = LoginStatus.TEMPPASS_ID.toString();
        String status = sharedPreferences.getString(mvpdKey, getString(R.string.temppass_id_not_loggedIn));

        if (!status.equals(getString(R.string.temppass_id_not_loggedIn))) {
            tvTempPassStatus.setText(getString(R.string.temppass_ON));
            tvTempPassId.setText(status);
        }

    }

    private View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tempPassId = etTempPassId.getText().toString();
            if (tempPassId == null || tempPassId.equals("")) {
                // User has not entered temp pass Id
                Toast.makeText(LoginTempPassActivity.this, getString(R.string.temppass_id_empty), Toast.LENGTH_SHORT).show();
            } else if (getRId() == null || getRId().equals("")) {
                // No RId saved
                Toast.makeText(LoginTempPassActivity.this, getString(R.string.setup_rId_false), Toast.LENGTH_SHORT).show();
            } else {
                // reset temp pass
                resetTempPass(tempPassId);
            }
        }
    };

    /**
     * Go back to main Activity
     * @return
     */
    public Button.OnClickListener backButtonListener() {
        return v -> {
            Intent intent = new Intent(LoginTempPassActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, intent);
            finish();
        };
    }

    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tempPassId = etTempPassId.getText().toString();
            if (tempPassId == null || tempPassId.equals("")) {
                // Make sure user has inputted a tempPassId
                Toast.makeText(LoginTempPassActivity.this, getString(R.string.temppass_id_empty), Toast.LENGTH_SHORT).show();
            } else {
                loginTempPass(tempPassId);
            }
        }
    };

    @SuppressLint("CheckResult")
    private void loginTempPass(String tempPassId) {
        sharedPreferences = getSharedPreferences();
        String rId = sharedPreferences.getString(MainActivity.sharedPrefKeys.REQUESTOR_ID.toString(), "");

        adobeClientless.loginTempPass(rId, tempPassId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        adobeAuth -> {
                            // Temp pass login success
                            String tempPassMvpd = adobeAuth.getAuthNToken().getMvpd();
                            // Expire date returned is in epoch time format
                            String tempPassExpire = adobeAuth.getAuthNToken().getExpires();
                            // Convert epoch to readable date format
                            Date expireDate = new Date(Long.parseLong(tempPassExpire));

                            // logs
                            Log.d(TAG, "TEMPPASSLOGIN: SUCCESS");
                            Log.d(TAG, "TEMPPASSLOGIN: MVPD = " + tempPassMvpd);
                            Log.d(TAG, "TEMPPASSLOGIN: EXPIRE = " + expireDate);
                            addToLogcat(TAG, "TempPass Expire Date = " + expireDate);
                            addToLogcat(TAG, "TempPass Mvpd = " + tempPassMvpd);
                            addToLogcat(TAG, "TempPassId = " + tempPassId);
                            addToLogcat(TAG, "TEMPPASS LOGIN SUCCESS");

                            loginSuccess(tempPassMvpd);

                        },
                        throwable -> {
                            // Temp pass error
                            Log.d(TAG, "TEMPPASSLOGIN: ERROR " + throwable.toString());

                            String failMessage = getString(R.string.temppass_fail_msg) + "\n\n" + throwable.toString();
                            alertDialog(getString(R.string.temppass_fail_title), failMessage);

                            addToLogcat(TAG, "TEMPPASS ERROR: " + throwable.toString());

                        });

    }

    /**
     * Save temp pass id in saved preferences. A dialog will pop up to indicate success.
     * @param tempPassMvpd
     */
    private void loginSuccess(String tempPassMvpd) {
        updateStatus(tempPassMvpd);

        Toast.makeText(this, "Temporary Pass Id Saved", Toast.LENGTH_SHORT).show();
        alertDialogBackButtonMainActivty(getString(R.string.temppass_success_title), getString(R.string.temppass_success_msg), this);
    }

    /**
     * Visually update temp pass id and status and save in shared preferences
     * @param tempPassMvpd
     */
    private void updateStatus(String tempPassMvpd) {
        // Save temp pass mvpd in shared preferences
        sharedPreferences = getSharedPreferences();
        String key = LoginStatus.TEMPPASS_ID.toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, tempPassMvpd);
        editor.apply();
    }

    /**
     * Modified from NBC Sports App.
     * Resets Temp Pass timer for short and long temp passes. Will work for both expired
     * temp pass and ongoing temp pass.
     * @param tempPassId
     */
    private void resetTempPass(String tempPassId) {
        String targetUrl = RELEASE_URL;
        String deviceId = getDeviceId();
        String requestorId = getRId();

        String url = String.format("https://%s%s?device_id=%s&requestor_id=%s&mvpd_id=%s", targetUrl,
                RESET_SERVICE, deviceId, requestorId, tempPassId);
        Request request = new Request.Builder()
                .delete()
                .url(url)
                .header("apiKey", apiKey)
                .header("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                // Error when resetting temp pass
                runOnUiThread(() -> {
                    Toast.makeText(LoginTempPassActivity.this, getString(R.string.temppass_reset_fail), Toast.LENGTH_SHORT).show();
                    addToLogcat(TAG, "Reset Error: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                // Successful Reset
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        alertDialog(getString(R.string.temppass_reset_success_title),getString(R.string.temppass_reset_success_message));
                        addToLogcat(TAG, getString(R.string.temppass_reset_success_title));
                        // update temp pass status to OFF
                        updateStatus(getString(R.string.temppass_id_not_loggedIn));
                        // Update UI
                        tvTempPassStatus.setText(getString(R.string.temppass_OFF));
                        tvTempPassId.setText(getString(R.string.temppass_id_not_loggedIn));
                    } else {
                        Toast.makeText(LoginTempPassActivity.this, getString(R.string.temppass_reset_fail), Toast.LENGTH_SHORT).show();
                        addToLogcat(TAG, "Reset Error");
                    }
                });
            }
        });

    }

    private AdobeConfig getAdobeConfigFromJson() {
        return MainActivity.getAdobeConfigFromJson(getSharedPreferences());
    }

    private String getRId() {
        sharedPreferences = getSharedPreferences();
        String rIdKey = MainActivity.sharedPrefKeys.REQUESTOR_ID.toString();
        return sharedPreferences.getString(rIdKey, "");
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
    }

    private String getDeviceId() {
        return Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
