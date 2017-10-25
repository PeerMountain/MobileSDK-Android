package com.peermountain.sdk.ui.register;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.ariadnext.android.smartsdk.interfaces.bean.AXTImageResult;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.peermountain.core.model.guarded.Document;
import com.peermountain.core.model.guarded.PmAccessToken;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.ImageUtils;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PeerMountainCoreConstants;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.ToolbarFragment;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.DocumentUtils;
import com.peermountain.sdk.utils.PmFragmentUtils;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;
import com.peermountain.sdk.utils.ripple.RippleUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class RegisterProfileFragment extends ToolbarFragment {
    private static final String ARG_PARAM1 = "param1";
    public static final int REQUEST_IMAGE_CAPTURE = 111;
    public static final int REQUEST_CODE_WRITE_PERMISSION = 123;

    private OnFragmentInteractionListener mListener;
    private Document document;
    private CallbackManager callbackManager;
    private LoginManager manager;

    public RegisterProfileFragment() {
        // Required empty public constructor
    }

    public static RegisterProfileFragment newInstance(Document document) {
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
        if (getArguments() != null) {
            document = getArguments().getParcelable(ARG_PARAM1);
            resizeIdImages();
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
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                saveProfile();
            } else {
                if (avatarFile != null && avatarFile.exists()) {
                    imageUri = Uri.fromFile(avatarFile);
                    saveProfile();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isDone)
            if (mListener != null)
                mListener.onProfileRegistered();
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

    boolean foundEmptyField = false;
    Uri imageUri = null;

    private void setView() {
        if (document != null) {
            if (DocumentUtils.checkDocumentImageNotEmpty(document.getImageFace())) {
                imageUri = Uri.parse(document.getImageFace().getImageUri());
                setImage();
            }
            String names = null;
            if (DocumentUtils.checkDocumentTextNotEmpty(document.getFirstName())) {
                names = document.getFirstName();
            }
            if (DocumentUtils.checkDocumentTextNotEmpty(document.getLastName())) {
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
        if (DocumentUtils.checkDocumentTextNotEmpty(value)) {
            et.setText(value);
        } else if (!foundEmptyField) {
            foundEmptyField = true;
            et.requestFocus();
        }
    }

    ImageView pmIvAvatar, pmIvNext;
    EditText pmEtNames, pmEtDob, pmEtPob, pmEtMail, pmEtPhone;
    TextView pmTvFB, pmTvFBConnect, pmTvLN, pmTvLNConnect, tvNext;

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
    }

    private File avatarFile = null;

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
        RippleUtils.setRippleEffectSquare(tvNext, pmTvFBConnect, pmTvLNConnect);
    }

    private void requestCapture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_PERMISSION);
        } else {
            avatarFile = dispatchTakePictureIntent(getActivity(), RegisterProfileFragment.this, REQUEST_IMAGE_CAPTURE);
        }
    }

    String pictureUrl = null;
    boolean isDone = false;

    private void saveProfile() {
        Profile profile = new Profile();
        profile.getDocuments().add(document);
        profile.setNames(pmEtNames.getText().toString());
        profile.setDob(pmEtDob.getText().toString());
        profile.setPob(pmEtPob.getText().toString());
        profile.setMail(pmEtMail.getText().toString());
        profile.setPhone(pmEtPhone.getText().toString());
        if (imageUri != null) {
            profile.setImageUri(imageUri.toString());
        }
        profile.setPictureUrl(pictureUrl);
        if (liUser != null) {
            profile.getPublicProfiles().add(liUser);
        }
        if (fbUser != null) {
            profile.getPublicProfiles().add(fbUser);
        }
        PeerMountainManager.saveProfile(profile);
        //nothing is mandatory
        isDone = true;
    }

    private Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    private void loginToLI() {
        LISessionManager.getInstance(getApplicationContext()).init(getActivity(), buildScope(), new
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

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    private PublicUser liUser = null;

    private void getLiUser() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(getContext(), PeerMountainCoreConstants.GET_LI_PROFILE, new ApiListener() {
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
                getFbUser(loginResult);
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
        List<String> permissionNeeds = Arrays.asList("public_profile", "email");
        manager.logInWithReadPermissions(getActivity(), permissionNeeds);
    }

    PublicUser fbUser = null;

    private void getFbUser(LoginResult loginResult) {
        if (loginResult.getAccessToken() != null) {
            final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject userJ, GraphResponse response) {
                    fbUser = parseFbUser(userJ);
                    if (imageUri == null && fbUser.getPictureUrl() != null) {
                        pictureUrl = fbUser.getPictureUrl();
                        loadAvatarFromPublicProfileUrl();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,last_name,email,gender,picture");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    private PublicUser parseFbUser(JSONObject userJ) {
        PublicUser publicUser = null;
        if (userJ != null) {
            LogUtils.d("fb user", userJ.toString());
            if (!userJ.optString("id").isEmpty()) {
                String id = userJ.optString("id");
                String name = userJ.optString("first_name");
                String lastName = userJ.optString("last_name");
                String email = userJ.optString("email");
                String gender = userJ.optString("gender");
                String picture = userJ.optString("picture");
                if (picture != null) {
                    JSONObject picObj = userJ.optJSONObject("picture");
                    if (picObj != null && picObj.has("data")) {
                        picture = picObj.optJSONObject("data").optString("url");
                    } else {
                        picture = null;
                    }
                }
                publicUser = new PublicUser(id, email, name, lastName, picture);
                pmTvFB.setText(TextUtils.isEmpty(email) ? name : email);
                pmTvFBConnect.setText(R.string.pm_register_btn_disconnect);
//                Toast.makeText(getContext(), "FB logged", Toast.LENGTH_LONG).show();
            }
        }
        return publicUser;
    }

    private void resizeIdImages() {
        int size = getResources().getDimensionPixelSize(R.dimen.pm_id_size);
        if (document != null && document.getImageCropped() != null
                && document.getImageCropped().getWidth() > size) {
            resizeImage(document.getImageCropped().getImageUri(),
                    new SizeImageEvent() {
                        @Override
                        public void onSized(AXTImageResult image) {
                            document.setImageCroppedSmall(image);
                            LogUtils.d("onSized", "image : " + image.getImageUri());
                        }
                    }, System.currentTimeMillis() + "");
        }
        if (document != null && document.getImageCroppedBack() != null
                && document.getImageCroppedBack().getWidth() > size) {
            resizeImage(document.getImageCroppedBack().getImageUri(),
                    new SizeImageEvent() {
                        @Override
                        public void onSized(AXTImageResult image) {
                            document.setImageCroppedBackSmall(image);
                            LogUtils.d("onSized", "image back : " + image.getImageUri());
                        }
                    }, System.currentTimeMillis() + 1 + "");
        }
    }

    private void resizeImage(String uri, final SizeImageEvent callback, String name) {
        File originalImage = new File(Uri.parse(uri).getPath());
        File imagePath = new File(getContext().getFilesDir(), PeerMountainCoreConstants.LOCAL_IMAGE_DIR);
        imagePath.mkdirs();
        File newSmallerImage = new File(imagePath,
                name + ".jpg");
//                        java.io.File dir = new File(getFilesDir()
//                                + PeerMountainCoreConstants.LOCAL_IMAGE_DIR);
//                        dir.delete();
//                        FileUtils.copyFileAsync(payloadFile, localPayloadFile,true,null);
        // resize image and rotate
        int size = getResources().getDimensionPixelSize(R.dimen.pm_id_size);
        ImageUtils.rotateAndResizeImageAsync(originalImage, newSmallerImage, size,
                size / 2, false, false, new ImageUtils.ConvertImageTask.ImageCompressorListener() {
                    @Override
                    public void onImageCompressed(Bitmap bitmap, Uri uri) {
                        if (callback != null) {
                            AXTImageResult image = new AXTImageResult();
                            image.setImageUri(uri.toString());
                            callback.onSized(image);
                        }
                    }

                    @Override
                    public void onError() {
                        LogUtils.d("onSized", "error");
                    }
                }
        );
    }

    private interface SizeImageEvent {
        void onSized(AXTImageResult image);
    }

    public static File dispatchTakePictureIntent(Activity atv, Fragment fragment, int REQUEST_IMAGE_CAPTURE) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(atv.getPackageManager()) != null) {
            // Create the File where the photo should go
            File imagePath = new File(atv.getFilesDir(), PeerMountainCoreConstants.LOCAL_IMAGE_DIR);
            imagePath.mkdirs();
            File photoFile = new File(imagePath,
                    System.currentTimeMillis() + ".jpg");
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

    public interface OnFragmentInteractionListener {
        void onProfileRegistered();
    }
}
