package com.peermountain.core.model.guarded;

import java.io.File;

/**
 * Created by Galeen on 10/16/17.
 */

public class FileDocument {
    public static final String TYPE_IMAGE = "image/*";
    public static final String TYPE_PDF = "image/*";
    private String uri;
    public File file;
    private String type;// = TYPE_IMAGE;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
