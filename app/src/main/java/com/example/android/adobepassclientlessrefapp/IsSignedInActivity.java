package com.example.android.adobepassclientlessrefapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IsSignedInActivity extends AbstractActivity {

    @BindView(R.id.btn_isSignedIn_back)
    Button backButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // change layout
        setContentView(R.layout.issignedin_layout);
        ButterKnife.bind(this);

        // Button listeners
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
                Intent intent = new Intent(IsSignedInActivity.this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        };
    }
}
