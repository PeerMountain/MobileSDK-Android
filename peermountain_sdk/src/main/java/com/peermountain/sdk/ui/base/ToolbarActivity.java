package com.peermountain.sdk.ui.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.peermountain.pm_livecycle.ToolbarFragment;
import com.peermountain.sdk.R;
import com.peermountain.pm_livecycle.base.BaseActivity;
import com.peermountain.pm_livecycle.base.BaseViewModel;
import com.peermountain.common.utils.ripple.RippleOnClickListener;

public abstract class ToolbarActivity<T extends BaseViewModel> extends BaseActivity<T> implements
        ToolbarFragment.ToolbarEvents, HomeToolbarFragment.HomeToolbarEvents{

    public ToolbarFragment topFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void setContentView(@LayoutRes int layoutResID,@IdRes int mainViewID) {
        super.setContentView(layoutResID);
        llMainView = findViewById(mainViewID);
        getViews();
    }

    public void initParentToolbarViews(View llMainView) {
        this.llMainView = llMainView;
    }


    public ImageView pmMenuLeft, pmMenuRight;
    public TextView pmToolbarTitle;
    public View llMainView,toolbar;//we need main view to change the background of it

    private void getViews() {
        pmMenuLeft = findViewById(R.id.pmMenuLeft);
        pmMenuRight = findViewById(R.id.pmMenuRight);
        pmToolbarTitle = findViewById(R.id.pmToolbarTitle);
        toolbar = findViewById(R.id.toolbar);
//        RippleUtils.setRippleEffectSquare(pmMenuLeft);
    }

    @Override
    public void hideToolbar() {
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void setToolbarTitle(int resTitle, String title) {
        toolbar.setVisibility(View.VISIBLE);
        if (resTitle != ToolbarFragment.MENU_HIDE) {
            pmToolbarTitle.setText(resTitle);
        } else {
            pmToolbarTitle.setText(title);
        }
    }

    @Override
    public void setLeftMenuButtonEvent(final View.OnClickListener listener) {
        toolbar.setVisibility(View.VISIBLE);
        if (listener != null) {
            pmMenuLeft.setOnClickListener(new RippleOnClickListener() {
                @Override
                public void onClickListener(View view) {
                    listener.onClick(view);
                }
            });
        } else {
            pmMenuLeft.setOnClickListener(null);
        }
    }

    @Override
    public void setMenuLeftIcon(int res) {
        toolbar.setVisibility(View.VISIBLE);
        if (res == ToolbarFragment.MENU_HIDE) {
            pmMenuLeft.setImageResource(android.R.color.transparent);
        } else {
            pmMenuLeft.setImageResource(res);
        }
    }

    public int currentTheme = ToolbarFragment.THEME_DARK;

    @Override
    public void setToolbarTheme(int theme) {
        toolbar.setVisibility(View.VISIBLE);
        if (currentTheme == theme) return;
        switch (theme) {
            case ToolbarFragment.THEME_LIGHT:
                pmToolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.pm_text_color_dark));
                pmToolbarTitle.setShadowLayer(getResources().getInteger(R.integer.pm_tv_shadow_radius),
                        getResources().getInteger(R.integer.pm_tv_shadow_radius),
                        getResources().getInteger(R.integer.pm_tv_shadow_radius),
                        Color.TRANSPARENT);
                llMainView.setBackgroundResource(R.color.pm_theme_light_bkg);
                break;
            default:
                pmToolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.pm_text_color));
                pmToolbarTitle.setShadowLayer(getResources().getInteger(R.integer.pm_tv_shadow_radius),
                        getResources().getInteger(R.integer.pm_tv_shadow_radius),
                        getResources().getInteger(R.integer.pm_tv_shadow_radius),
                        ContextCompat.getColor(this,R.color.pm_text_shadow));
                llMainView.setBackgroundResource(R.drawable.pm_bkg);
        }
        currentTheme = theme;
    }

    @Override
    public void setTopFragment(ToolbarFragment topFragment) {
        this.topFragment = topFragment;
    }

    @Override
    public void setMenuRightIcon(int res) {
        toolbar.setVisibility(View.VISIBLE);
        if (res == ToolbarFragment.MENU_HIDE) {
            pmMenuRight.setImageResource(android.R.color.transparent);
        } else {
            pmMenuRight.setImageResource(res);
        }
    }

    @Override
    public void setRightMenuButtonEvent(final View.OnClickListener listener) {
        toolbar.setVisibility(View.VISIBLE);
        if (listener != null) {
            pmMenuRight.setOnClickListener(new RippleOnClickListener() {
                @Override
                public void onClickListener(View view) {
                    listener.onClick(view);
                }
            });
        } else {
            pmMenuRight.setOnClickListener(null);
        }
    }

    @Override
    public View.OnClickListener getOpenMenuListener() {
        return null;
    }


    @Override
    public View.OnClickListener getOpenBarcodeListener() {
        return null;
    }
}
