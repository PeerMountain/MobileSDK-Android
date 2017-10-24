package com.peermountain.core.model.guarded;

/**
 * Created by Galeen on 10/12/2017.
 */

public class PmJob {
    public static final int TYPE_CARD = 580;
    public static final int TYPE_ELITE_CARD = 260;
    public static final int TYPE_WORLD_CARD = 856;
    public static final int TYPE_SV_CARD = 43;

    private String activity, information;
    private int type = TYPE_CARD;
    private boolean open;

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
}
