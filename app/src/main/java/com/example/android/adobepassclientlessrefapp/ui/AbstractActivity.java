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
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

public class AbstractActivity extends Activity {
    private static final String LOG_TAG = "AbstractActivity";

    protected void trace(String logTag, String msg) {
        Log.d(logTag, msg);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void alertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(LOG_TAG + "#alert", "OK");
            }
        });

        AlertDialog alert = builder.create();

        alert.show();
        alert.getWindow().setLayout(600, 800);
    }
}
