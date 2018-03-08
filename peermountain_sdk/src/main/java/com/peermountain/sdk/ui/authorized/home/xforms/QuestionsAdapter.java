package com.peermountain.sdk.ui.authorized.home.xforms;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peermountain.core.R;

import java.util.ArrayList;

/**
 * Created by Galeen on 3/6/18.
 */

public class QuestionsAdapter extends PagerAdapter {
    private ArrayList<Boolean> values;

    @Override
    public int getCount() {
        return values == null ? 0 : values.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.pm_question_pager_item, container, false);
        container.addView(view);
        ImageView ivChecked = view.findViewById(R.id.ivChecked);
        ivChecked.setVisibility(values.get(position) ? View.VISIBLE : View.GONE);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
