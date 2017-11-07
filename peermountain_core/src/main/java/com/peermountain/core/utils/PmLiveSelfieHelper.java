package com.peermountain.core.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.peermountain.core.camera.CameraActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Galeen on 11/7/17.
 */

public class PmLiveSelfieHelper {
    private File dir;
    private ArrayList<File> files = new ArrayList<>();
    private int imagesInProcess = 0;
    private Events callback;

    public PmLiveSelfieHelper(Events callback) {
        this.callback = callback;
    }

    public void saveLiveSelfie(){
        prepareDir();
        imagesInProcess = 0;
        for (Bitmap bitmap : CameraActivity.bitmaps) {
            saveFrame(bitmap);
        }
        CameraActivity.bitmaps=null;
    }

    private void saveFrame(Bitmap bitmap) {
        String name = System.currentTimeMillis()+imagesInProcess + ".jpg";
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
        if (callback == null) {
            LogUtils.e("PmLiveSelfieHelper","callback is null");
            return;
        }
        // TODO: 11/6/17 set dir in local storage?
        dir = new File(callback.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"/liveSelfie");
        if(dir.exists()){
            if(dir.listFiles().length>0) {
                for (File file : dir.listFiles()) {
                    file.delete();
                }
            }
        }else {
            dir.mkdirs();
        }
    }

    private void linkImagesToMyProfile() {
        if (callback == null) {
            LogUtils.e("PmLiveSelfieHelper","callback is null");
            return;
        }
        ArrayList<String> liveSelfie = new ArrayList<>();
        if (files.size() == 0) return;
        for (int i = 0; i < files.size(); i++) {
            liveSelfie.add(Uri.fromFile(files.get(i)).toString());
        }
        callback.onLiveSelfieReady(liveSelfie);
    }

    public interface Events{
        Activity getActivity();
        void onLiveSelfieReady(ArrayList<String> liveSelfie);
    }
}
