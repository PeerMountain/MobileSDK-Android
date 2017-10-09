package com.peermountain.sdk.views;

/**
 * Created by Galeen on 10/9/17.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;

import com.peermountain.sdk.R;


/**
 * Created by Galeen on 9.5.2016 г..
 */
public class PeerMountainTextView extends android.support.v7.widget.AppCompatTextView {
    private String boldFont = null;//"Fonts/AvenirNextLTPro-BoldCond.otf";
    private String regularFont = null;//"Fonts/AvenirNextLTPro-MediumCond.otf";
    private String italicFont = null;//"Fonts/AvenirNextLTPro-CondRegular.otf";
    private String lightFont = null;//"Fonts/SourceSansPro-Regular.ttf";
    public static final int BOLD = 1;
    public static final int NORMAL = 0;
    public static final int ITALIC = 2;
    public static final int LIGHT = 3;
    private static ArrayMap<String, Typeface> fontCache = new ArrayMap<>();
    FontType type = FontType.REGULAR;

    public static Typeface getTypeface(String fontName, Context context) {
        Typeface typeface = fontCache.get(fontName);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), fontName);
            } catch (Exception e) {
                return null;
            }

            fontCache.put(fontName, typeface);
        }

        return typeface;
    }

    public PeerMountainTextView(Context context) {
        super(context);
        init(context, null);
    }

    public PeerMountainTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PeerMountainTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.PeerMountainTextView, 0, 0);
            try {
                if (a.getString(R.styleable.PeerMountainTextView_tv_font) != null) {
                    String fontName = a.getString(R.styleable.PeerMountainTextView_tv_font);
                    regularFont = "Fonts/" + fontName;
                    //setTypeface(getTypeface(regularFont, context));//Typeface.createFromAsset(getParentActivity().getAssets(), regularFont));
                } else {
                    if (a.getInt(R.styleable.PeerMountainTextView_pmTvType, -1) != -1)
                        type = FontType.fromType(a.getInt(R.styleable.PeerMountainTextView_pmTvType, 0));
                }
                setViewFont(type);
            } finally {
                a.recycle();
            }
        }
    }


    public void setViewFont(FontType type) {
        switch (type) {
            case BOLD:
                if(boldFont!=null) {
                    setTypeface(getTypeface(boldFont, getContext()));
                }
                break;
            case ITALIC:
                if(italicFont!=null) {
                    setTypeface(getTypeface(italicFont, getContext()));
                }
                break;
            case LIGHT:
                if(lightFont!=null) {
                    setTypeface(getTypeface(lightFont, getContext()));
                }
                break;
            default:
                if(regularFont!=null) {
                    setTypeface(getTypeface(regularFont, getContext()));
                }
                break;
        }
    }

    public void setViewFont(boolean goBold) {

//        if (goBold)
//            setTypeface(null, Typeface.BOLD);
//        else
//            setTypeface(null, Typeface.NORMAL);

        if (goBold)
            setTypeface(getTypeface(boldFont, getContext()));
        else
            setTypeface(getTypeface(regularFont, getContext()));
    }

    public String getBoldFont() {
        return boldFont;
    }

    public String getRegularFont() {
        return regularFont;
    }

    public void setRegularFont(String regularFont) {
        this.regularFont = regularFont;
    }

    public enum FontType {
        REGULAR(0),
        BOLD(1),
        ITALIC(2),
        LIGHT(3);


        int type;

        FontType(int type) {
            this.type = type;
        }

        static PeerMountainTextView.FontType fromType(int type) {
            for (PeerMountainTextView.FontType drf : values()) {
                if (drf.type == type) return drf;
            }
            throw new IllegalArgumentException();
        }
    }
}


