package com.peermountain.sdk.ui.register;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.ToolbarFragment;
import com.peermountain.sdk.utils.ripple.RippleUtils;

public class SecurityTutorialFragment extends ToolbarFragment implements View.OnTouchListener {
    ViewFlipper vf;
    private float downXValue;
    private OnFragmentInteractionListener mListener;
    View view;
    LinearLayout layMain;
    TextView tvCurrentPageTitle, tvCurrentPageMessage1, tvCurrentPageMessage2, tvCurrentPageNext;
    View btnNext;
    AlertDialog tosDialog;


    public SecurityTutorialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCallbacks();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pm_fragment_security_tutorial, container, false);
        vf = (ViewFlipper) view.findViewById(R.id.details);
        tvCurrentPageTitle = (TextView) view.findViewById(R.id.tvCurrentPageTitle);
        tvCurrentPageMessage1 = (TextView) view.findViewById(R.id.tvCurrentPageMessage1);
        tvCurrentPageMessage2 = (TextView) view.findViewById(R.id.tvCurrentPageMessage2);
        tvCurrentPageNext = (TextView) view.findViewById(R.id.tvCurrentPageNext);
        btnNext = view.findViewById(R.id.pmIvNext);
        layMain = view.findViewById(R.id.layout_main);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideToolbar();
        setText();
        setListeners();
    }

    public void setListeners() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vf.getDisplayedChild() < vf.getChildCount() - 1) {
                    showNext();
                } else {
                    endIntro();
                }
            }
        };
        btnNext.setOnClickListener(onClickListener);
        tvCurrentPageNext.setOnClickListener(onClickListener);
        //startFlipper();
        layMain.setOnTouchListener(SecurityTutorialFragment.this);
    }


    private void setFlipperListener() {
        vf.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_left));
        vf.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_out_left));
        vf.getInAnimation().setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
                setText();
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                int displayedChild = vf.getDisplayedChild();
                int childCount = vf.getChildCount();
                if (displayedChild == childCount - 1) {
                   // if (!isInfinite) {
                        vf.stopFlipping();
                        vf.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                                R.anim.slide_right));
                        vf.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                                R.anim.slide_out_right));
                    //}
                }
            }
        });
    }

    private void startFlipper() {
        /*vf.setFlipInterval(5000);
        vf.startFlipping();
        setFlipperListener();*/
    }

    public void moveLeft() {
        if (vf.getDisplayedChild() < vf.getChildCount() - 1) {
            showNext();
        } else {
            endIntro();
        }
    }

    private void showNext() {
        vf.stopFlipping();
        // Set the animation
        vf.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_left));
        vf.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_out_left));
        // Flip!
        vf.showNext();
        startFlipper();
        setText();
    }

    private void showPrev() {
        vf.stopFlipping();
        // Set the animation
        vf.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_right));
        vf.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_out_right));
        // Flip!
        vf.showPrevious();
        startFlipper();
        setText();
    }

    public void moveRight() {
        if (vf.getDisplayedChild() > 0) {
            showPrev();
        }
    }

    private void setText() {
        switch (vf.getDisplayedChild()) {
            case 0:
                tvCurrentPageTitle.setText(R.string.pm_security_tutorial_title_1);
                tvCurrentPageMessage1.setText(R.string.pm_security_tutorial_msg1_1);
                tvCurrentPageMessage2.setText(R.string.pm_security_tutorial_msg2_1);
                tvCurrentPageNext.setText(R.string.pm_security_tutorial_next_1);
                break;
            case 1:
                tvCurrentPageTitle.setText(R.string.pm_security_tutorial_title_2);
                tvCurrentPageMessage1.setText(R.string.pm_security_tutorial_msg1_2);
                tvCurrentPageMessage2.setText(R.string.pm_security_tutorial_msg2_2);
                tvCurrentPageNext.setText(R.string.pm_security_tutorial_next_2);
                break;
            case 2:
                tvCurrentPageTitle.setText(R.string.pm_security_tutorial_title_3);
                tvCurrentPageMessage1.setText(R.string.pm_security_tutorial_msg1_3);
                tvCurrentPageMessage2.setText(R.string.pm_security_tutorial_msg2_3);
                tvCurrentPageNext.setText(R.string.pm_security_tutorial_next_3);
                break;
        }
    }

    public void endIntro() {
        if (mListener != null) {
            mListener.onSecurityTutorialEnd();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // Get the action that was done on this touch event
        switch (arg1.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // store the X value when the user's finger was pressed down
                downXValue = arg1.getX();
                break;
            }

            case MotionEvent.ACTION_UP: {
                // Get the X value when the user released his/her finger
                float currentX = arg1.getX();

                // going backwards: pushing stuff to the right
                if (downXValue - currentX < -200) {
                    moveRight();
                }

                // going forwards: pushing stuff to the left
                if (downXValue - currentX > 200) {
                    moveLeft();
                }
                break;
            }
        }

        // if you return false, these actions will not be recorded
        return true;
    }

    public interface OnFragmentInteractionListener {
        void onSecurityTutorialEnd();
    }

    private void createCallbacks() {
    }

    //    private void showIntro() {
////        isIntro = true;
//        if (isIntro) {
//            // First Time App launched
//            showTosPopup();
//        } else {
//            skipIntro(null);
//        }
//    }
/*
    private void showIntro() {
        isIntro = true;
        if (isIntro) {
            isIntro = false;
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isInitialAppLaunch", isIntro);
            editor.apply();
            RelativeLayout layMain = (RelativeLayout) view.findViewById(R.id
                    .layout_main);
            layMain.setOnTouchListener(IntroFragment.this);
            startFlipper();
            showTosPopup();
        } else {
            (view.findViewById(R.id.llLogo)).setVisibility(View.VISIBLE);
            skipIntro(null);
        }

        layMain.setOnTouchListener(SecurityTutorialFragment.this);
        if (isAutoStart)
            startFlipper();
    }
*/
/*
    private void showTosPopup() {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//            dialog.setMessage(R.string.tos_msg);
            View v = View.inflate(getActivity(),R.layout.tos_popup,null);
            dialog.setView(v);
            TextView accept = (TextView) v.findViewById(R.id.accept);
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    SharedPreferences sp = SharedPreferencesManager.getPrefs(getActivity());
//                    SharedPreferences.Editor ed = sp.edit();
//                    ed.putBoolean("tos", true);
//                    ed.commit();
                    isIntro = false;
                    final SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isInitialAppLaunch", isIntro);
                    editor.commit();
                    tosDialog.dismiss();
                }
            });
            TextView tos = (TextView) v.findViewById(R.id.goTos);
            tos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://www.kliner.fr/mobile/legal"));
                    getActivity().startActivity(i);
                }
            });
            dialog.setCancelable(false);
            tosDialog = dialog.create();
            tosDialog.show();
    }
    */
}
