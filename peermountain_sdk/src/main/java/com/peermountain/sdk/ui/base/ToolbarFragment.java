package com.peermountain.sdk.ui.base;

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

    public static final int MENU_HIDE = -1;
    public static final int MENU_MAIN_SHOW = -2;

    public void setToolbar(int leftIconRes,int resTitle, View.OnClickListener listener){
        setToolbar(leftIconRes,MENU_HIDE,resTitle,listener,null);
    }

    public void setToolbar(int leftIconRes,int rightIconRes,int resTitle, View.OnClickListener listener, View.OnClickListener listenerRight){
        if (mToolbarListener == null) return;
        mToolbarListener.setMenuLeftIcon(leftIconRes);
        mToolbarListener.setToolbarTitle(resTitle, null);
        mToolbarListener.setLeftMenuButtonEvent(listener);
        mToolbarListener.setMenuRightIcon(rightIconRes);
        mToolbarListener.setRightMenuButtonEvent(listenerRight);
    }
    public void setToolbar(int leftIconRes,String title, View.OnClickListener listener){
        if (mToolbarListener == null) return;
        mToolbarListener.setMenuLeftIcon(leftIconRes);
        mToolbarListener.setToolbarTitle(-1, title);
        mToolbarListener.setLeftMenuButtonEvent(listener);
    }

    public void setTheme(int theme){
        if (mToolbarListener == null) return;
        mToolbarListener.setToolbarTheme(theme);
    }

    public void hideToolbar(){
        if (mToolbarListener == null) return;
        mToolbarListener.hideToolbar();
    }

    public void lockMenu(boolean lock){
        if (mToolbarListener == null) return;
        mToolbarListener.lockMenu(lock);
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
        void setLeftMenuButtonEvent(View.OnClickListener listener);
        void setMenuLeftIcon(int res);
        void setRightMenuButtonEvent(View.OnClickListener listener);
        void setMenuRightIcon(int res);
        void setToolbarTheme(int theme);
        void hideToolbar();
        void setTopFragment(ToolbarFragment topFragment);
        void lockMenu(boolean lock);
    }
}
