package com.example.android.adobepassclientlessrefapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;
import com.example.android.adobepassclientlessrefapp.adobeauth.GenerateSampleData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdobeAuthActivity extends AbstractActivity {

    private String TAG = "AdobeAuthActivity";

    @BindView(R.id.btn_adobe_auth_back)
    Button backButton;
    @BindView(R.id.btn_adobe_auth_ok)
    Button okButton;
    @BindView(R.id.btn_adobe_auth_generate)
    Button generateButton;
    // TODO: Get browse button to work >>> Should lead to url opened in the web?
    @BindView(R.id.btn_adobe_auth_browse)
    Button browseButton;

    @BindView(R.id.baseurl)
    EditText baseUrl;
    @BindView(R.id.reggieCodePath)
    EditText reggieCodePath;
    @BindView(R.id.checkAuthenticationPath)
    EditText checkAuthenticationPath;
    @BindView(R.id.authnTokenTimeoutSeconds)
    EditText authnTokenTimeoutSeconds;
    @BindView(R.id.tempPassProvider)
    EditText tempPassProvider;
    @BindView(R.id.getUserMetadataPath)
    EditText getUserMetadataPath;
    @BindView(R.id.authnTokenPath)
    EditText authnTokenPath;
    @BindView(R.id.temppassurl)
    EditText tempPassUrl;
    @BindView(R.id.authzTokenPath)
    EditText authzTokenPath;
    @BindView(R.id.authorizePath)
    EditText authorizePath;
    @BindView(R.id.logoutPath)
    EditText logoutPath;
    @BindView(R.id.mvpdListPath)
    EditText mvpdListPath;
    @BindView(R.id.redirecturl)
    EditText redirectUrl;
    @BindView(R.id.tokenizationurl)
    EditText tokenizationUrl;
    @BindView(R.id.logosurl)
    EditText logosUrl;
    @BindView(R.id.externalBrowserDomains)
    EditText externalBrowserDomains;
    @BindView(R.id.nbcTokenurl)
    EditText nbcTokenUrl;

    //TODO: figure out tempPassSelection when working with temp pass

    // List of key values
    private ArrayList<String> listOfValues;
    // list of edit text form
    private ArrayList<EditText> listOfEditText;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adobe_auth_layout);
        ButterKnife.bind(this);

        // Hide keyboard on launch
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // List of key values
        this.listOfValues = getFormNamesArray();
        // list of edit text form
        this.listOfEditText = getFormArray();

        // setup listeners
        backButton.setOnClickListener(backButtonListener());
        generateButton.setOnClickListener(generateListener);
    }

    /**
     * Go back to main Activity
     * @return
     */
    private Button.OnClickListener backButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: prompt user if they really want to go back if theres data un saved in form
                Intent intent = new Intent(AdobeAuthActivity.this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        };
    }

    private Button.OnClickListener generateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG,"Clicked generate button");
            // Uses stored sample data needed for adobe auth
            JSONObject sampleJson = GenerateSampleData.makeJsonSample();
            // Add sample data to edit text views
            generateSampleDataInEditText(sampleJson);

        }
    };

    /**
     * Fills out edit text form with the sample data generated from GenerateSampleData.java
     * @param json
     */
    private void generateSampleDataInEditText(JSONObject json) {

        try {
            int index = 0;
            for (EditText editText : listOfEditText) {
                editText.setText(json.getString(listOfValues.get(index)));

                // debug
                Log.d(TAG, "listOfvalue : " + listOfValues.get(index));
                Log.d(TAG, "edit text form text: " + listOfEditText.get(index).getText());

                index++;
            }
        } catch (JSONException e){
            Log.d(TAG, "Error generating text to adobe auth form");
        }
    }

    /**
     * Returns an array list of all the edit text views in the adobe auth setup form
     * @return
     */
    private ArrayList<EditText> getFormArray() {
        ArrayList<EditText> arrayForm = new ArrayList<>();
        // Note: Do not change order of views added. They correspond to the order appeared on the form
        arrayForm.add(baseUrl);
        arrayForm.add(reggieCodePath);
        arrayForm.add(checkAuthenticationPath);
        arrayForm.add(authnTokenTimeoutSeconds);
        arrayForm.add(tempPassProvider);
        arrayForm.add(getUserMetadataPath);
        arrayForm.add(authnTokenPath);
        arrayForm.add(tempPassUrl);
        arrayForm.add(authzTokenPath);
        arrayForm.add(authorizePath);
        arrayForm.add(logoutPath);
        arrayForm.add(mvpdListPath);
        arrayForm.add(redirectUrl);
        arrayForm.add(tokenizationUrl);
        arrayForm.add(logosUrl);
        arrayForm.add(externalBrowserDomains);
        arrayForm.add(nbcTokenUrl);

        return arrayForm;
    }

    /**
     * Returns a list of strings containing the name of each form value.
     * i.e) "tempPassUrl"
     * @return
     */
    private ArrayList<String> getFormNamesArray() {
        ArrayList<String> formNames = new ArrayList<>();

        formNames.add(this.getString(R.string.auth1_baseUrl));
        formNames.add(this.getString(R.string.auth2_reggieCodePath));
        formNames.add(this.getString(R.string.auth3_checkAuthenticationPath));
        formNames.add(this.getString(R.string.auth4_authnTokenTimeoutSeconds));
        formNames.add(this.getString(R.string.auth5_tempPassProvider));
        formNames.add(this.getString(R.string.auth6_getUserMetadataPath));
        formNames.add(this.getString(R.string.auth7_authnTokenPath));
        formNames.add(this.getString(R.string.auth8_tempPassUrl));
        formNames.add(this.getString(R.string.auth9_authzTokenPath));
        formNames.add(this.getString(R.string.auth10_authorizePath));
        formNames.add(this.getString(R.string.auth11_logoutPath));
        formNames.add(this.getString(R.string.auth12_mvpdListPath));
        formNames.add(this.getString(R.string.auth13_redirectUrl));
        formNames.add(this.getString(R.string.auth14_tokenizationUrl));
        formNames.add(this.getString(R.string.auth15_logosUrl));
        formNames.add(this.getString(R.string.auth16_externalBrowserDomains));
        formNames.add(this.getString(R.string.auth17_nbcTokenUrl));
        formNames.add(this.getString(R.string.auth18_tempPassSelection));

        return formNames;
    }


}
