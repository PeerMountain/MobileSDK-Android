package com.peermountain.sdk.ui.register;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.peermountain.common.model.DocumentID;
import com.peermountain.common.model.ImageResult;
import com.peermountain.common.utils.PmFragmentUtils;
import com.peermountain.common.utils.PmSystemHelper;
import com.peermountain.common.utils.ripple.RippleOnClickListener;
import com.peermountain.common.utils.ripple.RippleUtils;
import com.peermountain.core.camera.CameraActivity;
import com.peermountain.core.model.guarded.PmAccessToken;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.model.guarded.VerifySelfie;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PmCoreUtils;
import com.peermountain.core.utils.PmDocumentsHelper;
import com.peermountain.core.utils.PmLiveSelfieHelper;
import com.peermountain.core.utils.constants.PmCoreConstants;
import com.peermountain.pm_livecycle.ToolbarFragment;
import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.PublicProfileUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class RegisterProfileFragment extends ToolbarFragment {
    private static final String ARG_PARAM1 = "param1";
    public static final int REQUEST_IMAGE_CAPTURE = 111;
    public static final int REQUEST_CODE_WRITE_PERMISSION = 123;
    public static final int REQUEST_CODE_SIGN_IN_GOOGLE = 1727;

    private OnFragmentInteractionListener mListener;
    private DocumentID document;
    private CallbackManager callbackManager;
    private LoginManager manager;

    public RegisterProfileFragment() {
        // Required empty public constructor
    }

    public static RegisterProfileFragment newInstance(DocumentID document) {
        RegisterProfileFragment fragment = new RegisterProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, document);
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
        callbackManager = CallbackManager.Factory.create();
        prepareGoogleSignIn();
        if (getArguments() != null) {
            document = getArguments().getParcelable(ARG_PARAM1);
            if (document == null) return;
            imageSidesDone = 4;
//            if (document.getImageCropped() != null) imageSidesDone+=2;//first make a smaller image, then copy and delete the original
//            if (document.getImageCroppedBack() != null) imageSidesDone+=2;
            PmDocumentsHelper.resizeIdImages(getActivity(), document,
                    new SizeImageEventCallback(true, false),
                    new SizeImageEventCallback(false, false),
                    new SizeImageEventCallback(true, true),
                    new SizeImageEventCallback(false, true));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pm_fragment_register_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViews(view);
        initToolbar();
        setView();
        setListeners();
    }

    @Override
    public boolean onBackPressed() {
        new PmFragmentUtils.FragmentBuilder(getActivity()).remove(this);
        return super.onBackPressed();
    }

    private void initToolbar() {
        setToolbar(R.drawable.pm_ic_logo, R.string.pm_register_title, null);
        setTheme(ToolbarFragment.THEME_LIGHT);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(getActivity(), requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {// TODO: 8/9/2018 scan_id_sdk
            if (CameraActivity.bitmaps != null
                    && CameraActivity.bitmaps.size() > 0) {
                pmIvAvatar.setImageDrawable(new BitmapDrawable(getResources(), CameraActivity.bitmaps.get(0)));
                saveProfile();
            } else {
                DialogUtils.showInfoSnackbar(getActivity(), R.string.pm_err_no_liveselfie_created);
            }
//            if (data != null && data.getData() != null) {
//                imageUri = data.getData();
//                saveProfile();
//            } else {
//                if (avatarFile != null && avatarFile.exists()) {
//                    imageUri = Uri.fromFile(avatarFile);
//                    saveProfile();
//                }
//            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissErrors();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_WRITE_PERMISSION:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    requestCapture();
                } else {
                    // TODO: 10/23/17 show exp dialog
                }
                break;
        }
    }

    private void finish() {
        if (isDone && imageSidesDone == 0)
            if (mListener != null)
                mListener.onProfileRegistered();
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

    boolean foundEmptyField = false;
    Uri imageUri = null;

    private void setView() {
        if (document != null) {
            if (PmDocumentsHelper.checkDocumentImageNotEmpty(document.getImageFace())) {
                imageUri = Uri.parse(document.getImageFace().getImageUri());
                setImage();
            }
            String names = null;
            if (PmDocumentsHelper.checkDocumentTextNotEmpty(document.getFirstName())) {
                names = document.getFirstName();
            }
            if (PmDocumentsHelper.checkDocumentTextNotEmpty(document.getLastName())) {
                if (names != null) {
                    names = names + " " + document.getLastName();
                } else {
                    names = document.getLastName();
                }
            }
            setText(pmEtNames, names);
            setText(pmEtDob, document.getBirthday());
            setText(pmEtPob, document.getCountry());
            if (!foundEmptyField) {
                pmEtMail.requestFocus();
            }
        }
    }

    private void setImage() {
        pmIvAvatar.setImageURI(imageUri);
    }

    private void setText(EditText et, String value) {
        if (PmDocumentsHelper.checkDocumentTextNotEmpty(value)) {
            et.setText(value);
        } else if (!foundEmptyField) {
            foundEmptyField = true;
            et.requestFocus();
        }
    }

    ImageView pmIvAvatar, pmIvNext;
    EditText pmEtNames, pmEtDob, pmEtPob, pmEtMail, pmEtPhone;
    TextView pmTvFB, pmTvFBConnect, pmTvLN, pmTvLNConnect, tvNext, pmTvG, pmTvGConnect;

    /**
     * type ff to fast get new views
     */
    private void getViews(View view) {
        pmIvAvatar = view.findViewById(R.id.pmIvAvatar);
        pmEtNames = view.findViewById(R.id.pmEtNames);
        pmEtDob = view.findViewById(R.id.pmEtDob);
        pmEtPob = view.findViewById(R.id.pmEtPob);
        pmEtMail = view.findViewById(R.id.pmEtMail);
        pmEtPhone = view.findViewById(R.id.pmEtPhone);
        pmTvFB = view.findViewById(R.id.pmTvFB);
        pmTvFBConnect = view.findViewById(R.id.pmTvFBConnect);
        pmTvLN = view.findViewById(R.id.pmTvLN);
        pmTvLNConnect = view.findViewById(R.id.pmTvLNConnect);
        pmIvNext = view.findViewById(R.id.pmIvNext);
        tvNext = view.findViewById(R.id.tvNext);
        pmTvG = view.findViewById(R.id.pmTvG);
        pmTvGConnect = view.findViewById(R.id.pmTvGConnect);
    }

    private File avatarFile = null;
    GoogleApiClient mGoogleApiClient;

    /**
     * type oclf to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        tvNext.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                requestCapture();
            }
        });
        pmIvAvatar.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                requestCapture();
            }
        });
        pmTvFBConnect.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                if (fbUser == null) {
                    loginToFb();
                } else {
                    LoginManager.getInstance().logOut();
                    fbUser = null;
                    pmTvFBConnect.setText(R.string.pm_register_btn_connect);
                    pmTvFB.setText("");
                }
            }
        });
        pmTvLNConnect.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                if (liUser == null) {
                    loginToLI();
                } else {
                    PeerMountainManager.logoutPublicProfile();
                    liUser = null;
                    pmTvLNConnect.setText(R.string.pm_register_btn_connect);
                    pmTvLN.setText("");
                }
            }
        });
        pmTvGConnect.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                if (gUser == null) {
                    loginToG();
                } else {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    // ...
                                }
                            });
                    gUser = null;
                    pmTvGConnect.setText(R.string.pm_register_btn_connect);
                    pmTvG.setText("");
                }
            }
        });
        RippleUtils.setRippleEffectSquare(tvNext, pmTvFBConnect, pmTvLNConnect, pmTvGConnect);
    }

    private void requestCapture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_PERMISSION);
        } else {// TODO: 8/9/2018 scan_id_sdk
            PmSystemHelper.hideKeyboard(getActivity(), pmEtNames);
//            Intent intent = new Intent(getActivity(), CameraActivity.class);
//            getActivity().startActivityForResult(intent, REQUEST_ID_CAPTURE);
            CameraActivity.show(getActivity(), false, REQUEST_IMAGE_CAPTURE);
//            avatarFile = dispatchTakePictureIntent(getActivity(), RegisterProfileFragment.this, REQUEST_ID_CAPTURE);
        }
    }

    String pictureUrl = null;
    boolean isDone = false;
    private ArrayList<String> liveSelfie = null;

    private void saveProfile() {// TODO: 8/9/2018 scan_id_sdk
        if (CameraActivity.bitmaps != null && CameraActivity.bitmaps.size() > 0) {
            new PmLiveSelfieHelper(new PmLiveSelfieHelper.Events() {

                @Override
                public void onLiveSelfieReady(ArrayList<String> liveSelfie) {
                    if (mListener == null) return;
                    // verify images with ID image
                    if (liveSelfie == null || liveSelfie.size() <= 10 || document == null
                            || document.getImageCropped() == null) return;
                    RegisterProfileFragment.this.liveSelfie = liveSelfie;

                    ArrayList<File> files = new ArrayList<>();

                    for (int i = 0; i < 5; i++) {
                        files.add(new File(Uri.parse(liveSelfie.get(i*2)).getPath()));
                    }

                    files.add(document.getImageCropped().takeImageAsFile());

//                    if(document.getImageCroppedBack()!=null){
//                        files.add(document.getImageCroppedBack().takeImageAsFile());
//                    }
                    mListener.verifyLiveSelfie(files);

//                    onSelfieVerified();

                }
            }).saveLiveSelfie();
        }
//        Profile profile = new Profile();
//        profile.getDocuments().add(document);
//        profile.setNames(pmEtNames.getText().toString());
//        profile.setDob(pmEtDob.getText().toString());
//        profile.setPob(pmEtPob.getText().toString());
//        profile.setMail(pmEtMail.getText().toString());
//        profile.setPhone(pmEtPhone.getText().toString());
//        if (imageUri != null) {
//            profile.setImageUri(imageUri.toString());
//        }
//        profile.setPictureUrl(pictureUrl);
//        if (liUser != null) {
//            profile.getPublicProfiles().add(liUser);
//        }
//        if (fbUser != null) {
//            profile.getPublicProfiles().add(fbUser);
//        }
//        if (gUser != null) {
//            profile.getPublicProfiles().add(gUser);
//        }
//        PeerMountainManager.saveProfile(profile);
//        //nothing is mandatory
//        isDone = true;
    }


    public void onSelfieRejected(VerifySelfie verifySelfie) {
        // TODO: 5/14/2018 show message. put in strings
        liveSelfie = null;
        String msg = "";
        if(verifySelfie == null){
            msg = "Your selfie does not match with your document";
        }else{
            if(!verifySelfie.isHumanFace()){
                msg = "No human face detected";
            }
            if(!verifySelfie.isLiveliness()){
                msg += " No liveliness detected";
            }
            if(!verifySelfie.isFaceMatch()){
                msg += " Face does not match";
            }
        }
        msg += ". Please try again";
        dismissErrors();
        snackbar = DialogUtils.showError(getActivity(), msg);
    }

    public void onSelfieVerified() {
        if (liveSelfie == null || liveSelfie.size() == 0) return;

        Profile profile = new Profile();
        profile.getDocuments().add(document);
        profile.setNames(pmEtNames.getText().toString());
        profile.setDob(pmEtDob.getText().toString());
        profile.setPob(pmEtPob.getText().toString());
        profile.setMail(pmEtMail.getText().toString());
        profile.setPhone(pmEtPhone.getText().toString());
//        if (imageUri != null) {
//            profile.setImageUri(imageUri.toString());
//        }
        profile.setImageUri(liveSelfie.get(0));
        profile.setLiveSelfie(liveSelfie);
        profile.setPictureUrl(pictureUrl);
        if (liUser != null) {
            profile.getPublicProfiles().add(liUser);
        }
        if (fbUser != null) {
            profile.getPublicProfiles().add(fbUser);
        }
        if (gUser != null) {
            profile.getPublicProfiles().add(gUser);
        }
        PeerMountainManager.saveProfile(profile);
        //nothing is mandatory
        isDone = true;
        finish();
    }

    private Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    public void loginToG() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        getActivity().startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN_GOOGLE);
    }

    PublicUser gUser;

    private void handleSignInResult(GoogleSignInResult result) {
        LogUtils.d("getGUser", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            gUser = new PublicUser(acct);
            pmTvG.setText(gUser.getEmail());
            pmTvGConnect.setText(R.string.pm_register_btn_disconnect);
        }
    }

    private void loginToLI() {
        LISessionManager.getInstance(getApplicationContext()).init(getActivity(), PublicProfileUtils.buildScopeLn(), new
                AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        AccessToken accessToken = LISessionManager.getInstance
                                (getApplicationContext()).getSession().getAccessToken();
                        PeerMountainManager.saveLiAccessToken(new PmAccessToken(accessToken.getValue(), accessToken.getExpiresOn()));
                        getLiUser();
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        LogUtils.e("LISessionManager", getString(R.string.pm_error_msg, error.toString()));
                        onLoginError(error.toString());
                    }
                }, true);
    }


    private PublicUser liUser = null;

    private void getLiUser() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(getContext(), PmCoreConstants.GET_LI_PROFILE, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse s) {
                LogUtils.d("getLiUser", s.toString());
                liUser = PeerMountainManager.readPublicUser(s.getResponseDataAsString());
                if (imageUri == null && liUser.getPictureUrl() != null) {
                    pictureUrl = liUser.getPictureUrl();
                    loadAvatarFromPublicProfileUrl();
                }
                pmTvLN.setText(liUser.getEmail());
                pmTvLNConnect.setText(R.string.pm_register_btn_disconnect);
            }

            @Override
            public void onApiError(LIApiError error) {
                LogUtils.e("getLiUser", error.toString());
                onLoginError(error.toString());
            }
        });
    }

    public void loadAvatarFromPublicProfileUrl() {
        Picasso.with(getContext())
                .load(pictureUrl)
                .error(R.color.pm_error_loading_avatar)
                .into(pmIvAvatar);
    }

    private void onLoginError(String errorMsg) {
        DialogUtils.showErrorToast(getContext(), getString(R.string.pm_error_msg, errorMsg));
    }

    public void loginToFb() {
        manager = LoginManager.getInstance();
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                PublicProfileUtils.getFbUser(loginResult, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject userJ, GraphResponse response) {
                        fbUser = PeerMountainManager.parseFbPublicProfile(userJ);
                        pmTvFB.setText(TextUtils.isEmpty(fbUser.getEmail()) ? fbUser.getSurname() : fbUser.getEmail());
                        pmTvFBConnect.setText(R.string.pm_register_btn_disconnect);
                        if (imageUri == null && fbUser.getPictureUrl() != null) {
                            pictureUrl = fbUser.getPictureUrl();
                            loadAvatarFromPublicProfileUrl();
                        }
                    }
                });
            }

            @Override
            public void onCancel() {
                LogUtils.e("FacebookException", "onCancel");
                onLoginError(getString(R.string.pm_error_canceled));
            }

            @Override
            public void onError(FacebookException exception) {
                LogUtils.e("FacebookException", exception.getMessage());
                onLoginError(exception.getMessage());
            }
        });
        manager.logInWithReadPermissions(getActivity(), PublicProfileUtils.fbPermissions());
    }

    PublicUser fbUser = null;


    public static File dispatchTakePictureIntent(Activity atv, Fragment fragment, int REQUEST_IMAGE_CAPTURE) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(atv.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = PmCoreUtils.createLocalFile(atv, PmCoreConstants.FILE_TYPE_IMAGES);
//            File photoFile = new File(
//                    Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_PICTURES),//+"/PM",
//                    System.currentTimeMillis() + ".jpg");

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri uri = FileProvider.getUriForFile(atv,
                        atv.getPackageName() + ".provider",
                        photoFile);

//                Uri uri =      Uri.fromFile(photoFile);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                atv.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                return photoFile;
            }
        }
        return null;
    }

    int imageSidesDone = 0;

    private void onIdDocumentImageHandled() {
        imageSidesDone--;
        if (imageSidesDone == 0 && isDone && mListener != null) {
            mListener.onProfileRegistered();
        }
    }

    private class SizeImageEventCallback implements PmDocumentsHelper.SizeImageEvent {
        private Boolean isFront, isMoving;

        public SizeImageEventCallback(Boolean isFront, Boolean isMoving) {
            this.isFront = isFront;
            this.isMoving = isMoving;
        }

        @Override
        public void onSized(ImageResult image) {
            if (!isMoving) {
                if (isFront) {
                    document.setImageCroppedSmall(image);
                } else {
                    document.setImageCroppedBackSmall(image);
                }
            } else {
                if (isFront) {
                    document.setImageCropped(image);
                } else {
                    document.setImageCroppedBack(image);
                }
            }
            onIdDocumentImageHandled();
        }

        @Override
        public void onError() {
            onIdDocumentImageHandled();
        }
    }

    public interface OnFragmentInteractionListener {
        void onProfileRegistered();

        GoogleApiClient getGoogleApiClientForSignIn(GoogleSignInOptions gso);

        void verifyLiveSelfie(ArrayList<File> filesToSend);
    }
}
