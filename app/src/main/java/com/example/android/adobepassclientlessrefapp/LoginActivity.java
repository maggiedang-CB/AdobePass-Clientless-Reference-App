package com.example.android.adobepassclientlessrefapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AbstractActivity {

    @BindView(R.id.login_mvpd)
    EditText etMvpd;
    @BindView(R.id.btn_login_ok)
    Button okButton;
    @BindView(R.id.btn_login_back)
    Button backButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);

        // set listeners
        backButton.setOnClickListener(backButtonListener());
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

}
