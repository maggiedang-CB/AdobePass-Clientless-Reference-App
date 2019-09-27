package com.example.android.adobepassclientlessrefapp.utils;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Common methods used for saving, editing, checking and setting up edit text forms.
 * Used for setting up Adobe Auth and Media Info forms.
 */
public class SetUpUtils {

    private static String TAG = "SetUpUtils";

    /**
     * If there was saved data for the current form activity, fill out the form fields.
     * @param sharedPreferences
     * @param prefKey Shared Preference key containing saved data of the form
     * @param formData Hashmap containing the json key mapped to the form's edit text view of the key's value
     */
    public static void showLastSavedFormData(SharedPreferences sharedPreferences, String prefKey, HashMap<String, EditText> formData) {
        try {
            if (sharedPreferences.contains(prefKey)) {
                JSONObject json = new JSONObject(sharedPreferences.getString(prefKey, ""));

                // debug
                Log.d(TAG, "Saved Data Json: " + json.toString());

                // show in edit text forms
                generateDataInEditText(json, formData);
            }
        } catch (JSONException e) {
            Log.d(TAG, "Error obtaining shared preference media info json");
        }
    }

    /**
     * Convert the media info form fields into a JSON object.
     * @return
     */
    public static JSONObject convertFormToJson(HashMap<String, EditText> formHashMap) {
        // Convert to JSONObject
        JSONObject json = new JSONObject();

        try {
            for (Map.Entry field : formHashMap.entrySet()) {
                String jsonKey = field.getKey().toString();
                EditText jsonValue = (EditText) field.getValue();

                json.put(jsonKey, jsonValue.getText().toString());
            }
            // debug
            Log.d(TAG, "convertFormToJson: " + json.toString());

            return json;
        } catch (JSONException e) {}

        return json;
    }


    /**
     * Fills out edit text form with data contained in the json object
     * @param json Contains the form's data as a json object
     * @param formData Hashmap of json keys mapped to its corresponding edit text view on the form
     */
    public static void generateDataInEditText(JSONObject json, HashMap<String, EditText> formData) {

        try {
            for (Map.Entry field : formData.entrySet()) {
                String jsonKey = field.getKey().toString();
                EditText editText = (EditText) field.getValue();

                editText.setText(json.getString(jsonKey));
            }
        } catch (JSONException e){
            Log.d(TAG, "Error generating text to edit text form");
        }
    }

    /**
     * Checks if every edit text field on the form is filled and not empty.
     * @param listOfEditText
     * @return
     */
    public static boolean isAllFieldsFilled(ArrayList<EditText> listOfEditText) {
        for (EditText et : listOfEditText) {
            if (et.getText() == null || et.getText().toString().equals("")) {
                // A field is empty
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if there is unsaved data on the form when the user wants to press BACK.
     * @param listOfEditText Array of edit text form
     * @param sharedPreferences Shared preferences
     * @param prefKey The shared preference key name to save the form data
     * @param currentFormJson The json string value of the current form (Modified or not modified)
     * @return
     */
    public static boolean isUnsavedData(ArrayList<EditText> listOfEditText, SharedPreferences sharedPreferences,
                                  String prefKey, String currentFormJson) {
        if (!sharedPreferences.contains(prefKey) && isAllFieldsFilled(listOfEditText)) {
            // No instance of adobe auth data has been saved before
            return true;
        } else if (sharedPreferences.contains(prefKey)
                && !sharedPreferences.getString(prefKey, "").equals(currentFormJson) && isAllFieldsFilled(listOfEditText)){
            // Current fields have been modified
            return true;
        }
        return false;
    }

    /**
     * Deletes all current edit text fields and replaces it with empty string.
     * @param listOfEditText
     */
    public static void clearForm(ArrayList<EditText> listOfEditText) {
        for (EditText editText : listOfEditText) {
            editText.setText("");
        }
    }

}
