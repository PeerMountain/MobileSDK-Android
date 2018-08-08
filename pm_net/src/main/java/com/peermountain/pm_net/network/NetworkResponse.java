package com.peermountain.pm_net.network;

import java.io.File;

/**
 * Created by Galeen on 18.1.2016 г..
 * This is a data holder created from NetworkRequestHelper on server response.
 It keeps the responseCode and json/error from the server.
 It has 2 objects that are used as a Tags to send the parsed object
 from inTheEndOfDoInBackground to onPostExecute.
 */
public class NetworkResponse {
    public static final int FIXE_EXIST = 199;
    public static final int ERROR = 11941;
    public int responseCode;
    public String json;
    public String headerLastModified=null;
    public Object object, object2;
    public String error;
    public File file;
    public int errorMsg;
    public boolean isErrorHandled = false;

    public NetworkResponse(String json, int responseCode) {
        this.json = json;
        this.responseCode = responseCode;
    }

    public NetworkResponse(int responseCode, File file) {
        this.responseCode = responseCode;
        this.file = file;
    }

    public NetworkResponse(String headerLastModified, String json, int responseCode) {
        this.headerLastModified = headerLastModified;
        this.json = json;
        this.responseCode = responseCode;
    }

    public NetworkResponse(String error) {
        this.error = error;
        responseCode = ERROR;
    }

    public NetworkResponse(int errorMsg) {
        this.errorMsg = errorMsg;
        responseCode = ERROR;
    }
}
