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

    private int lastPosition = 1, positionToSend = 2;

    @Override
    public boolean fling(int velocityX, int velocityY) {
//        Log.e("onFling","x : "+ velocityX+" y : "+velocityY);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
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
            int positionToScroll;
            if (lastPosition != -1) {
                positionToScroll = lastPosition;
            } else {
                positionToScroll = linearLayoutManager.findFirstVisibleItemPosition();
            }
            if (velocityX > 0) {
                if (positionToScroll < getAdapter().getItemCount() - 1) {
                    positionToScroll++;
                    positionToSend = positionToSend + 1;
                }
                if (positionToSend > getAdapter().getItemCount() - 2) {
                    positionToSend = getAdapter().getItemCount() - 3;
                }
            } else {
                if (positionToScroll > 0) {
                    positionToScroll--;
                    positionToSend = positionToSend - 1;
                }
                if (positionToSend < 2) {
                    positionToSend = 2;
                }
            }
//                positionToScroll = linearLayoutManager.findLastVisibleItemPosition();
            if (positionToScroll >= 2 && positionToScroll <= getAdapter().getItemCount() - 3){
                linearLayoutManager.scrollToPositionWithOffset(positionToScroll,0);
            }
            if (onFlingListenerGaleen != null)
                onFlingListenerGaleen.onFling(positionToSend);
            lastPosition = positionToScroll;
            return false;
        } else {
            return super.fling(velocityX, velocityY);
        }
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
