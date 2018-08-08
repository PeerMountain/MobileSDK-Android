package com.peermountain.sdk.ui.authorized.documents;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.peermountain.common.model.DocumentID;
import com.peermountain.core.model.guarded.VerifySelfie;
import com.peermountain.pm_net.network.BaseEvents;
import com.peermountain.pm_net.network.MainCallback;
import com.peermountain.pm_net.network.NetworkManager;
import com.peermountain.pm_net.network.NetworkResponse;
import com.peermountain.core.persistence.MyJsonParser;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.PmDocumentsHelper;
import com.peermountain.sdk.R;
import com.peermountain.pm_livecycle.base.BaseViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Galeen on 5/11/2018.
 */
public class DocumentsViewModel extends BaseViewModel {
    private MutableLiveData<DocumentID> onDocumentScannedFromServer;
    private MutableLiveData<VerifySelfie> onSelfieVerifiedFromServer;

    @Override
    protected void init() {

    }

    public LiveData<DocumentID> getOnDocumentScannedFromServer() {
        if (onDocumentScannedFromServer == null) {
            onDocumentScannedFromServer = new MutableLiveData<>();
        }
        return onDocumentScannedFromServer;
    }

    public LiveData<VerifySelfie> getOnSelfieVerifiedFromServer() {
        if (onSelfieVerifiedFromServer == null) {
            onSelfieVerifiedFromServer = new MutableLiveData<>();
        }
        return onSelfieVerifiedFromServer;
    }

    public void sendFiles(ArrayList<File> filesToSend) {
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("passportFront");
        fileNames.add("passportBack");
        NetworkManager.sendFiles(
                new SendIDCallback(getNetworkCallback(), MainCallback.TYPE_DIALOG, false,
                        PeerMountainManager.getApplicationContext().getString(R.string.pm_msg_extracting_data),
                        filesToSend),
                "https://api.kyc3.com/rest/api/_mrzExtractor?api_key=bStfjjadHizdxqabdcStOg==",
                filesToSend,
                fileNames);
    }

    public void sendFilesToVerifyLiveSelfie(ArrayList<File> filesToSend) {
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("file1");
        fileNames.add("file2");
        fileNames.add("file3");
        fileNames.add("file4");
        fileNames.add("file5");
        fileNames.add("passportFront");
        fileNames.add("passportBack");
        NetworkManager.sendFiles(
                new VerifySelfieCallback(getNetworkCallback(), MainCallback.TYPE_DIALOG,
                        false,
                        PeerMountainManager.getApplicationContext().getString(R.string.pm_msg_verify),
                        filesToSend),
                "https://api.kyc3.com/rest/api/_faceRecognizer?api_key=bStfjjadHizdxqabdcStOg==",
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

    private class VerifySelfieCallback extends MainCallback {
        private ArrayList<File> filesToSend;

        public VerifySelfieCallback(BaseEvents presenterCallback, int progressType, boolean cancelable, String loadingMsg, ArrayList<File> filesToSend) {
            super(presenterCallback, progressType, cancelable, loadingMsg);
            this.filesToSend = filesToSend;
        }

        @Override
        public void inTheEndOfDoInBackground(NetworkResponse networkResponse) {
            super.inTheEndOfDoInBackground(networkResponse);
            try {
                networkResponse.object = MyJsonParser.readLiveSelfieResponse(networkResponse.json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPostExecute(NetworkResponse networkResponse) {
            super.onPostExecute(networkResponse);
            VerifySelfie verifySelfie = (VerifySelfie) networkResponse.object;
            if (onSelfieVerifiedFromServer != null) onSelfieVerifiedFromServer.postValue(verifySelfie);

        }

        @Override
        public void onError(String msg, NetworkResponse networkResponse) {
            super.onError(msg, networkResponse);
            if (onSelfieVerifiedFromServer != null) onSelfieVerifiedFromServer.postValue(null);

            if (filesToSend != null) {
                for (File file : filesToSend) {
                    file.delete();
                }
            }
        }
    }
}
