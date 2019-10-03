package com.example.android.adobepassclientlessrefapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.adobepassclientlessrefapp.fragments.ProviderDialogFragment;
import com.example.android.adobepassclientlessrefapp.ui.AuthenticationWebView;
import com.example.android.adobepassclientlessrefapp.utils.DeviceUtils;
import com.example.android.adobepassclientlessrefapp.utils.NetworkUtils;
import com.nbcsports.leapsdk.authentication.adobepass.AdobeClientlessService;
import com.nbcsports.leapsdk.authentication.adobepass.api.MvpdListAPI;
import com.nbcsports.leapsdk.authentication.adobepass.config.AdobeConfig;
import com.nbcsports.leapsdk.authentication.common.AdobeAuth;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * LoginActivity shows login status and allows user to select a MVPD (Provider) to login to.
 * MVPD information contained in https://api.auth.adobe.com/api/v1/config/nbcsports
 */
public class LoginActivity extends FragmentActivity {

    final String TAG = "LoginActivity";

    @BindView(R.id.login_description)
    TextView tvLoginDescription;
    @BindView(R.id.login_separator)
    TextView tvSeparator;
    @BindView(R.id.login_separator2)
    TextView tvSeparator2;
    @BindView(R.id.mvpd_provider_name)
    TextView tvProviderText;
    @BindView(R.id.login_provider)
    TextView tvProvider;
    @BindView(R.id.mvpd_value_name)
    TextView tvMvpdValueName;
    @BindView(R.id.mvpd_login_status)
    TextView tvLoginStatusText;
    @BindView(R.id.login_status)
    TextView tvLoginStatus;
    @BindView(R.id.btn_login_select_mvpd)
    Button btnSelectMvpd;
    @BindView(R.id.login_mvpd)
    TextView tvMvpdId;
    @BindView(R.id.btn_login_ok)
    Button okButton;
    @BindView(R.id.btn_login_back)
    Button backButton;
    @BindView(R.id.web_view)
    AuthenticationWebView webView;

    SharedPreferences sharedPreferences;
    AdobeConfig adobeConfig;
    AdobeClientlessService adobeClientless;

    /**
     * Shared Preference keys containing login information.
     * LOGIN_STATUS -> States if user is signed in or not
     * MVPD_NAME -> Contains the display name of the mvod
     * MVPD_ID -> Contains MVPD/Provider ID
     */
    public enum LoginStatus {
        LOGIN_STATUS, MVPD_NAME, MVPD_ID
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);

        // set listeners
        backButton.setOnClickListener(backButtonListener());
        okButton.setOnClickListener(okButtonListener());
        btnSelectMvpd.setOnClickListener(selectMvpdListener());

        this.adobeConfig = getAdobeConfigFromJson();
        this.adobeClientless = new AdobeClientlessService(this, adobeConfig, DeviceUtils.getDeviceInfo());

        // Load saved login data
        loadSavedLoginData();
    }

    /**
     * Collects saved login data from Shared Preferences.
     */
    private void loadSavedLoginData() {
        sharedPreferences = getSharedPreferences();
        String statusKey = LoginStatus.LOGIN_STATUS.toString();
        String mvpdNameKey = LoginStatus.MVPD_NAME.toString();
        String mvpdIdKey = LoginStatus.MVPD_ID.toString();

        tvLoginStatus.setText(sharedPreferences.getString(statusKey, getString(R.string.login_status_not_logged)));
        tvProvider.setText(sharedPreferences.getString(mvpdNameKey, getString(R.string.mvpd_id_not_selected)));
        tvMvpdId.setText(sharedPreferences.getString(mvpdIdKey, getString(R.string.mvpd_id_not_selected)));
    }

    /**
     * Go back to main Activity
     * @return
     */
    public Button.OnClickListener backButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        };
    }

    private View.OnClickListener okButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get value of mvpd id
                String mvpdId = tvMvpdId.getText().toString();

                if (mvpdId.equals(getString(R.string.mvpd_id_not_selected))) {
                    Toast.makeText(LoginActivity.this, "Please Select MVPD", Toast.LENGTH_SHORT).show();
                } else {
                    // Open login web page
                    openWebView(mvpdId);
                }

            }
        };
    }

    private View.OnClickListener selectMvpdListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!NetworkUtils.isWifiConnected(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, getString(R.string.no_internet_toast), Toast.LENGTH_SHORT).show();
                } else {
                    String rId = getSharedPreferences().getString(MainActivity.sharedPrefKeys.REQUESTOR_ID.toString(), "");
                    // Show mvpd dialog
                    printMvpdList(rId);
                }
            }
        };
    }

    /**
     * From the provider dialog fragment, set the value of the mvpd id and name on the UI for
     * visual purposes.
     * @param mvpd The selected MVPD / Provider
     */
    public void setMvpdIdSelected(MvpdListAPI.Mvpd mvpd) {
        tvMvpdId.setText(mvpd.getId());
        tvProvider.setText(mvpd.getDisplayName());
    }

    /**
     * Method extracted from Nbc Sports App, will launch the web view of the corresponding mvpd (provider).
     * @param mvpdId
     */
    @SuppressLint("CheckResult")
    private void openWebView(String mvpdId) {
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
        adobeClientless.login(mvpdId, sharedPreferences.getString(MainActivity.sharedPrefKeys.REQUESTOR_ID.toString(), ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        adobeAuth -> launchWebView(adobeAuth.getRedirectUrl()),
                        throwable -> {
                            String logMessage = "Webview Error: " + throwable.toString();
                            Log.d(TAG, logMessage);
                            addToLogcat(logMessage);
                            Toast.makeText(this, "Webview Error. See logcat.", Toast.LENGTH_SHORT).show();
                        });
    }

    private void launchWebView(final String adobeAuthRedirectUrl) {

        if (!adobeAuthRedirectUrl.contains("TempPass")) {
            webView.setVisibility(View.VISIBLE);
        }

        webView.setCallback(new AuthenticationWebView.Callback() {

            @Override
            public void onBack() { }

            @Override
            public void onComplete() {
                Log.d(TAG, "Login Success");
                addToLogcat("Current MvpdId =  " + tvMvpdId.getText().toString());
                addToLogcat("Current Provider = "+ tvProvider.getText().toString());
                addToLogcat("LOGIN SUCCESS");

                // Save login info
                tvLoginStatus.setText(getString(R.string.login_status_signed_in));
                saveLoginStatus();

                // If the user was on temp pass, logout of temp pass
                logoutTempPass();

                // Add some views back to page to state success
                tvLoginDescription.setVisibility(View.VISIBLE);
                tvLoginDescription.setText(R.string.login_success_message);
                backButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageStarted(String url) {
                addToLogcat("Login Url: " + url);
                // Hide Buttons and other UI
                tvLoginDescription.setVisibility(View.GONE);
                tvSeparator.setVisibility(View.GONE);
                tvSeparator2.setVisibility(View.GONE);
                btnSelectMvpd.setVisibility(View.GONE);
                tvMvpdValueName.setVisibility(View.GONE);
                tvMvpdId.setVisibility(View.GONE);
                okButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
                tvProvider.setVisibility(View.GONE);
                tvLoginStatus.setVisibility(View.GONE);
                tvProviderText.setVisibility(View.GONE);
                tvLoginStatusText.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished() {
                //progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(WebResourceResponse errorResponse) {
                Log.e(TAG, "Login Error: " + errorResponse.toString());
            }
        });
        webView.loadUrl(adobeAuthRedirectUrl);
    }

    @SuppressLint("CheckResult")
    private void printMvpdList(String rId) {

        Observable<AdobeAuth> mvpdListObservable = adobeClientless.getMpvdList(rId);

        mvpdListObservable.flatMap((Function<AdobeAuth, ObservableSource<List<MvpdListAPI.Mvpd>>>)
                adobeAuth -> Observable.just(adobeAuth.getMvpds()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mvpdList -> {
                    Log.d(TAG, "MVPD LIST = " + new ArrayList<>(mvpdList));

                    showProviderDialogFrag(new ArrayList<>(mvpdList));

                }, throwable -> {
                    // The Error most likely came from invalid adobe config data
                    Log.d(TAG, "getMvpdList subscribe Error: " + throwable.toString());
                    addToLogcat(throwable.toString());
                    Toast.makeText(this, getString(R.string.mvpdlist_login_error_msg), Toast.LENGTH_SHORT).show();
                });
    }

    private void showProviderDialogFrag(ArrayList mvpds) {
        ProviderDialogFragment fragment = ProviderDialogFragment.getInstance(mvpds);
        fragment.show(getSupportFragmentManager(), null);
    }

    private void saveLoginStatus() {
        String signedInStatus = getString(R.string.login_status_signed_in);
        String providerName = tvProvider.getText().toString();
        String mvpdId = tvMvpdId.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(LoginStatus.LOGIN_STATUS.toString(), signedInStatus);
        editor.putString(LoginStatus.MVPD_NAME.toString(), providerName);
        editor.putString(LoginStatus.MVPD_ID.toString(), mvpdId);

        editor.apply();
    }

    @SuppressLint("CheckResult")
    private void logoutTempPass() {
        sharedPreferences = getSharedPreferences();
        // Change status of temp pass
        String key = LoginTempPassActivity.LoginStatus.TEMPPASS_ID.toString();
        String offStatus = getString(R.string.temppass_id_not_loggedIn);

        if (isTempPass(sharedPreferences, offStatus)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, offStatus);
            editor.apply();
            // Perform logout
            String rId = sharedPreferences.getString(MainActivity.sharedPrefKeys.REQUESTOR_ID.toString(), "");
            adobeClientless.logout(rId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            adobeAuth -> {
                                Toast.makeText(LoginActivity.this,
                                        getString(R.string.temppass_log_off_msg), Toast.LENGTH_LONG).show();
                            },
                            throwable -> {
                                // Error when logging out temp pass.
                                Log.d(TAG, "TEMPPASS LOGOUT: ERROR");
                            });
        }

    }

    /**
     * Returns true if the user is logged in temp pass.
     * @param sharedPreferences
     * @param offStatus Default off status value for temp pass. (R.string.temppass_id_not_loggedIn)
     * @return
     */
    public static boolean isTempPass(SharedPreferences sharedPreferences, String offStatus) {
        String key = LoginTempPassActivity.LoginStatus.TEMPPASS_ID.toString();
        return sharedPreferences.contains(key) && !sharedPreferences.getString(key, offStatus).equals(offStatus);
    }

    private void addToLogcat(String logMessage) {
        sharedPreferences = getSharedPreferences();
        String logcatKey = MainActivity.sharedPrefKeys.LOGCAT.toString();
        MainActivity.addToLogcat(sharedPreferences, logcatKey, TAG, logMessage);
    }

    private AdobeConfig getAdobeConfigFromJson() {
        return MainActivity.getAdobeConfigFromJson(getSharedPreferences());
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
    }

}
