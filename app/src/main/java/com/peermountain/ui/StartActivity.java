package com.peermountain.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;

import com.peermountain.R;
import com.peermountain.core.camera.CameraActivity;
import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.network.BaseEvents;
import com.peermountain.core.network.MainCallback;
import com.peermountain.core.network.NetworkManager;
import com.peermountain.core.network.NetworkResponse;
import com.peermountain.core.network.teleferique.TfConstants;
import com.peermountain.core.network.teleferique.callbacks.TimeCallback;
import com.peermountain.core.network.teleferique.model.Persona;
import com.peermountain.core.network.teleferique.model.SendObject;
import com.peermountain.core.network.teleferique.model.body.invitation.InvitationBuilder;
import com.peermountain.core.network.teleferique.model.body.registration.RegistrationBuilder;
import com.peermountain.core.odk.views.widgets.image.DocumentsFragmentDialog;
import com.peermountain.core.persistence.MyJsonParser;
import com.peermountain.core.utils.PmDocumentsHelper;
import com.peermountain.core.utils.PmLiveSelfieHelper;
import com.peermountain.sdk.PeerMountainSDK;
import com.peermountain.sdk.utils.DialogUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class StartActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 111;
    private PmDocumentsHelper pmDocumentsHelper;
    private AppDocument appDocument = new AppDocument(true),
            appDocumentBack = new AppDocument(true);
    DocumentsFragmentDialog dialog;
    int step = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
//        PeerMountainSDK.logout();//to test login again
        findViewById(R.id.flMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                invite();

//                switch (step) {
//                    case 0:
//                        pmDocumentsHelper.addDocument(appDocument);
                CameraActivity.show(StartActivity.this, true, REQUEST_IMAGE_CAPTURE);

//                        break;
//                    case 1 :
//                        pmDocumentsHelper.addDocument(appDocumentBack);
//                        break;
//                    default:
//                        sendFiles();
//                }

//                dialog = new DocumentsFragmentDialog();
//                dialog.setListener(new AppDocumentsAdapter.Events() {
//                    @Override
//                    public void onDocumentSelected(AppDocument document) {
//                        if (document != null) {
//                            appDocument = document;
//                            sendFiles();
//                        }
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show(getSupportFragmentManager(), "documents_dialog");
            }
        });
//        showSplash();

        initDocumentHelper();


//                finish();
//        authorize();

//        NetworkRequestHelper.init();// TODO: 3/19/18 remove after certificate is OK

//        Teleferic
//        NetworkManager.sendToServer(new KeyCallback(null,MainCallback.TYPE_NO_PROGRESS),
//                new SendObject().preparePublicPersonaAddress());
//        invite();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        pmDocumentsHelper.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            if (CameraActivity.idImages != null
                    && CameraActivity.idImages[0] != null) {
                //save files and send
                new PmLiveSelfieHelper(true, new PmLiveSelfieHelper.Events() {
                    @Override
                    public Activity getActivity() {
                        return StartActivity.this;
                    }

                    @Override
                    public void onLiveSelfieReady(ArrayList<String> liveSelfie) {
                        ArrayList<File> files = new ArrayList<>();
                        Uri uri = Uri.parse(liveSelfie.get(0));
                        File file = new File(uri.getPath());
                        files.add(file);
                        if(liveSelfie.size()>1){
                            uri = Uri.parse(liveSelfie.get(0));
                            File file2 = new File(uri.getPath());
                            files.add(file2);
                        }
                        sendFiles(files);
                    }
                }).saveID();
            } else {
                DialogUtils.showInfoSnackbar(this, com.peermountain.sdk.R.string.pm_err_no_liveselfie_created);
            }
        }
    }

    public void initDocumentHelper() {
        pmDocumentsHelper = new PmDocumentsHelper(
                new PmDocumentsHelper.Events() {
                    @Override
                    public void refreshAdapter() {
                        if (step == 0) {
                            step++;
                            pmDocumentsHelper.addDocument(appDocumentBack);
                        } else {
                            sendFiles(null);
                        }
                    }

                    @Override
                    public Activity getActivity() {
                        return StartActivity.this;
                    }

                    @Override
                    public Fragment getFragment() {
                        return null;
                    }

                    @Override
                    public void onScanSDKLoading(boolean loading) {

                    }

                    @Override
                    public void onAddingDocumentCanceled(AppDocument document) {

                    }
                }
        );
    }

    private void sendFiles(ArrayList<File> filesToSend) {
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add("passportFront");
        fileNames.add("passportBack");
        ArrayList<File> files;
        if (filesToSend != null) {
            files = filesToSend;
        } else {
            files = new ArrayList<>();
            Uri uri = Uri.parse(appDocument.getFileDocuments().get(0).getImageUri());
            File file = new File(uri.getPath());
            files.add(file);

            uri = Uri.parse(appDocumentBack.getFileDocuments().get(0).getImageUri());
            File file2 = new File(uri.getPath());
            files.add(file2);
        }
        NetworkManager.sendFiles(new MainCallback(null, MainCallback.TYPE_NO_PROGRESS),
                "https://api.kyc3.com/rest/api/_mrzExtractor?api_key=bStfjjadHizdxqabdcStOg==",
                files,
                fileNames);
    }

    private void invite() {
        //get time first
        NetworkManager.sendToServer(new TimeCallback(null, MainCallback.TYPE_NO_PROGRESS,
                        new TimeCallback.Events(null, MainCallback.TYPE_NO_PROGRESS) {
                            @Override
                            public void onTime(String time) {
                                if (time != null) sendInvite(time);
                            }
                        }),
                new SendObject().prepareTime());
    }

    private void sendInvite(String time) {// TODO: 4/2/18 get time first
        InvitationBuilder invitationBuilder = new InvitationBuilder(TfConstants.BODY_TYPE_INVITATION,
                time)
                .setBootstrapNode("https://teleferic-dev.dxmarkets.com/teleferic/")
                .setBootstrapAddr("8MSd91xr6jSV5pS29RkV7dLeE3hDgLHJGrsyXpdSf4iitj6c75tVSNESywBzYzFEeyu5D1zyrL")
                .setOfferingAddr("8MSd91xr6jSV5pS29RkV7dLeE3hDgLHJGrsyXpdSf4iitj6c75tVSNESywBzYzFEeyu5D1zyrL")
                .setServiceAnnouncementMessage("L+ViP+UFnhc6ObWfhugqNZfE+SZkqoS46I4Qbw+NbOY=")
                .setInviteName("Invite 1")
                .setServiceOfferingID("1")
                .setInviteKey("72x35FDOXuTkxivh7qYlqPU91jVgy607");

        RegistrationBuilder registrationBuilder = new RegistrationBuilder(TfConstants.BODY_TYPE_REGISTRATION, time)
//                .setInviteMsgID("XY+IUYG2tojWCPSQz7dVhcSoEDOTZdGsPlfDIDsYIKg=")
//                .setInviteName("Invite 1")
//                .setKeyProof("72x35FDOXuTkxivh7qYlqPU91jVgy607")
//                .setPublicNickname("Future1");
                .setInviteMsgID("+axfoOXMBqG4iZctjRDQWqajUimzt3ZxIZ9Zf4tgiLU=")
                .setInviteName("Galeen")
                .setKeyProof("g4l3n")
                .setPublicNickname("Galeen-test");

        NetworkManager.sendToServer(new InviteCallback(null, MainCallback.TYPE_NO_PROGRESS),
//                "http://b9d87780.ngrok.io/teleferic/",
                registrationBuilder.build()
//                invitationBuilder.build()
        );
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }

    CountDownTimer timer;

    private void showSplash() {
        timer = new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                PeerMountainSDK.goHome(StartActivity.this);
                timer = null;
                finish();
            }
        };
        timer.start();
    }

    private class InviteCallback extends MainCallback {

        public InviteCallback(BaseEvents presenterCallback, int progressType) {
            super(presenterCallback, progressType);
        }

        @Override
        public void inTheEndOfDoInBackground(NetworkResponse networkResponse) {
            super.inTheEndOfDoInBackground(networkResponse);
            // TODO: 4/2/18 parse hash
        }

        @Override
        public void onPostExecute(NetworkResponse networkResponse) {
            super.onPostExecute(networkResponse);
            // TODO: 4/2/18 register
        }

        @Override
        public void onError(String msg, NetworkResponse networkResponse) {
            super.onError(msg, networkResponse);
        }
    }

    private class KeyCallback extends MainCallback {
        public KeyCallback(BaseEvents presenterCallback, int progressType) {
            super(presenterCallback, progressType);
        }

        @Override
        public void onPostExecute(NetworkResponse networkResponse) {
            super.onPostExecute(networkResponse);
            try {
                Persona persona = MyJsonParser.readServerPersona(networkResponse.json);
                TfConstants.KEY_PUBLIC_SERVER = new
                        String(Base64.decode(persona.getPubkey(), Base64.DEFAULT));
                TfConstants.ADDRESS_PUBLIC_SERVER = persona.getAddress();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    private class TimeCallback extends MainCallback {
//        public TimeCallback(BaseEvents presenterCallback, int progressType) {
//            super(presenterCallback, progressType);
//        }
//
//        @Override
//        public void inTheEndOfDoInBackground(NetworkResponse networkResponse) {
//            super.inTheEndOfDoInBackground(networkResponse);
//            try {
//                networkResponse.object = MyJsonParser.readServerTime(networkResponse.json);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onPostExecute(NetworkResponse networkResponse) {
//            super.onPostExecute(networkResponse);
//            if(networkResponse.object!=null){
//                sendInvite((String) networkResponse.object);
////                        ("gqlzaWduYXR1cmXaAqxCZHFtTlMycWZQbk1JVm1PY05JWjdQVy9Da1FpZDB4SlFjS20wTzZHNzNuZUYzYlF5R1R0c3hRd0tPV0d5NFEzNnJNK3pQblU4Ri95R0dkRFozQW1JQUV6Tlp2RjRyc2RIZE42SDg4MFJjNG01YzdVQkk1MWhqOXJ4TXRNY0pXZTVQTUpyNFo4dlhPQUcrTmdVQVNScmtHeHBUNDhJTVlCVkpHQWxIZ3FsaVNTeTJoSzdjdDhHZzlkQk1yVFpWcXFLaVVMNkJ6MGthcmVQazA0WjZrZ2hwOEwyS1ovYjBFM0RnSS9mTVlLMDFjNDl3cUxRRExuZWF4a0NFREhtQUUvYlBha3B2eWJYVk5ldmtEaWtKOVpnbzdsdFhhR1BjVG1jTXZoYjFDVUtieHZISlVFRUpXbVFXb0tCTWhwRzJqWlFtWGJCRFZUczBkUEYzUXlqZnhLelJFWVFTWGJRZk85eFNmdTlwckQrUWtRSmRqdHY2Z2RURHJsU3BMaFloZnJDNXkwclA2amVyQnpzVjVmQkNuMGlzZzB2VllVUGM2M3k0ZW45d3R5UHhhcTlTZ3cvZDhLUmgyS2ZuaG4yNi9QSWxtdlYrU1hYbUFNV2c5TDNOcTdVVXBUNHJNKy9WWGZTNzAvUXRyQmVMdkYvUUFmSkhhRDJFK2RLeEV5cndXdHQ1SEQyVWxBOTlUb01YVk15UHhHaFErOS9Ba21QWVpxS2RzekU3Ym9OYjhQTG44S2dGNUMzYkkrajh4VVhsN3d3Rm9YZ0NlV09VSmdmUHJZV1hETUg2WERRQ3dYZzQyMVN6Y3Jpd2k1dWtKOVhmYjgrVXl6d3locWJQcG9mWEFjZnhHRWJLNVVUZ0NGQlZEMkFRNzFPbytmbnVuYURpNGR1UjV4bWVjbE5FWT2pdGltZXN0YW1wsjE1MjEyMTQ5MDguNzAwMjkxMg==");
//            }
//        }
//    }
}
