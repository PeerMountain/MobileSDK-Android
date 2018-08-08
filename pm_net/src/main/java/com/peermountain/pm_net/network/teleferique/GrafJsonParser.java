package com.peermountain.pm_net.network.teleferique;

import android.text.TextUtils;
import android.util.JsonWriter;

import com.peermountain.pm_net.network.teleferique.model.SendObject;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by Galeen on 8/8/2018.
 */
public class GrafJsonParser {

    public static String writeToGraphQL(SendObject sendObject) throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        if (!TextUtils.isEmpty(sendObject.getQuery())) {
            writer.name("query").value(sendObject.getQuery());
        }
        if (!TextUtils.isEmpty(sendObject.getVariables())) {
            writer.name("variables").value(sendObject.getVariables());
        }
        writer.endObject();
        writer.close();
        return sw.toString();
    }
}
