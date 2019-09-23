package com.example.android.adobepassclientlessrefapp.adobeauth;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.nbcsports.leapsdk.authentication.adobepass.config.TempPassSelectionConfig;

import java.io.IOException;
import java.util.List;

/**
 * Type adapter for gson to convert the string of {@link TempPassSelectionConfig} to its object form.
 */
public class TypeAdapterStringToObject extends TypeAdapter<TempPassSelectionConfig> {

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<List<String>>(){}.getType(), new TypeAdapterStringToList())
            .create();

    @Override
    public void write(JsonWriter out, TempPassSelectionConfig value) throws IOException {
        gson.toJson(value, TempPassSelectionConfig.class, out);
    }

    @Override
    public TempPassSelectionConfig read(JsonReader in) throws IOException {

        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else if (in.peek() == JsonToken.STRING) {
            // Do conversion from string to TempPassSelection Object
            String jsonString = in.nextString();

            return gson.fromJson(jsonString, new TypeToken<TempPassSelectionConfig>(){}.getType());
        } else {
            return new TempPassSelectionConfig();
        }

    }
}
