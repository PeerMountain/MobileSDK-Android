package com.peermountain.sdk.ui.base;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;

public abstract class ToolbarActivity extends AppCompatActivity implements
        ToolbarFragment.ToolbarEvents{

    public ToolbarFragment topFragment;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(llMainView==null){
//            initParentToolbarViews();
//        }
//    }

    public void initParentToolbarViews(View llMainView) {
        this.llMainView = llMainView;
        getViews();
    }


    public ImageView pmMenuLeft, pmMenuRight;
    public TextView pmToolbarTitle;
    public View llMainView;

    private void getViews() {
        pmMenuLeft = findViewById(R.id.pmMenuLeft);
        pmMenuRight = findViewById(R.id.pmMenuRight);
        pmToolbarTitle = findViewById(R.id.pmToolbarTitle);
//        RippleUtils.setRippleEffectSquare(pmMenuLeft);
    }

    @Override
    public void setToolbarTitle(int resTitle, String title) {
        if (resTitle != -1) {
            pmToolbarTitle.setText(resTitle);
        } else {
            pmToolbarTitle.setText(title);
        }
    }

    @Override
    public void setLeftMenuButtonEvent(final View.OnClickListener listener) {
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
        if (res == -1) {
            pmMenuLeft.setImageResource(android.R.color.transparent);
        } else {
            pmMenuLeft.setImageResource(res);
        }
    }

    public int currentTheme = ToolbarFragment.THEME_DARK;

    @Override
    public void setToolbarTheme(int theme) {
        if (currentTheme == theme) return;
        switch (theme) {
            case ToolbarFragment.THEME_LIGHT:
                pmToolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.text_color_dark));
                llMainView.setBackgroundResource(R.color.white);
                break;
            default:
                pmToolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.text_color));
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
        if (res == -1) {
            pmMenuRight.setImageResource(android.R.color.transparent);
        } else {
            pmMenuRight.setImageResource(res);
        }
    }

    @Override
    public void setRightMenuButtonEvent(final View.OnClickListener listener) {
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
}
