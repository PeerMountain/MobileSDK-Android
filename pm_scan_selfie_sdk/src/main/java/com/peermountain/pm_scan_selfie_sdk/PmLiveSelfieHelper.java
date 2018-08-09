package com.peermountain.pm_scan_selfie_sdk;

import android.graphics.Bitmap;

import com.peermountain.common.PmBaseConfig;
import com.peermountain.common.utils.ImageUtils;
import com.peermountain.common.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Galeen on 11/7/17.
 */

public class PmLiveSelfieHelper {
    private boolean isId = false;
    private File dir;
    private ArrayList<File> files = new ArrayList<>();
    private int imagesInProcess = 0;
    private Events callback;

    public PmLiveSelfieHelper(Events callback) {
        this.callback = callback;
    }

    public PmLiveSelfieHelper(boolean isId, Events callback) {
        this.isId = isId;
        this.callback = callback;
    }

    public void saveLiveSelfie(ArrayList<Bitmap> bitmaps) {
        prepareDir();
        imagesInProcess = 0;
        for (Bitmap bitmap : bitmaps) {
            saveFrame(bitmap);
        }
        if (callback != null) callback.clearCashBitmaps();
    }


    private void saveFrame(Bitmap bitmap) {
        String name = "liveSelfie_" + imagesInProcess + ".jpg";
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

//        if (isId) {
//            dir = new File(PmBaseConfig.getApplicationContext().getFilesDir(), "/scannedIds");
//            dir.mkdirs();
//        } else {//set dir in local storage?
        dir = new File(PmBaseConfig.getApplicationContext().getFilesDir(),
//                    PmBaseConfig.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "/liveSelfie");
        if (dir.exists()) {
            if (dir.listFiles().length > 0) {
                for (File file : dir.listFiles()) {
                    file.delete();
                }
            }
        } else {
            dir.mkdirs();
        }
//        }
    }

    private void linkImagesToMyProfile() {
        if (callback == null) {
            LogUtils.e("PmLiveSelfieHelper", "callback is null");
            return;
        }
//        ArrayList<String> liveSelfie = new ArrayList<>();
//        if (files.size() == 0) return;
//        for (int i = 0; i < files.size(); i++) {
//            liveSelfie.add(Uri.fromFile(files.get(i)).toString());
//        }
        callback.onLiveSelfieReady(files);
    }

    public interface Events {
        void onLiveSelfieReady(ArrayList<File> liveSelfie);

        void clearCashBitmaps();
    }
}
