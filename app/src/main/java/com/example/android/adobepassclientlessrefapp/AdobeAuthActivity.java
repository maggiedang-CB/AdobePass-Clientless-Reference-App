package com.example.android.adobepassclientlessrefapp;

import android.os.Bundle;
import android.view.WindowManager;

import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;

import butterknife.ButterKnife;

public class AdobeAuthActivity extends AbstractActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adobe_auth_layout);
        ButterKnife.bind(this);

        // Hide keyboard on launch
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

}
