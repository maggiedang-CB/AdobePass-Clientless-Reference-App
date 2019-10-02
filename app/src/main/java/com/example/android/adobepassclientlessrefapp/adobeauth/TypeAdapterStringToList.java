package com.example.android.adobepassclientlessrefapp.adobeauth;

import android.util.Log;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Type adapter for gson to convert json values lists as string into actual list objects
 */
public class TypeAdapterStringToList extends TypeAdapter<List<String>>{

    public static String TAG = "TypeAdapterStringToList";

    @Override
    public void write(JsonWriter out, List<String> list) throws IOException {
        if (list == null) {
            out.nullValue();
            return;
        }
        out.beginArray();
        for (String string : list) {
            out.value(string);
        }
        out.endArray();
    }

    @Override
    public List<String> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else if (in.peek() == JsonToken.STRING) {
            // here we convert to list<String>
            String nextString = in.nextString();

            Log.d(TAG, "JsonReader in.nextString() = " + nextString);

            return GenerateSampleData.stringToList(nextString);
        } else {
            Log.d(TAG, "JsonReader return new array");
            return new ArrayList<>();
        }
    }
}
