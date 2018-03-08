package com.peermountain.core.model.guarded;

import java.io.File;

/**
 * Created by Galeen on 10/12/2017.
 */

public class PmJob {
    public static final int TYPE_CARD = 580;
    public static final int TYPE_ELITE_CARD = 260;
    public static final int TYPE_WORLD_CARD = 856;
    public static final int TYPE_SV_CARD = 43;

    private String activity, information, xFormPath;
    private int type = TYPE_CARD;
    private boolean open;
    private File file;

    public PmJob() {
    }

    public PmJob(String activity, String information) {
        this.activity = activity;
        this.information = information;
        open = true;
    }

    public PmJob(String activity, int type) {
        this.activity = activity;
        this.type = type;
        open = false;
    }

    public PmJob(boolean open) {
        this.open = open;
        activity = "Locked";
        information = "Locked Information";
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getxFormPath() {
        return xFormPath;
    }

    public void setxFormPath(String xFormPath) {
        this.xFormPath = xFormPath;
    }

    public File getFile() {
        if(file==null){
            file = new File(xFormPath);
        }
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
