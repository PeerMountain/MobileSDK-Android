package com.peermountain.sdk.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.peermountain.core.model.unguarded.Keyword;
import com.peermountain.core.views.PeerMountainTextView;


/**
 * Created by Galeen on 1.12.2015 г..
 */
public class KeywordView extends PeerMountainTextView {
    private boolean checked = false;
    private Drawable imgOn, imgOff;
    private OnClick mOnClick = null;
    private ImageView imageView;
    private TextView textView;
    private Keyword keyword;

    public KeywordView(Context context, Keyword keyword) {
        super(context);
        this.keyword = keyword;
        init(context, null);
    }

    public KeywordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public KeywordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(final Context context, AttributeSet attrs) {
//        if (attrs != null) {
//            TypedArray a = context.getTheme().obtainStyledAttributes(
//                    attrs,
//                    R.styleable.GaleenSwitchView,
//                    0, 0);
//
//            try {
//                imgOn = a.getDrawable(R.styleable.GaleenSwitchView_img_on);
//                imgOff = a.getDrawable(R.styleable.GaleenSwitchView_img_off);
//                checked = a.getBoolean(R.styleable.GaleenSwitchView_on,false);
//            } finally {
//                a.recycle();
//            }
//        }
//        if (imgOff == null) {
//            imgOff = ContextCompat.getDrawable(context, R.drawable.icn_annual);
//        }
//        if (imgOn == null) {
//            imgOn = ContextCompat.getDrawable(context, R.drawable.icn_annual_active);
//        }
//
////        setImageDrawable(imgOff);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.visit_type, this);
//        imageView = (ImageView) findViewById(R.id.ivTypeVisit);
//        textView = (TextView) findViewById(R.id.tvTypeName);
//        textView.setText(keyword.getName());
        refresh();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checked = !checked;
                refresh();
                if (mOnClick != null)
                    mOnClick.onClick(v, checked);
//                Toast.makeText(context,"and"+ checked,Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = !checked;
        performClick();
    }

    public void initView(boolean checked) {
        this.checked = checked;
        refresh();
    }

    public void refresh() {
//        if (checked) {
//            if (keyword.getImageSelected()==null || keyword.getImageSelected().bitmap == null)
//                imageView.setImageDrawable(imgOn);
//            else
//                imageView.setImageBitmap(keyword.getImageSelected().bitmap);
//            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
//        } else {
//            if (keyword.getImage()==null || keyword.getImage().bitmap == null)
//                imageView.setImageDrawable(imgOff);
//            else
//                imageView.setImageBitmap(keyword.getImage().bitmap);
//            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_text));
//        }
    }

    public OnClick getmOnClick() {
        return mOnClick;
    }

    public void setmOnClick(OnClick mOnClick) {
        this.mOnClick = mOnClick;
    }

    public Drawable getImgOff() {
        return imgOff;
    }

    public void setImgOff(Drawable imgOff) {
        this.imgOff = imgOff;
    }

    public Drawable getImgOn() {
        return imgOn;
    }

    public void setImgOn(Drawable imgOn) {
        this.imgOn = imgOn;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public void setVisitType(Keyword keyword) {
        this.keyword = keyword;
    }

    public interface OnClick {
        void onClick(View v, boolean isChecked);
    }
}
