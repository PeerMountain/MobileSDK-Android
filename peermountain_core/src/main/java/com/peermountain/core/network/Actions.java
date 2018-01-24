package com.peermountain.core.network;

import android.support.annotation.NonNull;

import java.io.File;

/**
 * Created by Galeen on 1/2/2018.
 */

public class Actions {

    @NonNull
    static Action getUser() {
        return new Action(Action.GET,
                NetConstants.API_USERS,
                null);
    }

    @NonNull
    static Action getForm(File file) {
        Action action = new Action(Action.DOWNLOAD_FILE,
                "https://www.dropbox.com/s/9kj12067gqhst42/Sample%20Form.xml?dl=1",
                "",file);
        action.isFullUrl = true;
        return action;
    }


}
