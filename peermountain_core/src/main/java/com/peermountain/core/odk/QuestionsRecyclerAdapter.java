package com.peermountain.core.odk;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peermountain.core.R;

import java.util.ArrayList;

/**
 * Created by Galeen on 3/6/18.
 */

public class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionsRecyclerAdapter.ViewHolder> {
    private ArrayList<Boolean> values;

    public QuestionsRecyclerAdapter(ArrayList<Boolean> values) {
        this.values = values;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pm_question_pager_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return values == null ? 0 : values.size();
    }

    private int lastListPosition = -1;

    public void updateItems(int newPosition) {
        if (newPosition < 2 || newPosition > values.size() - 3) return;
        if (lastListPosition != -1) values.set(lastListPosition, false);
        values.set(newPosition, true);
        if (lastListPosition != -1) notifyItemChanged(lastListPosition);
        notifyItemChanged(newPosition);
        lastListPosition = newPosition;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivQuestionBkg;
        private int position;

        ViewHolder(View itemView) {
            super(itemView);
            ivQuestionBkg = itemView.findViewById(R.id.ivQuestionBkg);
        }

        void bind(int position) {
            this.position = position;
            if (position < 2 || position > values.size() - 3) {
                ivQuestionBkg.setVisibility(View.GONE);
            } else {
                ivQuestionBkg.setVisibility(View.VISIBLE);
                if (values.get(position)) {
                    //scale
//                ivQuestionBkg.setScaleX(1);
                    ivQuestionBkg.setScaleY(1);
                } else {
                    //downscale
//                ivQuestionBkg.setScaleX(0.75f);
                    ivQuestionBkg.setScaleY(0.75f);
                }
            }
        }
    }
}
