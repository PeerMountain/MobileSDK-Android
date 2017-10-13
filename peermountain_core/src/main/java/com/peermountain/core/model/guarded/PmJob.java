package com.peermountain.core.model.guarded;

/**
 * Created by Galeen on 10/12/2017.
 */

public class PmJob {
    private String activity, information;
    private boolean fake;

    public PmJob() {
    }

    public PmJob(String activity, String information) {
        this.activity = activity;
        this.information = information;
    }

    public PmJob(boolean fake) {
        this.fake = fake;
        activity = "Locked";
        information = "Locked Information";
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public boolean isFake() {
        return fake;
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
