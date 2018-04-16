package com.peermountain.core.network.teleferique.model.body.base;

/**
 * Created by Galeen on 3/14/2018.
 * Invitation Response / Registration Request
 */

public abstract class BaseBody {
    private String time;

    public BaseBody(String time) {
        this.time = time;
    }

    public abstract int takeBodyType();

    public abstract String takeMessageType() ;

    public String takeTime(){
        return time;
    }

}