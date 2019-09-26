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

import com.example.android.adobepassclientlessrefapp.mediaInfo.GenerateSampleMediaInfo;
import com.example.android.adobepassclientlessrefapp.ui.AbstractActivity;
import com.example.android.adobepassclientlessrefapp.utils.SetUpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MediaInfoActivity extends AbstractActivity {

    public static String TAG = "MediaInfo Activity";
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
    private ArrayList<String> listOfValues;
    SharedPreferences sharedPreferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_info_layout);
        ButterKnife.bind(this);

        // Hide keyboard on launch
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        this.listOfEditText = getFormArray();
        this.listOfValues = getFormNamesArray();

        // setup buttons
        backButton.setOnClickListener(backButtonListener);
        saveButton.setOnClickListener(saveListener);
        clearButton.setOnClickListener(clearListener);
        generateButton.setOnClickListener(generateListener);

        showLastSavedFormData();
    }

    /**
     * If there was saved data for media info, fill out the form fields
     */
    private void showLastSavedFormData() {
        sharedPreferences = getSharedPreferences();
        SetUpUtils.showLastSavedFormData(sharedPreferences, MEDIA_INFO, listOfEditText, listOfValues);
    }

    private View.OnClickListener generateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSONObject sampleMediaInfoJson = GenerateSampleMediaInfo.makeSampleJsonObject();
            generateDataInEditText(sampleMediaInfoJson);
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
     * Convert the adobe auth form fields into a JSON object.
     * @return
     */
    private JSONObject convertFormToJson() {
        // Convert to JSONObject
        JSONObject json = new JSONObject();

        try {
            int index = 0;
            for (EditText editText : listOfEditText) {
                json.put(listOfValues.get(index), editText.getText().toString());
                index++;
            }
            // debug
            Log.d(TAG, "convertFormToJson: " + json.toString());

            return json;
        } catch (JSONException e) {}

        return json;
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

    private ArrayList<EditText> getFormArray() {
        ArrayList<EditText> arrayForm = new ArrayList<>();
        // Note: Do not change order of views added. They correspond to the order appeared on the form
        arrayForm.add(etPId);
        arrayForm.add(etStreamUrl);
        arrayForm.add(etRId);
        arrayForm.add(etChannel);
        arrayForm.add(etAssetId);
        arrayForm.add(etCdn);

        return arrayForm;
    }

    private ArrayList<String> getFormNamesArray() {
        ArrayList<String> formNames = new ArrayList<>();
        // Note: Do not change order of values added. They correspond to the order appeared on the form
        formNames.add(getString(R.string.mediaInfo1_pId));
        formNames.add(getString(R.string.mediaInfo2_streamUrl));
        formNames.add(getString(R.string.mediaInfo3_requestorId));
        formNames.add(getString(R.string.mediaInfo4_channel));
        formNames.add(getString(R.string.mediaInfo5_assetId));
        formNames.add(getString(R.string.mediaInfo6_cdn));

        return formNames;
    }

    /**
     * Fills out edit text form with data contained in the adobe auth json object
     * @param json
     */
    private void generateDataInEditText(JSONObject json) {
        SetUpUtils.generateDataInEditText(json, listOfEditText, listOfValues);
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
    }

}
