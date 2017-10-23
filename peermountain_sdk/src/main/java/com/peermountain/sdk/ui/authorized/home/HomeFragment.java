package com.peermountain.sdk.ui.authorized.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.peermountain.core.model.guarded.PmJob;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.HomeToolbarFragment;
import com.peermountain.sdk.views.PeerMountainTextView;
import com.yuyakaido.android.cardstackview.CardStackView;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends HomeToolbarFragment {

    private OnFragmentInteractionListener mListener;
    private PeerMountainTextView mTvTabActivity;
    private PeerMountainTextView mTvTabInfo, mTvJobMsg;
    private ImageView mPmIvDoJob;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViews(view);
        setUpView();
        setListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private CardStackView cardStackView;

    private void getViews(View view) {
        mTvTabActivity = (PeerMountainTextView) view.findViewById(R.id.tvTabActivity);
        mTvTabInfo = (PeerMountainTextView) view.findViewById(R.id.tvTabInfo);
        mPmIvDoJob = view.findViewById(R.id.pmIvDoJob);
        mTvJobMsg = view.findViewById(R.id.tvJobMsg);
        cardStackView = (CardStackView) view.findViewById(R.id.pm_main_card_stack_view);
    }

    private void setUpView() {
        setHomeToolbar(R.string.pm_title_home);
        setCardsView();
    }

    private void setCardsView() {
        cardStackView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cardStackView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setCards();
            }
        });

    }

    List<PmJob> jobs = new ArrayList<PmJob>();
    JobsAdapter jobsAdapter;

    private void setCards() {
        addStaticJobs();
        if (jobs == null) return;
        jobsAdapter = new JobsAdapter(getContext(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onHomeJobedClicked();
            }
        });
        jobsAdapter.addAll(jobs);
        cardStackView.setAdapter(jobsAdapter);
        cardStackView.setCardEventListener(new CardsEventListener(jobs,jobsAdapter,cardStackView));//jobsEvents);
    }

    private void addStaticJobs() {
        jobs.add(new PmJob("Upload Passport", "Scan your ID or Passport"));
        jobs.add(new PmJob(true));
        jobs.add(new PmJob(true));
        jobs.add(new PmJob(true));
    }

//    CardStackView.CardEventListener jobsEvents = new CardStackView.CardEventListener() {
//        @Override
//        public void onCardDragging(float percentX, float percentY) {
////            LogUtils.d("CardStackView", "onCardDragging");
//        }
//
//        @Override
//        public void onCardSwiped(SwipeDirection direction) {
//            LogUtils.d("CardStackView", "onCardSwiped: " + direction.toString());
//            LogUtils.d("CardStackView", "topIndex: " + cardStackView.getTopIndex());
//            if (cardStackView.getTopIndex() == jobsAdapter.getCount() - 3) {
//                LogUtils.d("CardStackView", "Paginate: " + cardStackView.getTopIndex());
//                addNewJobs();
//            } else {
//                setCardsBackground();
//            }
//        }
//
//        @Override
//        public void onCardReversed() {
//            LogUtils.d("CardStackView", "onCardReversed");
//        }
//
//        @Override
//        public void onCardMovedToOrigin() {
//            LogUtils.d("CardStackView", "onCardMovedToOrigin");
//        }
//
//        @Override
//        public void onCardClicked(int index) {
//            LogUtils.d("CardStackView", "onCardClicked: " + index);
//        }
//    };
//
//    public void setCardsBackground() {
//        View target = cardStackView.getTopView();
//        target.setBackgroundResource(R.drawable.pm_card_white);
//        View back = cardStackView.getChildAt(0);
//        if (back != null && !back.equals(target))
//            back.setBackgroundResource(R.drawable.pm_card);
//
//        View back1 = cardStackView.getChildAt(1);
//        if (back1 != null && !back1.equals(target))
//            back1.setBackgroundResource(R.drawable.pm_card);
//
//        View back2 = cardStackView.getChildAt(2);
//        if (back2 != null && !back2.equals(target))
//            back2.setBackgroundResource(R.drawable.pm_card);
//    }
//
//    private LinkedList<PmJob> extractRemainingJobs() {
//        LinkedList<PmJob> spots = new LinkedList<>();
//        for (int i = cardStackView.getTopIndex(); i < jobsAdapter.getCount(); i++) {
//            spots.add(jobsAdapter.getItem(i));
//        }
//        return spots;
//    }
//
//    private void addNewJobs() {
//        LinkedList<PmJob> jobs = extractRemainingJobs();
//        jobs.addAll(this.jobs);
//        jobsAdapter.clear();
//        jobsAdapter.addAll(jobs);
//        jobsAdapter.notifyDataSetChanged();
//    }

    private void setListeners() {
        mTvTabActivity.setOnClickListener(tabClick);
        mTvTabInfo.setOnClickListener(tabClick);
    }

    private void initTabs() {
        int colorActive = ContextCompat.getColor(getActivity(), R.color.pm_tab_text_color);
        int colorInactive = ContextCompat.getColor(getActivity(), R.color.pm_tab_text_inactive);
        mTvTabActivity.setTextColor(showActivity?colorActive:colorInactive);
        mTvTabInfo.setTextColor(showActivity?colorInactive:colorActive);
    }

    private boolean showActivity = true;
    View.OnClickListener tabClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean shouldChangeActivity = false;
            if (view.getId()==mTvTabInfo.getId()) {
                if(showActivity){
                    showActivity = false;
                    shouldChangeActivity = true;
                }
            }else{
                if(!showActivity){
                    showActivity = true;
                    shouldChangeActivity = true;
                }
            }
            if(shouldChangeActivity && jobsAdapter!=null){
                jobsAdapter.setShowActivity(showActivity);
            }
            initTabs();
        }
    };

    public interface OnFragmentInteractionListener {
        void onHomeJobedClicked();
    }
}
