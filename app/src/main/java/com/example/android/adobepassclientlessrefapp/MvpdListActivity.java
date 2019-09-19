package com.example.android.adobepassclientlessrefapp;

import android.os.Bundle;

import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;

public class MvpdListActivity extends AbstractActivity {
    Bundle extras;
    private String rId;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.extras = getIntent().getExtras();
        this.rId = extras.getString("requestorId");

    }
}
