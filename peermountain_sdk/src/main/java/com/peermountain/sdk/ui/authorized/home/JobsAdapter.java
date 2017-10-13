package com.peermountain.sdk.ui.authorized.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.peermountain.core.model.guarded.PmJob;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;
import com.peermountain.sdk.utils.ripple.RippleUtils;

/**
 * Created by Galeen on 10/12/2017.
 */

public class JobsAdapter extends ArrayAdapter<PmJob> {

    public JobsAdapter(Context context) {
        super(context, 0);

    }

    private boolean showActivity = true;
    private boolean setAsFirst = true;
    public void setShowActivity(boolean showActivity) {
        this.showActivity = showActivity;
        setAsFirst = true;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder;

        if (contentView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.pm_job_tinder_card_view, parent, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }
        if (position == 0 || setAsFirst) {//first time when the adapter is created to set the bkg white
            setAsFirst = false;
            holder.parent.setBackgroundResource(R.drawable.pm_card_white);
        } else {
            holder.parent.setBackgroundResource(R.drawable.pm_card);
        }

        holder.bind(getItem(position), showActivity);

        return contentView;
    }

    private static class ViewHolder {
        final View parent, btnRipple;
        final TextView tvMsg;
        final ImageView btn;
        PmJob job;

        ViewHolder(View view) {
            parent = view;
            tvMsg = (TextView) view.findViewById(R.id.tvJobMsg);
            btn = (ImageView) view.findViewById(R.id.pmIvDoJob);
            btnRipple = RippleUtils.setRippleEffectSquare(btn);
            btn.setOnClickListener(new RippleOnClickListener() {
                @Override
                public void onClickListener(View clickedView) {
                    LogUtils.d("jobsAdapter","click on fake job :"+ job.isFake());
                }
            });
        }

        void bind(PmJob pmJob, boolean isActivity) {
            job = pmJob;
            if (job == null) return;
            if (!job.isFake()) {
                btnRipple.setEnabled(true);
                btn.setEnabled(true);
            } else {
                btn.setEnabled(false);
                btnRipple.setEnabled(false);
            }
            tvMsg.setText(isActivity?job.getActivity():job.getInformation());
        }
    }
}
