package com.peermountain.sdk.ui.base;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.View;

import com.peermountain.sdk.R;

/**
 * Created by Galeen on 10/12/2017.
 */

public abstract class HomeToolbarFragment extends ToolbarFragment {
    public HomeToolbarEvents homeToolbarEvents;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeToolbarEvents) {
            homeToolbarEvents = (HomeToolbarEvents) context;
        } else {
            // TODO: 10/10/2017 may be don't throw exception
            throw new RuntimeException(context.toString()
                    + " must implement HomeToolbarEvents");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeToolbarEvents = null;
    }



    public void setHomeToolbar(@StringRes int title){
        setToolbar(R.drawable.pm_ic_logo_white, R.drawable.pm_ic_qrcode, title, homeToolbarEvents!=null?homeToolbarEvents.getOpenMenuListener():null, homeToolbarEvents!=null?homeToolbarEvents.getOpenBarcodeListener():null);
    }

    public interface HomeToolbarEvents{
        View.OnClickListener getOpenMenuListener();
        View.OnClickListener getOpenBarcodeListener();
    }
}
