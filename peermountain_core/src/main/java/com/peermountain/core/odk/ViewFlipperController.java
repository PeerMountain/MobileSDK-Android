package com.peermountain.core.odk;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.peermountain.core.R;
import com.peermountain.core.odk.exeptions.JavaRosaException;
import com.peermountain.core.odk.model.FormController;
import com.peermountain.core.odk.model.SaveResult;
import com.peermountain.core.odk.tasks.SaveToDiskTask;
import com.peermountain.core.odk.utils.Timber;
import com.peermountain.core.odk.views.ODKView;

import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.data.IAnswerData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Galeen on 1/29/18.
 * keeps common logic for viewFlipper
 */

public class ViewFlipperController implements View.OnTouchListener {
    private ViewFlipper viewFlipper;
    private boolean withAnimation = false, withFinishOnLast = false, isInfinite = false, isAutoStart = false;
    private float downXValue;
    private Callback callback;
    private View viewToFling;
    private boolean withSwipe;

    public ViewFlipperController(ViewFlipper viewFlipper, View viewToFling, Callback callback, boolean withSwipe) {
        this.viewFlipper = viewFlipper;
        this.callback = callback;
        this.viewToFling = viewToFling;
        this.withSwipe = withSwipe;
        setFlipperListeners();
        if (withSwipe) {
            viewToFling.setOnTouchListener(this);
        }
    }

    public ViewFlipperController(ViewFlipper viewFlipper, View viewToFling, boolean withAnimation, boolean withFinishOnLast, boolean isInfinite, boolean isAutoStart, Callback callback, boolean withSwipe) {
        this(viewFlipper, viewToFling, callback, withSwipe);
        this.withAnimation = withAnimation;
        this.withFinishOnLast = withFinishOnLast;
        this.isInfinite = isInfinite;
        this.isAutoStart = isAutoStart;
        startFlipper();
    }

    private ArrayList<View> views;
    private int displayedChild = 0;

    public void addViews(ArrayList<View> views) {
        this.views = views;
//        int size = views.size();
//        for (int i = 0; i < size; i++) {
        if (viewFlipper.getChildCount() == 0 && callback != null) {
            callback.onNewScreen(0);
        }

        viewFlipper.addView(views.get(0));
        viewFlipper.addView(views.get(1));
//        }
    }

    private void addView(View view, boolean back) {
        if (viewFlipper.getChildCount() == 3) {//keep only 3 views current 1 before and 1 after
            int pos = getDisplayedChild() + (back ? 2 : -2);
            if (pos <= getChildCount() - 1 && pos >= 0) {//if valid view remove
                View viewToRemove = views.get(pos);
                removeView(viewToRemove);
            }
        }
        if (view.getParent() == null){
            viewFlipper.addView(view, back ? 0 : 2);// add 1 view before or after
        }
    }

    private void removeView(View view) {
        viewFlipper.removeView(view);
    }

    public int getChildCount() {
        return views == null ? 0 : views.size();
    }

    private int getDisplayedChild() {
        return displayedChild;
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // Get the action that was done on this touch event
        switch (arg1.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // store the X value when the user's finger was pressed down
                downXValue = arg1.getX();
                break;
            }

//            case MotionEvent.ACTION_MOVE: {
//                // store the X value when the user's finger was pressed down
//                if (downXValue == 0) downXValue = arg1.getX();
//                break;
//            }

            case MotionEvent.ACTION_UP: {
                // Get the X value when the user released his/her finger
                float currentX = arg1.getX();

                // going backwards: pushing stuff to the right
                if (downXValue - currentX < -200) {
                    moveRight();
                }

                // going forwards: pushing stuff to the left
                if (downXValue - currentX > 200) {
                    moveLeft();
                }
                downXValue = 0;
                break;
            }
        }

        // if you return false, these actions will not be recorded
        return false;
    }

    private void moveLeft() {
        if (getDisplayedChild() < getChildCount() - 1 || isInfinite) {
            showNext();
        } else {
            if (withFinishOnLast) finish();
        }
    }

    private void showNextQuestion() {
        viewFlipper.stopFlipping();
        // Set the animation
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.pm_slide_left));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.pm_slide_out_left));
        boolean updateParent = true;
        if (getDisplayedChild() == getChildCount() - 1 && callback != null) {
            callback.onNewScreen(getDisplayedChild());
            updateParent = false;
        }
        // Flip!
        viewFlipper.showNext();
        displayedChild++;
        if (displayedChild < getChildCount() - 1) {
            addView(views.get(displayedChild + 1), false);//first
        }
        startFlipper();
        if (updateParent) setTextAndDots();
    }

    SaveToDiskTask saveToDiskTask;

    public boolean showNext() {
        if (getDisplayedChild() == getChildCount() - 1) {
            saveXFormAnswers();
            return true;
        }

        if (viewFlipper.getCurrentView() instanceof ODKView && callback != null
                && callback.getFormController() != null) {
            ODKView view = (ODKView) viewFlipper.getCurrentView();
            HashMap<FormIndex, IAnswerData> answers = view.getAnswers();
            try {
                FormController.FailedConstraint constraint = callback.getFormController().saveAllScreenAnswers(answers, true);
                view.onAnswerQuestion(constraint);
                if (constraint == null) {
                    showNextQuestion();
                    return true;
                }
            } catch (JavaRosaException e) {
                Timber.e(e);
            }
        } else {
            showNextQuestion();
            return true;
        }
        return false;
    }

    public void saveXFormAnswers() {
        if (saveToDiskTask == null || saveToDiskTask.getStatus() != AsyncTask.Status.RUNNING) {
            saveToDiskTask = new SaveToDiskTask(null, true, true, "test_to_save");
            saveToDiskTask.setFormSavedListener(new SaveToDiskTask.FormSavedListener() {
                @Override
                public void savingComplete(SaveResult saveStatus) {
                    if (saveStatus != null) {
                        Toast.makeText(viewFlipper.getContext(), "code : " + saveStatus.getSaveResult() +
                                "\nmsg : " +
                                saveStatus.getSaveErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onProgressStep(String stepMessage) {
                    Toast.makeText(viewFlipper.getContext(), stepMessage, Toast.LENGTH_SHORT).show();
                }
            });
            saveToDiskTask.execute();
        }
    }

    private void showPrev() {
        viewFlipper.stopFlipping();
        // Set the animation
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.pm_slide_right));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.pm_slide_out_right));
        // Flip!
        viewFlipper.showPrevious();
        displayedChild--;
        if (displayedChild > 0) {
            addView(views.get(displayedChild - 1), true);//first
        }
        startFlipper();
        setTextAndDots();
    }

    public void moveRight() {
        if (getDisplayedChild() > 0 || isInfinite) {
            showPrev();
        }
    }

    private void startFlipper() {
        if (withAnimation) {
            viewFlipper.setFlipInterval(5000);
            viewFlipper.startFlipping();
        }
    }

    private void setFlipperListeners() {
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.pm_slide_left));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.pm_slide_out_left));
        viewFlipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
                setTextAndDots();
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                int displayedChild = getDisplayedChild();
                int childCount = getChildCount();
                if (displayedChild == childCount - 1) {
                    if (!isInfinite) {
                        viewFlipper.stopFlipping();
                        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),
                                R.anim.pm_slide_right));
                        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
                                R.anim.pm_slide_out_right));
                    }
                }
            }
        });
    }

    private void setTextAndDots() {
        if (callback != null) callback.onNewScreen(getDisplayedChild());
    }

    private void finish() {
        if (callback != null) callback.onFinish();
    }

    private Context getContext() {
        return viewFlipper.getContext();
    }

    public ArrayList<ODKView> getOdkViews() {
        ArrayList<ODKView> res = new ArrayList<>();
        int end = views.size()-1;
        for (int i = 0; i < end; i++) {
            if (views.get(i) instanceof ODKView) {
                res.add((ODKView) views.get(i));
            }
        }
        return res;
    }

    public ODKView getCurrentOdkView() {
        return viewFlipper.getCurrentView() instanceof ODKView ? (ODKView) viewFlipper.getCurrentView() : null;
    }

    public boolean isWithSwipe() {
        return withSwipe;
    }

    public void setWithSwipe(boolean withSwipe) {
        this.withSwipe = withSwipe;
    }

    public interface Callback {
        void onNewScreen(int position);

        void onFinish();

        FormController getFormController();
    }
}
