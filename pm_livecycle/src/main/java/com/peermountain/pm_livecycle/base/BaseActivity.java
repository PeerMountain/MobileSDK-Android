package com.peermountain.pm_livecycle.base;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Galeen on 12/20/2017.
 * Has BaseUI object to implement the main logic for show/hide progress, errors and messages
 * Each activity with presenter as BaseViewModel should extend this one to inherit BaseUI functionality
 * keep track of top fragment in main container
 */

public abstract class BaseActivity<T extends BaseViewModel> extends AppCompatActivity implements BaseFragment.Events {
    public static final int REQUEST_CHECK_SETTINGS = 112;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 33;
    private BaseUI baseUI;
    public BaseFragment topBaseFragment;
    public T activityViewModel;
//    private LocationController locationController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a ViewModel the first time the system calls an presenterCallback's onCreate() method.
        // Re-created activities receive the same MyViewModel instance created by the first presenterCallback.
        baseUI = new BaseUI(this, findViewById(android.R.id.content), this);
        baseUI.setBaseViewModel(activityViewModel =
                ViewModelProviders.of(this).get(getViewModel()));
        setObservers();
//        locationController = new LocationController(this);
    }

    public BaseUI getBaseUI() {
        return baseUI;
    }

    @Override
    protected void onDestroy() {
        baseUI.hideProgresses();
        super.onDestroy();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CHECK_SETTINGS) {
//            if(resultCode == RESULT_OK){
//                locationController.requestLocation();
//            }else{
//                locationController.notifyForLocationCanceled();
//            }
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    locationController.requestLocation();
//                } else {
//                    if (isMandatoryRequest){
//                        showPermissionExplanationDialog();
//                    }else if(callback!=null){
//                        callback.onPermissionDenied();
//                    }
//                }
//            }
//
//        }
//    }

//    void showPermissionExplanationDialog() {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setMessage(R.string.no_geo_permission);
//        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                requestLocationPermission(isMandatoryRequest,callback);
//            }
//        });
//        dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if(callback!=null){
//                    callback.onPermissionDenied();
//                }
//            }
//        });
//        dialog.show();
//    }
//
//    private boolean isMandatoryRequest = false;
//    private LocationController.PermissionCallback callback;

//    public void requestLocationPermission(boolean isMandatoryRequest,LocationController.PermissionCallback callback) {
//        this.callback = callback;
//        this.isMandatoryRequest = isMandatoryRequest;
//        ActivityCompat.requestPermissions(BaseActivity.this,
//                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//    }

//    /**
//     *
//     * @param callback
//     * @param progressMessage - message to show in progress dialog
//     * @param isMandatoryRequest true(show explanation dialog), false(ask for permission but don't (show explanation dialog)), null for silent request
//     */
//    public void requestLocation(LocationController.Callback callback, String progressMessage, Boolean isMandatoryRequest,boolean withProgress) {
//        locationController.requestLocation(callback, progressMessage, isMandatoryRequest, withProgress);
//    }

    @Override
    public void setBaseTopFragment(BaseFragment topFragment) {
        this.topBaseFragment = topFragment;
    }

    protected abstract void setObservers();

    protected abstract @NonNull Class<T> getViewModel();
}
