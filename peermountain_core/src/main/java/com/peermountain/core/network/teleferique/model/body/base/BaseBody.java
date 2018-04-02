package com.peermountain.core.network.teleferique.model.body.base;

import com.peermountain.core.network.teleferique.TfConstants;
import com.peermountain.core.network.teleferique.model.body.MessageBodyObject;

/**
 * Created by Galeen on 3/14/2018.
 * Invitation Response / Registration Request
 */

public class BaseBody implements MessageBodyObject {
    public transient String time;

    public BaseBody(String time) {
        this.time = time;
    }


    @Override
    public int takeBodyType() {
        return TfConstants.BODY_TYPE_INVITATION;
    }

    @Override
    public String takeTime() {
        return time;
    }

}