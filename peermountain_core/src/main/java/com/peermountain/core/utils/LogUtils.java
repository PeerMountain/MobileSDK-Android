package com.peermountain.core.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.peermountain.core.BuildConfig;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;



/**
 * Created by Galeen on 22.7.2016 г..
 */
public class LogUtils {
    public static void d(String tag, String msg){
        if (BuildConfig.DEBUG)
            Log.d(tag, prettyJson(msg));
    }
    public static void d(String tag, HashMap<String, String> postDataParams){
        if ( postDataParams!=null) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : postDataParams.entrySet()) {
                sb.append(entry.getKey());
                sb.append(" : ");
                sb.append(entry.getValue());
                sb.append("\n");
            }
            Log.d(tag, sb.toString());
        }
    }
    public static void e(String tag, String msg){
        if (BuildConfig.DEBUG)
            Log.e(tag, prettyJson(msg));
    }
    public static void i(String tag, String msg){
            Log.i(tag, prettyJson(msg));
    }

    public static String prettyJson(String body) {
        if (body != null && body.isEmpty()) {
            return body;
        }
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setIndent("\u00A0\u00A0");
            JsonElement jsonElement = new JsonParser().parse(body);
            gson.toJson(jsonElement, jsonWriter);
            return stringWriter.toString();
        } catch (JsonParseException e) {
            return body;
        }
    }
}
