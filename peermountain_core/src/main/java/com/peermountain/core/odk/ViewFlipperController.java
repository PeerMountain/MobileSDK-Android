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

    public ViewFlipperController(ViewFlipper viewFlipper, View viewToFling, Callback callback) {
        this.viewFlipper = viewFlipper;
        this.callback = callback;
        this.viewToFling = viewToFling;
        setFlipperListeners();
        viewToFling.setOnTouchListener(this);
    }

    public ViewFlipperController(ViewFlipper viewFlipper, View viewToFling, boolean withAnimation, boolean withFinishOnLast, boolean isInfinite, boolean isAutoStart, Callback callback) {
        this(viewFlipper, viewToFling, callback);
        this.withAnimation = withAnimation;
        this.withFinishOnLast = withFinishOnLast;
        this.isInfinite = isInfinite;
        this.isAutoStart = isAutoStart;
        startFlipper();
    }

    public void addViews(ArrayList<View> views) {
        int size = views.size();
        for (int i = 0; i < size; i++) {
            addView(views.get(i));
        }
    }

    public void addView(View view) {
        if (viewFlipper.getChildCount() == 0 && callback != null) {
            callback.onNewScreen(0);
        }
        viewFlipper.addView(view);

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
        if (viewFlipper.getDisplayedChild() < viewFlipper.getChildCount() - 1 || isInfinite) {
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
        if (viewFlipper.getDisplayedChild() == viewFlipper.getChildCount() - 2 && callback != null) {
            callback.onNewScreen(viewFlipper.getDisplayedChild() + 1);
            updateParent = false;
        }
        // Flip!
        viewFlipper.showNext();
        startFlipper();
        if (updateParent) setTextAndDots();
    }

    SaveToDiskTask saveToDiskTask;

    public void showNext() {
        if (viewFlipper.getDisplayedChild() == viewFlipper.getChildCount() - 1) {
            if (saveToDiskTask == null || saveToDiskTask.getStatus() != AsyncTask.Status.RUNNING) {
                saveToDiskTask = new SaveToDiskTask(null,true,true,"test_to_save");
                saveToDiskTask.setFormSavedListener(new SaveToDiskTask.FormSavedListener() {
                    @Override
                    public void savingComplete(SaveResult saveStatus) {
                        if(saveStatus!=null){
                            Toast.makeText(viewFlipper.getContext(), "code : "+saveStatus.getSaveResult()+
                                    "\nmsg : "+
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
            return;
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
                }
            } catch (JavaRosaException e) {
                Timber.e(e);
            }
        } else {
            showNextQuestion();
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
        startFlipper();
        setTextAndDots();
    }

    private void moveRight() {
        if (viewFlipper.getDisplayedChild() > 0 || isInfinite) {
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
                int displayedChild = viewFlipper.getDisplayedChild();
                int childCount = viewFlipper.getChildCount();
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
        if (callback != null) callback.onNewScreen(viewFlipper.getDisplayedChild());
    }

    private void finish() {
        if (callback != null) callback.onFinish();
    }

    private Context getContext() {
        return viewFlipper.getContext();
    }

    public ArrayList<ODKView> getOdkViews() {
        ArrayList<ODKView> res = new ArrayList<>();
        int end = viewFlipper.getChildCount();
        for (int i = 0; i < end; i++) {
            if (viewFlipper.getChildAt(i) instanceof ODKView) {
                res.add((ODKView) viewFlipper.getChildAt(i));
            }
        }
        return res;
    }

    public ODKView getCurrentOdkView() {
        return viewFlipper.getCurrentView() instanceof ODKView ? (ODKView) viewFlipper.getCurrentView() : null;
    }

    public interface Callback {
        void onNewScreen(int position);

        void onFinish();

        FormController getFormController();
    }
}
