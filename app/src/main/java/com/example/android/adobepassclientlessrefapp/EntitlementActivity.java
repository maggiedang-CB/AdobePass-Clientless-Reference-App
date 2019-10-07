package com.example.android.adobepassclientlessrefapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.adobepassclientlessrefapp.entitlement.GenerateEntitlementSample;
import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;
import com.example.android.adobepassclientlessrefapp.utils.SetUpUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Entitlement Activity containing a form to set up all the required entitlement parameters.
 * <p>
 *  NOTE: To better understand what each value means and where the values are taken from,
 *  look at class EntitlementParams.java in the NBC Sports App.
 * </p>
 */
public class EntitlementActivity extends AbstractActivity {

    public static String TAG = "EntitlementActivity";

    //TODO: Pop up dialog saying where/how the generated data is taken from when generate is pressed

    @BindView(R.id.btn_entitlement_generate)
    Button generateButton;
    @BindView(R.id.btn_entitlement_clear)
    Button clearButton;
    @BindView(R.id.btn_entitlement_save)
    Button saveButton;
    @BindView(R.id.btn_entitlement_back)
    Button backButton;

    @BindView(R.id.entitlement_zipCode)
    EditText etZipCode;
    @BindView(R.id.entitlement_blackoutId)
    EditText etBlackoutId;
    @BindView(R.id.entitlement_mlbBlackoutServiceurl)
    EditText etMlbBlackoutServiceUrl;
    @BindView(R.id.entitlement_entitlementId)
    EditText etEntitlementId;
    @BindView(R.id.entitlement_requiresEntitlementRightsCheck)
    EditText etRequiresEntitlementRightsCheck;
    @BindView(R.id.entitlement_useAnvatoService)
    EditText etUseAnvatoService;
    @BindView(R.id.entitlement_comcast)
    EditText etComcast;
    @BindView(R.id.entitlement_currentChannel)
    EditText etCurrentChannel;
    @BindView(R.id.entitlement_currentChannelMvpdDigital)
    EditText etCurrentChannelMvpdDigital;
    @BindView(R.id.entitlement_currentChannelMvpdDigitalContainsMvpd)
    EditText etCurrentChannelMvpdDigitalContainsMvpd;
    @BindView(R.id.entitlement_blackoutServiceurl)
    EditText etBlackoutServiceUrl;
    @BindView(R.id.entitlement_anvatoDomain)
    EditText etAnvatoDomain;
    @BindView(R.id.entitlement_anvatoChannelDomain)
    EditText etAnvatoChannelDomain;
    @BindView(R.id.entitlement_anvatoAnvack)
    EditText etAnvatoAnvack;


    ArrayList<EditText> listOfEditText;
    HashMap<String, EditText> formHashMap;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entitlement_setup_layout);
        ButterKnife.bind(this);

        // Hide keyboard on launch
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // setup buttons
        generateButton.setOnClickListener(generateListener);
        clearButton.setOnClickListener(clearListener);
        saveButton.setOnClickListener(saveListener);
        backButton.setOnClickListener(backListener);

        this.listOfEditText = getFormArray();
        this.formHashMap = getFormHash();
    }


    private View.OnClickListener generateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSONObject json = GenerateEntitlementSample.makeSampleJsonObject();
            Log.d(TAG, "Entitlement Json: " + json.toString());
            SetUpUtils.generateDataInEditText(json, formHashMap);
        }
    };

    private View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SetUpUtils.clearForm(listOfEditText);
        }
    };

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };


    private HashMap<String, EditText> getFormHash() {
        HashMap<String, EditText> formHash = new HashMap<>();

        formHash.put(getString(R.string.entitlement_zipCode), etZipCode);
        formHash.put(getString(R.string.entitlement_blackoutId), etBlackoutId);
        formHash.put(getString(R.string.entitlement_mlbBlackoutServiceUrl), etMlbBlackoutServiceUrl);
        formHash.put(getString(R.string.entitlement_entitlementId), etEntitlementId);
        formHash.put(getString(R.string.entitlement_requiresEntitlementRightsCheck), etRequiresEntitlementRightsCheck);
        formHash.put(getString(R.string.entitlement_useAnvatoService), etUseAnvatoService);
        formHash.put(getString(R.string.entitlement_comcast), etComcast);
        formHash.put(getString(R.string.entitlement_currentChannel), etCurrentChannel);
        formHash.put(getString(R.string.entitlement_currentChannelMvpdDigital), etCurrentChannelMvpdDigital);
        formHash.put(getString(R.string.entitlement_currentChannelMvpdDigitalContainsMvpd), etCurrentChannelMvpdDigitalContainsMvpd);
        formHash.put(getString(R.string.entitlement_blackoutServiceUrl), etBlackoutServiceUrl);
        formHash.put(getString(R.string.entitlement_anvatoDomain), etAnvatoDomain);
        formHash.put(getString(R.string.entitlement_anvatoChannelDomain), etAnvatoChannelDomain);
        formHash.put(getString(R.string.entitlement_anvatoAnvack), etAnvatoAnvack);


        return formHash;
    }

    /**
     * @return An array of edit text views taken from every field on the form
     */
    private ArrayList<EditText> getFormArray() {
        ArrayList<EditText> arrayForm = new ArrayList<>();

        arrayForm.add(etZipCode);
        arrayForm.add(etBlackoutId);
        arrayForm.add(etMlbBlackoutServiceUrl);
        arrayForm.add(etEntitlementId);
        arrayForm.add(etRequiresEntitlementRightsCheck);
        arrayForm.add(etUseAnvatoService);
        arrayForm.add(etComcast);
        arrayForm.add(etCurrentChannel);
        arrayForm.add(etCurrentChannelMvpdDigital);
        arrayForm.add(etCurrentChannelMvpdDigitalContainsMvpd);
        arrayForm.add(etBlackoutServiceUrl);
        arrayForm.add(etAnvatoDomain);
        arrayForm.add(etAnvatoChannelDomain);
        arrayForm.add(etAnvatoAnvack);

        return arrayForm;
    }

}
