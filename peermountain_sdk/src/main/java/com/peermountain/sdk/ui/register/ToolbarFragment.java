package com.peermountain.sdk.ui.register;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Galeen on 10/9/17.
 */

public abstract class ToolbarFragment extends Fragment {
    public ToolbarEvents mToolbarListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToolbarEvents) {
            mToolbarListener = (ToolbarEvents) context;
        } else {
            // TODO: 10/10/2017 may be don't throw exception
            throw new RuntimeException(context.toString()
                    + " must implement ToolbarEvents");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mToolbarListener!=null) mToolbarListener.setTopFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarListener = null;
    }

    public void setToolbar(int leftIconRes,int resTitle, View.OnClickListener listener){
        if (mToolbarListener == null) return;
        mToolbarListener.setMenuLeftIcon(leftIconRes);
        mToolbarListener.setToolbarTitle(resTitle, null);
        mToolbarListener.setMenuButtonEvent(listener);
    }
    public void setToolbar(int leftIconRes,String title, View.OnClickListener listener){
        if (mToolbarListener == null) return;
        mToolbarListener.setMenuLeftIcon(leftIconRes);
        mToolbarListener.setToolbarTitle(-1, title);
        mToolbarListener.setMenuButtonEvent(listener);
    }

    public void setTheme(int theme){
        if (mToolbarListener == null) return;
        mToolbarListener.setToolbarTheme(theme);
    }

    public static final int THEME_DARK = 1;
    public static final int THEME_LIGHT = 2;


    /**
     * @return if is handled
     */
    public boolean onBackPressed(){
        return false;
    }

    public interface ToolbarEvents{
        void setToolbarTitle(int resTitle, String title);
        void setMenuButtonEvent(View.OnClickListener listener);
        void setMenuLeftIcon(int res);
        void setToolbarTheme(int theme);
        void setTopFragment(ToolbarFragment topFragment);
    }
}
