package com.peermountain.sdk.ui.authorized.menu;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makeramen.roundedimageview.RoundedImageView;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.sdk.PeerMountainSDK;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.authorized.HomeActivity;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;
import com.peermountain.sdk.utils.ripple.RippleUtils;
import com.peermountain.sdk.views.PeerMountainTextView;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MenuFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RoundedImageView mPmIvAvatar;
    private PeerMountainTextView mTvUsername;
    private PeerMountainTextView mTvMenuHome, mTvMenuWipe;
    private PeerMountainTextView mTvMenuDocuments;
    private PeerMountainTextView mTvMenuContacts;
    private PeerMountainTextView mTvMenuSettings;

    public MenuFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initView(View view) {
        mPmIvAvatar = (RoundedImageView) view.findViewById(R.id.pmIvAvatar);
        mTvUsername = (PeerMountainTextView) view.findViewById(R.id.tvUsername);
        mTvMenuHome = (PeerMountainTextView) view.findViewById(R.id.tvMenuHome);
        mTvMenuDocuments = (PeerMountainTextView) view.findViewById(R.id.tvMenuDocuments);
        mTvMenuContacts = (PeerMountainTextView) view.findViewById(R.id.tvMenuContacts);
        mTvMenuSettings = (PeerMountainTextView) view.findViewById(R.id.tvMenuSettings);
        mTvMenuWipe = view.findViewById(R.id.tvMenuWipe);
    }

    /**
     * type oclf to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        mTvMenuHome.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                if (mListener != null)
                    mListener.onMenuHomeClicked();
            }
        });
        mTvMenuContacts.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                if (mListener != null)
                    mListener.onMenuContactsClicked();
            }
        });
        mTvMenuDocuments.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                if (mListener != null)
                    mListener.onMenuDocumentsClicked();
            }
        });
        mTvMenuSettings.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                if (mListener != null)
                    mListener.onMenuSettingsClicked();
            }
        });
        mTvMenuWipe.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                // TODO: 10/13/2017 remove when is done
                DialogUtils.showChoiceDialog(getContext(), -1, R.string.pm_msg_wipe,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PeerMountainSDK.resetProfile();
                                HomeActivity ha = (HomeActivity) getActivity();
                                ha.authorize();
                            }
                        }, null, R.string.pm_btn_ok_wipe, R.string.pm_btn_cancel_wipe);
            }
        });
        RippleUtils.setRippleEffectSquareWhite(mTvMenuContacts, mTvMenuHome, mTvMenuDocuments, mTvMenuSettings);
    }

    /**
     * When the user is authorized HomeActivity will call this method to set btn and name
     */
    public void updateView() {
        Profile profile = PeerMountainManager.getProfile();
        if (profile != null) {
            mTvUsername.setText(profile.getNames());
            if (!TextUtils.isEmpty(profile.getImageUri())) {
                mPmIvAvatar.setImageURI(Uri.parse(profile.getImageUri()));
            } else if (!TextUtils.isEmpty(profile.getPictureUrl())) {
                Picasso.with(getContext())
                        .load(profile.getPictureUrl())
                        .placeholder(R.drawable.pm_profil_white)
                        .error(R.color.pm_error_loading_avatar)
                        .into(mPmIvAvatar);
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onMenuHomeClicked();

        void onMenuDocumentsClicked();

        void onMenuSettingsClicked();

        void onMenuContactsClicked();
    }
}
