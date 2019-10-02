package com.example.android.adobepassclientlessrefapp.utils;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Taken from NBC Sports app
 */
public class DeviceUtils {

    // Sample device info (Samsung S7 Edge)
    public static String SAMPLE_DEVICE_INFO = "eyJtb2RlbCI6IlNNLUc5MzVXOCIsInZlbmRvciI6InNhbXN1" +
            "bmciLCJtYW51ZmFjdHVyZXIiOiJzYW1zdW5nIiwib3NOYW1lIjoiQW5kcm9pZCIsIm9zVmVuZG9yIj" +
            "oiR29vZ2xlIiwib3NWZXJzaW9uIjoiNy4wIiwiYnJvd3NlclZlbmRvciI6Ikdvb2dsZSIsImJyb3dzZXJO" +
            "YW1lIjoiQ2hyb21lIiwidmVyc2lvbiI6Imhlcm8ybHRlYm1jIn0=";

    public static String getDeviceInfo() {
        String LOGGING_TAG = "DeviceUtils.class";
        JSONObject di = new JSONObject();
        try {
            di.put("model", Build.MODEL);
            di.put("vendor", Build.BRAND);
            di.put("manufacturer", Build.MANUFACTURER);
            di.put("osName", "Android");
            di.put("osVendor", "Google");
            di.put("osVersion", Build.VERSION.RELEASE);
            di.put("browserVendor", "Google");
            di.put("browserName", "Chrome");
            di.put("version", Build.DEVICE);

        } catch (JSONException e) {
            Log.e(LOGGING_TAG, e.getMessage());
        }

        return base64EncodeDeviceInformation(di);
    }

    private static String base64EncodeDeviceInformation(JSONObject di) {
        return Base64.encodeToString(di.toString().getBytes(), Base64.NO_WRAP);
    }

}
