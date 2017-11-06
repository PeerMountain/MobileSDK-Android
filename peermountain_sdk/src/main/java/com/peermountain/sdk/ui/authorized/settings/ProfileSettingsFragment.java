package com.peermountain.sdk.ui.authorized.settings;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.peermountain.core.camera.CameraActivity;
import com.peermountain.core.camera.ShowLiveSelfieActivity;
import com.peermountain.core.model.guarded.Contact;
import com.peermountain.core.model.guarded.PmAccessToken;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.FileUtils;
import com.peermountain.core.utils.ImageUtils;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PmSystemHelper;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.HomeToolbarFragment;
import com.peermountain.sdk.ui.register.RegisterProfileFragment;
import com.peermountain.sdk.utils.PublicProfileUtils;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;
import com.peermountain.sdk.views.PeerMountainTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ProfileSettingsFragment extends HomeToolbarFragment {
    private static final String ARG_CONTACT = "param1";
    public static final int REQUEST_IMAGE_CAPTURE = 711;
    public static final int REQUEST_LIVE_IMAGE_CAPTURE = 771;
    public static final int REQUEST_CODE_LIVE_WRITE_PERMISSION = 773;
    public static final int REQUEST_CODE_WRITE_PERMISSION = 723;
    public static final int REQUEST_CODE_SIGN_IN_GOOGLE = 727;

    private OnFragmentInteractionListener mListener;

    Contact contact = null;
    private RoundedImageView pmIvAvatar;
    private ImageView ivValidated;
    private FrameLayout flValidate;
    private PeerMountainTextView tvTabGeneral;
    private PeerMountainTextView tvTabSocial, tvAddContact, tvValidateContact;
    private LinearLayout llGeneralData;
    private EditText etNames;
    private EditText etDob;
    private EditText etPob;
    private EditText etEmail;
    private EditText etPhone;
    private LinearLayout llSocialData;
    private boolean isMe = true;

    private CallbackManager callbackManager;
    private LoginManager manager;

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

    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        prepareGoogleSignIn();

        if (getArguments() != null) {
            Contact otherContact = getArguments().getParcelable(ARG_CONTACT);
            if (otherContact != null) {
                contact = otherContact;
                isMe = false;
                if (PeerMountainManager.getContacts().contains(contact)) {
                    toAdd = false;
                }
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
        initBtn();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
//        if we have removed the contact from ours than remove the image from app memory
        if (!isMe
                && !PeerMountainManager.getContacts().contains(contact)
                && !TextUtils.isEmpty(contact.getImageUri())) {
            File avatar = FileUtils.getFile(getContext(), Uri.parse(contact.getImageUri()));
            if (avatar != null && avatar.exists()) {
                avatar.delete();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_WRITE_PERMISSION:
                if (checkPermissions()) {
                    requestCapture();
                } else {
                    // TODO: 10/23/17 show exp dialog
                }
                break;
            case REQUEST_CODE_LIVE_WRITE_PERMISSION:
                if (checkPermissions()) {
                    doLiveSelfie();
                } else {
                    // TODO: 10/23/17 show exp dialog
                }
                break;
        }
    }

    Uri imageUri = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(getActivity(), requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    if (data != null && data.getData() != null) {
                        imageUri = data.getData();
                        saveProfile();
                    } else {
                        if (avatarFile != null && avatarFile.exists()) {
                            imageUri = Uri.fromFile(avatarFile);
                            saveProfile();
                        }
                    }
                    break;
                case REQUEST_LIVE_IMAGE_CAPTURE:
                    if(CameraActivity.bitmaps!=null
                            && CameraActivity.bitmaps.size()>0) {
                        pmIvAvatar.setImageDrawable(new BitmapDrawable(getResources(), CameraActivity.bitmaps.get(0)));
                    }
//                    loadAvatar(getContext(), PeerMountainManager.getProfile(), pmIvAvatar);
                    break;
            }
        }
    }

    public void prepareGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = mListener.getGoogleApiClientForSignIn(gso);
    }

    private Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    private void saveProfile() {
        if (contact != null) {
            if (isMe) {
                if (imageUri != null) {
                    contact.setImageUri(imageUri.toString());
                    setUpAvatar();
                }
                saveMyProfile();
            } else if (imageUri != null) {
                contact.setValidated(true);
                contact.setValidatedImageUri(imageUri.toString());
                PeerMountainManager.updateContact(contact);
                initValidateBtn();
            }
        }
    }

    File dir;
    ArrayList<File> files = new ArrayList<>();
    int imagesInProcess = 0;
    private void saveLiveSelfie(){
        setToolbar(-1,R.string.pm_profile_settings_title,null);
        prepareDir();
        imagesInProcess = 0;
        for (Bitmap bitmap : CameraActivity.bitmaps) {
            saveFrame(bitmap);
        }
        CameraActivity.bitmaps=null;
    }

    private void saveFrame(Bitmap bitmap) {
        String name = System.currentTimeMillis()+imagesInProcess + ".jpg";
        File file = new File(dir, name);
        files.add(file);
        imagesInProcess++;
        ImageUtils.saveImageAsync(file, bitmap, new ImageUtils.SaveImageEvents() {
            @Override
            public void onFinish(boolean isSuccess) {
                imagesInProcess--;
                LogUtils.d("onPicture", "is saved : " + isSuccess);
                if (imagesInProcess <= 0) {
                    linkImagesToMyProfile();
                }
            }
        });
    }

    private void prepareDir() {
        // TODO: 11/6/17 set dir in local storage?
        dir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"/liveSelfie");
        if(dir.exists()){
            if(dir.listFiles().length>0) {
                for (File file : dir.listFiles()) {
                    file.delete();
                }
            }
        }else {
            dir.mkdirs();
        }
    }

    private void linkImagesToMyProfile() {
        ArrayList<String> liveSelfie = new ArrayList<>();
        if (files.size() == 0) return;
        Profile profile = (Profile) contact;
        profile.setImageUri(Uri.fromFile(files.get(0)).toString());
        for (int i = 0; i < files.size(); i++) {
            liveSelfie.add(Uri.fromFile(files.get(i)).toString());
        }
        profile.setLiveSelfie(liveSelfie);
        setViewForSavedProfile();
    }


    private void saveMyProfile() {
        contact.setNames(etNames.getText().toString());
        contact.setDob(etDob.getText().toString());
        contact.setPob(etPob.getText().toString());
        contact.setPhone(etPhone.getText().toString());
        contact.setMail(etEmail.getText().toString());
        if(CameraActivity.bitmaps!=null && CameraActivity.bitmaps.size()>0){
            saveLiveSelfie();
        }else {
            setViewForSavedProfile();
        }
    }

    private void setViewForSavedProfile() {
        PeerMountainManager.saveProfile((Profile) contact);
        setUpView();
        if (mListener != null) mListener.onMyProfileUpdated();
    }

    TextView pmTvFB, pmTvFBConnect, pmTvLN, pmTvLNConnect, pmTvG, pmTvGConnect;
    ImageView pmIvAvatarLive;

    private void initView(View view) {
        pmIvAvatar = view.findViewById(R.id.pmIvAvatar);
        pmIvAvatarLive = view.findViewById(R.id.pmIvAvatarLive);
        tvTabGeneral = (PeerMountainTextView) view.findViewById(R.id.tvTabGeneral);
        tvTabSocial = (PeerMountainTextView) view.findViewById(R.id.tvTabSocial);
        llGeneralData = (LinearLayout) view.findViewById(R.id.llGeneralData);
        etNames = (EditText) view.findViewById(R.id.etNames);
        etDob = (EditText) view.findViewById(R.id.etDob);
        etPob = (EditText) view.findViewById(R.id.etPob);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPhone = (EditText) view.findViewById(R.id.etPhone);
        llSocialData = (LinearLayout) view.findViewById(R.id.llSocialData);
        tvAddContact = view.findViewById(R.id.tvAddContact);
        tvValidateContact = view.findViewById(R.id.tvValidateContact);
        flValidate = view.findViewById(R.id.flValidate);
        ivValidated = view.findViewById(R.id.ivValidated);
        pmTvFB = view.findViewById(R.id.pmTvFB);
        pmTvFBConnect = view.findViewById(R.id.pmTvFBConnect);
        pmTvLN = view.findViewById(R.id.pmTvLN);
        pmTvLNConnect = view.findViewById(R.id.pmTvLNConnect);
        pmTvG = view.findViewById(R.id.pmTvG);
        pmTvGConnect = view.findViewById(R.id.pmTvGConnect);
    }

    private boolean canEdit = false;

    public void setUpView() {
        if (isMe) {
            setViewForMyContact();
        } else {
            setToolbarForContact();
        }
        if (contact == null) return;
        etNames.setText(contact.getNames());
        etDob.setText(contact.getDob());
        etPob.setText(contact.getPob());
        etEmail.setText(contact.getMail());
        etPhone.setText(contact.getPob());
        setUpPublicProfiles();
        setUpAvatar();
    }

    public void setViewForMyContact() {
        tvAddContact.setVisibility(View.GONE);
        flValidate.setVisibility(View.GONE);
        ivValidated.setVisibility(View.GONE);
        setToolbarForMyProfile();
        etNames.setEnabled(canEdit);
        etDob.setEnabled(canEdit);
        etPob.setEnabled(canEdit);
        etPhone.setEnabled(canEdit);
        etEmail.setEnabled(canEdit);
        if (canEdit && llGeneralData.getVisibility() == View.VISIBLE) {
            PmSystemHelper.showKeyboard(getActivity(), etNames);
        }
        pmTvLNConnect.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        pmTvFBConnect.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        pmTvGConnect.setVisibility(canEdit ? View.VISIBLE : View.GONE);
    }

    public void setToolbarForContact() {
        setToolbar(R.drawable.pm_ic_arrow_back_24dp, -1, R.string.pm_other_profile_settings_title,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().onBackPressed();
                    }
                }, null);
    }

    public void setToolbarForMyProfile() {
        setToolbar(!canEdit ? R.drawable.pm_ic_logo_white : R.drawable.pm_ic_close_24dp,
                !canEdit ? R.drawable.pm_ic_edit_24dp : R.drawable.pm_ic_check_24dp,
                R.string.pm_profile_settings_title,
                !canEdit ? homeToolbarEvents.getOpenMenuListener() : new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CameraActivity.bitmaps=null;
                        contact = PeerMountainManager.getProfile();//reset
                        canEdit = false;
                        setUpView();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        canEdit = !canEdit;
                        if (!canEdit) {
                            saveProfile();
                        }else {
                            setUpView();
                        }
                    }
                });
    }

    public void setUpAvatar() {
        loadAvatar(getContext(), contact, pmIvAvatar);
    }

    public static void loadAvatar(Context context, Contact contact, ImageView iv) {
        String uri = null;
        if (!TextUtils.isEmpty(contact.getImageUri())) {
//            iv.setImageURI(Uri.parse(contact.getImageUri()));
            if (Build.VERSION.SDK_INT >= 24) {
                uri = contact.getImageUri();
            } else {
                File file = FileUtils.getFile(context, Uri.parse(contact.getImageUri()));
//                int size = context.getResources().getDimensionPixelSize(R.dimen.pm_avatar_size);
                iv.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));//decodeSampledBitmapFromFile(file.getPath(),size,size));
            }
        } else if (!TextUtils.isEmpty(contact.getPictureUrl())) {
            uri = contact.getPictureUrl();
        }
        if (uri != null) {
//            File file = new File(URI.create(uri));

            Picasso.with(context)
                    .load(uri)
                    .placeholder(R.drawable.pm_profil_white)
                    .error(R.color.pm_error_loading_avatar)
                    .into(iv);
        }
    }

    public static void loadAvatar(Context context, String uri, ImageView iv) {
        if (!TextUtils.isEmpty(uri)) {
//            iv.setImageURI(Uri.parse(contact.getImageUri()));
            if (Build.VERSION.SDK_INT < 24) {
                File file = FileUtils.getFile(context, Uri.parse(uri));
//                int size = context.getResources().getDimensionPixelSize(R.dimen.pm_avatar_size);
                iv.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));//decodeSampledBitmapFromFile(file.getPath(),size,size));
            }
        }
        if (uri != null) {
//            File file = new File(URI.create(uri));

            Picasso.with(context)
                    .load(uri)
                    .placeholder(R.drawable.pm_profil_white)
                    .error(R.color.pm_error_loading_avatar)
                    .into(iv);
        }
    }

    public void setUpPublicProfiles() {
        if (contact.getPublicProfiles() != null && contact.getPublicProfiles().size() > 0) {
            for (PublicUser publicUser : contact.getPublicProfiles()) {
                if (publicUser != null) {
                    switch (publicUser.getLoginType()) {
                        case PublicUser.LOGIN_TYPE_FB:
                            pmTvFB.setText(TextUtils.isEmpty(publicUser.getEmail()) ?
                                    publicUser.getFirstname() : publicUser.getEmail());
                            pmTvFBConnect.setText(R.string.pm_register_btn_disconnect);
                            break;
                        case PublicUser.LOGIN_TYPE_LN:
                            pmTvLN.setText(TextUtils.isEmpty(publicUser.getEmail()) ?
                                    publicUser.getFirstname() : publicUser.getEmail());
                            pmTvLNConnect.setText(R.string.pm_register_btn_disconnect);
                            break;
                        case PublicUser.LOGIN_TYPE_G:
                            pmTvG.setText(TextUtils.isEmpty(publicUser.getEmail()) ?
                                    publicUser.getFirstname() : publicUser.getEmail());
                            pmTvGConnect.setText(R.string.pm_register_btn_disconnect);
                            break;
                    }
                }
            }
        }
    }

    boolean toAdd = true;

    /**
     * type oclf to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        if (!isMe) {
            tvAddContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (contact != null) {
                        if (toAdd) {
                            PeerMountainManager.addContact(contact);
                        } else {
                            PeerMountainManager.removeContact(contact);
                        }
                        toAdd = !toAdd;
                        initBtn();
                    }
                }
            });
            tvValidateContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (contact != null && !contact.isValidated()) {
                        requestCapture();
                    }
                }
            });
        } else {
            pmTvFBConnect.setOnClickListener(new RippleOnClickListener() {
                @Override
                public void onClickListener(View view) {
                    onPublicProfileClick(pmTvFBConnect, pmTvFB, PublicUser.LOGIN_TYPE_FB);
                }
            });
            pmTvGConnect.setOnClickListener(new RippleOnClickListener() {
                @Override
                public void onClickListener(View view) {
                    onPublicProfileClick(pmTvGConnect, pmTvG, PublicUser.LOGIN_TYPE_G);
                }
            });
            pmTvLNConnect.setOnClickListener(new RippleOnClickListener() {
                @Override
                public void onClickListener(View view) {
                    onPublicProfileClick(pmTvLNConnect, pmTvLN, PublicUser.LOGIN_TYPE_LN);
                }
            });
            pmIvAvatar.setOnClickListener(new RippleOnClickListener() {
                @Override
                public void onClickListener(View view) {
                    if (canEdit) {
                        doLiveSelfie();
                    } else {
                        if (PeerMountainManager.getProfile().hasLiveSelfie()) {
                            PmSystemHelper.hideKeyboard(getActivity(), etNames);
                            startActivity(new Intent(getActivity(), ShowLiveSelfieActivity.class));
                        }
                    }
                }
            });
//            RippleUtils.setRippleEffectSquare( pmTvFBConnect, pmTvLNConnect);
        }
        tvTabGeneral.setOnClickListener(tabClick);
        tvTabSocial.setOnClickListener(tabClick);
    }

    public void onPublicProfileClick(TextView pmTvConnect, TextView pmTv, String type) {
        PublicUser user = contact.getPublicUser(type);
        if (user == null) {
            switch (type) {
                case PublicUser.LOGIN_TYPE_LN:
                    loginToLI();
                    break;
                case PublicUser.LOGIN_TYPE_FB:
                    loginToFb();
                    break;
                case PublicUser.LOGIN_TYPE_G:
                    loginToG();
                    break;
            }
        } else {
            switch (type) {
                case PublicUser.LOGIN_TYPE_LN:
                    PeerMountainManager.logoutPublicProfile();
                    break;
                case PublicUser.LOGIN_TYPE_FB:
                    LoginManager.getInstance().logOut();
                    break;
                case PublicUser.LOGIN_TYPE_G:
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    // ...
                                }
                            });
                    break;
            }
            contact.getPublicProfiles().remove(user);
            PeerMountainManager.saveProfile((Profile) contact);
            pmTvConnect.setText(R.string.pm_register_btn_connect);
            pmTv.setText("");
        }
    }

    public void loginToFb() {
        manager = LoginManager.getInstance();
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                PublicProfileUtils.getFbUser(loginResult, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject userJ, GraphResponse response) {
                        PublicUser fbUser = PeerMountainManager.parseFbPublicProfile(userJ);
                        pmTvFB.setText(TextUtils.isEmpty(fbUser.getEmail()) ? fbUser.getSurname() : fbUser.getEmail());
                        pmTvFBConnect.setText(R.string.pm_register_btn_disconnect);
                        contact.getPublicProfiles().add(fbUser);
//                        PeerMountainManager.saveProfile((Profile) contact);
                    }
                });
            }

            @Override
            public void onCancel() {
                LogUtils.e("FacebookException", "onCancel");
                PublicProfileUtils.onLoginError(getContext(), getString(R.string.pm_error_canceled));
            }

            @Override
            public void onError(FacebookException exception) {
                LogUtils.e("FacebookException", exception.getMessage());
                PublicProfileUtils.onLoginError(getContext(), exception.getMessage());
            }
        });
        manager.logInWithReadPermissions(getActivity(), PublicProfileUtils.fbPermissions());
    }

    private void loginToLI() {
        LISessionManager.getInstance(getApplicationContext()).init(getActivity(), PublicProfileUtils.buildScopeLn(), new
                AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        getLiUser();
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        LogUtils.e("LISessionManager", getString(R.string.pm_error_msg, error.toString()));
                        PublicProfileUtils.onLoginError(getContext(), error.toString());
                    }
                }, true);
    }

    public void getLiUser() {
        AccessToken accessToken = LISessionManager.getInstance
                (getApplicationContext()).getSession().getAccessToken();
        PeerMountainManager.saveLiAccessToken(new PmAccessToken(accessToken.getValue(), accessToken.getExpiresOn()));
        PublicProfileUtils.getLiUser(getApplicationContext(),
                new ApiListener() {
                    @Override
                    public void onApiSuccess(ApiResponse s) {
                        PublicUser liUser = PeerMountainManager.readPublicUser(s.getResponseDataAsString());
                        pmTvLN.setText(liUser.getEmail());
                        pmTvLNConnect.setText(R.string.pm_register_btn_disconnect);
                        contact.getPublicProfiles().add(liUser);
//                        PeerMountainManager.saveProfile((Profile) contact);
                    }

                    @Override
                    public void onApiError(LIApiError error) {
                        LogUtils.e("getLiUser", error.toString());
                        PublicProfileUtils.onLoginError(getContext(), error.toString());
                    }
                });
    }

    public void loginToG() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN_GOOGLE);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        LogUtils.d("getGUser", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            PublicUser gUser = new PublicUser(acct);
            pmTvG.setText(gUser.getEmail());
            pmTvGConnect.setText(R.string.pm_register_btn_disconnect);
            contact.getPublicProfiles().add(gUser);
        }
    }

    private void initBtn() {
        if (toAdd) {
            tvAddContact.setText(getString(R.string.pm_settings_add_to_contacts));
            flValidate.setVisibility(View.GONE);
            ivValidated.setVisibility(View.GONE);
        } else {
            tvAddContact.setText(getString(R.string.pm_settings_remove_from_contacts));
            initValidateBtn();
        }
    }

    private void initValidateBtn() {
        if (!isMe && contact != null) {
            ivValidated.setVisibility(View.VISIBLE);
            if (contact.isValidated()) {
                flValidate.setVisibility(View.GONE);
                ivValidated.setImageResource(R.drawable.pm_ic_shield_on);
                pmIvAvatar.setBorderColor(ContextCompat.getColor(getContext(), R.color.pm_contact_validated));
            } else {
                flValidate.setVisibility(View.VISIBLE);
                ivValidated.setImageResource(R.drawable.pm_shield_off);
                pmIvAvatar.setBorderColor(ContextCompat.getColor(getContext(), R.color.pm_contact_not_validated));
            }
        }
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
                    if (canEdit) PmSystemHelper.hideKeyboard(getActivity(), etNames);
                }
            } else {
                if (!showGeneral) {
                    showGeneral = true;
                    if (canEdit) PmSystemHelper.showKeyboard(getActivity(), etNames);
                    ;
                    initTabs();
                }
            }
        }
    };

    private File avatarFile = null;

    private void requestCapture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !checkPermissions()) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, REQUEST_CODE_WRITE_PERMISSION);
        } else {
            avatarFile = RegisterProfileFragment.dispatchTakePictureIntent(getActivity(), ProfileSettingsFragment.this, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void doLiveSelfie() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !checkPermissions()) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, REQUEST_CODE_LIVE_WRITE_PERMISSION);
        } else {
            PmSystemHelper.hideKeyboard(getActivity(), etNames);
            Intent intent = new Intent(getActivity(), CameraActivity.class);
            startActivityForResult(intent, REQUEST_LIVE_IMAGE_CAPTURE);
//            avatarFile = RegisterProfileFragment.dispatchTakePictureIntent(getActivity(), ProfileSettingsFragment.this, REQUEST_LIVE_IMAGE_CAPTURE);
        }
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public interface OnFragmentInteractionListener {
        void onMyProfileUpdated();

        GoogleApiClient getGoogleApiClientForSignIn(GoogleSignInOptions gso);
    }
}
