package com.example.android.adobepassclientlessrefapp.utils;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Common methods used for saving, editing, checking and setting up edit text forms
 * Used for setting up Adobe Auth and Media Info forms.
 */
public class SetUpUtils {

    private static String TAG = "SetUpUtils";

    /**
     * Fills out edit text form with data contained in the adobe auth json object
     * @param json
     * @param listOfEditText
     * @param listOfValues
     */
    public static void generateDataInEditText(JSONObject json, ArrayList<EditText> listOfEditText, ArrayList<String> listOfValues) {

        try {
            int index = 0;
            for (EditText editText : listOfEditText) {
                editText.setText(json.getString(listOfValues.get(index)));
                index++;
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
