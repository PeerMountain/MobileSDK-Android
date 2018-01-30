package com.peermountain.core.odk;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.peermountain.core.R;
import com.peermountain.core.odk.exeptions.JavaRosaException;
import com.peermountain.core.odk.utils.Collect;
import com.peermountain.core.odk.utils.Timber;
import com.peermountain.core.odk.utils.TimerLogger;
import com.peermountain.core.odk.views.ODKView;

import org.javarosa.form.api.FormEntryCaption;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryPrompt;

import java.util.ArrayList;

public class XFormFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FormController formController;

    public XFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PmXFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static XFormFragment newInstance(String param1, String param2) {
        XFormFragment fragment = new XFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pm_xform, container, false);
    }

    private ViewFlipper viewFlipper;
    private ViewFlipperController viewFlipperController;
    private TextView tvText, pmTvTitle;
    ArrayList<View> screenViews = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        formController = Collect.getInstance().getFormController();
        getViews(view);
        viewFlipperController = new ViewFlipperController(viewFlipper, view, new ViewFlipperController.Callback() {
            @Override
            public void onNewScreen(int position) {
                tvText.setText("" + position);
            }

            @Override
            public void onFinish() {

            }
        });
        view.findViewById(R.id.pmBtnAddItems).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipperController.showNext();
            }
        });
        if (formController != null) {
            pmTvTitle.setText(formController.getFormTitle());
            int event;
//            try {
            event = formController.stepToNextEvent(true);

            // Helps prevent transition animation at the end of the form (if user swipes left
            // she will stay on the same screen) originalEvent != event ||
            while (event != FormEntryController.EVENT_END_OF_FORM) {
                screenViews.add(createView(event, false));
                if (event == FormEntryController.EVENT_GROUP) {
                    formController.stepOverToGroupEnd();
                }
                event = formController.stepToNextEvent(true);
            }
            screenViews.add(createView(event, false));//this is EVENT_END_OF_FORM
            viewFlipperController.addViews(screenViews);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean dispatchTouchEvent(MotionEvent mv) {
        return viewFlipperController != null && viewFlipperController.onTouch(null, mv);
    }

    private void getViews(View view) {
        tvText = view.findViewById(R.id.tvText);
        pmTvTitle = view.findViewById(R.id.pmTvTitle);
        viewFlipper = view.findViewById(R.id.pmVfXForm);
    }

    ODKView odkView;

    /**
     * Creates a view given the View type and an event
     *
     * @param advancingPage -- true if this results from advancing through the form
     * @return newly created View
     */
    private View createView(int event, boolean advancingPage) {
        if (formController == null) return null;

        formController.getTimerLogger().logTimerEvent(TimerLogger.EventTypes.FEC,
                event, formController.getFormIndex().getReference(), advancingPage, true);

        switch (event) {
            case FormEntryController.EVENT_BEGINNING_OF_FORM:
                return createViewForFormBeginning(event, true, formController);

            case FormEntryController.EVENT_END_OF_FORM:
                TextView endView = new TextView(getContext());
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
                    odkView = new ODKView(getContext(), prompts, groups, advancingPage);
                    Timber.i("Created view for group %s %s",
                            (groups.length > 0 ? groups[groups.length - 1].getLongText() : "[top]"),
                            (prompts.length > 0 ? prompts[0].getQuestionText() : "[no question]"));
                } catch (RuntimeException e) {
                    Timber.e(e);
                    // this is badness to avoid a crash.
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

    private void releaseOdkView() {
//        if (odkView != null) {
//            odkView.releaseWidgetResources();
//            odkView = null;
//        }
    }

    public interface OnFragmentInteractionListener {
    }

    /**
     * Used whenever we need to show empty view and be able to recognize it from the code
     */
    class EmptyView extends View {

        public EmptyView(Context context) {
            super(context);
        }
    }
}
