package com.peermountain.pm_net.network.teleferique;

import android.util.JsonReader;

import com.peermountain.common.utils.Pm_JsonParser;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Galeen on 8/8/2018.
 */
public class TeleferiqueJsonParser extends Pm_JsonParser {

    public static String readServerTime(String json) throws IOException {
        if (json == null) return null;
        String name;
        String time = null;
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("data")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    name = reader.nextName();
                    if (name.equals("teleferic")) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            name = reader.nextName();
                            if (name.equals("signedTimestamp")) {
                                time = getString(reader);
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        reader.close();
        return time;
    }

}
