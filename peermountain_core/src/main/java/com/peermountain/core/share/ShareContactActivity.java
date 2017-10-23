package com.peermountain.core.share;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.Payload;
import com.peermountain.core.BuildConfig;
import com.peermountain.core.R;
import com.peermountain.core.model.guarded.ShareObject;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.ImageUtils;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PeerMountainCoreConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

public class ShareContactActivity extends ConnectionsActivity {
    private static final String ENDPOINT_ID_EXTRA = "endpointId";
    private static final int READ_REQUEST_CODE = 42;
    /**
     * This service id lets us find other nearby devices that are interested in the same thing. Our
     * sample does exactly one thing, so we hardcode the ID.
     */
    private static final String SERVICE_ID = BuildConfig.APPLICATION_ID +
            "shareContactService";
    public static final String SHARE_DATA = "shareObject";

    /**
     * profile name used as this device's endpoint name.
     */
    private String mName;
    /**
     * A random UID used as this device's unique service id.
     */
    private String mCode;
    /**
     * States that the UI goes through.
     */
    private static final int UNKNOWN = 1;
    private static final int DISCOVERING = 2;
    private static final int ADVERTISING = 3;
    private static final int CONNECTED = 4;

    private int mState = UNKNOWN;
    private TextView mTvMsg;
    private TextView mTvCode;
    private TextView mTvMsgConnect;
    private EditText mEtCode;
    private TextView mBtnConnect;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_contact);
        setResult(RESULT_CANCELED);
        initView();

        mName = PeerMountainManager.getProfile().getNames();
        mCode = generateRandomName();
        mTvCode.setText(mCode);

        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEtCode.getText().length() >= 5)
                    setState(DISCOVERING);
//                progressBar.setVisibility(View.VISIBLE);
            }
        });
        mBtnConnect.setEnabled(false);
        if(PeerMountainManager.getProfile().getImageUri()!=null) {
            uri = Uri.parse(PeerMountainManager.getProfile().getImageUri());
        }
//        showImageChooser("");
    }

    @Override
    protected void onStop() {
        setState(UNKNOWN);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (getState() == DISCOVERING) {//getState() == CONNECTED ||
            setState(ADVERTISING);//UNKNOWN
            return;
        }
        super.onBackPressed();
    }

    Uri uri = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            if (resultData != null) {
                // The URI of the file selected by the user.
                uri = resultData.getData();
            }
        }
    }

    /**
     * We've connected to Nearby Connections. We can now start calling {@link #startDiscovering()} and
     * {@link #startAdvertising()}.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        setState(ADVERTISING);
        mBtnConnect.setEnabled(true);
    }

    /**
     * We were disconnected! Halt everything!
     */
    @Override
    public void onConnectionSuspended(int reason) {
        super.onConnectionSuspended(reason);
        setState(UNKNOWN);
    }

    @Override
    protected void onEndpointDiscovered(Endpoint endpoint) {
        // We found an advertiser!
        connectToEndpoint(endpoint);
    }

    @Override
    protected void onConnectionInitiated(final Endpoint endpoint, ConnectionInfo connectionInfo) {
        // A connection to another device has been initiated! We can accept the connection immediately, but better ask
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.pm_share_accept_dialog_title, connectionInfo.getEndpointName()))
                .setMessage(getString(R.string.pm_share_accept_dialog_msg, connectionInfo.getAuthenticationToken()))
                .setPositiveButton(R.string.pm_share_accept_dialog_btn_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The user confirmed, so we can accept the connection.
                        acceptConnection(endpoint);
                    }
                })
                .setNegativeButton(R.string.pm_share_accept_dialog_btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The user canceled, so we should reject the connection.
                        rejectConnection(endpoint);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onEndpointConnected(Endpoint endpoint) {
        Toast.makeText(
                this, "Exchanging data with " + endpoint.getName(), Toast.LENGTH_SHORT)
                .show();
        setState(CONNECTED);
    }

    @Override
    protected void onEndpointDisconnected(Endpoint endpoint) {
        Toast.makeText(
                this, "Disconnected from " + endpoint.getName(), Toast.LENGTH_SHORT)
                .show();

        // If we lost all our endpoints, then we should reset the state of our app and go back
        // to our initial state (discovering).
        if (getConnectedEndpoints().isEmpty()) {
            setState(ADVERTISING);
        }
    }

    @Override
    protected void onConnectionFailed(Endpoint endpoint) {
        Toast.makeText(
                this, "Connection with " + endpoint.getName() + " failed. Please try again.", Toast.LENGTH_SHORT)
                .show();
        LogUtils.d("onConnectionFailed", endpoint.toString());
        setState(ADVERTISING);
        progressBar.setVisibility(View.GONE);
        // Let's try someone else.
//        if (getState() == DISCOVERING && !getDiscoveredEndpoints().isEmpty()) {
//            connectToEndpoint(pickRandomElem(getDiscoveredEndpoints()));
//        }
    }

    /**
     * {@see ConnectionsActivity#onReceive(Endpoint, Payload)}
     */
    @Override
    protected void onReceive(Endpoint endpoint, Payload payload) {
        ShareObject shareObject = null;
        switch (payload.getType()) {
            case Payload.Type.BYTES:
                String json = new String(payload.asBytes());
                LogUtils.d("payload msg", json);
                shareObject = PeerMountainManager.parseSharedObject(json);
//            Toast.makeText(this, endpoint.getName()+" says : "+msg, Toast.LENGTH_SHORT).show();
                break;
            case Payload.Type.FILE:
                fileSaved = false;
                java.io.File payloadFile = payload.asFile().asJavaFile();
                LogUtils.d("payload file", payloadFile.toString());
                shareObject = new ShareObject(ShareObject.OPERATION_SHARE_CONTACT_IMAGE_FILE, payloadFile);
                break;
        }
        handleMessage(shareObject);
    }

    private ShareObject shareObject = new ShareObject();
    private boolean confirmedFinish = false, needToConfirmFinish = false;

    private void handleMessage(ShareObject receivedShareObject) {
        if (receivedShareObject != null) {
            switch (receivedShareObject.getOperation()) {
                case ShareObject.OPERATION_SHARE_FINISH:// the other side is done sending,I have it all
                    if (fileSaved) {
                        sendConfirmFinish();//confirm I have nothing to send any more and ready to finish
                    } else {
                        needToConfirmFinish = true;
                    }
                    break;
                case ShareObject.OPERATION_SHARE_CONFIRM_FINISH:// the other side is done sending and has received my data "OPERATION_SHARE_FINISH"
                    confirmedFinish = true;
                    returnResult();
                    break;
                case ShareObject.OPERATION_SHARE_CONTACT_DATA:
                    if(receivedShareObject.getContact()!=null) {
                        receivedShareObject.getContact().setImageUri(null);
                    }
                    shareObject.setContact(receivedShareObject.getContact());
                    break;
                case ShareObject.OPERATION_SHARE_CONTACT_IMAGE_FILE:
                    if (shareObject.getContact() != null
                            && receivedShareObject.getReceivedFile() != null
                            && receivedShareObject.getReceivedFile().exists()) {
                        java.io.File payloadFile = receivedShareObject.getReceivedFile();
                        // Rename the file.
//                        payloadFile.renameTo(new File(payloadFile.getParentFile(),
//                                payloadFile.getName()+".jpg"));
//                        java.io.File localPayloadFile = new File(payloadFile.getParentFile(),
//                                payloadFile.getName()+".jpg");

                        java.io.File localPayloadFile = new File(getFilesDir()
                                + PeerMountainCoreConstants.LOCAL_IMAGE_DIR,
                                payloadFile.getName() + ".jpg");
                        LogUtils.d("local payload file", localPayloadFile.toString());
//                        java.io.File dir = new File(getFilesDir()
//                                + PeerMountainCoreConstants.LOCAL_IMAGE_DIR);
//                        dir.delete();
//                        FileUtils.copyFileAsync(payloadFile, localPayloadFile,true,null);
                        // resize image and rotate
                        int size = getResources().getDimensionPixelSize(R.dimen.pm_avatar_size);
                        ImageUtils.rotateAndResizeImageAsync(payloadFile, localPayloadFile, size,
                                size, true, new ImageUtils.ConvertImageTask.ImageCompressorListener() {
                                    @Override
                                    public void onImageCompressed(Bitmap bitmap, Uri uri) {
                                        onFileSaved();
                                    }

                                    @Override
                                    public void onError() {
                                        onFileSaved();
                                    }
                                }
                        );
                        shareObject.getContact().setImageUri(Uri.fromFile(localPayloadFile).toString());
                    }
                    break;
            }
        }
    }

    public void onFileSaved() {
        fileSaved = true;
        if (needToConfirmFinish) {
            sendConfirmFinish();
        }
        returnResult();

    }

    private boolean fileSaved = true;

    private void returnResult() {
        if (!fileSaved || !confirmedFinish) return;
        Intent data = new Intent();
        data.putExtra(SHARE_DATA, shareObject);
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * The state has changed. I wonder what we'll be doing now.
     *
     * @param state The new state.
     */
    private void setState(int state) {
        if (mState == state) {
            logW("State set to " + getStateName() + " but already in that state");
            return;
        }

        int oldState = mState;
        mState = state;
        logD("State set to " + getStateName());
        onStateChanged(oldState, state);
    }

    /**
     * State has changed.
     *
     * @param oldState The previous state we were in. Clean up anything related to this state.
     * @param newState The new state we're now in. Prepare the UI for this state.
     */
    private void onStateChanged(int oldState, int newState) {
        // Update Nearby Connections to the new state.
        switch (newState) {
            case DISCOVERING:
                progressBar.setVisibility(View.VISIBLE);
                if (isAdvertising()) {
                    stopAdvertising();
                }
                disconnectFromAllEndpoints();
                startDiscovering();
                break;
            case ADVERTISING:
                progressBar.setVisibility(View.GONE);
                if (isDiscovering()) {
                    stopDiscovering();
                }
                disconnectFromAllEndpoints();
                startAdvertising();
                break;
            case CONNECTED:
                if (isDiscovering()) {
                    stopDiscovering();
                } else if (isAdvertising()) {//start receiving data
//                    stopAdvertising();
                    progressBar.setVisibility(View.VISIBLE);
                }
                sendContact();
                sendFile();//test
                sendFinish();
                break;
            default:
                // no-op
                break;
        }
        updateUiForState(oldState, newState);
    }

    private void sendContact() {
        ShareObject shareObject = new ShareObject(ShareObject.OPERATION_SHARE_CONTACT_DATA);
        shareObject.setContact(PeerMountainManager.getProfile());
        send(Payload.fromBytes(PeerMountainManager.shareObjectToJson(shareObject).getBytes()));
    }

    private void sendFinish() {
        ShareObject shareObject = new ShareObject(ShareObject.OPERATION_SHARE_FINISH);
        send(Payload.fromBytes(PeerMountainManager.shareObjectToJson(shareObject).getBytes()));
    }

    private void sendConfirmFinish() {
        needToConfirmFinish = false;
        ShareObject shareObject = new ShareObject(ShareObject.OPERATION_SHARE_CONFIRM_FINISH);
        send(Payload.fromBytes(PeerMountainManager.shareObjectToJson(shareObject).getBytes()));
    }

    private void sendFile() {
        if (uri == null) return;
        // Open the ParcelFileDescriptor for this URI with read access.
        ParcelFileDescriptor pfd = null;
        try {
            pfd = getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (pfd != null) {
            Payload filePayload = Payload.fromFile(pfd);
            send(filePayload);
        }
    }

    /**
     * @return The current state.
     */
    private int getState() {
        return mState;
    }

    private String getStateName() {
        String state;
        switch (mState) {
            case CONNECTED:
                state = "CONNECTED";
                break;
            case DISCOVERING:
                state = "DISCOVERING";
                break;
            case ADVERTISING:
                state = "ADVERTISING";
                break;
            default:
                state = "UNKNOWN";
        }
        return state;
    }

    /**
     * Fires an intent to spin up the file chooser UI and select an image for
     * sending to endpointId.
     */
    private void showImageChooser(String endpointId) {
        new AlertDialog.Builder(this)
                .setTitle("Select image")
                .setMessage("If you want your avatar image on the other device to be different than your current, please point which one you want to send.")
                .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(intent, READ_REQUEST_CODE);
                    }
                })
                .setNegativeButton("Use current", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void updateUiForState(int oldState, int newState) {
        // Update the UI.
        switch (oldState) {
            case UNKNOWN:
                // Unknown is our initial state. Whatever state we move to,
                // we're transitioning forwards.
                transitionForward(oldState, newState);
                break;
            case DISCOVERING:
                switch (newState) {
                    case UNKNOWN:
                        transitionBackward(oldState, newState);
                        break;
                    case ADVERTISING:
                    case CONNECTED:
                        transitionForward(oldState, newState);
                        break;
                    default:
                        // no-op
                        break;
                }
                break;
            case ADVERTISING:
                switch (newState) {
                    case UNKNOWN:
                    case DISCOVERING:
                        transitionBackward(oldState, newState);
                        break;
                    case CONNECTED:
                        transitionForward(oldState, newState);
                        break;
                    default:
                        // no-op
                        break;
                }
                break;
            case CONNECTED:
                // Connected is our final state. Whatever new state we move to,
                // we're transitioning backwards.
                transitionBackward(oldState, newState);
                break;
            default:
                // no-op
                break;
        }
    }


    /**
     * Transitions from the old state to the new state with an animation implying moving forward.
     */
    @UiThread
    private void transitionForward(int oldState, int newState) {
//        mPreviousStateView.setVisibility(View.VISIBLE);
//        mCurrentStateView.setVisibility(View.VISIBLE);
//
//        updateTextView(mPreviousStateView, oldState);
//        updateTextView(mCurrentStateView, newState);
    }

    /**
     * Transitions from the old state to the new state with an animation implying moving backward.
     */
    @UiThread
    private void transitionBackward(int oldState, int newState) {
//        mPreviousStateView.setVisibility(View.VISIBLE);
//        mCurrentStateView.setVisibility(View.VISIBLE);
//
//        updateTextView(mCurrentStateView, oldState);
//        updateTextView(mPreviousStateView, newState);
    }

    @Override
    protected String getName() {
        return mName;
    }

    @Override
    protected String getCode() {
        return mCode;
    }

    @Override
    protected String getServiceId() {
        return SERVICE_ID + mCode;
    }

    @Override
    protected String getServiceIdToDiscover() {
        return SERVICE_ID + mEtCode.getText().toString();
    }

    private static String generateRandomName() {
        String name = "";
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            name += random.nextInt(10);
        }
        return name;
    }

    private void initView() {
        mTvMsg = (TextView) findViewById(R.id.tvMsg);
        mTvCode = (TextView) findViewById(R.id.tvCode);
        mTvMsgConnect = (TextView) findViewById(R.id.tvMsgConnect);
        mEtCode = (EditText) findViewById(R.id.etCode);
        mBtnConnect = findViewById(R.id.btnConnect);
        progressBar = findViewById(R.id.progressBar);
    }
}
