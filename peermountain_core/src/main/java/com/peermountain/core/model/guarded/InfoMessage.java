package com.peermountain.core.model.guarded;

/**
 * Created by Galeen on 12/22/2017.
 */

public class InfoMessage {
    private String message;
    private int messageResource;

    public InfoMessage(String message) {
        this.message = message;
    }

    public InfoMessage(int messageResource) {
        this.messageResource = messageResource;
    }

    public String getMessage() {
        return message;
    }

    public int getMessageResource() {
        return messageResource;
    }
}
