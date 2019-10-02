package com.example.android.adobepassclientlessrefapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isWifiConnected = NetworkUtils.isWifiConnected(context);
        if (!isWifiConnected) {
            Log.e("Network Receiver", " No Internet Connection");
            Toast.makeText(context, "Warning: No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

}
