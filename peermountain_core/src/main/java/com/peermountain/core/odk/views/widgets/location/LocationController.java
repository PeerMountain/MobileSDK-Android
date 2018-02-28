package com.peermountain.core.odk.views.widgets.location;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.peermountain.core.odk.views.widgets.base.QuestionWidget;

import java.util.ArrayList;

/**
 * Created by Galeen on 1/17/2018.
 */

public class LocationController {
    public static final int REQUEST_CHECK_SETTINGS = 112;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 33;
    private Activity activity;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Location location;
    private String progressMessage;
    private Boolean isMandatoryRequest = true;
    private boolean withProgress;

    public LocationController(Activity activity) {
        this.activity = activity;
        createLocationRequest();
        initLocationCallback();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    private ArrayList<Callback> callbacks = new ArrayList<>();

    public void requestLocation(Callback callback, String progressMessage, Boolean isMandatoryRequest, boolean withProgress) {
        this.withProgress = withProgress;
        this.isMandatoryRequest = isMandatoryRequest;
        callbacks.add(callback);
        this.progressMessage = progressMessage;
        requestLocation();
    }

    private void requestLocation() {
        testConditionsAndRequestLocation();
    }

    private void testConditionsAndRequestLocation() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize location requests here.
                getLocation();
            }
        });
        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (isSilent()) {
                    notifyForLocationCanceled();
                } else if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private boolean isSilent() {
        return isMandatoryRequest == null;
    }

    private boolean isMandatory() {
        return isMandatoryRequest != null && isMandatoryRequest;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (isSilent()) {
//                notifyForLocationCanceled();
//            } else {
                requestLocationPermission();
//            }
            return;
        }
        if (withProgress) {
            notifyForProgress();
//            activity.getBaseUI().showProgressDialog(progressMessage,
//                    new DialogInterface.OnCancelListener() {
//                        @Override
//                        public void onCancel(DialogInterface dialogInterface) {
//                            stopListening();
//                            //notify activity location is canceled
//                            notifyForLocationCanceled();
//                        }
//                    });
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    private void requestLocationPermission() {
        for (Callback callback : callbacks) {
            if (callback != null) {
                callback.requestLocationPermission(isMandatory(), new QuestionWidget.PermissionCallback() {
                    @Override
                    public void onPermission(int[] grantResults) {
                        if(grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                            getLocation();
                        }else{
                            notifyForLocationCanceled();
                        }
                    }
                });
                return;//call for permission only once
            }
        }
    }

    private void initLocationCallback() {
        this.mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                stopListening();
                if (locationResult.getLocations().size() > 0) {
                    location = locationResult.getLocations().get(locationResult.getLocations().size() - 1);
                    notifyForLocation(location);
                } else {
                    notifyForLocation(null);
                }
            }
        };
    }

    private void notifyForLocation(Location location) {
        for (Callback callback : callbacks) {
            if (callback != null) callback.onLocation(location);
        }
        callbacks.clear();
    }

    private void notifyForLocationCanceled() {
        for (Callback callback : callbacks) {
            if (callback != null) callback.onLocationCanceled();
        }
        callbacks.clear();
    }

    private void notifyForProgress() {
        for (Callback callback : callbacks) {
            if (callback != null) callback.showProgress();
        }
    }

    private void stopListening() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public interface Callback {
        void onLocation(Location location);

        void onLocationCanceled();

        void showProgress();

        void requestLocationPermission(boolean isMandatory, QuestionWidget.PermissionCallback callback);
    }

}
