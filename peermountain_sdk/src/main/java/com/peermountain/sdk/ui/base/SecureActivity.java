package com.peermountain.sdk.ui.base;

import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by Galeen on 12.7.2017 г..
 */

public abstract class SecureActivity extends ToolbarActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState );
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }
}
