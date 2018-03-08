package com.peermountain.sdk.ui.authorized;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.peermountain.core.model.guarded.Contact;
import com.peermountain.core.model.guarded.PmJob;
import com.peermountain.core.model.guarded.ShareObject;
import com.peermountain.core.network.BaseEvents;
import com.peermountain.core.network.MainCallback;
import com.peermountain.core.network.NetworkResponse;
import com.peermountain.sdk.ui.authorized.home.xforms.XFormFragment;
import com.peermountain.core.odk.model.FormController;
import com.peermountain.core.odk.tasks.FormLoaderTask;
import com.peermountain.core.odk.utils.Collect;
import com.peermountain.core.odk.utils.TimerLogger;
import com.peermountain.core.odk.views.widgets.base.QuestionWidget;
import com.peermountain.core.persistence.InactiveTimer;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.share.ShareContactActivity;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PmCoreUtils;
import com.peermountain.core.utils.PmDialogUtils;
import com.peermountain.sdk.PeerMountainSDK;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.authorized.contacts.ContactsFragment;
import com.peermountain.sdk.ui.authorized.contacts.MyQrCodeFragment;
import com.peermountain.sdk.ui.authorized.contacts.ScanQRFragment;
import com.peermountain.sdk.ui.authorized.contacts.ShareFragment;
import com.peermountain.sdk.ui.authorized.documents.DocumentsFragment;
import com.peermountain.sdk.ui.authorized.home.HomeFragment;
import com.peermountain.sdk.ui.authorized.home.HomeJobFragment;
import com.peermountain.sdk.ui.authorized.menu.MenuFragment;
import com.peermountain.sdk.ui.authorized.settings.ProfileSettingsFragment;
import com.peermountain.sdk.ui.base.SecureActivity;
import com.peermountain.sdk.utils.PmFragmentUtils;

import java.io.File;
import java.util.ArrayList;

// TODO: 3/8/2018 lifecycle activity
public class HomeActivity extends SecureActivity implements HomeJobFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener, MenuFragment.OnFragmentInteractionListener, ProfileSettingsFragment.OnFragmentInteractionListener,
        DocumentsFragment.OnFragmentInteractionListener, MyQrCodeFragment.OnFragmentInteractionListener,
        ScanQRFragment.OnFragmentInteractionListener, ContactsFragment.OnListFragmentInteractionListener,
        ShareFragment.OnFragmentInteractionListener, XFormFragment.OnFragmentInteractionListener, QuestionWidget.Events {
    private static final int REQUEST_LOGIN = 123;
    private static final int REQUEST_REGISTER = 321;
    public static final int REQUEST_GET_NEAR_BY_CONTACT = 111;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pm_activity_home, R.id.llMain);
        getViews();
        initDrawer();
//        setUpView();
        getJobs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean handled = false;
        switch (requestCode) {
            case REQUEST_LOGIN:
            case REQUEST_REGISTER:
                if (resultCode != RESULT_OK || !checkUserIsValid()) {
                    finish();
                    return;
                } else {
                    setUpView();
                    handled = true;
                }
                break;
            case REQUEST_GET_NEAR_BY_CONTACT:
                if (resultCode == RESULT_OK && data != null && checkUserIsValid()) {
                    ShareObject shareObject = data.getParcelableExtra(ShareContactActivity.SHARE_DATA);
                    if (shareObject != null && shareObject.getContact() != null) {
//                        new PmFragmentUtils.FragmentBuilder(this).pop();//remove shareFragment
                        showContactProfileSettingsFragment(shareObject.getContact());
                        handled = true;
                    }
                }
                break;
        }

        if (!handled && topFragment != null) {
            topFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        authorize();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (inactivityCallback != null) {
            InactiveTimer.startListeningForNewInactivity();
            PeerMountainManager.saveLastTimeActive();
        }
//        LogUtils.w("Secure atv", "onUserInteraction");
    }

    @Override
    protected void onStart() {
        super.onStart();
        startListenForUserInteraction();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopListenForUserInteraction();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == waitingPermissionRequestCode && waitingPermissionCallback != null) {
            waitingPermissionCallback.onPermission(grantResults);
        } else {
            if (topFragment != null) {
                topFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private int waitingPermissionRequestCode;
    private QuestionWidget.PermissionCallback waitingPermissionCallback;

    @Override
    public void requestPermission(String[] permissions, int requestCode, boolean isMandatory, QuestionWidget.PermissionCallback callback) {
        waitingPermissionCallback = callback;
        waitingPermissionRequestCode = requestCode;
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public View.OnClickListener getOpenMenuListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.openDrawer(Gravity.LEFT);
                }
            }
        };
    }

    @Override
    public View.OnClickListener getOpenBarcodeListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyShareFragment();
            }
        };
    }

    private boolean checkUserIsValid() {
        return PeerMountainManager.getProfile() != null;
    }

    public void authorize() {
        if (checkUserIsValid()
                && (PeerMountainManager.getPin() != null || PeerMountainManager.getFingerprint())) {
            //check if was not active for more than 5 min ago
            if (PeerMountainManager.shouldAuthorize()) {
                PeerMountainSDK.authorize(this, REQUEST_LOGIN);
            } else {
                setUpView();
            }
        } else
            PeerMountainSDK.registerFlow(this, REQUEST_REGISTER);
    }

    NavigationView navigationView;
    LinearLayout llContentView;
    View shadowView;

    private void initDrawer() {
        navigationView = findViewById(R.id.navigationView);
        llContentView = findViewById(R.id.llContentView);
        shadowView = findViewById(R.id.shadowView);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
        drawer.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawer, float slideOffset) {
//                        LogUtils.d("slideOffset",""+slideOffset);
                        pmIvLogout.setAlpha(slideOffset);
                        pmIvLogout.setEnabled(slideOffset > 0.8);
                        pmIvLogout.setVisibility(slideOffset > 0 ? View.VISIBLE : View.GONE);
                        shadowView.setAlpha(slideOffset);
                        scaleAndTranslateMainContent(drawer, slideOffset);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        pmIvLogout.setVisibility(View.GONE);
                        shadowView.setAlpha(0);
//                        pmIvLogout.setAlpha(0f);
//                        pmIvLogout.setEnabled(false);
                    }
                }
        );
    }

    public void scaleAndTranslateMainContent(View drawer, float slideOffset) {
//        // Scale the View based on current slide offset
//        final float diffScaledOffset = slideOffset * 0.3f;
//        final float offsetScale = 1 - diffScaledOffset;
//        llContentView.setScaleX(offsetScale);
//        llContentView.setScaleY(offsetScale);
//
//        // Translate the View, accounting for the scaled width
//        final float xOffset = drawer.getWidth() * slideOffset;
//        final float xOffsetDiff = llContentView.getWidth() * diffScaledOffset / 2;
//        final float xTranslation = xOffset - xOffsetDiff;
//        llContentView.setTranslationX(xTranslation);

        llContentView.setX(navigationView.getWidth() * slideOffset);
        FrameLayout.LayoutParams lp =
                (FrameLayout.LayoutParams) llContentView.getLayoutParams();
        lp.height = drawer.getHeight() -
                (int) (drawer.getHeight() * slideOffset * 0.3f);
        lp.topMargin = (drawer.getHeight() - lp.height) / 2;
        llContentView.setLayoutParams(lp);
    }

    private MenuFragment menuFragment;
    private boolean isViewSet = false;

    private void setUpView() {
        inactivityCallback = new InactiveTimer.InactiveTimerInteractions() {
            @Override
            public void onTimeOfInactivityEnds() {
                LogUtils.w("inactivityCallback", "onTimeOfInactivityEnds");
                stopListenForUserInteraction();
                PeerMountainSDK.authorize(HomeActivity.this, REQUEST_LOGIN);
            }
        };
        startListenForUserInteraction();
        if (!isViewSet) {
            if (isDownloadingJobs) {
                waitingToSetView = true;
            }
            isViewSet = true;
            showHomeFragment();
            setListeners();
        }
        refreshMenu();
    }

    private boolean isDownloadingJobs = false, waitingToSetView = false;

    private void getJobs() {
        PeerMountainManager.downloadXForm(new DownloadXFormCallback(null,
                        MainCallback.TYPE_NO_PROGRESS), // TODO: 3/8/2018 update link
                "https://www.dropbox.com/s/1yctej8ukivs7eo/AllFieldsFormTermsLong.xml?dl=1");
    }

    private void refreshMenu() {
        if (menuFragment == null) {
            menuFragment = (MenuFragment) getSupportFragmentManager().findFragmentById(R.id.menuFragment);
        }
        if (menuFragment != null) {
            menuFragment.updateView();
        }
    }


    ImageView pmIvLogout;

    /**
     * type fa to fast get new views
     */
    private void getViews() {
        pmIvLogout = findViewById(R.id.pmIvLogout);
        pmIvLogout.setEnabled(false);//enable this button only when is 80%+ visible
    }

    /**
     * type ocl to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        pmIvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                PeerMountainSDK.resetProfile();
//                authorize();//go to register
                finish();
            }
        });
    }

    @IdRes
    int containerId = R.id.flContainer;
    private PmFragmentUtils.FragmentBuilder fragmentBuilder = PmFragmentUtils.init(this, containerId);

    private void showHomeFragment() {
        fragmentBuilder.addToBackStack(false);
        fragmentBuilder.replace(HomeFragment.newInstance());
    }

    private void showMyProfileSettingsFragment() {
        fragmentBuilder.addToBackStack(false);
        fragmentBuilder.replace(ProfileSettingsFragment.newInstance(null));
    }

    private void showMyContactsFragment() {
        fragmentBuilder.addToBackStack(false);
        fragmentBuilder.replace(ContactsFragment.newInstance(1));
    }


    private void showContactProfileSettingsFragment(Contact contact) {
        fragmentBuilder.addToBackStack(true);
        fragmentBuilder.replace(ProfileSettingsFragment.newInstance(contact));
    }

    private void showDocumentsFragment() {
        fragmentBuilder.addToBackStack(false);
        fragmentBuilder.replace(DocumentsFragment.newInstance());
    }

    private void showHomeJobFragment() {
        fragmentBuilder.addToBackStack(true);
        // TODO: 3/8/2018 load XForm frag
        fragmentBuilder.replace(HomeJobFragment.newInstance());
    }

    private void showMyQrCodeFragment() {
        fragmentBuilder.addToBackStack(true);
        fragmentBuilder.replace(MyQrCodeFragment.newInstance());
    }

    private void showShareWithNearBy() {
        startActivityForResult(new Intent(this, ShareContactActivity.class), REQUEST_GET_NEAR_BY_CONTACT);
    }

    private void showMyShareFragment() {
        fragmentBuilder.addToBackStack(true);
        fragmentBuilder.replace(new ShareFragment());
    }

    public void closeMenu() {
        drawer.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void onMenuHomeClicked() {
        closeMenu();
        if (topFragment == null || !(topFragment instanceof HomeFragment)) {
            showHomeFragment();
        }
    }

    @Override
    public void onMenuDocumentsClicked() {
        closeMenu();
        if (topFragment == null || !(topFragment instanceof DocumentsFragment)) {
            showDocumentsFragment();
        }
    }

    @Override
    public void onMenuSettingsClicked() {
        closeMenu();
        if (topFragment == null || !(topFragment instanceof ProfileSettingsFragment)) {
            showMyProfileSettingsFragment();
        }
    }

    @Override
    public void onMenuContactsClicked() {
        closeMenu();
        showMyContactsFragment();
//        HashSet<Contact> contacts = PeerMountainManager.getContacts();
//        StringBuilder sb = new StringBuilder("Contacts (" + contacts.size() + ") :\n");
//        Contact contact1 = null;
//        for (Contact contact : contacts) {
//            if (contact1 == null) contact1 = contact;
//            sb.append(contact.getNames());
//            sb.append("\n");
//        }
//        final Contact finalContact = contact1;
//        DialogUtils.showSimpleDialog(this, sb.toString(), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if(finalContact!=null) showContactProfileSettingsFragment(finalContact);
//            }
//        });
    }

    @Override
    public void onContactScannedFromQR(Contact contact) {
        fragmentBuilder.pop();//remove scan qr fragment from stack
        fragmentBuilder.pop();//remove qr code fragment from stack back to share
//        builder.pop();//remove share fragment from stack
        showContactProfileSettingsFragment(contact);
    }

    @Override
    public void showQrReader() {
        fragmentBuilder.addToBackStack(true);
        fragmentBuilder.replace(ScanQRFragment.newInstance());
    }

    @Override
    public void onContactSelected(Contact contact) {
        showContactProfileSettingsFragment(contact);
    }

    @Override
    public void onHomeJobClicked(PmJob job) {
//        showHomeJobFragment();
        if(job.getFile()!=null){
            loadXForm(job.getFile());
        }else{
            PmDialogUtils.showError(this,"No XForm file!");
        }
    }

    @Override
    public void onJobFinished() {
        fragmentBuilder.pop();
    }

    public void onShareRefused() {
        fragmentBuilder.pop();//return
    }

    @Override
    public void onRequestShareByQRCode() {
        showMyQrCodeFragment();
    }

    @Override
    public void onRequestShareByNearBy() {
        showShareWithNearBy();
    }

    @Override
    public void lockMenu(boolean lock) {
//        drawer.setDrawerLockMode(lock?DrawerLayout.LOCK_MODE_LOCKED_CLOSED:DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
    }

    @Override
    public void onMyProfileUpdated() {
        refreshMenu();
    }

    private void loadXForm(File file) {
        // TODO: 3/8/2018 show progress
        PeerMountainManager.loadXForm(file, new FormLoaderTask.FormLoaderListener() {
            @Override
            public void loadingComplete(FormLoaderTask task) {
                LogUtils.d("ttt", "ttt " + task.getRequestCode());
                Fragment fragment = new XFormFragment();
                FormController formController = task.getFormController();
                Collect.getInstance().setFormController(formController);
                if (formController.getInstancePath() == null) {
                    String file = task.formPath.substring(task.formPath.lastIndexOf('/') + 1,
                            task.formPath.lastIndexOf('.'));
                    formController.setInstancePath(PmCoreUtils.getAnswersForXForm(file));
                    formController.getTimerLogger().logTimerEvent(TimerLogger.EventTypes.FORM_START, 0, null, false, true);
                }
                fragmentBuilder.addToBackStack(true);
                fragmentBuilder.replace(fragment);
            }

            @Override
            public void loadingError(String errorMsg) {
                LogUtils.d("eee", "eee " + errorMsg);
            }
        });
    }

    GoogleApiClient mGoogleApiClient;

    @Override
    public GoogleApiClient getGoogleApiClientForSignIn(GoogleSignInOptions gso) {
        if (mGoogleApiClient == null) {
            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */,
                            new GoogleApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                    Toast.makeText(HomeActivity.this, "Unable to connect to Google Api", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
        return mGoogleApiClient;
    }

    private class DownloadXFormCallback extends MainCallback {
        // TODO: 2/12/18 add media file links
        DownloadXFormCallback(BaseEvents presenterCallback, int progressType) {
            super(presenterCallback, progressType);
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            isDownloadingJobs = true;
        }

        @Override
        public void inTheEndOfDoInBackground(NetworkResponse networkResponse) {
            super.inTheEndOfDoInBackground(networkResponse);
            // TODO: 2/12/18 download media files
        }

        @Override
        public void onPostExecute(NetworkResponse networkResponse) {
            super.onPostExecute(networkResponse);
            if (networkResponse.file != null) {
                ArrayList<PmJob> jobs = new ArrayList<PmJob>();
                PmJob job = new PmJob("Miles & More Card", "Scan your ID or Passport");
                job.setxFormPath(networkResponse.file.getAbsolutePath());
                job.setFile(networkResponse.file);
                jobs.add(job);
                PeerMountainManager.saveJobs(jobs);
//                loadXForm(networkResponse.file);
            }
            end();
        }

        @Override
        public void onError(String msg, NetworkResponse networkResponse) {
            super.onError(msg, networkResponse);
            end();
//            progress.setVisibility(View.GONE);
        }

        private void end() {
            isDownloadingJobs = false;
            if (waitingToSetView) {
                waitingToSetView = false;
                setUpView();
            }
        }
    }
}
