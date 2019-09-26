package com.example.android.adobepassclientlessrefapp.mediaInfo;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class GenerateSampleMediaInfo {

    private static String TAG = "GenerateSampleMediaInfo";

    /*
        This Media Info Sample data is taken from the nbcsports video asset titled
        "David Montgomery Memorial Service" (NBCS PHI)
     */
    public static String sampleMediaInfoData = "{\n" +
            "\t\t\"pid\": \"221510\",\n" +
            "\t\t\"streamUrl\": \"http://sprtlive14.akamaized.net/hls/live/586301/nbcs-philadelphia0606065516/master_vod.m3u8\",\n" +
            "\t\t\"requestorId\": \"nbcsports\",\n" +
            "\t\t\"channel\": \"CSNPhiladelphia\",\n" +
            "\t\t\"assetId\": \"nbcs_164639_vod\",\n" +
            "\t\t\"cdn\": \"akamai\"\n" +
            "}";

    public static JSONObject makeSampleJsonObject() {
        try {
            JSONObject json = new JSONObject(sampleMediaInfoData);
            Log.d(TAG, json.toString());
            return json;
        } catch (JSONException e) {
            Log.e(TAG, "ERROR when converting sample json string data to Json object");
        }
        return null;
    }

}
