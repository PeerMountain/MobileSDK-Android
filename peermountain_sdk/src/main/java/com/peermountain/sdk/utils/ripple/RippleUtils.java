package com.peermountain.sdk.utils.ripple;

import android.graphics.Color;
import android.view.View;

import com.balysv.materialripple.MaterialRippleLayout;

/**
 * Created by Galeen on 3.5.2016 г..
 */
public class RippleUtils {
    private static final int CORNERS = 6;//in dp
    static final int RIPPLE_DURATION = 250;

    public static View setRippleEffectSquareInAdapter(View view) {
        return MaterialRippleLayout.on(view).rippleColor(Color.BLACK).rippleAlpha(0.2f).rippleDuration(RIPPLE_DURATION).rippleOverlay(true)
                .rippleHover(true).create();
    }

    public static MaterialRippleLayout setRippleEffectSquare(View view) {
        if (view != null)
           return MaterialRippleLayout.on(view).rippleColor(Color.BLACK).rippleAlpha(0.2f).rippleDuration(RIPPLE_DURATION).rippleOverlay(true)
                    .rippleHover(true).create();
        return null;
    }
    public static void setRippleEffectSquare(View... views) {
        for (View view : views) {
            setRippleEffectSquare(view);
        }
    }
    public static void setRippleEffect(View view) {
        if (view != null)
            MaterialRippleLayout.on(view).rippleColor(Color.BLACK).rippleAlpha(0.2f).rippleDuration(RIPPLE_DURATION).rippleOverlay(true)
                    .rippleHover(true).rippleRoundedCorners(CORNERS).create();
    }
    public static void setRippleEffectCircle(View view) {
        if (view != null)
            MaterialRippleLayout.on(view).rippleColor(Color.BLACK).rippleAlpha(0.2f).rippleDuration(RIPPLE_DURATION).rippleOverlay(true)
                    .rippleHover(true).rippleRoundedCorners(100).create();
    }
    public static void setRippleEffectCircleWhite(View view) {
        if (view != null)
            MaterialRippleLayout.on(view).rippleColor(Color.WHITE).rippleAlpha(0.9f).rippleDuration(RIPPLE_DURATION).rippleOverlay(true)
                    .rippleHover(true).rippleRoundedCorners(100).create();
    }
    public static void setRippleEffect(View view, int color) {
        if (view != null)
            MaterialRippleLayout.on(view).rippleColor(color).rippleAlpha(0.2f).rippleDuration(RIPPLE_DURATION).rippleOverlay(true)
                    .rippleHover(true).create();
    }

    public static void setRippleEffectWhite(View view) {
        if (view != null)
            MaterialRippleLayout.on(view).rippleColor(Color.WHITE).rippleAlpha(0.9f).rippleDuration(RIPPLE_DURATION).rippleOverlay(true)
                    .rippleHover(true).rippleRoundedCorners(CORNERS).create();
    }
    public static void setRippleEffectSquareWhite(View view) {
        if (view != null)
            MaterialRippleLayout.on(view).rippleColor(Color.WHITE).rippleAlpha(0.9f).rippleDuration(RIPPLE_DURATION).rippleOverlay(true)
                    .rippleHover(true).create();
    }
    public static void setRippleEffectSquareWhite(View... views) {
        for (View view : views) {
            setRippleEffectSquareWhite(view);
        }
    }

}
