package com.peermountain.scan_id_sdk.scan;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Flash;
import com.peermountain.common.model.DocumentID;
import com.peermountain.common.utils.PmDialogUtils;
import com.peermountain.common.utils.PmFragmentUtils;
import com.peermountain.pm_livecycle.base.BaseActivity;
import com.peermountain.scan_id_sdk.R;
import com.peermountain.scan_id_sdk.show_data.ShowScannedIdFragment;

import java.io.File;
import java.util.ArrayList;

public class ScanIdentityDocumentCameraActivity extends BaseActivity<IdentityDocumentViewModel>
        implements ScanDocumentFragment.OnFragmentInteractionListener, ShowScannedIdFragment.OnFragmentInteractionListener {
    public static final String ID = "ID";
    private static final String ARG_RETURN = "RETURN";

    private CameraView camera;
    private ImageView btnRecord;
    private Button btnDone, btnFlash;
    private ProgressBar progress;
    private TextView tvMsg;
    private boolean shouldReturn = false;
    private ScanDocumentFragment scanDocumentFragment;
    private PmFragmentUtils.FragmentBuilder fragmentBuilder;

    public static void show(Activity activity, int requestCode) {
        Intent starter = new Intent(activity, ScanIdentityDocumentCameraActivity.class);
        if (requestCode == -1) {
            activity.startActivity(starter);
        } else {
            starter.putExtra(ARG_RETURN, true);
            activity.startActivityForResult(starter, requestCode);
        }
    }

    public static void show(Activity activity) {
        Intent starter = new Intent(activity, ScanIdentityDocumentCameraActivity.class);
        activity.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.pm_activity_camera_id);

        shouldReturn = getIntent().getBooleanExtra(ARG_RETURN, false);
        fragmentBuilder = PmFragmentUtils.init(this, R.id.flFragmentContainer);
        if(savedInstanceState == null){
            fragmentBuilder.addToBackStack(false)
                    .replace(scanDocumentFragment = ScanDocumentFragment.newInstance(null, null));
        }
//        initView();
//        initCamera();
//        setListeners();
    }

//    private boolean permissionRejected = false;
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!permissionRejected) camera.start();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        camera.stop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        camera.destroy();
//    }

    private ArrayList<File> filesToSend;

    @Override
    protected void setObservers() {
        activityViewModel.getOnDocumentScannedFromServer().observe(this, new Observer<DocumentID>() {
            @Override
            public void onChanged(@Nullable DocumentID documentID) {
                if (documentID == null || documentID.getErrorMessage() != null) {//!documentID.checkIsValid()
                    String msg;
                    if (documentID != null) {
                        documentID.deleteDocumentImages();
                        msg = documentID.getErrorMessage();
                    } else {
                        msg = getString(R.string.pm_err_msg_scan_data);
                    }
                    PmDialogUtils.showErrorToast(ScanIdentityDocumentCameraActivity.this, msg);
                    if(scanDocumentFragment!=null) scanDocumentFragment.resetScanning();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(ID, documentID);
                if (shouldReturn) {// return document to caller
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    fragmentBuilder.addToBackStack(true)
                            .replace(ShowScannedIdFragment.newInstance(intent));
                }
            }
        });
    }

    @NonNull
    @Override
    protected Class<IdentityDocumentViewModel> getViewModel() {
        return IdentityDocumentViewModel.class;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(scanDocumentFragment!=null)scanDocumentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        boolean valid = true;
//        for (int grantResult : grantResults) {
//            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
//        }
//        if (valid) {
//            if (!camera.isStarted()) camera.start();
//        } else {
//            permissionRejected = true;
//            // show permission info dialog
//            PmDialogUtils.showChoiceDialog(this, -1, R.string.pm_ask_for_permission_again_camera,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            camera.start();
//                        }
//                    },
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    },
//                    R.string.pm_btn_ask_for_permission_again,
//                    R.string.pm_btn_refuse_permission);
//        }
    }

    private void initCamera() {
        camera.addCameraListener(new CameraListener() {
            public void onCameraOpened(CameraOptions options) {
                onOpened();
                camera.startAutoFocus(camera.getWidth() / 2, camera.getHeight() / 2);
            }

            public void onPictureTaken(byte[] jpeg) {
                onPicture(jpeg);
            }

            public void onVideoTaken(File video) {
            }
        });
    }

    private void onOpened() {//ready
        btnRecord.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private Bitmap[] idImages = new Bitmap[2];

    private void onPicture(byte[] jpeg) {
        if (jpeg != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);

            if (idImages[0] == null) {
                idImages[0] = bitmap;
                setViewForIdStatus();
            } else {
                idImages[1] = bitmap;
                endScanning();
            }
        }
    }


    private void initView() {
        btnDone = findViewById(R.id.btnDone);
        camera = findViewById(R.id.camera);
        btnRecord = findViewById(R.id.btnRecord);
        progress = findViewById(R.id.progress);
        tvMsg = findViewById(R.id.tvMsg);
        btnFlash = findViewById(R.id.btnFlash);
        btnFlash.setVisibility(View.VISIBLE);
        idImages = new Bitmap[2];
        camera.setPlaySounds(true);
        tvMsg.setVisibility(View.VISIBLE);
        setViewForIdStatus();
    }

    private void setViewForIdStatus() {
        if (idImages[0] == null) {//take first image
            tvMsg.setText(R.string.pm_msg_capture_mrz);
        } else {//take second image
            tvMsg.setText(R.string.pm_msg_capture_not_mrz);
            btnDone.setVisibility(View.VISIBLE);
            onOpened();
        }
    }

    /**
     * type ocl to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        if (btnFlash != null) {
            btnFlash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnFlash.setSelected(!btnFlash.isSelected());
                    camera.setFlash(btnFlash.isSelected() ? Flash.TORCH : Flash.OFF);
                }
            });
        }
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRecord.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);

                camera.capturePicture();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endScanning();
            }
        });


    }

    private void endScanning() {
        camera.stop();
        camera.setVisibility(View.GONE);
        tvMsg.setVisibility(View.VISIBLE);

        new PmScanIdentityDocumentHelper(new PmScanIdentityDocumentHelper.Events() {
            @Override
            public void onID_Ready(ArrayList<File> files) {
                filesToSend = files;
                activityViewModel.sendFiles(filesToSend);
            }

            @Override
            public Bitmap[] getID_Images() {
                return idImages;
            }

            @Override
            public void clearID_Images() {
                idImages = null;
            }
        }).saveID();
    }

    @Override
    public void onID_Ready(ArrayList<File> files) {
        filesToSend = files;
        activityViewModel.sendFiles(filesToSend);
    }

    @Override
    public void onPermissionRefused() {
        finish();
    }

    @Override
    public void onScannedIdDataAccepted(DocumentID scannedDocument) {
        Intent intent = new Intent();
        intent.putExtra(ID, scannedDocument);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onScannedIdDataRejected(DocumentID scannedDocument) {
        fragmentBuilder.pop();
    }

//    private void resetScanning() {
//        idImages = new Bitmap[2];
//        camera.setVisibility(View.VISIBLE);
//        if (!permissionRejected) camera.start();
//        setViewForIdStatus();
//    }
}
