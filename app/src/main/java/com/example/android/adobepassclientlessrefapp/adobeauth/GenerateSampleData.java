package com.example.android.adobepassclientlessrefapp.adobeauth;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Uses sample data to generate and auto fill adobe auth form
 */
public class GenerateSampleData {

    private static String TAG = "GenerateSampleData";

    public static String sampleJsonString = "{\n" +
            "\t\t\"baseUrl\": \"https://api.auth.adobe.com\",\n" +
            "\t\t\"checkAuthenticationPath\": \"/api/v1/checkauthn?deviceId={DEVICE_ID}\",\n" +
            "\t\t\"reggieCodePath\": \"/reggie/v1/{REQUESTOR_ID}/regcode\",\n" +
            "\t\t\"registrationUrl\": \"https://dev.ott.nbcsports.com/\",\n" +
            "\t\t\"authenticatePath\": \"/api/v1/authenticate?reg_code={REGGIE_CODE}&requestor_id={REQUESTOR_ID}&domain_name=adobe.com&noflash=true&no_iframe=true&mso_id={MSO_ID}&redirect_url={REDIRECT_URL}\",\n" +
            "\t\t\"authnTokenPath\": \"/api/v1/tokens/authn\",\n" +
            "\t\t\"authnTokenCheckInterval\": 10,\n" +
            "\t\t\"authnTokenTimeoutSeconds\": 60,\n" +
            "\t\t\"authorizePath\": \"/api/v1/authorize?deviceId={DEVICE_ID}&requestor={REQUESTOR_ID}&resource={RESOURCE}\",\n" +
            "\t\t\"authzTokenPath\": \"/api/v1/tokens/authz?deviceId={DEVICE_ID}&requestor={REQUESTOR_ID}&resource={RESOURCE}\",\n" +
            "\t\t\"mediaTokenPath\": \"/api/v1/tokens/media?deviceId={DEVICE_ID}&requestor={REQUESTOR_ID}&resource={RESOURCE}\",\n" +
            "\t\t\"tokenizationUrl\": \"http://stream.nbcsports.com/data/mobile/passnbc.xml\",\n" +
            "\t\t\"redirectUrl\": \"https://smart.link/5be5f39d4b556\",\n" +
            "\t\t\"logoutPath\": \"/api/v1/logout\",\n" +
            "\t\t\"tempPassUrl\": \"/api/v1/authenticate/freepreview\",\n" +
            "\t\t\"tempPassRequestor\": \"nbcsports\",\n" +
            "\t\t\"tempPassProvider\": \"TempPass_10min\",\n" +
            "\t\t\"preauthorizePath\": \"/api/v1/preauthorize\",\n" +
            "\t\t\"preauthorizeBody\": \"deviceType=android&deviceId={DEVICE_ID}&appId=RSN%20App&ttl=300&requestor={REQUESTOR_ID}&resource=CSNChicago,CSNPhiladelphia,CSNCalifornia,CSNNorthwest,CSNNewEngland,CSNMidAtlantic,CSNBayArea\",\n" +
            "\t\t\"getUserMetadataPath\": \"/api/v1/tokens/usermetadata?deviceType=android&deviceId={DEVICE_ID}&requestor={REQUESTOR_ID}\",\n" +
            "\t\t\"logosUrl\": \"http://stream.nbcsports.com/data/mobile/mvpd_logos_doc.json\",\n" +
            "\t\t\"mvpdListPath\": \"/api/v1/config\",\n" +
            "\t\t\"externalBrowserDomains\": [\n" +
            "\t\t\t\"YouTubeTV\",\n" +
            "\t\t\t\"GoogleFiber\",\n" +
            "\t\t\t\"googlefiber_auth-gateway_net\"],\n" +
            "\t\t\"nbcTokenUrl\": \"https://token.playmakerservices.com/cdn\",\n" +
            "\t\t\"tempPassSelection\": {\n" +
            "\t\t\t\"requestorId\": \"nbcsports\",\n" +
            "\t\t\t\"deviceType\": \"Android\",\n" +
            "\t\t\t\"passes\": [{\n" +
            "\t\t\t\t\"requestorID\": \"NBCOlympics\",\n" +
            "\t\t\t\t\"shortTempPassId\": \"TempPass-ShortTTL\",\n" +
            "\t\t\t\t\"longTempPassId\": \"TempPass-NBCSPORTS-LongTTL\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"requestorID\": \"nbcentertainment\",\n" +
            "\t\t\t\t\"shortTempPassId\": \"TempPass-ShortTTL\",\n" +
            "\t\t\t\t\"longTempPassId\": \"TempPass-NBCSPORTS-LongTTL\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"requestorID\": \"telemundo\",\n" +
            "\t\t\t\t\"shortTempPassId\": \"TempPass-ShortTTL\",\n" +
            "\t\t\t\t\"longTempPassId\": \"TempPass-NBCSPORTS-LongTTL\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"requestorID\": \"nbcsports\",\n" +
            "\t\t\t\t\"shortTempPassId\": \"TempPass-Sports-10min\",\n" +
            "\t\t\t\t\"longTempPassId\": \"\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"requestorID\": \"golf\",\n" +
            "\t\t\t\t\"shortTempPassId\": \"TempPass-Golf-10min\",\n" +
            "\t\t\t\t\"longTempPassId\": \"\"\n" +
            "\t\t\t}]\n" +
            "\t\t}\n" +
            "\t}";


    public static JSONObject makeJsonSample() {
        try {
            JSONObject json = new JSONObject(sampleJsonString);

            Log.d(TAG, json.toString());
            return json;
        } catch (JSONException e) {
            Log.d(TAG, "ERROR when converting sample json string data to Json object");
        }
        return null;
    }

    public static List<String> stringToList(String listAsString) {
        // Split out square brackets
        String listAsStringCommaSeparated = listAsString.substring(1, listAsString.length() - 2);

        Log.d(TAG, "listAsStringCommaSeparated = " + listAsStringCommaSeparated);

        List<String> list = new ArrayList<>(Arrays.asList(listAsStringCommaSeparated.split("\\s*,\\s*")));

        Log.d(TAG, "strinToList = " + list);

        return list;
    }

}
