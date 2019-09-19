package com.example.android.adobepassclientlessrefapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthorizeActivity extends AbstractActivity {

    @BindView(R.id.btn_authorize_ok)
    Button okButton;
    @BindView(R.id.btn_authorize_back)
    Button backButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorize_layout);
        ButterKnife.bind(this);

        // Setup button listeners
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
                Intent intent = new Intent(AuthorizeActivity.this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        };
    }
}
