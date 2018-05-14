package com.peermountain.core.odk.views.widgets.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.peermountain.core.R;
import com.peermountain.core.odk.exeptions.JavaRosaException;
import com.peermountain.core.odk.model.FormController;
import com.peermountain.core.odk.utils.Collect;
import com.peermountain.core.odk.utils.TextUtils;
import com.peermountain.core.odk.utils.Timber;
import com.peermountain.core.odk.utils.ViewIds;
import com.peermountain.core.odk.views.widgets.edit_text.StringWidget;
import com.peermountain.core.utils.constants.PmCoreConstants;
import com.peermountain.core.views.PeerMountainTextView;

import org.javarosa.core.model.FormIndex;
import org.javarosa.form.api.FormEntryPrompt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Galeen on 1/29/18.
 */

public abstract class QuestionWidget
        extends LinearLayout
        implements Widget, AudioPlayListener {

    private static final int DEFAULT_PLAY_COLOR = Color.BLUE;
    private static final int DEFAULT_PLAY_BACKGROUND_COLOR = Color.WHITE;

    private final FormEntryPrompt formEntryPrompt;
    private final MediaLayout questionMediaLayout = null;
    private MediaPlayer player;
    private final FrameLayout answerViewParent;

    private Bundle state;

    private int playColor = DEFAULT_PLAY_COLOR;
    private int playBackgroundColor = DEFAULT_PLAY_BACKGROUND_COLOR;

    protected final PeerMountainTextView tvHelp;
    protected final PeerMountainTextView tvLabel;
    protected final int padding, paddingSmall;
    protected static int colorActive, colorError, colorInactive;
    protected final int questionFontSize;

    public QuestionWidget(Context context, FormEntryPrompt prompt) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        if (colorActive == 0) {
            colorActive = getColor(R.color.pm_odk_active);
            colorInactive = getColor(R.color.pm_odk_text_hint);
            colorError = getColor(R.color.pm_odk_text_error);
        }
//        if (context instanceof FormEntryActivity) {
//            state = ((FormEntryActivity) context).getState();
//        }

//        player = new MediaPlayer();
//        getPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                getQuestionMediaLayout().resetTextFormatting();
//                mediaPlayer.reset();
//            }
//
//        });
//
//        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                Timber.e("Error occured in MediaPlayer. what = %d, extra = %d",
//                        what, extra);
//                return false;
//            }
//        });

        questionFontSize = getContext().getResources().getDimensionPixelSize(R.dimen.pm_text_normal);//Collect.getQuestionFontsize();

        padding = getContext().getResources().getDimensionPixelSize(R.dimen.pm_margin_normal);
        paddingSmall = getContext().getResources().getDimensionPixelSize(R.dimen.pm_margin_small);

        formEntryPrompt = prompt;

        setGravity(Gravity.TOP);
//        setPadding(0, 7, 0, 0);

//        questionMediaLayout = createQuestionMediaLayout(prompt);
        tvLabel = createLabelText(prompt);
        tvHelp = createHelpText(prompt);

        answerViewParent = new FrameLayout(getContext());
//
//        addQuestionMediaLayout(getQuestionMediaLayout());
        addLabelTextView(tvLabel);
        addAnswerViewParent();
        addHelpTextView(tvHelp);
    }

    /**
     * Releases resources held by this widget
     */
    public void release() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private MediaLayout createQuestionMediaLayout(FormEntryPrompt prompt) {
        String promptText = prompt.getLongText();//title
        // Add the text view. Textview always exists, regardless of whether there's text.
        TextView questionText = new TextView(getContext());
        questionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, getQuestionFontSize());
        questionText.setTypeface(null, Typeface.BOLD);
        questionText.setTextColor(getColor(R.color.pm_odk_text));
        questionText.setPadding(0, 0, 0, 7);
        questionText.setText(promptText == null ? "" : TextUtils.textToHtml(promptText));
        questionText.setMovementMethod(LinkMovementMethod.getInstance());

        // Wrap to the size of the parent view
        questionText.setHorizontallyScrolling(false);

        if (promptText == null || promptText.length() == 0) {
            questionText.setVisibility(GONE);
        }

        String imageURI = prompt.getImageText();
        String audioURI = prompt.getAudioText();
        String videoURI = prompt.getSpecialFormQuestionText("video");

        // shown when image is clicked
        String bigImageURI = prompt.getSpecialFormQuestionText("big-image");

        // Create the layout for audio, image, text
        MediaLayout questionMediaLayout = new MediaLayout(getContext(), getPlayer());
        questionMediaLayout.setId(ViewIds.generateViewId()); // assign random id
        questionMediaLayout.setAVT(prompt.getIndex(), "", questionText, audioURI, imageURI, videoURI,
                bigImageURI);
        questionMediaLayout.setAudioListener(this);

        String playColorString = prompt.getFormElement().getAdditionalAttribute(null, "playColor");
        if (playColorString != null) {
            try {
                playColor = Color.parseColor(playColorString);
            } catch (IllegalArgumentException e) {
                Timber.e(e, "Argument %s is incorrect", playColorString);
            }
        }
        questionMediaLayout.setPlayTextColor(getPlayColor());

        String playBackgroundColorString = prompt.getFormElement().getAdditionalAttribute(null,
                "playBackgroundColor");
        if (playBackgroundColorString != null) {
            try {
                playBackgroundColor = Color.parseColor(playBackgroundColorString);
            } catch (IllegalArgumentException e) {
                Timber.e(e, "Argument %s is incorrect", playBackgroundColorString);
            }
        }
        questionMediaLayout.setPlayTextBackgroundColor(getPlayBackgroundColor());

        return questionMediaLayout;
    }

    public TextView getHelpTextView() {
        return tvHelp;
    }

    public void playAudio() {
        playAllPromptText();
    }

    public void playVideo() {
        getQuestionMediaLayout().playVideo();
    }

    public FormEntryPrompt getFormEntryPrompt() {
        return formEntryPrompt;
    }

    // http://code.google.com/p/android/issues/detail?id=8488
    private void recycleDrawablesRecursive(ViewGroup viewGroup, List<ImageView> images) {

        int childCount = viewGroup.getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = viewGroup.getChildAt(index);
            if (child instanceof ImageView) {
                images.add((ImageView) child);
            } else if (child instanceof ViewGroup) {
                recycleDrawablesRecursive((ViewGroup) child, images);
            }
        }
        viewGroup.destroyDrawingCache();
    }

    // http://code.google.com/p/android/issues/detail?id=8488
    public void recycleDrawables() {
        List<ImageView> images = new ArrayList<>();
        // collect all the image views
        recycleDrawablesRecursive(this, images);
        for (ImageView imageView : images) {
            imageView.destroyDrawingCache();
            Drawable d = imageView.getDrawable();
            if (d != null && d instanceof BitmapDrawable) {
                imageView.setImageDrawable(null);
                BitmapDrawable bd = (BitmapDrawable) d;
                Bitmap bmp = bd.getBitmap();
                if (bmp != null) {
                    bmp.recycle();
                }
            }
        }
    }

    // Abstract methods

    public abstract void setFocus(Context context);

    public abstract boolean canGetFocus();

    public abstract void setOnLongClickListener(OnLongClickListener l);

    /**
     * Override this to implement fling gesture suppression (e.g. for embedded WebView treatments).
     *
     * @return true if the fling gesture should be suppressed
     */
    public boolean suppressFlingGesture(MotionEvent e1, MotionEvent e2, float velocityX,
                                        float velocityY) {
        return false;
    }

    /*
     * Add a Views containing the question text, audio (if applicable), and image (if applicable).
     * To satisfy the RelativeLayout constraints, we add the audio first if it exists, then the
     * TextView to fit the rest of the space, then the image if applicable.
     */
    /*
     * Defaults to adding questionlayout to the top of the screen.
     * Overwrite to reposition.
     */
    protected void addQuestionMediaLayout(View v) {
        if (v == null) {
            Timber.e("cannot add a null view as questionMediaLayout");
            return;
        }
        // default for questionmedialayout
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.setMargins(10, 0, 10, 0);
        addView(v, params);
    }

    public Bundle getState() {
        return state;
    }

    public Bundle getCurrentState() {
        saveState();
        return state;
    }

    protected void saveState() {
        state = new Bundle();
    }

    /**
     * Add a TextView containing the help text to the default location.
     * Override to reposition.
     */
    protected void addHelpTextView(View v) {
        if (v == null) {
            Timber.e("cannot add a null view as tvHelp");
            return;
        }

        // default for helptext
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//        params.addRule(RelativeLayout.BELOW, getQuestionMediaLayout().getId());
//        params.setMargins(10, 0, 10, 0);
        addView(v, params);
    }

    private PeerMountainTextView createHelpText(FormEntryPrompt prompt) {
        PeerMountainTextView tvHelp = new PeerMountainTextView(getContext());
        String helpText = prompt.getHelpText();

        if (validateHelpText(helpText)) {
            tvHelp.setId(ViewIds.generateViewId());
            setTextSize(tvHelp, R.dimen.pm_text_small);
            //noinspection ResourceType
            tvHelp.setPadding(padding, paddingSmall, padding, paddingSmall);
            // wrap to the widget of view
            tvHelp.setHorizontallyScrolling(false);
//            helpText.setTypeface(null, Typeface.ITALIC);
            tvHelp.setText(TextUtils.textToHtml(helpText));
            tvHelp.setTextColor(getColor(R.color.pm_odk_text_hint));
            tvHelp.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            tvHelp.setVisibility(View.GONE);
        }
        return tvHelp;
    }

    public boolean validateHelpText(String helpText) {
        return helpText != null && !helpText.equals("") && !helpText.startsWith(StringWidget.PREFIX);
    }

    /**
     * Add a TextView containing the label text to the default location.
     * Override to reposition.
     */
    protected void addLabelTextView(View v) {
        if (v == null) {
            Timber.e("cannot add a null view as tvHelp");
            return;
        }

        // default for helptext
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//        params.addRule(RelativeLayout.BELOW, getQuestionMediaLayout().getId());
//        params.setMargins(10, 0, 10, 0);
        addView(v, params);
    }

    private PeerMountainTextView createLabelText(FormEntryPrompt prompt) {
        PeerMountainTextView tvLabel = new PeerMountainTextView(getContext());
        String labelText = prompt.getQuestionText();

        if (labelText != null && !labelText.equals("")) {
            tvLabel.setId(ViewIds.generateViewId());
            setTextSize(tvLabel, R.dimen.pm_text_normal);
            //noinspection ResourceType
            tvLabel.setPadding(padding, paddingSmall, padding, paddingSmall);
            // wrap to the widget of view
            tvLabel.setHorizontallyScrolling(false);
//            helpText.setTypeface(null, Typeface.ITALIC);
            tvLabel.setText(TextUtils.textToHtml(labelText));
            tvLabel.setTextColor(colorActive);
//            tvLabel.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            tvLabel.setVisibility(View.GONE);
        }
        return tvLabel;
    }

    protected void addAnswerViewParent() {
        if (answerViewParent == null) {
            Timber.e("cannot add a null view as addAnswerViewParent");
            return;
        }

        // default for helptext
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(answerViewParent, params);
    }


    public FrameLayout getAnswerViewParent() {
        return answerViewParent;
    }

    /**
     * Default place to put the answer
     * (below the help text or question text if there is no help text)
     * If you have many elements, use this first
     * and use the standard addView(view, params) to place the rest
     */
    protected void addAnswerView(View v) {
        if (v == null) {
            Timber.e("cannot add a null view as an answerView");
            return;
        }
        // default place to add answer
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//        if (getHelpTextView().getVisibility() == View.VISIBLE) {
//            params.addRule(RelativeLayout.BELOW, getHelpTextView().getId());
//        } else {
//            params.addRule(RelativeLayout.BELOW, getQuestionMediaLayout().getId());
//        }
//        int padding = getContext().getResources().getDimensionPixelSize(R.dimen.pm_margin_normal);
//        params.setMargins(padding, 0, padding, 0);
        answerViewParent.removeAllViews();
        answerViewParent.addView(v, params);
    }

    /**
     * Every subclassed widget should override this, adding any views they may contain, and calling
     * super.cancelLongPress()
     */
    public void cancelLongPress() {
        super.cancelLongPress();
        if (getQuestionMediaLayout() != null) {
            getQuestionMediaLayout().cancelLongPress();
        }
        if (getHelpTextView() != null) {
            getHelpTextView().cancelLongPress();
        }
    }

    /*
     * Prompts with items must override this
     */
    public void playAllPromptText() {
        getQuestionMediaLayout().playAudio();
    }

    public void resetQuestionTextColor() {
        getQuestionMediaLayout().resetTextFormatting();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == INVISIBLE || visibility == GONE) {
            stopAudio();
        }
    }

    public void stopAudio() {
        if (player != null && player.isPlaying()) {
            Timber.i("stopAudio " + player);
            player.stop();
            player.reset();
        }
    }

    protected Button getSimpleButton(String text, @IdRes final int withId) {
        final QuestionWidget questionWidget = this;
        final Button button = new Button(getContext());

        button.setId(withId);
        button.setText(text);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getAnswerFontSize());
        button.setPadding(20, 20, 20, 20);

        TableLayout.LayoutParams params = new TableLayout.LayoutParams();
        params.setMargins(7, 5, 7, 5);

        button.setLayoutParams(params);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Collect.allowClick()) {
                    ((ButtonWidget) questionWidget).onButtonClick(withId);
                }
            }
        });
        return button;
    }

    protected Button getSimpleButton(@IdRes int id) {
        return getSimpleButton(null, id);
    }

    protected Button getSimpleButton(String text) {
        return getSimpleButton(text, R.id.simple_button);
    }

    protected TextView getCenteredAnswerTextView() {
        TextView textView = getAnswerTextView();
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    protected TextView getAnswerTextView() {
        TextView textView = new TextView(getContext());

        textView.setId(R.id.answer_text);
        textView.setTextColor(getColor(R.color.pm_odk_text));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getAnswerFontSize());
        textView.setPadding(20, 20, 20, 20);

        return textView;
    }

    protected ImageView getAnswerImageView(Bitmap bitmap) {
        final QuestionWidget questionWidget = this;
        final ImageView imageView = new ImageView(getContext());
        imageView.setId(ViewIds.generateViewId());
        imageView.setPadding(10, 10, 10, 10);
        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionWidget instanceof BaseImageWidget && Collect.allowClick()) {
                    ((BaseImageWidget) questionWidget).onImageClick();
                }
            }
        });
        return imageView;
    }

    /**
     * It's needed only for external choices. Everything works well and
     * out of the box when we use internal choices instead
     */
    protected void clearNextLevelsOfCascadingSelect() {
        FormController formController = Collect.getInstance().getFormController();
        if (formController == null) {
            return;
        }

        if (formController.currentCaptionPromptIsQuestion()) {
            try {
                FormIndex startFormIndex = formController.getQuestionPrompt().getIndex();
                formController.stepToNextScreenEvent();
                while (formController.currentCaptionPromptIsQuestion()
                        && formController.getQuestionPrompt().getFormElement().getAdditionalAttribute(null, "query") != null) {
                    formController.saveAnswer(formController.getQuestionPrompt().getIndex(), null);
                    formController.stepToNextScreenEvent();
                }
                formController.jumpToIndex(startFormIndex);
            } catch (JavaRosaException e) {
                Timber.e(e);
            }
        }
    }

    //region Data waiting

    @Override
    public final void waitForData() {
        Collect collect = Collect.getInstance();
        if (collect == null) {
            throw new IllegalStateException("Collect application instance is null.");
        }

        FormController formController = collect.getFormController();
        if (formController == null) {
            return;
        }

        formController.setIndexWaitingForData(getFormEntryPrompt().getIndex());
    }

    @Override
    public final void cancelWaitingForData() {
        Collect collect = Collect.getInstance();
        if (collect == null) {
            throw new IllegalStateException("Collect application instance is null.");
        }

        FormController formController = collect.getFormController();
        if (formController == null) {
            return;
        }

        formController.setIndexWaitingForData(null);
    }

    @Override
    public final boolean isWaitingForData() {
        Collect collect = Collect.getInstance();
        if (collect == null) {
            throw new IllegalStateException("Collect application instance is null.");
        }

        FormController formController = collect.getFormController();
        if (formController == null) {
            return false;
        }

        FormIndex index = getFormEntryPrompt().getIndex();
        return index.equals(formController.getIndexWaitingForData());
    }

    //region Accessors

    @Nullable
    public final String getInstanceFolder() {
        Collect collect = Collect.getInstance();
        if (collect == null) {
            throw new IllegalStateException("Collect application instance is null.");
        }

        FormController formController = collect.getFormController();
        if (formController == null) {
            return null;
        }

        return formController.getInstancePath().getParent();
    }

    @NonNull
//    public final ActivityLogger getActivityLogger() {
//        Collect collect = Collect.getInstance();
//        if (collect == null) {
//            throw new IllegalStateException("Collect application instance is null.");
//        }
//
//        return collect.getActivityLogger();
//    }

    public int getQuestionFontSize() {
        return questionFontSize;
    }

    public int getAnswerFontSize() {
        return questionFontSize;
    }

    public MediaLayout getQuestionMediaLayout() {
        return questionMediaLayout;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public int getPlayColor() {
        return playColor;
    }

    public int getPlayBackgroundColor() {
        return playBackgroundColor;
    }

    protected boolean isWithError = false;

    public void onAnswerQuestion(boolean isAnswered) {
        isWithError = !isAnswered;
        if (!isAnswered) {
            String errMsg = formEntryPrompt.getConstraintText();
            if(!android.text.TextUtils.isEmpty(errMsg)) {
                tvHelp.setVisibility(VISIBLE);
                tvHelp.setText(errMsg);
                tvHelp.setTextColor(colorError);
            }
            tvLabel.setTextColor(colorError);
        } else {
            String helpText = formEntryPrompt.getHelpText();
            if(validateHelpText(helpText)) {
                tvHelp.setVisibility(View.VISIBLE);
                tvHelp.setText(TextUtils.textToHtml(helpText));
            }else{
                tvHelp.setVisibility(View.GONE);
            }
            tvHelp.setTextColor(colorInactive);
            tvLabel.setTextColor(hasFocus() ? colorActive : colorInactive);
        }
    }

    protected void onFocus(boolean hasFocus) {
        if (isWithError) return;
        if (hasFocus) {
            tvLabel.setTextColor(colorActive);
        } else {
            tvLabel.setTextColor(colorInactive);
        }
    }


    public void onAnswerQuestion(FormController.FailedConstraint constraint) {
        onAnswerQuestion(constraint == null || !constraint.index.equals(formEntryPrompt.getIndex()));
    }

    protected int getColor(int res) {
        return ContextCompat.getColor(getContext(), res);
    }

    protected void setTextSize(TextView tv, int res) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getContext().getResources().getDimension(res));
    }

    /**
     * notify any parent if is listening for touch events to slide
     * to next question or etc, that currently the view needs to slide
     * only
     *
     * @param canParentSlide false - the view needs to slide
     *                       only , true - the view is done
     */
    protected void blockParentTouch(boolean canParentSlide) {
        Intent intent = new Intent(PmCoreConstants.BROAD_CAST_TOUCH_ACTION);
        intent.putExtra(PmCoreConstants.EXTRA_CAN_SLIDE, canParentSlide);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.sendBroadcast(intent);
    }

    protected void defaultSetFocus() {
        requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    /**
     * @param requestCode param from Activity result
     * @param resultCode param from Activity result
     * @param data param from Activity result
     * @return true if is handled
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent data){
        return false;
    }

    public interface Events {
        void requestPermission(String[] permissions,int requestCode,boolean isMandatory, PermissionCallback callback);
    }

    public interface PermissionCallback {
        void onPermission(int[] grantResults);
    }
}

