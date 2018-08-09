package com.peermountain.scan_id_sdk.scan;

import android.graphics.Bitmap;

import com.peermountain.common.PmBaseConfig;
import com.peermountain.common.utils.ImageUtils;
import com.peermountain.common.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Galeen on 11/7/17.
 */

public class PmScanIdentityDocumentHelper {
    private File dir;
    private ArrayList<File> files = new ArrayList<>();
    private int imagesInProcess = 0;
    private Events callback;

    public PmScanIdentityDocumentHelper(Events callback) {
        this.callback = callback;
    }

    public void saveID() {
        if(callback== null) return;

        prepareDir();

        imagesInProcess = 0;
        Bitmap[] idImages = callback.getID_Images();

        if (idImages[1] != null) {  // this is face
            saveFrame(idImages[1],
                    System.currentTimeMillis() + "_not_mrz");
        }//else is a passport and idImages[0] is face and mrz

        saveFrame(idImages[0], System.currentTimeMillis() + "_mrz");

        callback.clearID_Images();
    }


    private void saveFrame(Bitmap bitmap, String fileName) {
        String name = fileName + ".jpg";
        File file = new File(dir, name);
        files.add(file);
        imagesInProcess++;
        ImageUtils.saveImageAsyncParallel(file, bitmap, new ImageUtils.SaveImageEvents() {
            @Override
            public void onFinish(boolean isSuccess) {
                imagesInProcess--;
                LogUtils.d("onPicture", "is saved : " + isSuccess);
                if (imagesInProcess <= 0) {
                    linkImagesToMyProfile();
                }
            }
        });
    }

    private void prepareDir() {
        if (callback == null || PmBaseConfig.getApplicationContext() == null) {
            LogUtils.e("PmLiveSelfieHelper", "callback is null");
            return;
        }

        dir = new File(PmBaseConfig.getApplicationContext().getFilesDir(), "/scannedIds");
        dir.mkdirs();

    }

    private void linkImagesToMyProfile() {
        if (callback == null) {
            LogUtils.e("PmLiveSelfieHelper", "callback is null");
            return;
        }
        callback.onID_Ready(files);
//        ArrayList<String> filePaths = new ArrayList<>();
//
//        for (int i = 0; i < files.size(); i++) {
//            filePaths.add(Uri.fromFile(files.get(i)).toString());
//        }
//
//        callback.onID_Ready(filePaths);
    }

    public interface Events {
        void onID_Ready(ArrayList<File> files);
        Bitmap[] getID_Images();
        void clearID_Images();
    }
}
