package com.peermountain.core.callbacks;

import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterfaceCallback;

/**
 * Created by Galeen on 10/5/2017.
 */

public interface ScanIdCallback extends AXTCaptureInterfaceCallback {
    void onNoNetwork();
    void onStartScanning();
//    void onInitError(int errorCode);
}
