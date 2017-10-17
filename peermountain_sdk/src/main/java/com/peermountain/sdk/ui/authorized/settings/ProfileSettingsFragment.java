package com.peermountain.sdk.ui.authorized.settings;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.peermountain.core.model.guarded.Contact;
import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.HomeToolbarFragment;
import com.peermountain.sdk.views.PeerMountainTextView;
import com.squareup.picasso.Picasso;

public class ProfileSettingsFragment extends HomeToolbarFragment {
    private static final String ARG_CONTACT = "param1";


    private OnFragmentInteractionListener mListener;

    Contact contact = null;
    private RoundedImageView pmIvAvatar;
    private PeerMountainTextView tvTabGeneral;
    private PeerMountainTextView tvTabSocial, tvAddContact;
    private LinearLayout llGeneralData;
    private EditText etNames;
    private EditText etDob;
    private EditText etPob;
    private EditText etEmail;
    private EditText etPhone;
    private LinearLayout llSocialData;
    private EditText etFb;
    private EditText etLn;
    private boolean isMe = true;

    public ProfileSettingsFragment() {
        // Required empty public constructor
    }

    public static ProfileSettingsFragment newInstance(Contact contact) {
        ProfileSettingsFragment fragment = new ProfileSettingsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CONTACT, contact);
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
            Contact otherContact = getArguments().getParcelable(ARG_CONTACT);
            if (otherContact != null) {
                contact = otherContact;
                isMe = false;
            } else {
                contact = PeerMountainManager.getProfile();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_fragment_profile_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setUpView();
        setListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initView(View view) {
        pmIvAvatar = (RoundedImageView) view.findViewById(R.id.pmIvAvatar);
        tvTabGeneral = (PeerMountainTextView) view.findViewById(R.id.tvTabGeneral);
        tvTabSocial = (PeerMountainTextView) view.findViewById(R.id.tvTabSocial);
        llGeneralData = (LinearLayout) view.findViewById(R.id.llGeneralData);
        etNames = (EditText) view.findViewById(R.id.etNames);
        etDob = (EditText) view.findViewById(R.id.etDob);
        etPob = (EditText) view.findViewById(R.id.etPob);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPhone = (EditText) view.findViewById(R.id.etPhone);
        llSocialData = (LinearLayout) view.findViewById(R.id.llSocialData);
        etFb = (EditText) view.findViewById(R.id.etFb);
        etLn = (EditText) view.findViewById(R.id.etLn);
        tvAddContact = view.findViewById(R.id.tvAddContact);
    }

    public void setUpView() {
        if (isMe) {
            tvAddContact.setVisibility(View.GONE);
            setToolbarForMyProfile();
        }else {
            setToolbarForContact();
        }
        if(contact==null) return;
        etNames.setText(contact.getNames());
        etDob.setText(contact.getDob());
        etPob.setText(contact.getPob());
        etEmail.setText(contact.getMail());
        etPhone.setText(contact.getPob());
        setUpPublicProfiles();
        setUpAvatar();
    }

    public void setToolbarForContact() {
        setToolbar(R.drawable.pm_ic_arrow_back_24dp,-1, R.string.pm_other_profile_settings_title, homeToolbarEvents != null ? homeToolbarEvents.getOpenMenuListener() : null, null);
    }

    public void setToolbarForMyProfile() {
        setToolbar(R.drawable.pm_ic_logo_white, R.drawable.pm_ic_edit_24dp, R.string.pm_profile_settings_title, homeToolbarEvents!=null?homeToolbarEvents.getOpenMenuListener():null, null);
    }

    public void setUpAvatar() {
        if (!TextUtils.isEmpty(contact.getImageUri())) {
            pmIvAvatar.setImageURI(Uri.parse(contact.getImageUri()));
        } else if (!TextUtils.isEmpty(contact.getPictureUrl())) {
            Picasso.with(getContext())
                    .load(contact.getPictureUrl())
                    .placeholder(R.drawable.pm_profil_white)
                    .error(R.color.pm_error_loading_avatar)
                    .into(pmIvAvatar);
        }
    }

    public void setUpPublicProfiles() {
        if (contact.getPublicProfiles() != null && contact.getPublicProfiles().size() > 0) {
            for (PublicUser publicUser : contact.getPublicProfiles()) {
                if (publicUser != null) {
                    switch (publicUser.getLoginType()) {
                        case PublicUser.LOGIN_TYPE_FB:
                            etFb.setText(TextUtils.isEmpty(publicUser.getEmail()) ?
                                    publicUser.getFirstname() : publicUser.getEmail());
                            break;
                        case PublicUser.LOGIN_TYPE_LN:
                            etLn.setText(TextUtils.isEmpty(publicUser.getEmail()) ?
                                    publicUser.getFirstname() : publicUser.getEmail());
                            break;
                    }
                }
            }
        }
    }

    /**
     * type oclf to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        if (!isMe) {
            tvAddContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: 10/14/2017 implement add contact
//                    if (mListener != null)
//                        mListener.;
                }
            });
        }
        tvTabGeneral.setOnClickListener(tabClick);
        tvTabSocial.setOnClickListener(tabClick);
    }

    private void initTabs() {
        int colorActive = ContextCompat.getColor(getActivity(), R.color.pm_tab_text_color);
        int colorInactive = ContextCompat.getColor(getActivity(), R.color.pm_tab_text_inactive);
        tvTabGeneral.setTextColor(showGeneral ? colorActive : colorInactive);
        tvTabSocial.setTextColor(showGeneral ? colorInactive : colorActive);
        llGeneralData.setVisibility(showGeneral ? View.VISIBLE : View.GONE);
        llSocialData.setVisibility(showGeneral ? View.GONE : View.VISIBLE);
    }

    private boolean showGeneral = true;
    View.OnClickListener tabClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == tvTabSocial.getId()) {
                if (showGeneral) {
                    showGeneral = false;
                    initTabs();
                }
            } else {
                if (!showGeneral) {
                    showGeneral = true;
                    initTabs();
                }
            }
        }
    };

    public interface OnFragmentInteractionListener {

    }
}
