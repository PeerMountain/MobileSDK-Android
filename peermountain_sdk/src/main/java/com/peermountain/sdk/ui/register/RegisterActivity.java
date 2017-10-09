package com.peermountain.sdk.ui.register;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.PmFragmentUtils;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;

public class RegisterActivity extends AppCompatActivity implements
        ToolbarFragments.ToolbarEvents, RegisterPinFragment.OnFragmentInteractionListener,
        RegisterKeywordsFragment.OnFragmentInteractionListener {
    @IdRes
    int containerId = R.id.flContainer;

    RegisterPinFragment registerPinFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pm_activity_register);
        showPinFragment();
//        showKeywordsFragment();
        initToolbar();
    }


    private void showPinFragment() {
        PmFragmentUtils.FragmentBuilder fb = PmFragmentUtils.init(this, containerId);
        fb.addToBackStack(false);
        registerPinFragment = RegisterPinFragment.newInstance();
        fb.replace(registerPinFragment);
    }

    private void showKeywordsFragment() {
        PmFragmentUtils.FragmentBuilder fb = PmFragmentUtils.init(this, containerId);
        fb.addToBackStack(false);
        fb.replace(RegisterKeywordsFragment.newInstance());
    }

    @Override
    public void goToRegisterKeyWords() {

    }

    ImageView pmMenuLeft;
    TextView pmToolbarTitle;

    private void initToolbar() {
        pmMenuLeft = findViewById(R.id.pmMenuLeft);
        pmToolbarTitle = findViewById(R.id.pmToolbarTitle);
//        RippleUtils.setRippleEffectSquare(pmMenuLeft);
    }

    @Override
    public void setToolbarTitle(int resTitle, String title) {
        if (title == null) {
            pmToolbarTitle.setText(R.string.pm_register_title);
        } else {
            pmToolbarTitle.setText(resTitle);
        }
    }

    @Override
    public void setMenuButtonEvent(final View.OnClickListener listener) {
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
        if(res==-1) {
            pmMenuLeft.setImageResource(android.R.color.transparent);
        }else{
            pmMenuLeft.setImageResource(res);
        }
    }
}
