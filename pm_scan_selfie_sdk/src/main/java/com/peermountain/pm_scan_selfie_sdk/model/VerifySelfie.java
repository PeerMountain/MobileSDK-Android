package com.peermountain.pm_scan_selfie_sdk.model;

/**
 * Created by Galeen on 5/14/2018.
 */
public class VerifySelfie {
    private boolean liveliness, humanFace, faceMatch;

    public boolean checkIsValid() {
        return liveliness && humanFace && faceMatch;
    }

    public boolean isLiveliness() {
        return liveliness;
    }

    public void setLiveliness(boolean liveliness) {
        this.liveliness = liveliness;
    }

    public boolean isHumanFace() {
        return humanFace;
    }

    public void setHumanFace(boolean humanFace) {
        this.humanFace = humanFace;
    }

    public boolean isFaceMatch() {
        return faceMatch;
    }

    public void setFaceMatch(boolean faceMatch) {
        this.faceMatch = faceMatch;
    }
}
