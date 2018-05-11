package com.peermountain.sdk.ui.register;

import android.app.Activity;
import android.net.Uri;

import com.peermountain.core.camera.CameraActivity;
import com.peermountain.core.utils.PmLiveSelfieHelper;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.authorized.documents.DocumentsViewModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Galeen on 5/10/2018.
 */
public class RegisterViewModel extends DocumentsViewModel {

    @Override
    protected void init() {

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
                showError(R.string.pm_err_no_id_captured);
            }
        }
    }


}
