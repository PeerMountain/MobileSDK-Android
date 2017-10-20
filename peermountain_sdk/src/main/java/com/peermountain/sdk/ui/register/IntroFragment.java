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

public class IntroFragment extends ToolbarFragment implements View.OnTouchListener {
    SharedPreferences prefs;
    ViewFlipper vf;
    private float downXValue;
    final Handler myHandler = new Handler();
    //    private Timer timer = null;
    private boolean isIntro = false, withAnimation = false, withFinishOnLast = false, isInfinite = false, isAutoStart = true;
    private OnFragmentInteractionListener mListener;
    View view, llAdvertise;
    LinearLayout layMain;
    TextView tvMessage, tvTutoTitle, tvMessage2;
    ImageView dot1, dot2, dot3;
    TextView btnSkip, btnNext, btnGetCard;
    AlertDialog tosDialog;


    public IntroFragment() {
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
        view = inflater.inflate(R.layout.pm_fragment_intro, container, false);

//        prefs = SharedPreferencesManager.getPrefs(getActivity());
//        isIntro = prefs.getBoolean("tutorial", true);
        vf = (ViewFlipper) view.findViewById(R.id.details);
        tvMessage = (TextView) view.findViewById(R.id.tvTutoMsg);
        tvMessage2 = (TextView) view.findViewById(R.id.tvTutoMsg2);
        tvTutoTitle = (TextView) view.findViewById(R.id.tvTutoTitle);
        dot1 = (ImageView) view.findViewById(R.id.ivDot1);
        dot2 = (ImageView) view.findViewById(R.id.ivDot2);
        dot3 = (ImageView) view.findViewById(R.id.ivDot3);
        btnSkip = view.findViewById(R.id.tvSkip);
        btnNext = view.findViewById(R.id.tvNext);
        llAdvertise = view.findViewById(R.id.llAdvertise);
        btnGetCard = view.findViewById(R.id.btnGetCard);
        layMain = view.findViewById(R.id
                .layout_main);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideToolbar();
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                myHandler.post(new Runnable() {
//                    public void run() {
//                        showIntro();
//                    }
//                });
//            }
//        }, 2000/* amount of time in milliseconds before execution, splash screen is visible */);
        setTextAndDots();
        setListeners();
//        RippleUtils.setRippleEffectLessRounded(btnSkip);
    }

    public void setListeners() {
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipIntro(null);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vf.getDisplayedChild() < vf.getChildCount() - 1) {
                    showNext();
                } else {
                    skipIntro(null);
                }
            }
        });
        btnGetCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layMain.removeView(llAdvertise);
                showIntro();
            }
        });
        RippleUtils.setRippleEffectSquare(btnGetCard);
    }


    private void setFlipperListener() {
        vf.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_left));
        vf.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_out_left));
        vf.getInAnimation().setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
                setTextAndDots();
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                int displayedChild = vf.getDisplayedChild();
                int childCount = vf.getChildCount();
                if (displayedChild == childCount - 1) {
                    if (!isInfinite) {
                        vf.stopFlipping();
                        vf.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                                R.anim.slide_right));
                        vf.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                                R.anim.slide_out_right));
                    }
                }
            }
        });
    }

    private void startFlipper() {
        if (withAnimation) {
            vf.setFlipInterval(5000);
            vf.startFlipping();
            setFlipperListener();
        }
    }

    public void moveLeft(View view) {
        if (vf.getDisplayedChild() < vf.getChildCount() - 1 || isInfinite) {
            showNext();
        } else {
            if (withFinishOnLast)
                skipIntro(null);
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
        setTextAndDots();
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
        setTextAndDots();
    }

    public void moveRight(View view) {
        if (vf.getDisplayedChild() > 0 || isInfinite) {
            showPrev();
        }
    }

    private void setTextAndDots() {
        switch (vf.getDisplayedChild()) {
            case 0:
                dot1.setImageResource(R.drawable.pm_blue_dot);
                dot2.setImageResource(R.drawable.pm_white_dot);
                dot3.setImageResource(R.drawable.pm_white_dot);
                tvTutoTitle.setText(R.string.pm_tuto1_title);
                tvMessage.setText(R.string.pm_tuto1_msg);
                tvMessage2.setText(R.string.pm_tuto1_msg2);
                btnSkip.setVisibility(View.VISIBLE);
                btnNext.setText(R.string.pm_tuto_next);
                break;
            case 1:
                dot1.setImageResource(R.drawable.pm_white_dot);
                dot2.setImageResource(R.drawable.pm_blue_dot);
                dot3.setImageResource(R.drawable.pm_white_dot);
                tvTutoTitle.setText(R.string.pm_tuto2_title);
                tvMessage.setText(R.string.pm_tuto2_msg);
                tvMessage2.setText(R.string.pm_tuto2_msg2);
                btnSkip.setVisibility(View.VISIBLE);
                btnNext.setText(R.string.pm_tuto_next);
                break;
            case 2:
                dot1.setImageResource(R.drawable.pm_white_dot);
                dot2.setImageResource(R.drawable.pm_white_dot);
                dot3.setImageResource(R.drawable.pm_blue_dot);
                tvTutoTitle.setText(R.string.pm_tuto3_title);
                tvMessage.setText(R.string.pm_tuto3_msg);
                tvMessage2.setText(R.string.pm_tuto3_msg2);
                btnSkip.setVisibility(View.INVISIBLE);
                btnNext.setText(R.string.pm_tuto_start);
                break;
        }
    }

    public void skipIntro(View view) {
//        vf.stopFlipping();
//        vf.setDisplayedChild(vf.getChildCount() - 1);

        PeerMountainManager.saveTutoSeen();
        if (mListener != null) {
            mListener.onTutoEnd();
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
                    moveRight(null);
                }

                // going forwards: pushing stuff to the left
                if (downXValue - currentX > 200) {
                    moveLeft(null);
                }
                break;
            }
        }

        // if you return false, these actions will not be recorded
        return true;
    }

    public interface OnFragmentInteractionListener {
        void onTutoEnd();
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
    private void showIntro() {
//        isIntro = true;
//        if (isIntro) {
//            isIntro = false;
//            final SharedPreferences.Editor editor = prefs.edit();
//            editor.putBoolean("isInitialAppLaunch", isIntro);
//            editor.apply();
//            RelativeLayout layMain = (RelativeLayout) view.findViewById(R.id
//                    .layout_main);
//            layMain.setOnTouchListener(IntroFragment.this);
//            startFlipper();
//            showTosPopup();
//        } else {
//            (view.findViewById(R.id.llLogo)).setVisibility(View.VISIBLE);
//            skipIntro(null);
//        }

        layMain.setOnTouchListener(IntroFragment.this);
        if (isAutoStart)
            startFlipper();
    }

    private void showTosPopup() {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
////            dialog.setMessage(R.string.tos_msg);
//            View v = View.inflate(getActivity(),R.layout.tos_popup,null);
//            dialog.setView(v);
//            TextView accept = (TextView) v.findViewById(R.id.accept);
//            accept.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    SharedPreferences sp = SharedPreferencesManager.getPrefs(getActivity());
////                    SharedPreferences.Editor ed = sp.edit();
////                    ed.putBoolean("tos", true);
////                    ed.commit();
//                    isIntro = false;
//                    final SharedPreferences.Editor editor = prefs.edit();
//                    editor.putBoolean("isInitialAppLaunch", isIntro);
//                    editor.commit();
//                    tosDialog.dismiss();
//                }
//            });
//            TextView tos = (TextView) v.findViewById(R.id.goTos);
//            tos.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse("http://www.kliner.fr/mobile/legal"));
//                    getActivity().startActivity(i);
//                }
//            });
//            dialog.setCancelable(false);
//            tosDialog = dialog.create();
//            tosDialog.show();
    }
}
