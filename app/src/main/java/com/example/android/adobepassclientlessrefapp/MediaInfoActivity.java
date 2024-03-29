package com.example.android.adobepassclientlessrefapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.adobepassclientlessrefapp.mediaInfo.GenerateSampleMediaInfo;
import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;
import com.example.android.adobepassclientlessrefapp.utils.SetUpUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity consists of a form to fill out a Media Info Json. The data is taken from any video
 * asset available in NBCSports App.
 * Live Data is found at http://api.leap.nbcsports.com/feed/NBCSports/all/replay/v1/ios
 */
public class MediaInfoActivity extends AbstractActivity {

    public static String TAG = "MediaInfo Activity";

    // Url to browse more similar data to fill out form
    private String liveDataUrl = "http://api.leap.nbcsports.com/feed/NBCSports/all/replay/v1/ios";

    // shared preference key to send media info json
    private static String MEDIA_INFO = MainActivity.sharedPrefKeys.MEDIA_INFO.toString();

    @BindView(R.id.btn_media_info_generate)
    Button generateButton;
    @BindView(R.id.btn_media_info_browse)
    Button browseButton;
    @BindView(R.id.btn_media_info_clear)
    Button clearButton;
    @BindView(R.id.btn_media_info_back)
    Button backButton;
    @BindView(R.id.btn_media_info_save)
    Button saveButton;

    @BindView(R.id.mediaInfo_pid)
    EditText etPId;
    @BindView(R.id.mediaInfo_streamurl)
    EditText etStreamUrl;
    @BindView(R.id.mediaInfo_requestorId)
    EditText etRId;
    @BindView(R.id.mediaInfo_channel)
    EditText etChannel;
    @BindView(R.id.mediaInfo_assetId)
    EditText etAssetId;
    @BindView(R.id.mediaInfo_cdn)
    EditText etCdn;

    private ArrayList<EditText> listOfEditText;
    private HashMap<String, EditText> formHashMap;
    SharedPreferences sharedPreferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_info_layout);
        ButterKnife.bind(this);

        // Hide keyboard on launch
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        this.listOfEditText = getFormArray();
        this.formHashMap = getFormHashMap();

        // setup buttons
        backButton.setOnClickListener(backButtonListener);
        saveButton.setOnClickListener(saveListener);
        clearButton.setOnClickListener(clearListener);
        generateButton.setOnClickListener(generateListener);
        browseButton.setOnClickListener(browseListener);

        showLastSavedFormData();
    }

    /**
     * If there was saved data for media info, fill out the form fields
     */
    private void showLastSavedFormData() {
        sharedPreferences = getSharedPreferences();
        SetUpUtils.showLastSavedFormData(sharedPreferences, MEDIA_INFO, formHashMap);
    }

    private View.OnClickListener generateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSONObject sampleMediaInfoJson = GenerateSampleMediaInfo.makeSampleJsonObject();
            generateDataInEditText(sampleMediaInfoJson);
        }
    };

    private View.OnClickListener browseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Clicking on browse live data button will open up a url containing live media info
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(liveDataUrl));
            startActivity(intent);
        }
    };

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: Check if each field has a valid input (i.e a number if int is wanted)

            if (!isAllFieldsFilled()) {
                // Not all fields have an input
                Toast.makeText(MediaInfoActivity.this, "Error Saving: Field(s) Empty", Toast.LENGTH_SHORT).show();
            } else {
                // save value of each field
                String saveForm = convertFormToJson().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(MEDIA_INFO, saveForm);
                editor.apply();

                // go back to main activity
                Intent intent = new Intent(MediaInfoActivity.this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();

                // show toast that data has been saved
                Toast.makeText(MediaInfoActivity.this, "Media Info Settings Saved", Toast.LENGTH_SHORT).show();
                addToLogcat(TAG, "Media Info Settings Saved");
            }
        }
    };

    /**
     * Go back to main Activity
     * @return
     */
    private Button.OnClickListener backButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prompt user if they really want to go back if theres data un saved in form
                if (isUnsavedData()) {
                    alertDialog2Buttons("Unsaved Data", getString(R.string.mediaInfo_unsaved_data),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MediaInfoActivity.this, MainActivity.class);
                                    setResult(RESULT_CANCELED, intent);
                                    finish();
                                }
                            });
                } else {
                    Intent intent = new Intent(MediaInfoActivity.this, MainActivity.class);
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            }
        };

    private View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SetUpUtils.clearForm(listOfEditText);
        }
    };


    /**
     * Convert the media info form fields into a JSON object.
     * @return
     */
    private JSONObject convertFormToJson() {
        return SetUpUtils.convertFormToJson(formHashMap);
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
        String mediaInfoKey = MainActivity.sharedPrefKeys.MEDIA_INFO.toString();
        String currentForm = convertFormToJson().toString();
        return SetUpUtils.isUnsavedData(listOfEditText, sharedPreferences, mediaInfoKey, currentForm);
    }

    /**
     * @return An array of edit text views taken from every field on the form
     */
    private ArrayList<EditText> getFormArray() {
        ArrayList<EditText> arrayForm = new ArrayList<>();
        arrayForm.add(etPId);
        arrayForm.add(etStreamUrl);
        arrayForm.add(etRId);
        arrayForm.add(etChannel);
        arrayForm.add(etAssetId);
        arrayForm.add(etCdn);

        return arrayForm;
    }

    /**
     * Return Hashmap where the key is the string value of the key presented in the JSON object
     * of mediaInfo, and the hashmap value is the edit text view for that key's field.
     * @return
     */
    private HashMap<String, EditText> getFormHashMap() {
        HashMap<String, EditText> formHash = new HashMap<>();

        formHash.put(getString(R.string.mediaInfo1_pId), etPId);
        formHash.put(getString(R.string.mediaInfo2_streamUrl), etStreamUrl);
        formHash.put(getString(R.string.mediaInfo3_requestorId), etRId);
        formHash.put(getString(R.string.mediaInfo4_channel), etChannel);
        formHash.put(getString(R.string.mediaInfo5_assetId), etAssetId);
        formHash.put(getString(R.string.mediaInfo6_cdn), etCdn);

        return formHash;
    }

    /**
     * Fills out edit text form with data contained in the media info json object
     * @param json
     */
    private void generateDataInEditText(JSONObject json) {
        SetUpUtils.generateDataInEditText(json, formHashMap);
    }

}
