/*************************************************************************
 * ADOBE SYSTEMS INCORPORATED
 * Copyright 2013 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the
 * terms of the Adobe license agreement accompanying it.  If you have received this file from a
 * source other than Adobe, then your use, modification, or distribution of it requires the prior
 * written permission of Adobe.
 *
 * For the avoidance of doubt, this file is Documentation under the Agreement.
 ************************************************************************/

package com.example.android.adobepassclientlessrefapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.android.adobepassclientlessrefapp.LoginTempPassActivity;
import com.example.android.adobepassclientlessrefapp.MainActivity;

/**
 * Taken and modified from Adobe AccessEnabler Demo App
 */
public class AbstractActivity extends Activity {
    private static final String LOG_TAG = "AbstractActivity";

    protected void trace(String logTag, String msg) {
        Log.d(logTag, msg);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Simple alert dialog where the OK button will return the user back to the current activity
     * @param title
     * @param message
     */
    protected void alertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Back to current activity
            }
        });

        AlertDialog alert = builder.create();

        alert.show();
        alert.getWindow().setLayout(900, 700);
    }

    /**
     * Alert dialog where the OK button will return the user back to the MainActivity
     * @param title
     * @param message
     */
    protected void alertDialogBackButtonMainActivty(String title, String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Go back to MainActivity
                Intent intent = new Intent(context, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        AlertDialog alert = builder.create();

        alert.show();
        alert.getWindow().setLayout(900, 700);
    }

    /**
     * Set up an alert dialog where the passed in click listener is used for the "YES" button and
     * "NO" returns user back to the current activity.
     * @param title
     * @param message
     * @param onClickListener
     */
    protected void alertDialog2Buttons(String title, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("YES", onClickListener);
        builder.setNeutralButton("NO" , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Back to current activity
            }
        });

        AlertDialog alert = builder.create();

        alert.show();
        alert.getWindow().setLayout(900, 700);
    }

    /**
     * Simplified method to add new logs saved in shared preferences. The logs will be outputted
     * in the logcat view in MainActivity.
     * @param TAG Usually the name of the activity log was called in
     * @param logMessage
     */
    protected void addToLogcat(String TAG, String logMessage) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
        String logKey = MainActivity.sharedPrefKeys.LOGCAT.toString();

        MainActivity.addToLogcat(sharedPreferences, logKey, TAG, logMessage);
    }
    protected void addToLogcat(String logMessage) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
        String logKey = MainActivity.sharedPrefKeys.LOGCAT.toString();

        MainActivity.addToLogcat(sharedPreferences, logKey, "", logMessage);
    }

}
