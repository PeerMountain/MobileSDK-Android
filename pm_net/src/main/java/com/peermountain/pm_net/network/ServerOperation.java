package com.peermountain.pm_net.network;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;


/**
 * Created by Galeen on 13.1.2016 г..
 * will do the request according to the Action object send to it and will
 update the MainCallback on each of it's stages.
 There is a doServerCall static method which can be called from another background thread.
 */
public class ServerOperation extends AsyncTask<Action, Void, NetworkResponse> {
    private NetworkOperationCallback mCallback = null;
    private String token = null;

    ServerOperation(NetworkOperationCallback mCallback, String token) {
        this.mCallback = mCallback;
        this.token = token;
    }

    public NetworkOperationCallback getmCallback() {
        return mCallback;
    }

    public void setmCallback(NetworkOperationCallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mCallback != null)
            mCallback.onPreExecute();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (mCallback != null)
            mCallback.onCancel();
    }

    @Override
    protected void onCancelled(NetworkResponse networkResponse) {
        super.onCancelled(networkResponse);
        if (mCallback != null)
            mCallback.onCancel();
    }

    @Override
    protected NetworkResponse doInBackground(Action... params) {
//       waitTime(10000);
        Action action = params[0];
        if (action.priority != Action.NORMAL_PRIORITY) {
            android.os.Process.setThreadPriority(action.priority);
        }
        NetworkResponse res = doServerCall(action, token);
        if (mCallback != null && res != null && res.responseCode < 300)
            mCallback.inTheEndOfDoInBackground(res);
        return res;
    }

    /**
     * This method is called on the same thread
     *
     * @param action - config object
     * @return - Server Response
     */
     static NetworkResponse doServerCall(final Action action, String token) {
        NetworkResponse res = null;
        if (action != null) {
            GaleenTracker tracker = new GaleenTracker(action.endpoint);
            try {
                tracker.startTracker();
                if (!action.isFullUrl)
                    action.endpoint = NetConstants.SERVER_ADDRESS + action.endpoint;
                tracker.logPrettyJson(action);
                switch (action.operation) {
                    case Action.PUT:
                        res = NetworkRequestHelper.sendAuthenticatedPut(action.endpoint, action.body, token);
                        break;
                    case Action.POST:
                        res = NetworkRequestHelper.sendPost(action.endpoint, action.body, token, new NetworkRequestHelper.ServerEvents() {
                            @Override
                            public void beforeToReceiveResponse() {
                                //clear the data to empty memory
                                action.body = null;
                            }
                        });
                        break;
                    case Action.DELETE:
                        res = NetworkRequestHelper.sendDelete(action.endpoint, action.body, token);
                        break;
                    case Action.UPLOAD_FILE:
//                        res = BackEndHelper.postFile(action.endpoint, action.file, token, action.params);
                        res = NetworkRequestHelper.multipartRequest(action.endpoint, action.params, action.files,
                                action.fileFields,  token);
                        break;
                    case Action.GET:
                        res = NetworkRequestHelper.sendAuthenticatedGet(action.endpoint, token
                                , action.params, action.headers);
                        break;
                    case Action.GET_UNAUTHORIZED:
                        res = NetworkRequestHelper.sendGet(action.endpoint, action.params, action.headers);
                        break;
                    case Action.DOWNLOAD_FILE:
                        res = NetworkRequestHelper.downloadFile(action.endpoint, action.file);
                        break;
                    case Action.DELAY_2_MIN:
                        waitTime(1000*60*2);
                        break;
                }
                tracker.stopTracker();
                tracker.logResponse(res);

            } catch (Exception e) {
                tracker.logError(e.toString());
                res = null;
            }
        }
        return res;
    }

    @Override
    protected void onPostExecute(NetworkResponse res) {
        super.onPostExecute(res);
        onFinish(mCallback, res);
    }

    public static void waitTime(int mills) {
        Log.e("waitTime","start "+ System.currentTimeMillis() );
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("waitTime","end "+ System.currentTimeMillis()+" for : "+ mills );
    }


    private static void onFinish(NetworkOperationCallback mCallback, NetworkResponse res) {
        if (mCallback != null) {
            if (res != null && res.responseCode < 300) {
//                Error error = MyJsonParser.checkForError(res.json);
//                if (error != null && error.getText() != null) {
//                    mCallback.onError(error.getText(), res);
//                } else
                mCallback.onPostExecute(res);
            } else
                mCallback.onError(null, res);
        }
    }

    /**
     * @return The MIME type for the given file.
     */
    public   static String getMimeType(File file) {
        String extension = getExtension(file.getName());

        if (extension.length() > 0)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));

        return "application/octet-stream";
    }

    private static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

}