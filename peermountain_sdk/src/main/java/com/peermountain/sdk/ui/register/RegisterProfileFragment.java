package com.peermountain.sdk.ui.register;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PeerMountainCoreConstants;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.base.ToolbarFragment;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.DocumentUtils;
import com.peermountain.sdk.utils.ripple.RippleOnClickListener;
import com.peermountain.sdk.utils.ripple.RippleUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class RegisterProfileFragment extends ToolbarFragment {
    private static final String ARG_PARAM1 = "param1";

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
        setView();
        setListeners();
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
    TextView pmTvFB, pmTvFBConnect, pmTvLN, pmTvLNConnect;

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
    }

    /**
     * type oclf to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        pmIvNext.setOnClickListener(new RippleOnClickListener() {
            @Override
            public void onClickListener(View view) {
                saveProfile();
                //nothing is mandatory
                if (mListener != null)
                    mListener.onProfileRegistered();
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
        RippleUtils.setRippleEffectSquare(pmIvNext, pmTvFBConnect, pmTvLNConnect);
    }

    String pictureUrl =null;
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
        if(liUser!=null) {
            profile.getPublicProfiles().add(liUser);
        }
        if(fbUser!=null) {
            profile.getPublicProfiles().add(fbUser);
        }
        PeerMountainManager.saveProfile(profile);
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
                if(imageUri==null && liUser.getPictureUrl()!=null){
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
                    if(imageUri==null && fbUser.getPictureUrl()!=null){
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
                pmTvFB.setText(TextUtils.isEmpty(email)?name:email);
                pmTvFBConnect.setText(R.string.pm_register_btn_disconnect);
//                Toast.makeText(getContext(), "FB logged", Toast.LENGTH_LONG).show();
            }
        }
        return publicUser;
    }

    public interface OnFragmentInteractionListener {
        void onProfileRegistered();
    }
}
