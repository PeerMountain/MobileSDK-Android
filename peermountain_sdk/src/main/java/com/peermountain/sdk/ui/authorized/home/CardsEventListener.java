package com.peermountain.sdk.ui.authorized.home;

import android.view.View;
import android.widget.ArrayAdapter;

import com.peermountain.core.utils.LogUtils;
import com.peermountain.sdk.R;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.CardStackView.CardEventListener;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Galeen on 10/16/17.
 */

public class CardsEventListener<T> implements CardEventListener {
    private List<T> jobs = new ArrayList<T>();
    private ArrayAdapter<T> jobsAdapter;
    private CardStackView cardStackView;

    public CardsEventListener(List<T> jobs, ArrayAdapter<T> jobsAdapter, CardStackView cardStackView) {
        this.jobs = jobs;
        this.jobsAdapter = jobsAdapter;
        this.cardStackView = cardStackView;
    }

    @Override
    public void onCardDragging(float percentX, float percentY) {
//            LogUtils.d("CardStackView", "onCardDragging");
    }

    @Override
    public void onCardSwiped(SwipeDirection direction) {
        LogUtils.d("CardStackView", "onCardSwiped: " + direction.toString());
        LogUtils.d("CardStackView", "topIndex: " + cardStackView.getTopIndex());
        if (cardStackView.getTopIndex() == jobsAdapter.getCount() - 3) {
            LogUtils.d("CardStackView", "Paginate: " + cardStackView.getTopIndex());
            addNewJobs();
        } else {
            setCardsBackground();
        }
    }

    @Override
    public void onCardReversed() {
        LogUtils.d("CardStackView", "onCardReversed");
    }

    @Override
    public void onCardMovedToOrigin() {
        LogUtils.d("CardStackView", "onCardMovedToOrigin");
    }

    @Override
    public void onCardClicked(int index) {
        LogUtils.d("CardStackView", "onCardClicked: " + index);
    }

    private void addNewJobs() {
        LinkedList<T> jobs = extractRemainingJobs();
        jobs.addAll(this.jobs);
        jobsAdapter.clear();
        jobsAdapter.addAll(jobs);
        jobsAdapter.notifyDataSetChanged();
    }

    private LinkedList<T> extractRemainingJobs() {
        LinkedList<T> spots = new LinkedList<>();
        for (int i = cardStackView.getTopIndex(); i < jobsAdapter.getCount(); i++) {
            spots.add(jobsAdapter.getItem(i));
        }
        return spots;
    }

    public void setCardsBackground() {
        View target = cardStackView.getTopView();
        target.setBackgroundResource(R.drawable.pm_card_white);
        View back = cardStackView.getChildAt(0);
        if (back != null && !back.equals(target))
            back.setBackgroundResource(R.drawable.pm_card);

        View back1 = cardStackView.getChildAt(1);
        if (back1 != null && !back1.equals(target))
            back1.setBackgroundResource(R.drawable.pm_card);

        View back2 = cardStackView.getChildAt(2);
        if (back2 != null && !back2.equals(target))
            back2.setBackgroundResource(R.drawable.pm_card);
    }

}
