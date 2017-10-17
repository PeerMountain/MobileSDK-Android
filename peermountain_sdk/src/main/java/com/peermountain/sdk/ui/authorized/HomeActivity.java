package com.peermountain.sdk.ui.authorized;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.peermountain.core.model.guarded.Contact;
import com.peermountain.core.model.guarded.ShareObject;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.share.ShareContactActivity;
import com.peermountain.sdk.PeerMountainSDK;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.authorized.contacts.MyQrCodeFragment;
import com.peermountain.sdk.ui.authorized.contacts.ScanQRFragment;
import com.peermountain.sdk.ui.authorized.documents.DocumentsFragment;
import com.peermountain.sdk.ui.authorized.home.HomeFragment;
import com.peermountain.sdk.ui.authorized.menu.MenuFragment;
import com.peermountain.sdk.ui.authorized.settings.ProfileSettingsFragment;
import com.peermountain.sdk.ui.base.ToolbarActivity;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.PmFragmentUtils;

import java.util.HashSet;

public class HomeActivity extends ToolbarActivity implements HomeFragment.OnFragmentInteractionListener, MenuFragment.OnFragmentInteractionListener, ProfileSettingsFragment.OnFragmentInteractionListener,
        DocumentsFragment.OnFragmentInteractionListener, MyQrCodeFragment.OnFragmentInteractionListener,
        ScanQRFragment.OnFragmentInteractionListener {
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
        authorize();
//        setUpView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOGIN:
            case REQUEST_REGISTER:
                if (resultCode != RESULT_OK || !checkUserIsValid()) {
                    finish();
                } else {
                    setUpView();
                }
                break;
            case REQUEST_GET_NEAR_BY_CONTACT:
                if (resultCode == RESULT_OK && data != null && checkUserIsValid()) {
                    ShareObject shareObject = data.getParcelableExtra(ShareContactActivity.SHARE_DATA);
                    if (shareObject != null && shareObject.getContact() != null) {
                        showContactProfileSettingsFragment(shareObject.getContact());
                    }
                }
                break;
        }
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
                showMyQrCodeFragment();
            }
        };
    }

    private boolean checkUserIsValid() {
        return PeerMountainManager.getProfile() != null;
    }

    public void authorize() {
        if (checkUserIsValid()
                && (PeerMountainManager.getPin() != null || PeerMountainManager.getFingerprint())) {
            // TODO: 10/13/2017 check if was active for less than 5 min ago
            PeerMountainSDK.authorize(this, REQUEST_LOGIN);
        } else
            PeerMountainSDK.registerFlow(this, REQUEST_REGISTER);
    }

    NavigationView navigationView;
    LinearLayout llContentView;

    private void initDrawer() {
        navigationView = findViewById(R.id.navigationView);
        llContentView = findViewById(R.id.llContentView);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawer, float slideOffset) {
//                        LogUtils.d("slideOffset",""+slideOffset);
                        pmIvLogout.setAlpha(slideOffset);
                        pmIvLogout.setEnabled(slideOffset > 0.8);
                        pmIvLogout.setVisibility(slideOffset > 0 ? View.VISIBLE : View.GONE);
                        llContentView.setX(navigationView.getWidth() * slideOffset);
                        FrameLayout.LayoutParams lp =
                                (FrameLayout.LayoutParams) llContentView.getLayoutParams();
                        lp.height = drawer.getHeight() -
                                (int) (drawer.getHeight() * slideOffset * 0.3f);
                        lp.topMargin = (drawer.getHeight() - lp.height) / 2;
                        llContentView.setLayoutParams(lp);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        pmIvLogout.setVisibility(View.GONE);
//                        pmIvLogout.setAlpha(0f);
//                        pmIvLogout.setEnabled(false);
                    }
                }
        );
    }

    private MenuFragment menuFragment;
    private boolean isViewSet = false;

    private void setUpView() {
        if (!isViewSet) {
            isViewSet = true;
            showHomeFragment();
            setListeners();
        }
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

    private void showHomeFragment() {
        PmFragmentUtils.FragmentBuilder fb = PmFragmentUtils.init(this, containerId);
        fb.addToBackStack(false);
        fb.replace(HomeFragment.newInstance());
    }

    private void showMyProfileSettingsFragment() {
        PmFragmentUtils.FragmentBuilder fb = PmFragmentUtils.init(this, containerId);
        fb.addToBackStack(false);
        fb.replace(ProfileSettingsFragment.newInstance(null));
    }

    private void showContactProfileSettingsFragment(Contact contact) {
        PmFragmentUtils.FragmentBuilder fb = PmFragmentUtils.init(this, containerId);
        fb.addToBackStack(true);
        fb.replace(ProfileSettingsFragment.newInstance(contact));
    }

    private void showDocumentsFragment() {
        PmFragmentUtils.FragmentBuilder fb = PmFragmentUtils.init(this, containerId);
        fb.addToBackStack(false);
        fb.replace(DocumentsFragment.newInstance());
    }

    private void showMyQrCodeFragment() {
//        PmFragmentUtils.FragmentBuilder fb = PmFragmentUtils.init(this, containerId);
//        fb.addToBackStack(true);
//        fb.replace(MyQrCodeFragment.newInstance());
        startActivityForResult(new Intent(this, ShareContactActivity.class), REQUEST_GET_NEAR_BY_CONTACT);
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
        HashSet<Contact> contacts = PeerMountainManager.getContacts();
        StringBuilder sb = new StringBuilder("Contacts (" + contacts.size() + ") :\n");
        Contact contact1 = null;
        for (Contact contact : contacts) {
            if (contact1 == null) contact1 = contact;
            sb.append(contact.getNames());
            sb.append("\n");
        }
        final Contact finalContact = contact1;
        DialogUtils.showSimpleDialog(this, sb.toString(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(finalContact!=null) showContactProfileSettingsFragment(finalContact);
            }
        });
    }

    @Override
    public void onContactScannedFromQR(Contact contact) {
        new PmFragmentUtils.FragmentBuilder(this).pop();//remove qr fragment from stack
        showContactProfileSettingsFragment(contact);
    }

    @Override
    public void showQrReader() {
        PmFragmentUtils.FragmentBuilder fb = PmFragmentUtils.init(this, containerId);
        fb.addToBackStack(true);
        fb.replace(ScanQRFragment.newInstance());
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
