package com.peermountain.sdk.ui.register;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.ToolbarFragment;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.fingerprint.FastLoginHelper;
import com.peermountain.sdk.utils.fingerprint.FingerprintHandler;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;
import com.peermountain.sdk.utils.ripple.RippleUtils;

public class RegisterPinFragment extends ToolbarFragment {
    private static final String ARG_LOGIN = "ARG_LOGIN";
    private OnFragmentInteractionListener mListener;

    public RegisterPinFragment() {
        // Required empty public constructor
    }

    private boolean isLogin;

    public static RegisterPinFragment newInstance(boolean isLogin) {
        RegisterPinFragment fragment = new RegisterPinFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_LOGIN, isLogin);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isLogin = getArguments().getBoolean(ARG_LOGIN, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_fragment_register_pin, container, false);
    }

    FastLoginHelper fastLoginHelper;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViews(view);
        setListeners();
        initToolbar();
        setUpView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!fastLoginHelper.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onBackPressed() {
        return onMenuBtnClick();
    }

    public void resetProfile() {
        PeerMountainManager.savePin(null);
        PeerMountainManager.saveFingerprint(false);
    }

    private void setUpView() {
        fastLoginHelper = new FastLoginHelper(getActivity(), "key", true);
        if (isLogin ) {
            if(!PeerMountainManager.getFingerprint()) {
                kbKeyFingerprint.setVisibility(View.GONE);
            }else {
                showFastLoginDialog();
            }
        }else{
            resetProfile();
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){//not available
                kbKeyFingerprint.setVisibility(View.GONE);
            }
        }
    }

    private void initToolbar() {
        setToolbar(R.drawable.pm_ic_logo, isLogin ? R.string.pm_login_title : R.string.pm_register_title, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuBtnClick();
            }
        });
    }

    ImageView[] dots = new ImageView[6];
    TextView pmTvRecover, pmTvMessage;
    LinearLayout kbKeyDelete, kbKeyFingerprint;
    LinearLayout[] keys = new LinearLayout[10];

    /**
     * type ff to fast get new views
     */
    private void getViews(View view) {
        dots[0] = view.findViewById(R.id.pmIvDot1);
        dots[1] = view.findViewById(R.id.pmIvDot2);
        dots[2] = view.findViewById(R.id.pmIvDot3);
        dots[3] = view.findViewById(R.id.pmIvDot4);
        dots[4] = view.findViewById(R.id.pmIvDot5);
        dots[5] = view.findViewById(R.id.pmIvDot6);
        kbKeyFingerprint = view.findViewById(R.id.kbKeyFingerprint);
        kbKeyDelete = view.findViewById(R.id.kbKeyDelete);
        keys[1] = view.findViewById(R.id.kbKey1);
        keys[2] = view.findViewById(R.id.kbKey2);
        keys[3] = view.findViewById(R.id.kbKey3);
        keys[4] = view.findViewById(R.id.kbKey4);
        keys[5] = view.findViewById(R.id.kbKey5);
        keys[6] = view.findViewById(R.id.kbKey6);
        keys[7] = view.findViewById(R.id.kbKey7);
        keys[8] = view.findViewById(R.id.kbKey8);
        keys[9] = view.findViewById(R.id.kbKey9);
        keys[0] = view.findViewById(R.id.kbKey0);
        pmTvRecover = view.findViewById(R.id.pmTvRecover);
        pmTvMessage = view.findViewById(R.id.pmTvMessage);
    }


    /**
     * type oclf to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        for (int i = keys.length - 1; i >= 0; i--) {
            keys[i].setTag(i);
            keys[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onKeyboardBtnClick(view);
                }
            });
//            RippleUtils.setRippleEffectSquare(keys[i]);
        }
        kbKeyDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onKeyboardDeleteBtnClick();
            }
        });


        kbKeyFingerprint.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View clickedView) {
                showFastLoginDialog();
            }
        });

        RippleUtils.setRippleEffectSquare(kbKeyFingerprint, pmTvRecover);//kbKeyDelete,

    }

    public boolean onMenuBtnClick() {
        if (isLogin) {
            if (mListener != null) mListener.onLoginCanceled();
        } else if (isRepeating) {
            isRepeating = false;
            pmTvMessage.setText(R.string.pm_please_enter_a_6_digits_pin_code);
            pin = new StringBuilder();
            pinRepeat = new StringBuilder();
            resetDots();
            if (mToolbarListener != null) {
                mToolbarListener.setMenuLeftIcon(R.drawable.pm_ic_logo);
            }
        }
        return isRepeating;
    }

    private void onKeyboardDeleteBtnClick() {
        StringBuilder pinBuf = getCurrentStringBuilder();
        if (pinBuf.length() > 0) {
            pinBuf.deleteCharAt(pinBuf.length() - 1);
            onPinChanged();
        }
    }

    private void onKeyboardBtnClick(View view) {
        StringBuilder pinBuf = getCurrentStringBuilder();
        if (pinBuf.length() < 6) {
            pinBuf.append(view.getTag());
            onPinChanged();
        }
    }

    private StringBuilder getCurrentStringBuilder() {
        return isRepeating ? pinRepeat : pin;
    }

    private StringBuilder pin = new StringBuilder();
    private StringBuilder pinRepeat = new StringBuilder();
    private boolean isRepeating = false;

    private void onPinChanged() {
        resetDots();
        StringBuilder pinBuf = getCurrentStringBuilder();
        if (pinBuf.length() == 6) {
            onPinEntered();
        }
    }

    private void onPinEntered() {
        if (isLogin) {
            if (pin.toString().equalsIgnoreCase(PeerMountainManager.getPin())) {
                if (mListener != null) {
                    mListener.onLogin();
                }
            } else {
                DialogUtils.showError(getActivity(), R.string.pm_pin_match_error_msg);
            }
        } else if (isRepeating) {
            if (pin.toString().equalsIgnoreCase(pinRepeat.toString())) {
                PeerMountainManager.savePin(pin.toString());
                if (mListener != null) {
                    mListener.goToRegisterKeyWords();
                }
            } else {
                DialogUtils.showError(getActivity(), R.string.pm_pin_match_error_msg);
            }
        } else {
            if (mToolbarListener != null) {
                mToolbarListener.setMenuLeftIcon(R.drawable.pm_ic_arrow_back_24dp);
            }
            pmTvMessage.setText(R.string.pm_please_confirm_your_pin_code);
            isRepeating = true;
            resetDots();
        }
    }

    private void resetDots() {
        //resetProfile circles
        int lastNumberPosition = getCurrentStringBuilder().length();
        for (int i = 0; i < dots.length; i++) {
            if (i < lastNumberPosition) {
                dots[i].setImageResource(R.drawable.pm_blue_dot);
            } else {
                dots[i].setImageResource(R.drawable.pm_white_dot);
            }
        }
    }

    private void showFastLoginDialog() {
        if (fastLoginHelper != null) {
            fastLoginHelper.autoPopUp = false;
            fastLoginHelper.directScan = isLogin;
            fastLoginHelper.checkFastLogin(
                    new FingerprintHandler.FingerprintEvents() {
                        @Override
                        public void onSuccess() {
                            onFingerprintAuthorized();
                        }
                    }, isLogin, true, false);
        }
    }

    private void onFingerprintAuthorized() {
        if (isLogin) {
            if (mListener != null) {
                mListener.onLogin();
            }
        } else {
            PeerMountainManager.saveFingerprint(true);
            if (mListener != null) {
                mListener.goToRegisterKeyWords();
            }
            DialogUtils.showInfoSnackbar(getActivity(), R.string.pm_message_fast_login_enabled);
        }
    }


    public interface OnFragmentInteractionListener {
        void goToRegisterKeyWords();

        void onLogin();

        void onLoginCanceled();


    }

}
