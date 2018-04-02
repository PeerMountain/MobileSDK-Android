package com.peermountain.core.network.teleferique.model.body.base;

import com.peermountain.core.network.teleferique.model.SendObject;

import java.io.Serializable;

/**
 * Created by Galeen on 3/14/2018.
 */

public abstract class BaseBuilder implements Serializable {
    private int bodyType;//from TfConstants
    private  String time;//from server

    public BaseBuilder(int bodyType, String time) {
        this.bodyType = bodyType;
        this.time = time;
    }

    public abstract SendObject build();

    public int getBodyType() {
        return bodyType;
    }

    public BaseBuilder setBodyType(int bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    public String getTime() {
        return time;
    }

    public BaseBuilder setTime(String time) {
        this.time = time;
        return this;
    }
}
