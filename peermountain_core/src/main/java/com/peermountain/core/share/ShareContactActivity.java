package com.peermountain.core.share;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import com.peermountain.core.utils.LogUtils;

import java.util.Random;

public class ShareContactActivity extends ConnectionsActivity {
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
    private Button mBtnConnect;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_contact);
        initView();

        mName = PeerMountainManager.getProfile().getNames();
        mCode = generateRandomName();
        mTvCode.setText(mCode);

        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setState(DISCOVERING);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
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

    /**
     * We've connected to Nearby Connections. We can now start calling {@link #startDiscovering()} and
     * {@link #startAdvertising()}.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        setState(ADVERTISING);
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
                .setTitle("Accept connection to " + connectionInfo.getEndpointName())
                .setMessage("Confirm if the code " + connectionInfo.getAuthenticationToken() + " is also displayed on the other device")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The user confirmed, so we can accept the connection.
                        acceptConnection(endpoint);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
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
                this, "Connected" + endpoint.getName(), Toast.LENGTH_SHORT)
                .show();
        setState(CONNECTED);
    }

    @Override
    protected void onEndpointDisconnected(Endpoint endpoint) {
        Toast.makeText(
                this, "Disconnected" + endpoint.getName(), Toast.LENGTH_SHORT)
                .show();

        // If we lost all our endpoints, then we should reset the state of our app and go back
        // to our initial state (discovering).
        if (getConnectedEndpoints().isEmpty()) {
            setState(ADVERTISING);
        }
    }

    @Override
    protected void onConnectionFailed(Endpoint endpoint) {
        LogUtils.d("onConnectionFailed",endpoint.toString());
        // Let's try someone else.
//        if (getState() == DISCOVERING && !getDiscoveredEndpoints().isEmpty()) {
//            connectToEndpoint(pickRandomElem(getDiscoveredEndpoints()));
//        }
    }

    /** {@see ConnectionsActivity#onReceive(Endpoint, Payload)} */
    @Override
    protected void onReceive(Endpoint endpoint, Payload payload) {
        if (payload.getType() == Payload.Type.BYTES) {
            String json = new String(payload.asBytes());
            LogUtils.d("payload",json);
            ShareObject shareObject = PeerMountainManager.parseSharedObject(json);
            handleMessage(shareObject);
//            Toast.makeText(this, endpoint.getName()+" says : "+msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleMessage(ShareObject shareObject) {
        if(shareObject!=null){
            switch (shareObject.getOperation()){
                case ShareObject.OPERATION_SHARE_CONTACT_DATA:
                    if(shareObject.getContact()!=null
                            && !TextUtils.isEmpty(shareObject.getContact().getImageUri())){
                        //we have to wait for image
                    }else{
                        returnResult(shareObject);
                    }
                    break;
            }
        }
    }

    private void returnResult(ShareObject shareObject) {
        Intent data = new Intent();
        data.putExtra(SHARE_DATA, shareObject);
        setResult(RESULT_OK,data);
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
     * State has changed.
     *
     * @param oldState The previous state we were in. Clean up anything related to this state.
     * @param newState The new state we're now in. Prepare the UI for this state.
     */
    private void onStateChanged(int oldState, int newState) {
        // Update Nearby Connections to the new state.
        switch (newState) {
            case DISCOVERING:
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
//                    send(Payload.fromBytes("Hello advertiser!".getBytes()));
                } else if (isAdvertising()) {
//                    stopAdvertising();
//                    send(Payload.fromBytes("Hello requester!".getBytes()));
                }
                sendContact();
                progressBar.setVisibility(View.GONE);
                break;
            default:
                // no-op
                break;
        }
        updateUiForState(oldState, newState);
    }

    private void sendContact(){
        ShareObject shareObject = new ShareObject(ShareObject.OPERATION_SHARE_CONTACT_DATA);
        shareObject.setContact(PeerMountainManager.getProfile());
        send(Payload.fromBytes(PeerMountainManager.shareObjectToJson(shareObject).getBytes()));
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
        mBtnConnect = (Button) findViewById(R.id.btnConnect);
        progressBar = findViewById(R.id.progressBar);
    }
}
