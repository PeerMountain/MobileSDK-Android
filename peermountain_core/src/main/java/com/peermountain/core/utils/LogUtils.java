package com.peermountain.core.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.peermountain.core.persistence.PeerMountainManager;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Galeen on 22.7.2016 г..
 */
public class LogUtils {

    public static class Logger {
        public String TAG = "LogUtils";

        public Logger(String TAG) {
            this.TAG = TAG;
        }

        public void d(String msg) {
            LogUtils.d(TAG, msg);
        }

        public void v(String tag, String msg) {
            LogUtils.v(TAG, msg);
        }

        public void w(String msg) {
            LogUtils.w(TAG, msg);
        }

        public void d(HashMap<String, String> postDataParams) {
            LogUtils.d(TAG, postDataParams);
        }

        public void e(String msg) {
            LogUtils.e(TAG, msg);
        }

        public void i(String msg) {
            LogUtils.i(TAG, msg);
        }
    }


    public static boolean isDebug() {
        return PeerMountainManager.getPeerMountainConfig() != null && PeerMountainManager.getPeerMountainConfig().isDebug();
    }

    public static void d(String tag, String msg) {
        if (isDebug())
            Log.d(tag, prettyJson(msg));
    }

    public static void v(String tag, String msg) {
        if (isDebug())
            Log.v(tag, prettyJson(msg));
    }

    public static void w(String tag, String msg) {
        if (isDebug())
            Log.w(tag, prettyJson(msg));
    }

    public static void d(String tag, HashMap<String, String> postDataParams) {
        if (isDebug() && postDataParams != null) {
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

    public static void e(String tag, String msg) {
        Log.e(tag, prettyJson(msg));
    }

    public static void i(String tag, String msg) {
        Log.i(tag, prettyJson(msg));
    }

    private static String prettyJson(String body) {
        if (TextUtils.isEmpty(body)) {
            return " ";
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
