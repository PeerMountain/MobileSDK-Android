package com.peermountain.core.model.guarded;

import java.io.File;

/**
 * Created by Galeen on 10/16/17.
 */

public class FileDocument {
    public static final String TYPE_IMAGE = "image/*";
    public static final String TYPE_PDF = "application/pdf";
    private String imageUri;
    private String fileUri;
    public File file;
    private String type;// = TYPE_IMAGE;

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }
}
