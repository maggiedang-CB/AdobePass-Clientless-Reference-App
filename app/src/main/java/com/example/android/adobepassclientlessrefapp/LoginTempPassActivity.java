package com.example.android.adobepassclientlessrefapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

    SharedPreferences sharedPreferences;
    AdobeConfig adobeConfig;
    AdobeClientlessService adobeClientless;

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

        this.adobeConfig = getAdobeConfigFromJson();
        this.adobeClientless = new AdobeClientlessService(this, adobeConfig, DeviceUtils.getDeviceInfo());

        loadSavedInfo();
    }

    private void loadSavedInfo() {
        sharedPreferences = getSharedPreferences();
        String mvpdKey = LoginStatus.TEMPPASS_ID.toString();
        String status = sharedPreferences.getString(mvpdKey, getString(R.string.temppass_id_not_loggedIn));

        if (!status.equals(getString(R.string.temppass_id_not_loggedIn))) {
            tvTempPassStatus.setText(getString(R.string.temppass_ON));
            tvTempPassId.setText(status);
        }

    }

    /**
     * Go back to main Activity
     * @return
     */
    public Button.OnClickListener backButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginTempPassActivity.this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
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
                            String tempPassExpire = adobeAuth.getAuthNToken().getExpires();

                            Log.d(TAG, "TEMPPASSLOGIN: SUCCESS");
                            Log.d(TAG, "TEMPPASSLOGIN: MVPD = " + tempPassMvpd);
                            Log.d(TAG, "TEMPPASSLOGIN: EXPIRE = " + tempPassExpire);


                            loginSuccess(tempPassMvpd);

                        },
                        throwable -> {
                            // TODO: Check temppass expired?

                            // No more temp pass
                            Log.d(TAG, "TEMPPASSLOGIN: ERROR " + throwable.toString());

                            String failMessage = getString(R.string.temppass_fail_msg) + "\n\n" + throwable.toString();
                            alertDialog(getString(R.string.temppass_fail_title), failMessage);
                        });

    }

    private void loginSuccess(String tempPassMvpd) {
        // Save temp pass mvpd
        sharedPreferences = getSharedPreferences();
        String key = LoginStatus.TEMPPASS_ID.toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, tempPassMvpd);
        editor.apply();

        Toast.makeText(this, "Temporary Pass Id Saved", Toast.LENGTH_SHORT).show();

        alertDialogBackButtonMainActivty(getString(R.string.temppass_success_title), getString(R.string.temppass_success_msg), this);

    }

    private AdobeConfig getAdobeConfigFromJson() {
        return MainActivity.getAdobeConfigFromJson(getSharedPreferences());
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
    }

}
