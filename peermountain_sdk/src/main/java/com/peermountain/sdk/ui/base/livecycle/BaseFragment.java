package com.peermountain.sdk.ui.base.livecycle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;


/**
 * Created by Galeen on 12/22/2017.
 * Has BaseUI object to implement the main logic for show/hide progress, errors and messages
 * Each fragment with presenter as BaseViewModel should extend this one to inherit BaseUI functionality
 */

public class BaseFragment extends Fragment {
    private BaseUI baseUI;
    private Events eventsCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseUI = new BaseUI(this, getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        eventsCallback = (Events) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        baseUI.setParentView(view);
        if(eventsCallback!=null) eventsCallback.setBaseTopFragment(this);
    }

    @Override
    public void onDetach() {
        baseUI.hideProgresses();
        eventsCallback=null;
        super.onDetach();
    }

    public BaseUI getBaseUI() {
        return baseUI;
    }

    public interface Events{
        void setBaseTopFragment(BaseFragment topFragment);
    }
}
