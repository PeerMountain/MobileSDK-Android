package com.peermountain.core.odk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.peermountain.core.R;
import com.peermountain.core.odk.exeptions.JavaRosaException;
import com.peermountain.core.odk.model.FormController;
import com.peermountain.core.odk.utils.Collect;
import com.peermountain.core.odk.utils.Timber;
import com.peermountain.core.odk.utils.TimerLogger;
import com.peermountain.core.odk.views.ODKView;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.constants.PmCoreConstants;
import com.peermountain.core.views.GaleenRecyclerView;
import com.rd.PageIndicatorView;

import org.javarosa.form.api.FormEntryCaption;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

import java.util.ArrayList;

public class XFormFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FormController formController;

    public XFormFragment() {
        // Required empty public constructor
    }

    public static XFormFragment newInstance(String param1, String param2) {
        XFormFragment fragment = new XFormFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pm_xform, container, false);
    }

    private ViewFlipper viewFlipper;
    private GaleenRecyclerView rvQuestions;
    private ViewFlipperController viewFlipperController;
    private TextView  pmTvTitle;
    ArrayList<View> odkViews = new ArrayList<>();
    private QuestionsRecyclerAdapter adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        formController = Collect.getInstance().getFormController();
        getViews(view);
        viewFlipperController = new ViewFlipperController(viewFlipper, view, viewFlipperCallback, false);
//        view.findViewById(R.id.pmBtnAddItems).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewFlipperController.showNext();
//            }
//        });
        initOdkViews();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (View view : odkViews) {
            if (view instanceof ODKView) {
                if (((ODKView) view).onActivityResult(requestCode, resultCode, data)) {
                    break;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(touchBroadcast, new IntentFilter(PmCoreConstants.BROAD_CAST_TOUCH_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(touchBroadcast);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
//        if (createOdkViewsTask != null) {
//            createOdkViewsTask.cancel(true);
//            createOdkViewsTask = null;
//        }
    }

//    CreateOdkViewsTask createOdkViewsTask;

    public void initOdkViews() {
        if (formController != null) {
            //add odkViews
            pmTvTitle.setText(formController.getFormTitle());
//            createOdkViewsTask = new CreateOdkViewsTask();
//            createOdkViewsTask.execute();
            int event = formController.getEvent();

            while (event != FormEntryController.EVENT_END_OF_FORM) {
                odkViews.add(createView(event, false));
                if (event == FormEntryController.EVENT_GROUP) {
                    formController.stepOverToGroupEnd();
                }
                try {
                    event = formController.stepToNextScreenEvent();
                } catch (JavaRosaException e) {
                    e.printStackTrace();
                    return;
                }
//                this was last question, noe create exit view and end
                if (event == FormEntryController.EVENT_END_OF_FORM) {
                    odkViews.add(createView(event, false));
                }
            }
            viewFlipperController.addViews(odkViews);
            if (odkViews.size() > 0 && odkViews.get(0) instanceof ODKView) {
                ((ODKView) odkViews.get(0)).setFocus(getContext());
            }

            initRecycler();
        }
    }

    int emptyFields = 1;

    public void initRecycler() {
        rvQuestions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvQuestions.setOnFlingListenerGaleen(new GaleenRecyclerView.OnFlingListenerGaleen() {
            int lastPosition = -1;

            @Override
            public void onFling(int scrolledPosition) {
                if (adapter != null) {
                    if (lastPosition < scrolledPosition
                            || scrolledPosition == adapter.getItemCount() - 1 - emptyFields) {
                        if (viewFlipperController.showNext()) {
                            adapter.updateItems(scrolledPosition);
                            rvQuestions.setCanFling(true);
                            lastPosition = scrolledPosition;
                        } else {
                            rvQuestions.setCanFling(true);
                            rvQuestions.revert();
                        }
                    } else {
                        viewFlipperController.moveRight();
                        adapter.updateItems(scrolledPosition);
                        rvQuestions.setCanFling(true);
                        lastPosition = scrolledPosition;
                    }
                    LogUtils.d("scrolledPosition", "" + scrolledPosition);
                }
            }
        });

        ArrayList<Boolean> list = new ArrayList<>();
        for (int i = 0; i < odkViews.size() + (emptyFields * 2); i++) {
            list.add(false);// TODO: 3/6/18 check if is answered
        }
        rvQuestions.setEmptyFields(emptyFields);
        pageIndicatorView.setCount(odkViews.size());
        rvQuestions.setAdapter(adapter = new QuestionsRecyclerAdapter(list, emptyFields,pageIndicatorView));
        adapter.updateItems(emptyFields);
    }

    public boolean dispatchTouchEvent(MotionEvent mv) {
        return canSlide && viewFlipperController != null
                && viewFlipperController.isWithSwipe()
                && viewFlipperController.onTouch(null, mv);
    }

    ViewFlipperController.Callback viewFlipperCallback = new ViewFlipperController.Callback() {
        @Override
        public void onNewScreen(int position) {
            //this one is called after all answers for the screen are validated and saved
            StringBuilder sb = new StringBuilder();
            if (position == viewFlipperController.getChildCount() - 1) {//init last screen
                for (ODKView odkView1 : viewFlipperController.getOdkViews()) {
                    for (FormEntryPrompt questionPrompt : odkView1.questionPrompts) {
                        sb.append(questionPrompt.getQuestionText());
                        sb.append(" : ");
                        sb.append(questionPrompt.getAnswerText());
                        sb.append("\n");
                    }
                }
                endView.setText(sb.toString());
            } else {
                ODKView currentOdkView = viewFlipperController.getCurrentOdkView();
                if (currentOdkView != null) {
                    currentOdkView.setFocus(getContext());
                }
            }
        }

        @Override
        public void onFinish() {

        }

        @Override
        public FormController getFormController() {
            return formController;
        }

    };
    private PageIndicatorView pageIndicatorView;

    private void getViews(View view) {
        pmTvTitle = view.findViewById(R.id.pmTvTitle);
        viewFlipper = view.findViewById(R.id.pmVfXForm);
        rvQuestions = view.findViewById(R.id.rvQuestions);
        pageIndicatorView = view.findViewById(R.id.pageIndicatorView);
    }

    ODKView odkView;
    TextView endView;

    /**
     * Creates a view given the View type and an event
     *
     * @param advancingPage -- true if this results from advancing through the form
     * @return newly created View
     */
    private View createView(int event, boolean advancingPage) {
        if (formController == null) return null;
// TODO: 2/12/18 this timer must be called on screen loaded
        formController.getTimerLogger().logTimerEvent(TimerLogger.EventTypes.FEC,
                event, formController.getFormIndex().getReference(), advancingPage, true);

        switch (event) {
            case FormEntryController.EVENT_BEGINNING_OF_FORM:
                return createViewForFormBeginning(event, true, formController);

            case FormEntryController.EVENT_END_OF_FORM:
                endView = new TextView(getContext());
                endView.setText("End view!");
//                ((TextView) endView.findViewById(R.id.description))
//                        .setText(getString(R.string.save_enter_data_description,
//                                formController.getFormTitle()));
//
//                // checkbox for if finished or ready to send
//                final CheckBox instanceComplete = endView
//                        .findViewById(R.id.mark_finished);
//                instanceComplete.setChecked(isInstanceComplete(true));
//
//                if (!(boolean) AdminSharedPreferences.getInstance().get(AdminKeys.KEY_MARK_AS_FINALIZED)) {
//                    instanceComplete.setVisibility(View.GONE);
//                }
//
//                // edittext to change the displayed name of the instance
//                final EditText saveAs = endView.findViewById(R.id.save_name);
//
//                // disallow carriage returns in the name
//                InputFilter returnFilter = new InputFilter() {
//                    public CharSequence filter(CharSequence source, int start,
//                                               int end, Spanned dest, int dstart, int dend) {
//                        for (int i = start; i < end; i++) {
//                            if (Character.getType((source.charAt(i))) == Character.CONTROL) {
//                                return "";
//                            }
//                        }
//                        return null;
//                    }
//                };
//                saveAs.setFilters(new InputFilter[]{returnFilter});
//
//                if (formController.getSubmissionMetadata().instanceName == null) {
//                    // no meta/instanceName field in the form -- see if we have a
//                    // name for this instance from a previous save attempt...
//                    String uriMimeType = null;
//                    Uri instanceUri = getIntent().getData();
//                    if (instanceUri != null) {
//                        uriMimeType = getContentResolver().getType(instanceUri);
//                    }
//
//                    if (saveName == null && uriMimeType != null
//                            && uriMimeType.equals(InstanceColumns.CONTENT_ITEM_TYPE)) {
//                        Cursor instance = null;
//                        try {
//                            instance = getContentResolver().query(instanceUri,
//                                    null, null, null, null);
//                            if (instance != null && instance.getCount() == 1) {
//                                instance.moveToFirst();
//                                saveName = instance
//                                        .getString(instance
//                                                .getColumnIndex(InstanceColumns.DISPLAY_NAME));
//                            }
//                        } finally {
//                            if (instance != null) {
//                                instance.close();
//                            }
//                        }
//                    }
//                    if (saveName == null) {
//                        // last resort, default to the form title
//                        saveName = formController.getFormTitle();
//                    }
//                    // present the prompt to allow user to name the form
//                    TextView sa = endView.findViewById(R.id.save_form_as);
//                    sa.setVisibility(View.VISIBLE);
//                    saveAs.setText(saveName);
//                    saveAs.setEnabled(true);
//                    saveAs.setVisibility(View.VISIBLE);
//                    saveAs.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void afterTextChanged(Editable s) {
//                            saveName = String.valueOf(s);
//                        }
//
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        }
//                    });
//                } else {
//                    // if instanceName is defined in form, this is the name -- no
//                    // revisions
//                    // display only the name, not the prompt, and disable edits
//                    saveName = formController.getSubmissionMetadata().instanceName;
//                    TextView sa = endView.findViewById(R.id.save_form_as);
//                    sa.setVisibility(View.GONE);
//                    saveAs.setText(saveName);
//                    saveAs.setEnabled(false);
//                    saveAs.setVisibility(View.VISIBLE);
//                }
//
//                // override the visibility settings based upon admin preferences
//                if (!(boolean) AdminSharedPreferences.getInstance().get(AdminKeys.KEY_SAVE_AS)) {
//                    saveAs.setVisibility(View.GONE);
//                    TextView sa = endView
//                            .findViewById(R.id.save_form_as);
//                    sa.setVisibility(View.GONE);
//                }
//
//                // Create 'save' button
//                endView.findViewById(R.id.save_exit_button)
//                        .setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Collect.getInstance()
//                                        .getActivityLogger()
//                                        .logInstanceAction(
//                                                this,
//                                                "createView.saveAndExit",
//                                                instanceComplete.isChecked() ? "saveAsComplete"
//                                                        : "saveIncomplete");
//                                // Form is marked as 'saved' here.
//                                if (saveAs.getText().length() < 1) {
//                                    ToastUtils.showShortToast(R.string.save_as_error);
//                                } else {
//                                    saveDataToDisk(EXIT, instanceComplete
//                                            .isChecked(), saveAs.getText()
//                                            .toString());
//                                }
//                            }
//                        });
//
//                if (showNavigationButtons) {
//                    backButton.setEnabled(allowMovingBackwards);
//                    nextButton.setEnabled(false);
//                }

                return endView;
            case FormEntryController.EVENT_QUESTION:
            case FormEntryController.EVENT_GROUP:
            case FormEntryController.EVENT_REPEAT:
                releaseOdkView();
                // should only be a group here if the event_group is a field-list
                try {
                    FormEntryPrompt[] prompts = formController.getQuestionPrompts();
                    FormEntryCaption[] groups = formController
                            .getGroupsForCurrentIndex();
                    odkView = new ODKView(getActivity(), prompts, groups, advancingPage);
                    Timber.i("Created view for group %s %s",
                            (groups.length > 0 ? groups[groups.length - 1].getLongText() : "[top]"),
                            (prompts.length > 0 ? prompts[0].getQuestionText() : "[no question]"));
                } catch (RuntimeException e) {
                    Timber.e(e);
                    // this is badness to avoid a crash.
                    if (event == FormEntryController.EVENT_GROUP) {
                        formController.stepOverToGroupEnd();
                    }
                    try {
                        event = formController.stepToNextScreenEvent();
//                        createErrorDialog(e.getMessage(), DO_NOT_EXIT);
                    } catch (JavaRosaException e1) {
                        Timber.e(e1);
//                        createErrorDialog(e.getMessage() + "\n\n" + e1.getCause().getMessage(),
//                                DO_NOT_EXIT);
                    }
                    return createView(event, advancingPage);
                }

                // Makes a "clear answer" menu pop up on long-click
//                for (QuestionWidget qw : odkView.getWidgets()) {
//                    if (!qw.getFormEntryPrompt().isReadOnly()) {
//                        // If it's a StringWidget register all its elements apart from EditText as
//                        // we want to enable paste option after long click on the EditText
//                        if (qw instanceof StringWidget) {
//                            for (int i = 0; i < qw.getChildCount(); i++) {
//                                if (!(qw.getChildAt(i) instanceof EditText)) {
//                                    registerForContextMenu(qw.getChildAt(i));
//                                }
//                            }
//                        } else {
//                            registerForContextMenu(qw);
//                        }
//                    }
//                }

//                if (showNavigationButtons) {
//                    adjustBackNavigationButtonVisibility();
//                    nextButton.setEnabled(true);
//                }
                return odkView;

            case FormEntryController.EVENT_PROMPT_NEW_REPEAT:
//                createRepeatDialog();
                return new EmptyView(getContext());

            default:
                Timber.e("Attempted to create a view that does not exist.");
                // this is badness to avoid a crash.
//                try {
                event = formController.stepToNextEvent(true);
//                    createErrorDialog(getString(R.string.survey_internal_error), EXIT);
//                } catch (JavaRosaException e) {
//                    Timber.e(e);
////                    createErrorDialog(e.getCause().getMessage(), EXIT);
//                }
                return createView(event, advancingPage);
        }
    }

    private View createViewForFormBeginning(int event, boolean advancingPage,
                                            FormController formController) {
        try {
            event = formController.stepToNextScreenEvent();

        } catch (JavaRosaException e) {
            Timber.e(e);
//            createErrorDialog(e.getMessage() + "\n\n" + e.getCause().getMessage(), DO_NOT_EXIT);
        }

        return createView(event, advancingPage);
    }

    /**
     * This must be called only if you show 1 question at a time
     */
    private void releaseOdkView() {
//        if (odkView != null) {
//            odkView.releaseWidgetResources();
//            odkView = null;
//        }
    }

    public interface OnFragmentInteractionListener {
    }

    private boolean canSlide = true;
    private BroadcastReceiver touchBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                canSlide = intent.getBooleanExtra(PmCoreConstants.EXTRA_CAN_SLIDE, true);
            }
        }
    };

    /**
     * Used whenever we need to show empty view and be able to recognize it from the code
     */
    class EmptyView extends View {

        public EmptyView(Context context) {
            super(context);
        }
    }
}
