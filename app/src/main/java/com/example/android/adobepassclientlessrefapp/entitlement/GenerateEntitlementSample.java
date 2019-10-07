package com.example.android.adobepassclientlessrefapp.entitlement;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Generated data is taken from video asset pid: "221510" with provider credentials of Philadelphia
 * RCN.
 */
public class GenerateEntitlementSample {

    public static String TAG = "GenerateEntitlementSample";

    public static String sampleEntitlement = "{\n" +
            "\t\t\"zipCode\": \"30301\",\n" +
            "\t\t\"blackoutID\": \"5\",\n" +
            "\t\t\"mlbBlackoutServiceUrl\": \"[eventid]&z=[zipcode]&accountID=[userId]&deviceID=[deviceID]&deviceType=android&callback=\",\n" +
            "\t\t\"entitlementId\": \"5861989\",\n" +
            "\t\t\"requiresEntitlementRightsCheck\": \"false\",\n" +
            "\t\t\"useAnvatoService\": \"false\",\n" +
            "\t\t\"comcast\": \"false\",\n" +
            "\t\t\"currentChannel\": \"true\",\n" +
            "\t\t\"currentChannelMvpdDigital\": \"false\",\n" +
            "\t\t\"currentChannelMvpdDigitalContainsMvpd\": \"false\",\n" +
            "\t\t\"blackoutServiceUrl\": \"http://api.nbcsports.com/geo.asmx/MLEAuth2_new_travel_rights?evt=5&z=30301&accountID=979597&deviceID=2ea29eea5109f7e0&deviceType=android&callback=\",\n" +
            "\t\t\"anvatoDomain\": \"http://tkx-cable-prod.nbc.anvato.net\",\n" +
            "\t\t\"anvatoChannelDomain\": \"https://access-cloudpath.media.nbcuni.com\",\n" +
            "\t\t\"anvatoAnvack\": \"nbcu_nbcsn_nbcsn_android_prod_b109b4f6825d04ea71bb272a16cd2c773aeb57df\"\n" +
            "}";

    public static JSONObject makeSampleJsonObject() {
        try {
            JSONObject json = new JSONObject(sampleEntitlement);
            Log.d(TAG, json.toString());
            return json;
        } catch (JSONException e) {
            Log.e(TAG, "ERROR when converting sample json string data to Json object");
        }
        return null;
    }

}
