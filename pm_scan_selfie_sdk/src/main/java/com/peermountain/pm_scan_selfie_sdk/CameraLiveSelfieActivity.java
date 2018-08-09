package com.peermountain.pm_scan_selfie_sdk;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.peermountain.common.utils.PmDialogUtils;
import com.peermountain.pm_livecycle.base.BaseActivity;
import com.peermountain.pm_scan_selfie_sdk.model.VerifySelfie;

import java.io.File;
import java.util.ArrayList;

// TODO: 8/9/2018 base CameraActivity
public class CameraLiveSelfieActivity extends BaseActivity<LiveSelfieViewModel> {
    public static final String ARG_LIVE_SELFIE_URIS = "liveSelfieUris";
    private static final int LIVE_SELFIE_FRAME_INTERVAL = 200;
    private static final String ARG_FILE = "File";

    private CameraView camera;
    private ImageView btnRecord;
    private Button btnDone;
    private ProgressBar progress;
    private TextView tvMsg;
    private File idFaceFile;

    public static void show(Activity activity, File idFaceFile, int requestCode) {
        Intent starter = new Intent(activity, CameraLiveSelfieActivity.class);
        starter.putExtra(ARG_FILE, idFaceFile);
        activity.startActivityForResult(starter, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setContentView(R.layout.pm_activity_camera);

        setResult(RESULT_CANCELED);

        idFaceFile = (File) getIntent().getSerializableExtra(ARG_FILE);
        if(idFaceFile==null || !idFaceFile.exists()){
            finish();
            return;
        }

        initView();
        initCamera();
        setListeners();
    }

    private boolean permissionRejected = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (!permissionRejected) camera.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    protected void setObservers() {
        activityViewModel.getOnSelfieVerifiedFromServer().observe(this, new Observer<VerifySelfie>() {
            @Override
            public void onChanged(@Nullable VerifySelfie verifySelfie) {
                if (verifySelfie != null && verifySelfie.checkIsValid()) {
                    onSelfieVerified();
                } else {
                    onSelfieRejected(verifySelfie);
                }
            }
        });
    }

    @NonNull
    @Override
    protected Class<LiveSelfieViewModel> getViewModel() {
        return LiveSelfieViewModel.class;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid) {
            if (!camera.isStarted()) camera.start();
        } else {
            permissionRejected = true;
            // show permission info dialog
            PmDialogUtils.showChoiceDialog(this, -1, R.string.pm_ask_for_permission_again_camera,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            camera.start();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    },
                    R.string.pm_btn_ask_for_permission_again,
                    R.string.pm_btn_refuse_permission);
        }
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
        tvMsg.setVisibility(View.VISIBLE);
        tvMsg.setText(R.string.pm_msg_live_selfie);
        camera.setVisibility(View.VISIBLE);
    }


    public ArrayList<Bitmap> bitmaps = null;

    private ArrayList<Bitmap> getBitmaps() {
        if (bitmaps == null) {
            bitmaps = new ArrayList<>();
        }
        return bitmaps;
    }

    private void onPicture(byte[] jpeg) {
        if (jpeg != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
            getBitmaps().add(bitmap);
        }
    }


    private void initView() {
        btnDone = findViewById(R.id.btnDone);
        camera = findViewById(R.id.camera);
        btnRecord = findViewById(R.id.btnRecord);
        progress = findViewById(R.id.progress);
        tvMsg = findViewById(R.id.tvMsg);
    }


    boolean isBigSize = false;
    boolean isDoneAutoCapturing = false;
    CountDownTimer countDownTimer;

    /**
     * type ocl to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRecord.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                doLiveSelfie();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endScanning();
            }
        });
    }

    private ArrayList<String> liveSelfieUris = null;

    private void onSelfieVerified() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(ARG_LIVE_SELFIE_URIS, liveSelfieUris);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onSelfieRejected(VerifySelfie verifySelfie) {
        liveSelfieUris = null;

        String msg = "";
        if (verifySelfie == null) {
            msg = getString(R.string.pm_main_server_error_selfie);
        } else {
            if (!verifySelfie.isHumanFace()) {
                msg = getString(R.string.pm_server_selfie_error_no_human);
            }
            if (!verifySelfie.isLiveliness()) {
                msg += getString(R.string.pm_server_error_selfie_liveliness);
            }
            if (!verifySelfie.isFaceMatch()) {
                msg += getString(R.string.pm_server_error_selfie_face);
            }
        }
        msg += getString(R.string.pm_server_error_selfie_end);
        PmDialogUtils.showError(this, msg);

        restartScan();
    }

    private void restartScan() {
        onOpened();
        if (!permissionRejected) camera.start();
    }


    private void endScanning() {
        camera.stop();
        finish();
    }

    private void doLiveSelfie() {
        camera.setPlaySounds(false);
        bitmaps = new ArrayList<>();
        countDownTimer = new CountDownTimer(3000, LIVE_SELFIE_FRAME_INTERVAL) {
            @Override
            public void onTick(long l) {
                camera.captureSnapshot();
            }

            @Override
            public void onFinish() {
                isDoneAutoCapturing = true;
                countDownTimer = null;
                camera.stop();
                camera.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
                tvMsg.setText(R.string.pm_msg_saving_live_selfie);
                tvMsg.setVisibility(View.VISIBLE);
                new PmLiveSelfieHelper(new PmLiveSelfieHelper.Events() {
                    @Override
                    public void onLiveSelfieReady(ArrayList<File> liveSelfieFiles) {
                        handleLiveSelfieImages(liveSelfieFiles);
                    }

                    @Override
                    public void clearCashBitmaps() {
                        bitmaps = null;
                    }
                }).saveLiveSelfie(bitmaps);
            }
        };
        countDownTimer.start();

//                if(isBigSize){
//                    camera.capturePicture();
//                }else {
//                    camera.captureSnapshot();
//                }
//                isBigSize = !isBigSize;
    }

    private void handleLiveSelfieImages(ArrayList<File> liveSelfieFiles) {
        liveSelfieUris = new ArrayList<>();

        if (liveSelfieFiles.size() >= 8) {
            for (int i = 0; i < liveSelfieFiles.size(); i++) {
                liveSelfieUris.add(Uri.fromFile(liveSelfieFiles.get(i)).toString());
            }
        }else{
            PmDialogUtils.showError(CameraLiveSelfieActivity.this, getString(R.string.pm_error_doing_selfie));
            restartScan();
            return;
        }

        ArrayList<File> files = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            files.add(liveSelfieFiles.get(i*2));
        }

        files.add(idFaceFile);
        tvMsg.setVisibility(View.GONE);
        // verify
        activityViewModel.sendFilesToVerifyLiveSelfie(files);
    }
}
