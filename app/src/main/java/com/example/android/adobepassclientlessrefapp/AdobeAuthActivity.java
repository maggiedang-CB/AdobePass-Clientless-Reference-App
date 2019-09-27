package com.example.android.adobepassclientlessrefapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;
import com.example.android.adobepassclientlessrefapp.adobeauth.GenerateSampleData;
import com.example.android.adobepassclientlessrefapp.utils.SetUpUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdobeAuthActivity extends AbstractActivity {

    private String TAG = "AdobeAuthActivity";

    // shared preference key to get adobeauth json
    public static String ADOBEAUTH = MainActivity.sharedPrefKeys.ADOBE_AUTH.toString();

    @BindView(R.id.btn_adobe_auth_back)
    Button backButton;
    @BindView(R.id.btn_adobe_auth_ok)
    Button saveButton;
    @BindView(R.id.btn_adobe_auth_generate)
    Button generateButton;
    // TODO: Get browse button to work
    // ^^ OR make it into a "Paste Json Data" button where user can put their own json data, then convert into the form
    @BindView(R.id.btn_adobe_auth_browse)
    Button browseButton;
    @BindView(R.id.btn_adobe_auth_clear)
    Button clearButton;

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
    @BindView(R.id.tempPassSelection)
    EditText tempPassSelection;

    //TODO: figure out temppass values usages when working with temp pass

    private ArrayList<EditText> listOfEditText;
    private HashMap<String, EditText> formHashMap;

    private SharedPreferences sharedPreferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adobe_auth_layout);
        ButterKnife.bind(this);

        // Hide keyboard on launch
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // list of edit text fields
        this.listOfEditText = getFormArray();
        // Hashmap of form where the key is the same as the key value in the json object
        this.formHashMap = getFormHashMap();

        // setup listeners
        backButton.setOnClickListener(backButtonListener());
        generateButton.setOnClickListener(generateListener);
        saveButton.setOnClickListener(saveListener);
        clearButton.setOnClickListener(clearListener);

        showLastSavedFormData();

    }

    /**
     * If there was saved data for adobe auth, fill out the form fields
     */
    private void showLastSavedFormData() {
        sharedPreferences = getSharedPreferences();
        SetUpUtils.showLastSavedFormData(sharedPreferences, ADOBEAUTH, formHashMap);
    }

    /**
     * Go back to main Activity
     * @return
     */
    private Button.OnClickListener backButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prompt user if they really want to go back if theres data un saved in form
                if (isUnsavedData()) {
                    alertDialog2Buttons("Unsaved Data", getString(R.string.auth_setup_unsaved_data),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(AdobeAuthActivity.this, MainActivity.class);
                                    setResult(RESULT_CANCELED, intent);
                                    finish();
                                }
                            });
                } else {
                    Intent intent = new Intent(AdobeAuthActivity.this, MainActivity.class);
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
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
            generateDataInEditText(sampleJson);

        }
    };

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: Check if each field has a valid input (i.e a number if int is wanted)

            if (!isAllFieldsFilled()) {
                // Not all fields have an input
                Toast.makeText(AdobeAuthActivity.this, "Error Saving: Field(s) Empty", Toast.LENGTH_SHORT).show();
            } else {
                // save value of each field
                String saveForm = convertFormToJson().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ADOBEAUTH, saveForm);
                editor.apply();

                // go back to main activity
                Intent intent = new Intent(AdobeAuthActivity.this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();

                // show toast that data has been saved
                Toast.makeText(AdobeAuthActivity.this, "Adobe Auth Settings Saved", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // clear all the edit text views
            SetUpUtils.clearForm(listOfEditText);
        }
    };

    /**
     * Convert the adobe auth form fields into a JSON object.
     * @return
     */
    private JSONObject convertFormToJson() {
        return SetUpUtils.convertFormToJson(formHashMap);
    }


    /**
     * Fills out edit text form with data contained in the adobe auth json object
     * @param json
     */
    private void generateDataInEditText(JSONObject json) {
        SetUpUtils.generateDataInEditText(json, formHashMap);
    }

    /**
     * Checks if every edit text field on the form is filled and not empty.
     * @return
     */
    private boolean isAllFieldsFilled() {
        return SetUpUtils.isAllFieldsFilled(listOfEditText);
    }

    /**
     * Returns true if there is unsaved data on the form when the user wants to press BACK.
     * @return
     */
    private boolean isUnsavedData() {
        sharedPreferences = getSharedPreferences();
        String adobeAuthKey = MainActivity.sharedPrefKeys.ADOBE_AUTH.toString();
        String currentForm = convertFormToJson().toString();
        return SetUpUtils.isUnsavedData(listOfEditText, sharedPreferences, adobeAuthKey, currentForm);
    }

    /**
     * Returns an array list of all the edit text views in the adobe auth setup form
     * @return
     */
    private ArrayList<EditText> getFormArray() {
        ArrayList<EditText> arrayForm = new ArrayList<>();
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
        arrayForm.add(tempPassSelection);

        return arrayForm;
    }

    /**
     * Hashmap containing the json key mapped to the form's edit text view displaying the key's value
     * @return
     */
    private HashMap<String, EditText> getFormHashMap() {
        HashMap<String, EditText> formHash = new HashMap<>();

        formHash.put(getString(R.string.auth1_baseUrl), baseUrl);
        formHash.put(getString(R.string.auth2_reggieCodePath), reggieCodePath);
        formHash.put(getString(R.string.auth3_checkAuthenticationPath), checkAuthenticationPath);
        formHash.put(getString(R.string.auth4_authnTokenTimeoutSeconds), authnTokenTimeoutSeconds);
        formHash.put(getString(R.string.auth5_tempPassProvider), tempPassProvider);
        formHash.put(getString(R.string.auth6_getUserMetadataPath), getUserMetadataPath);
        formHash.put(getString(R.string.auth7_authnTokenPath), authnTokenPath);
        formHash.put(getString(R.string.auth8_tempPassUrl), tempPassUrl);
        formHash.put(getString(R.string.auth9_authzTokenPath), authzTokenPath);
        formHash.put(getString(R.string.auth10_authorizePath), authorizePath);
        formHash.put(getString(R.string.auth11_logoutPath), logoutPath);
        formHash.put(getString(R.string.auth12_mvpdListPath), mvpdListPath);
        formHash.put(getString(R.string.auth13_redirectUrl), redirectUrl);
        formHash.put(getString(R.string.auth14_tokenizationUrl), tokenizationUrl);
        formHash.put(getString(R.string.auth15_logosUrl), logosUrl);
        formHash.put(getString(R.string.auth16_externalBrowserDomains), externalBrowserDomains);
        formHash.put(getString(R.string.auth17_nbcTokenUrl), nbcTokenUrl);
        formHash.put(getString(R.string.auth18_tempPassSelection), tempPassSelection);

        return formHash;
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
    }

}
