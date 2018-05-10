package com.peermountain.sdk.ui.register;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import com.peermountain.core.camera.CameraActivity;
import com.peermountain.core.model.guarded.DocumentID;
import com.peermountain.core.model.guarded.ImageResult;
import com.peermountain.core.network.BaseEvents;
import com.peermountain.core.network.MainCallback;
import com.peermountain.core.network.NetworkManager;
import com.peermountain.core.network.NetworkResponse;
import com.peermountain.core.persistence.MyJsonParser;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.PmLiveSelfieHelper;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.livecycle.BaseViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Galeen on 5/10/2018.
 */
public class RegisterViewModel extends BaseViewModel {
    private MutableLiveData<DocumentID> onDocumentScannedFromServer;

    @Override
    protected void init() {

    }

    public LiveData<DocumentID> getOnDocumentScannedFromServer() {
        if(onDocumentScannedFromServer == null){
            onDocumentScannedFromServer = new MutableLiveData<>();
        }
        return onDocumentScannedFromServer;
    }

    void checkIdCapture(int requestCode, int resultCode) {
        if (resultCode == Activity.RESULT_OK && requestCode == RegisterActivity.REQUEST_ID_CAPTURE) {
            if (CameraActivity.idImages != null
                    && CameraActivity.idImages[0] != null) {
                //save files and send
                new PmLiveSelfieHelper(true, new PmLiveSelfieHelper.Events() {
                    @Override
                    public void onLiveSelfieReady(ArrayList<String> liveSelfie) {
                        if (liveSelfie == null || liveSelfie.size() == 0) return;
                        ArrayList<File> files = new ArrayList<>();

                        Uri uri = Uri.parse(liveSelfie.get(0));
                        File file = new File(uri.getPath());

                        if (liveSelfie.size() > 1) {
                            uri = Uri.parse(liveSelfie.get(1));
                            File file2 = new File(uri.getPath());
                            files.add(file2);//this is front ID
                        }
                        files.add(file);//this is MRZ, if there is front is added before MRZ

                        sendFiles(files);
                    }
                }).saveID();
            } else {
                showError(R.string.pm_err_no_liveselfie_created);
            }
        }
    }
    private ArrayList<File> filesToSend;
    private void sendFiles(ArrayList<File> filesToSend) {
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
            if(filesToSend!=null && documentID!=null){
                if(filesToSend.size()==1){
                    documentID.setImageCropped(new ImageResult(Uri.fromFile(filesToSend.get(0)).toString()));
                }else{
                    documentID.setImageCropped(new ImageResult(Uri.fromFile(filesToSend.get(1)).toString()));
                    documentID.setImageCroppedBack(new ImageResult(Uri.fromFile(filesToSend.get(0)).toString()));
                }
            }

            if(onDocumentScannedFromServer!=null) onDocumentScannedFromServer.postValue(documentID);
        }

        @Override
        public void onError(String msg, NetworkResponse networkResponse) {
            super.onError(msg, networkResponse);
            if(filesToSend!=null){
                for (File file : filesToSend) {
                    file.delete();
                }
            }
        }
    }
}
