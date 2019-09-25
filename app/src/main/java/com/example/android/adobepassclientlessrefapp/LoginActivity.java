package com.example.android.adobepassclientlessrefapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.adobepassclientlessrefapp.fragments.ProviderDialogFragment;
import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;
import com.example.android.adobepassclientlessrefapp.ui.AuthenticationWebView;
import com.example.android.adobepassclientlessrefapp.utils.DeviceUtils;
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

public class LoginActivity extends FragmentActivity {

    final String TAG = "LoginActivity";

    @BindView(R.id.login_description)
    TextView tvLoginDescription;
    @BindView(R.id.login_separator)
    TextView tvSeparator;
    @BindView(R.id.mvpd_value_name)
    TextView tvMvpdValueName;


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
                String rId = getSharedPreferences().getString(MainActivity.sharedPrefKeys.REQUESTOR_ID.toString(), "");
                // Show mvpd dialog
                printMvpdList(rId);
            }
        };
    }

    /**
     * From the provider dialog fragment, set the value of the mvpd id on the UI for visual purposes.
     * @param mvpd The selected MVPD / Provider
     */
    public void setMvpdIdSelected(MvpdListAPI.Mvpd mvpd) {
        tvMvpdId.setText(mvpd.getId());
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
                // TODO: Do stuff here on success
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
                btnSelectMvpd.setVisibility(View.GONE);
                tvMvpdValueName.setVisibility(View.GONE);
                tvMvpdId.setVisibility(View.GONE);
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

                });
    }

    private void showProviderDialogFrag(ArrayList mvpds) {
        ProviderDialogFragment fragment = ProviderDialogFragment.getInstance(mvpds);
        fragment.show(getSupportFragmentManager(), null);
    }

    private AdobeConfig getAdobeConfigFromJson() {
        return MainActivity.getAdobeConfigFromJson(getSharedPreferences());
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
    }

}
