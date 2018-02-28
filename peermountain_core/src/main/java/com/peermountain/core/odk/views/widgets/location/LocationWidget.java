package com.peermountain.core.odk.views.widgets.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.peermountain.core.R;
import com.peermountain.core.odk.views.widgets.base.QuestionWidget;
import com.peermountain.core.views.PeerMountainTextView;

import org.javarosa.core.model.data.GeoPointData;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryPrompt;

/**
 * Created by Galeen on 2/28/2018.
 * Get current location and set as answer
 */

public class LocationWidget extends QuestionWidget {
    private Location myLocation;
    private Activity activity;
    private LocationController locationController;
    private Events activityCallback;
    private LocationController.Callback locationCallback;
    private PeerMountainTextView tvButton, tvLabel;
    private ProgressBar progressBar;
    private GeoPointData answeredLocation;

    public LocationWidget(Activity activity, FormEntryPrompt prompt) {
        super(activity, prompt);
        this.activity = activity;
        if (activity instanceof Events) {
            activityCallback = (Events) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " QuestionWidget.Events");
        }
        locationController = new LocationController(activity);
        setLocationCallback();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            ViewGroup viewParent = getAnswerViewParent();
            inflater.inflate(R.layout.pm_location_widget, viewParent);
            tvButton = viewParent.findViewById(R.id.pmBtnLocation);
            tvLabel = viewParent.findViewById(R.id.pmTvLocationData);
            progressBar = viewParent.findViewById(R.id.pmProgress);
            if (getFormEntryPrompt().getAnswerValue() != null) {
                answeredLocation = new GeoPointData((double[])getFormEntryPrompt().getAnswerValue().getValue());
                tvLabel.setText(answeredLocation.getDisplayText());
            }
            tvButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestLocation();
                }
            });
        }
    }

    public void requestLocation() {
        locationController.requestLocation(locationCallback, "Getting location...",
                true, true);
        tvButton.setEnabled(false);
    }

    @Override
    public IAnswerData getAnswer() {
        if (myLocation == null) {
            if(answeredLocation !=null){
                return answeredLocation;
            }
            return null;
        } else {
            double[] gp = new double[4];
            gp[0] = myLocation.getLatitude();
            gp[1] = myLocation.getLongitude();
            gp[2] = myLocation.getAltitude();
            gp[3] = myLocation.getAccuracy();
            return new GeoPointData(gp);
        }
    }

    @Override
    public void clearAnswer() {
        myLocation = null;
        tvButton.setEnabled(true);
        tvLabel.setText("");
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setFocus(Context context) {

    }

    @Override
    public boolean canGetFocus() {
        return false;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {

    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LocationController.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                requestLocation();
            } else {
                clearAnswer();
            }
            return true;
        }
        return false;
    }

    /**
     * This is a callback for LocationController Events
     */
    private void setLocationCallback() {
        locationCallback = new LocationController.Callback() {
            @Override
            public void onLocation(Location location) {
                myLocation = location;
                tvLabel.setText(location.toString());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLocationCanceled() {
                clearAnswer();
            }

            @Override
            public void showProgress() {
                progressBar.setVisibility(VISIBLE);
            }

            @Override
            public void requestLocationPermission(boolean isMandatory, PermissionCallback callback) {
                activityCallback.requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LocationController.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                        , isMandatory, callback);
            }
        };
    }
}
