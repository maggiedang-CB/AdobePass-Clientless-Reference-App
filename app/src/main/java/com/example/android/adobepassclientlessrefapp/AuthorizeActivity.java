package com.example.android.adobepassclientlessrefapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;
import com.example.android.adobepassclientlessrefapp.utils.DeviceUtils;
import com.nbcsports.leapsdk.authentication.adobepass.AdobeClientlessService;
import com.nbcsports.leapsdk.authentication.adobepass.config.AdobeConfig;
import com.nbcsports.leapsdk.authentication.common.AdobeAuth;
import com.nbcsports.leapsdk.authentication.common.AdobeMediaInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

//TODO: Delete if not in use
public class AuthorizeActivity extends AbstractActivity {

    public static String TAG = "AuthorizeActivity";

    @BindView(R.id.btn_authorize_back)
    Button backButton;

    AdobeConfig adobeConfig;
    AdobeClientlessService adobeClientless;
    AdobeMediaInfo adobeMediaInfo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorize_layout);
        ButterKnife.bind(this);

        // Setup button listeners
        backButton.setOnClickListener(backButtonListener());

        this.adobeConfig = getAdobeConfigFromJson();
        this.adobeClientless = new AdobeClientlessService(this, adobeConfig, DeviceUtils.getDeviceInfo());
        this.adobeMediaInfo = getAdobeMediaInfo();

        authorize();

    }

    @SuppressLint("CheckResult")
    private void authorize() {
        AdobeAuth adobeAuth = new AdobeAuth();

        adobeClientless.authorize(adobeAuth, adobeMediaInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(auth -> {
                    Log.d(TAG, "AUTHORIZE SUCCESS");
                }, throwable -> {
                    Log.e(TAG, "AUTHORIZE FAILURE");
                    // Check for 2 types of authz errors
                });
    }


    /**
     * Go back to main Activity
     * @return
     */
    public Button.OnClickListener backButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthorizeActivity.this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        };
    }

    private AdobeConfig getAdobeConfigFromJson() {
        return MainActivity.getAdobeConfigFromJson(getSharedPreferences());
    }

    private AdobeMediaInfo getAdobeMediaInfo() {
        return MainActivity.getMediaInfoFromJson(getSharedPreferences());
    }

}
