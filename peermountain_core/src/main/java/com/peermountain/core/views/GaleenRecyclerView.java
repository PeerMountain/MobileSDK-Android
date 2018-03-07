package com.peermountain.core.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by SmartIntr on 25.11.2015 г..
 */
public class GaleenRecyclerView extends RecyclerView {
    OnFlingListenerGaleen onFlingListenerGaleen;

    public GaleenRecyclerView(Context context) {
        super(context);
    }

    public GaleenRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GaleenRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private int emptyFields = 1, oldPosition;
    private int positionToScroll = emptyFields - 1, positionToSend = emptyFields;
    private boolean canFling = true;
    LinearLayoutManager linearLayoutManager;

    @Override
    public boolean fling(int velocityX, int velocityY) {
//        Log.e("onFling","x : "+ velocityX+" y : "+velocityY);
        if (!canFling) return false;
        if (linearLayoutManager == null) {
            linearLayoutManager = (LinearLayoutManager) getLayoutManager();
        }
        if (linearLayoutManager.getOrientation() == HORIZONTAL) {
//these four variables identify the views you see on screen.
//            int lastVisibleView = linearLayoutManager.findLastVisibleItemPosition();
//            int firstVisibleView = linearLayoutManager.findFirstVisibleItemPosition();
//            View firstView = linearLayoutManager.findViewByPosition(firstVisibleView);
//            View lastView = linearLayoutManager.findViewByPosition(lastVisibleView);
//
////these variables get the distance you need to scroll in order to center your views.
////my views have variable sizes, so I need to calculate side margins separately.
////note the subtle difference in how right and left margins are calculated, as well as
////the resulting scroll distances.
//            int leftMargin = 0;//(screenWidth - lastView.getWidth()) / 2;
//            int rightMargin = 0;//(screenWidth - firstView.getWidth()) / 2 + firstView.getWidth();
//            int leftEdge = lastView.getLeft();
//            int rightEdge = firstView.getRight();
//            int scrollDistanceLeft = leftEdge - leftMargin;
//            int scrollDistanceRight = rightMargin - rightEdge;

//if(user swipes to the left)
//            if (velocityX > 0) smoothScrollBy(scrollDistanceLeft, 0);
//            else smoothScrollBy(-scrollDistanceRight, 0);

            if (positionToScroll == -1) {
                positionToScroll = linearLayoutManager.findFirstVisibleItemPosition();
            }
            oldPosition = positionToScroll;

            if (velocityX > 0) {
                if (positionToScroll < getAdapter().getItemCount() - (emptyFields + 2)) {
                    positionToScroll++;
                }
            } else {
                if (positionToScroll > emptyFields - 1) {
                    positionToScroll--;
                }
            }
            positionToSend = positionToScroll + 1;
//                positionToScroll = linearLayoutManager.findLastVisibleItemPosition();
            linearLayoutManager.scrollToPositionWithOffset(positionToScroll, 0);
            if (onFlingListenerGaleen != null)
                onFlingListenerGaleen.onFling(positionToSend);
            return false;
        } else {
            return super.fling(velocityX, velocityY);
        }
    }

    public void setCanFling(boolean canFling) {
        this.canFling = canFling;
    }

    public void revert() {
        positionToScroll = oldPosition;
        positionToSend = positionToScroll + 1;
//                positionToScroll = linearLayoutManager.findLastVisibleItemPosition();
        linearLayoutManager.scrollToPositionWithOffset(positionToScroll, 0);
        if (onFlingListenerGaleen != null)
            onFlingListenerGaleen.onFling(positionToSend);
    }

    /**
     * This one must be called before setAdapter
     *
     * @param emptyFields how many empty fields we have upfront and in the back
     */
    public void setEmptyFields(int emptyFields) {
        this.emptyFields = emptyFields;
        positionToScroll = emptyFields - 1;
        positionToSend = emptyFields;
    }

    public OnFlingListenerGaleen getGellenOnFlingListener() {
        return onFlingListenerGaleen;
    }

    public void setOnFlingListenerGaleen(OnFlingListenerGaleen onFlingListenerGaleen) {
        this.onFlingListenerGaleen = onFlingListenerGaleen;
    }

    public interface OnFlingListenerGaleen {
        void onFling(int scrolledPosition);
    }

}
