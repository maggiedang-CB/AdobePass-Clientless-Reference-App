package com.example.android.adobepassclientlessrefapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.adobepassclientlessrefapp.MainActivity;
import com.example.android.adobepassclientlessrefapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Part of the action bar menu. Gives general description of what adobe pass clientless is.
 */
public class AboutClientlessActivity extends AbstractActivity {

    @BindView(R.id.btn_about_clientless_back)
    Button backButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_clientless_layout);
        ButterKnife.bind(this);

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
                Intent intent = new Intent(AboutClientlessActivity.this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        };
    }

}
