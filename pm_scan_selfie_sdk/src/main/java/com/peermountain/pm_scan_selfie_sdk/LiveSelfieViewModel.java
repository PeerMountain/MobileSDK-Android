package com.peermountain.pm_scan_selfie_sdk;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.peermountain.common.PmBaseConfig;
import com.peermountain.pm_livecycle.base.BaseViewModel;
import com.peermountain.pm_net.network.BaseEvents;
import com.peermountain.pm_net.network.MainCallback;
import com.peermountain.pm_net.network.NetworkManager;
import com.peermountain.pm_net.network.NetworkResponse;
import com.peermountain.pm_scan_selfie_sdk.model.VerifySelfie;
import com.peermountain.pm_scan_selfie_sdk.parser.PmSelfieParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Galeen on 5/11/2018.
 */
public class LiveSelfieViewModel extends BaseViewModel {
    private MutableLiveData<VerifySelfie> onSelfieVerifiedFromServer;

    @Override
    protected void init() {

    }

    LiveData<VerifySelfie> getOnSelfieVerifiedFromServer() {
        if (onSelfieVerifiedFromServer == null) {
            onSelfieVerifiedFromServer = new MutableLiveData<>();
        }
        return onSelfieVerifiedFromServer;
    }

    void sendFilesToVerifyLiveSelfie(ArrayList<File> filesToSend) {
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("file1");
        fileNames.add("file2");
        fileNames.add("file3");
        fileNames.add("file4");
        fileNames.add("file5");
        fileNames.add("passportFront");
        fileNames.add("passportBack");
        NetworkManager.sendFiles(// TODO: 8/9/2018 update key
                new VerifySelfieCallback(getNetworkCallback(), MainCallback.TYPE_DIALOG,
                        false,
                        PmBaseConfig.getApplicationContext().getString(R.string.pm_msg_verify),
                        filesToSend),
                "https://api.kyc3.com/rest/api/_faceRecognizer?api_key=bStfjjadHizdxqabdcStOg==",
                filesToSend,
                fileNames);
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
                networkResponse.object = PmSelfieParser.readLiveSelfieResponse(networkResponse.json);
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
