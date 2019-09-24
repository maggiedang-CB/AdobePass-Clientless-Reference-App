package com.example.android.adobepassclientlessrefapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceResponse;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;
import com.example.android.adobepassclientlessrefapp.ui.AuthenticationWebView;
import com.example.android.adobepassclientlessrefapp.utils.DeviceUtils;
import com.nbcsports.leapsdk.authentication.adobepass.AdobeClientlessService;
import com.nbcsports.leapsdk.authentication.adobepass.config.AdobeConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AbstractActivity {

    final String TAG = "LoginActivity";

    @BindView(R.id.login_description)
    TextView tvLoginDescription;
    @BindView(R.id.login_separator)
    TextView tvSeparator;
    @BindView(R.id.mvpd_value_name)
    TextView tvMvpdValueName;

    @BindView(R.id.login_mvpd)
    EditText etMvpd;
    @BindView(R.id.btn_login_ok)
    Button okButton;
    @BindView(R.id.btn_login_back)
    Button backButton;
    @BindView(R.id.web_view)
    AuthenticationWebView webView;

    SharedPreferences sharedPreferences;
    AdobeConfig adobeConfig;
    AdobeClientlessService adobeClientless;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);

        // set listeners
        backButton.setOnClickListener(backButtonListener());
        okButton.setOnClickListener(okButtonListener());

        this.adobeConfig = getAdobeConfigFromJson();
        this.adobeClientless = new AdobeClientlessService(this, adobeConfig, DeviceUtils.getDeviceInfo());
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
                String mvpdId = etMvpd.getText().toString();
                // Check if an mvpd Id has been entered and is valid. Show toast if not.
                if (mvpdId.equals("")) {

                }

                // Open login web page
                openWebView(mvpdId);
            }
        };
    }

    @SuppressLint("CheckResult")
    private void openWebView(String mvpdId) {
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
        adobeClientless.login(mvpdId, sharedPreferences.getString("rId", ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        adobeAuth -> launchWebView(adobeAuth.getRedirectUrl()),
                        throwable -> {});
    }

    private void launchWebView(final String adobeAuthRedirectUrl) {

        if (!adobeAuthRedirectUrl.contains("TempPass")) {
            webView.setVisibility(View.VISIBLE);
            //navigateMvpdUrl = adobeAuthRedirectUrl;
        }

        webView.setCallback(new AuthenticationWebView.Callback() {

            @Override
            public void onBack() { }

            @Override
            public void onComplete() {
                Log.d(TAG, "Login Success");
//                if (asset != null) {
//                    adobePassService.authorize(asset, null);
//                } else {
//                    finish();
//                }
            }

            @Override
            public void onPageStarted(String url) {
                // Hide Buttons and other UI
                tvLoginDescription.setVisibility(View.GONE);
                tvSeparator.setVisibility(View.GONE);
                tvMvpdValueName.setVisibility(View.GONE);
                etMvpd.setVisibility(View.GONE);
                okButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
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


    private AdobeConfig getAdobeConfigFromJson() {
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
        return MainActivity.getAdobeConfigFromJson(sharedPreferences);
    }

}
