package com.peermountain.scan_id_sdk.scan;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import com.peermountain.common.PmBaseConfig;
import com.peermountain.common.model.DocumentID;
import com.peermountain.common.model.ImageResult;
import com.peermountain.common.utils.DocumentJsonParser;
import com.peermountain.pm_livecycle.base.BaseViewModel;
import com.peermountain.pm_net.network.BaseEvents;
import com.peermountain.pm_net.network.MainCallback;
import com.peermountain.pm_net.network.NetworkManager;
import com.peermountain.pm_net.network.NetworkResponse;
import com.peermountain.scan_id_sdk.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Galeen on 5/11/2018.
 */
public class IdentityDocumentViewModel extends BaseViewModel {
    private MutableLiveData<DocumentID> onDocumentScannedFromServer;

    @Override
    protected void init() {

    }

    public LiveData<DocumentID> getOnDocumentScannedFromServer() {
        if (onDocumentScannedFromServer == null) {
            onDocumentScannedFromServer = new MutableLiveData<>();
        }
        return onDocumentScannedFromServer;
    }


    public void sendFiles(ArrayList<File> filesToSend) {
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("passportFront");
        fileNames.add("passportBack");
        NetworkManager.sendFiles(
                new SendIDCallback(getNetworkCallback(), MainCallback.TYPE_DIALOG, false,
                        PmBaseConfig.getApplicationContext().getString(R.string.pm_msg_extracting_data),
                        filesToSend),
                "https://api.kyc3.com/rest/api/_mrzExtractor?api_key=" + PmBaseConfig.getApiScanKey(),
                filesToSend,
                fileNames);
    }



    private class SendIDCallback extends MainCallback {
        private ArrayList<File> filesToSend;

        public SendIDCallback(BaseEvents presenterCallback, int progressType, boolean cancelable, String loadingMsg, ArrayList<File> filesToSend) {
            super(presenterCallback, progressType, cancelable, loadingMsg);
            this.filesToSend = filesToSend;
        }

        @Override
        public void inTheEndOfDoInBackground(NetworkResponse networkResponse) {
            super.inTheEndOfDoInBackground(networkResponse);
            try {
                networkResponse.object = DocumentJsonParser.readDocument(networkResponse.json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPostExecute(NetworkResponse networkResponse) {
            super.onPostExecute(networkResponse);
            DocumentID documentID = (DocumentID) networkResponse.object;
            if (filesToSend != null && filesToSend.size() > 0
                    && documentID != null && documentID.getErrorMessage() == null) {
                documentID.setImageCropped(new ImageResult(Uri.fromFile(filesToSend.get(0)).toString()));
                if (filesToSend.size() > 1) {
                    documentID.setImageCroppedBack(new ImageResult(Uri.fromFile(filesToSend.get(1)).toString()));
                }
            }

            if (onDocumentScannedFromServer != null) onDocumentScannedFromServer.postValue(documentID);
        }

        @Override
        public void onError(String msg, NetworkResponse networkResponse) {
            networkResponse.isErrorHandled = true;
            super.onError(msg, networkResponse);
            if (filesToSend != null) {
                for (File file : filesToSend) {
                    file.delete();
                }
            }
            if (onDocumentScannedFromServer != null) onDocumentScannedFromServer.postValue(null);
        }
    }
}
