package com.peermountain.sdk.ui.authorized.documents;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.peermountain.core.model.guarded.DocumentID;
import com.peermountain.core.network.BaseEvents;
import com.peermountain.core.network.MainCallback;
import com.peermountain.core.network.NetworkManager;
import com.peermountain.core.network.NetworkResponse;
import com.peermountain.core.persistence.MyJsonParser;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.PmDocumentsHelper;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.livecycle.BaseViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Galeen on 5/11/2018.
 */
public class DocumentsViewModel extends BaseViewModel {
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

    private ArrayList<File> filesToSend;

    public void sendFiles(ArrayList<File> filesToSend) {
        this.filesToSend = filesToSend;
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("passportFront");
        fileNames.add("passportBack");
        // TODO: 5/10/2018 onIdScanned
        NetworkManager.sendFiles(new SendIDCallback(getNetworkCallback(), MainCallback.TYPE_DIALOG, false,
                        PeerMountainManager.getApplicationContext().getString(R.string.pm_msg_loading)),
                "https://api.kyc3.com/rest/api/_mrzExtractor?api_key=bStfjjadHizdxqabdcStOg==",
                filesToSend,
                fileNames);
    }

    private class SendIDCallback extends MainCallback {

        public SendIDCallback(BaseEvents presenterCallback, int progressType, boolean cancelable, String loadingMsg) {
            super(presenterCallback, progressType, cancelable, loadingMsg);
        }

        @Override
        public void inTheEndOfDoInBackground(NetworkResponse networkResponse) {
            super.inTheEndOfDoInBackground(networkResponse);
            try {
                networkResponse.object = MyJsonParser.readDocument(networkResponse.json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPostExecute(NetworkResponse networkResponse) {
            super.onPostExecute(networkResponse);
            DocumentID documentID = (DocumentID) networkResponse.object;
            PmDocumentsHelper.addIDImagesToDocument(filesToSend, documentID);

            if (onDocumentScannedFromServer != null) onDocumentScannedFromServer.postValue(documentID);
        }

        @Override
        public void onError(String msg, NetworkResponse networkResponse) {
            super.onError(msg, networkResponse);
            if (filesToSend != null) {
                for (File file : filesToSend) {
                    file.delete();
                }
            }
        }
    }
}
