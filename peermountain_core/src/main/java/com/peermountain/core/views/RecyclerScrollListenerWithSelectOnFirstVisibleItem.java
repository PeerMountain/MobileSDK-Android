package com.peermountain.core.views;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.peermountain.core.odk.QuestionsRecyclerAdapter;

/**
 * Created by Galeen on 11.1.2017 г..
 */
public class RecyclerScrollListenerWithSelectOnFirstVisibleItem extends RecyclerView.OnScrollListener {
    private View lastSelectedView = null, newSelectedView = null;
    private int indexOfItem = 0,lastListPosition;
    private int selectedColor;
    private int deselectedColor;
    private int itemSize;
    private OnEvent callback;
    private boolean blockListScroll = false;

    public RecyclerScrollListenerWithSelectOnFirstVisibleItem(OnEvent callback, int
            itemSize, int selectedColor, int deselectedColor) {
        this.itemSize = itemSize;
        this.selectedColor = selectedColor;
        this.deselectedColor = deselectedColor;
        this.callback = callback;
    }


    public void selectView(int position) {
        if (lastSelectedView != null)
            lastSelectedView.setBackgroundColor(deselectedColor);
        LinearLayoutManager llm = callback.getLinearLayoutManager();
        newSelectedView = llm.findViewByPosition(position);
        updateBackgrounds();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
                if(!blockListScroll) {
                    blockListScroll=true;
                    View view = recyclerView.getChildAt(2);//recyclerView.getFocusedChild()
                    int pos = recyclerView.getChildAdapterPosition(view);
                    if (pos != lastListPosition) {
                        callback.getAdapter().updateItems(lastListPosition,pos);
                        lastListPosition = pos;
                        if (dx < 0) { // scroll from left to right
//                            if (pos > 0)
//                                recyclerView.getLayoutManager().scrollToPosition(pos - 1);
                        } else {
//                            if (pos < recyclerView.getAdapter().getItemCount() - 1)
//                                recyclerView.getLayoutManager().scrollToPosition(pos + 1);
                        }
                    }
                    blockListScroll=false;
                    Log.e("list item : ", " # " + pos + " x: " + dx + " y: " + dy);
                }
        updateBackgrounds();
//        if (canSelect()) {
////            LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
//            LinearLayoutManager llm = callback.getLinearLayoutManager();
//            indexOfItem = llm.findFirstVisibleItemPosition();
//            newSelectedView = llm.findViewByPosition(indexOfItem);
//            if (llm.getDecoratedLeft(newSelectedView) * -1 > itemSize / 3 &&
//                    llm.findFirstCompletelyVisibleItemPosition() >= 0) {//more than 1/3 of the view is invisible
//                indexOfItem = llm.findFirstCompletelyVisibleItemPosition();
//                newSelectedView = llm.findViewByPosition(indexOfItem);
//            }
//            updateBackgrounds();
//        }
    }

    private void updateBackgrounds() {
        if (newSelectedView != null){
            newSelectedView.setScaleY(1.5f);
            newSelectedView.setScaleX(1.5f);
            newSelectedView.setBackgroundColor(selectedColor);
        }
        if (lastSelectedView != null && lastSelectedView != newSelectedView){
            lastSelectedView.setScaleY(1);
            lastSelectedView.setScaleX(1);
            lastSelectedView.setBackgroundColor(deselectedColor);
        }
        lastSelectedView = newSelectedView;
    }

    private boolean canSelect() {
        return callback != null && callback.canSelectItem();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE && canSelect()) {
            int indexTopItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            int indexBottomItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            callback.onScrollFinished(lastListPosition, indexTopItem, indexBottomItem);
        }
    }

    public interface OnEvent {
        boolean canSelectItem();

        void onScrollFinished(int selectedPosition, int firstVisible, int lastVisible);

        LinearLayoutManager getLinearLayoutManager();

        QuestionsRecyclerAdapter getAdapter();
    }
}