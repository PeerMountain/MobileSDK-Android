package com.peermountain.core.odk;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peermountain.core.R;
import com.rd.PageIndicatorView;

import java.util.ArrayList;

/**
 * Created by Galeen on 3/6/18.
 */

public class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionsRecyclerAdapter.ViewHolder> {
    private ArrayList<Boolean> values;
    private int emptyFields = 1;
    private PageIndicatorView pageIndicatorView;
    private Events callback;

    public QuestionsRecyclerAdapter(ArrayList<Boolean> values, int emptyFields, PageIndicatorView pageIndicatorView, Events callback) {
        this.values = values;
        this.emptyFields = emptyFields;
        this.pageIndicatorView = pageIndicatorView;
        this.callback = callback;
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
        if (isEmptyField(newPosition)) return;
        if (lastListPosition != -1) values.set(lastListPosition, false);
        values.set(newPosition, true);
        if (lastListPosition != -1) notifyItemChanged(lastListPosition);
        notifyItemChanged(newPosition);
        lastListPosition = newPosition;
        pageIndicatorView.setSelection(lastListPosition - emptyFields);
    }

    private boolean isEmptyField(int position) {
        return position < emptyFields || position > values.size() - (emptyFields + 1);
    }

    public interface Events {
        void onItemClicked(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivQuestionBkg, ivChecked;
        private int position;

        ViewHolder(View itemView) {
            super(itemView);
            ivQuestionBkg = itemView.findViewById(R.id.ivQuestionBkg);
            ivChecked = itemView.findViewById(R.id.ivChecked);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null && position != lastListPosition) callback.onItemClicked(position);
                }
            });
        }

        void bind(int position) {
            this.position = position;
            if (isEmptyField(position)) {
                itemView.setVisibility(View.INVISIBLE);
            } else {
                itemView.setVisibility(View.VISIBLE);
                if (position >= lastListPosition)
                    ivChecked.setVisibility(View.GONE);
                else
                    ivChecked.setVisibility(View.VISIBLE);
                if (values.get(position)) {
                    //scale
//                ivQuestionBkg.setScaleX(1);
                    ivQuestionBkg.setScaleY(1);
                } else {
                    //downscale
//                ivQuestionBkg.setScaleX(0.75f);
                    ivQuestionBkg.setScaleY(0.8f);
                }
            }
        }
    }
}
