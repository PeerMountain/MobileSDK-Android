package com.peermountain.core.odk;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peermountain.core.R;

import java.util.ArrayList;

/**
 * Created by Galeen on 3/6/18.
 */

public class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionsRecyclerAdapter.ViewHolder>{
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
        if(values.get(position)){
            //scale
        }else{
            //downscale
        }
    }

    @Override
    public int getItemCount() {
        return values == null ? 0 : values.size();
    }

    public void updateItems(int old, int newPosition){
        values.set(old,false);
        values.set(newPosition,true);
        notifyItemChanged(old);
        notifyItemChanged(newPosition);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
