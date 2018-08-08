package com.peermountain.sdk.ui.authorized.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.peermountain.core.model.guarded.PmJob;
import com.peermountain.sdk.R;
import com.peermountain.common.utils.ripple.RippleOnClickListener;
import com.peermountain.common.utils.ripple.RippleUtils;
import com.peermountain.core.views.PeerMountainTextView;

/**
 * Created by Galeen on 10/12/2017.
 */

public class JobsAdapter extends ArrayAdapter<PmJob> {
    View.OnClickListener clickListener;


    public JobsAdapter(Context context, View.OnClickListener clickListener) {
        super(context, 0);
        this.clickListener = clickListener;
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
            holder = new ViewHolder(contentView, clickListener);
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

    public static class ViewHolder {
        final PeerMountainTextView tvJobMsg;
        final ImageView ivPmFullImage;
        final LinearLayout llBtn1;
        final PeerMountainTextView tvBtn1Title;
        final PeerMountainTextView tvBtn1Msg;
        final ImageView ivBtn1;
        final LinearLayout llBtn2;
        final PeerMountainTextView tvBtn2Title;
        final PeerMountainTextView tvBtn2Msg;
        final ImageView ivBtn2;
        final LinearLayout llBtn3;
        final PeerMountainTextView tvBtn3Title;
        final PeerMountainTextView tvBtn3Msg;
        final ImageView ivBtn3;
        final LinearLayout llBtn4;
        final PeerMountainTextView tvBtn4Title;
        final PeerMountainTextView tvBtn4Msg;
        final ImageView ivBtn4;
        final View parent, btnRipple;
        final ImageView btn;
        PmJob job;

        ViewHolder(View view, final View.OnClickListener clickListener) {
            parent = view;
            tvJobMsg = (PeerMountainTextView) view.findViewById(R.id.tvJobMsg);
            ivPmFullImage = (ImageView) view.findViewById(R.id.ivPmFullImage);
            llBtn1 = (LinearLayout) view.findViewById(R.id.llBtn1);
            tvBtn1Title = (PeerMountainTextView) view.findViewById(R.id.tvBtn1Title);
            tvBtn1Msg = (PeerMountainTextView) view.findViewById(R.id.tvBtn1Msg);
            ivBtn1 = (ImageView) view.findViewById(R.id.ivBtn1);
            llBtn2 = (LinearLayout) view.findViewById(R.id.llBtn2);
            tvBtn2Title = (PeerMountainTextView) view.findViewById(R.id.tvBtn2Title);
            tvBtn2Msg = (PeerMountainTextView) view.findViewById(R.id.tvBtn2Msg);
            ivBtn2 = (ImageView) view.findViewById(R.id.ivBtn2);
            llBtn3 = (LinearLayout) view.findViewById(R.id.llBtn3);
            tvBtn3Title = (PeerMountainTextView) view.findViewById(R.id.tvBtn3Title);
            tvBtn3Msg = (PeerMountainTextView) view.findViewById(R.id.tvBtn3Msg);
            ivBtn3 = (ImageView) view.findViewById(R.id.ivBtn3);
            llBtn4 = (LinearLayout) view.findViewById(R.id.llBtn4);
            tvBtn4Title = (PeerMountainTextView) view.findViewById(R.id.tvBtn4Title);
            tvBtn4Msg = (PeerMountainTextView) view.findViewById(R.id.tvBtn4Msg);
            ivBtn4 = (ImageView) view.findViewById(R.id.ivBtn4);

            btn = (ImageView) view.findViewById(R.id.pmIvDoJob);
            btnRipple = RippleUtils.setRippleEffectSquare(btn);
            btn.setOnClickListener(new RippleOnClickListener() {
                @Override
                public void onClickListener(View clickedView) {
                    clickedView.setTag(job);
                    clickListener.onClick(clickedView);
                }
            });
        }

        void bind(PmJob pmJob, boolean isActivity) {
            job = pmJob;
            if (job == null) return;
            setImage();
            if (job.isOpen()) {
                setViewForOpenJob();
            } else {
                setViewForLockedJob();
            }
            tvJobMsg.setText(isActivity ? job.getActivity() : job.getInformation());
        }

        private void setViewForOpenJob() {
            btnRipple.setEnabled(true);
            btn.setEnabled(true);
//            showViews(true);
//            setImage();
        }

        private void setImage() {
            switch (job.getType()){
                case PmJob.TYPE_CARD:
                    ivPmFullImage.setImageResource(R.drawable.pm_platinum);
                    break;
                case PmJob.TYPE_ELITE_CARD:
                    ivPmFullImage.setImageResource(R.drawable.pm_elite_card);
                    break;
                case PmJob.TYPE_WORLD_CARD:
                    ivPmFullImage.setImageResource(R.drawable.pm_card_world);
                    break;
                case PmJob.TYPE_SV_CARD:
                    ivPmFullImage.setImageResource(R.drawable.pm_card_sv);
                    break;
            }
        }

        private void setViewForLockedJob() {
            btn.setEnabled(false);
            btnRipple.setEnabled(false);
//            showViews(false);
        }

        private void showViews(boolean show) {
            int status = show?View.VISIBLE:View.GONE;
            ivPmFullImage.setVisibility(status);
            llBtn1.setVisibility(status);
            llBtn2.setVisibility(status);
            llBtn3.setVisibility(status);
            llBtn4.setVisibility(status);
        }
    }
}
