package com.peermountain.core.model.guarded;

import android.net.Uri;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Galeen on 5/10/2018.
 */
public class ImageResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private String imageUri;

    public ImageResult() {
    }

    public ImageResult(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public File takeImageAsFile(){
        if(imageUri==null) return null;
        return new File(Uri.parse(imageUri).getPath());
    }
}
