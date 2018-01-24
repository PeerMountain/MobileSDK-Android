package com.peermountain.core.network;

import java.io.File;
import java.util.Map;

/**
 * Created by Galeen on 15.1.2016 г..
 * data holder with multiple constructors it has operation(PUT,GET,POST,DELETE or UPLOAD_FILE)
 endpoint, params, headers,body and file which can be set to be send to the server from ServerOperation.
 */
public class Action {
     static final int HIGH_PRIORITY = 5;
     static final int NORMAL_PRIORITY = 10;//THREAD_PRIORITY_BACKGROUND
     static final int LOW_PRIORITY = 15;
     static final int PUT =1;
     static final int GET =2;
     static final int POST =3;
     static final int DELETE =4;
     static final int UPLOAD_FILE =5;
     static final int DELAY_2_MIN =6;
     static final int GET_UNAUTHORIZED =7;
    static final int DOWNLOAD_FILE =8;
     int operation;
    String endpoint;
    Map<String, String> params, headers;
    String body;
    File file;
    boolean isFullUrl = false;
    int priority = NORMAL_PRIORITY;

     Action(){}

     Action(int operation, String endpoint, String body, Map<String, String> headers, Map<String, String> params) {
        this.body = body;
        this.endpoint = endpoint;
        this.headers = headers;
        this.operation = operation;
        this.params = params;
    }
     Action(int operation, File file) {
        this.operation = operation;
        this.file = file;
    }
     Action(int operation, String endpoint, String body, File file) {
        this.body = body;
        this.endpoint = endpoint;
        this.operation = operation;
        this.file = file;
    }

     Action(int operation, String endpoint, Map<String,String> params, File file) {
        this.params = params;
        this.endpoint = endpoint;
        this.operation = operation;
        this.file = file;
    }

     Action(int operation, String endpoint, String body) {
        this.body = body;
        this.endpoint = endpoint;
        this.operation = operation;
    }
     Action(int operation, String endpoint, String body, boolean fullAddress) {
        this.body = body;
        this.endpoint = endpoint;
        this.operation = operation;
        isFullUrl = fullAddress;
    }
     Action(String body) {
        this.body = body;
        this.endpoint = "";
        this.operation = POST;
    }

     String getOperation(){
        String action = "send";
        switch (operation){
            case Action.PUT:
                action = "PUT";
                break;
            case Action.POST:
                action = "POST";
                break;
            case Action.DELETE:
                action = "DELETE";
                break;
            case Action.UPLOAD_FILE:
                action = "UPLOAD_FILE";
                break;
            case Action.GET:
                action = "GET";
                break;
        }
        return action;
    }

}
