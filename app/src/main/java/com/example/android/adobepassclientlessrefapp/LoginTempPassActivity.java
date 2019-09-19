package com.example.android.adobepassclientlessrefapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginTempPassActivity extends AbstractActivity {

    @BindView(R.id.logintemppass_requestorId)
    EditText etRId;
    @BindView(R.id.logintemppass_tempPassId)
    EditText etTempPassId;
    @BindView(R.id.btn_logintemppass_ok)
    Button okButton;
    @BindView(R.id.btn_logintemppass_back)
    Button backButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logintemppass_layout);
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
                Intent intent = new Intent(LoginTempPassActivity.this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        };
    }


}
