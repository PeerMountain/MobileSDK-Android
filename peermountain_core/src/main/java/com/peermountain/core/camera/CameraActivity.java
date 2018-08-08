package com.peermountain.core.camera;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.peermountain.core.R;
import com.peermountain.common.utils.PmDialogUtils;

import java.io.File;
import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {
    public static final String EXTRA_SCAN = "EXTRA_SCAN";
    public static final int LIVE_SELFIE_FRAME_INTERVAL = 200;
    private CameraView camera;
    private ImageView btnRecord;
    private Button btnDone, btnFlash;
    private ProgressBar progress;
    private TextView tvMsg;
    private boolean isScanningDocument = false;

    public static void show(Activity activity, boolean isScan, int requestCode) {
        Intent starter = new Intent(activity, CameraActivity.class);
        starter.putExtra(EXTRA_SCAN, isScan);
        if (requestCode == -1) {
            activity.startActivity(starter);
        } else {
            activity.startActivityForResult(starter, requestCode);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        isScanningDocument = getIntent().getBooleanExtra(EXTRA_SCAN, false);
        if (isScanningDocument) {
            setContentView(R.layout.pm_activity_camera_id);
        } else {
            setContentView(R.layout.pm_activity_camera);
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
            // TODO: 5/11/2018 show permission info dialog
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
    }


    public static ArrayList<Bitmap> bitmaps = null;

    private static ArrayList<Bitmap> getBitmaps() {
        if (bitmaps == null) {
            bitmaps = new ArrayList<>();
        }
        return bitmaps;
    }

    public static Bitmap[] idImages = new Bitmap[2];

    private void onPicture(byte[] jpeg) {
        if (jpeg != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
            if (!isScanningDocument) {
                getBitmaps().add(bitmap);
            } else {
                if (idImages[0] == null) {
                    idImages[0] = bitmap;
                    setViewForIdStatus();
                } else {
                    idImages[1] = bitmap;
                    endScanning();
                }
            }
        }
    }


    private void initView() {
        btnDone = findViewById(R.id.btnDone);
        camera = findViewById(R.id.camera);
        btnRecord = findViewById(R.id.btnRecord);
        progress = findViewById(R.id.progress);
        tvMsg = findViewById(R.id.tvMsg);
        if (isScanningDocument) {
            btnFlash = findViewById(R.id.btnFlash);
            btnFlash.setVisibility(View.VISIBLE);
            idImages = new Bitmap[2];
            camera.setPlaySounds(true);
            tvMsg.setVisibility(View.VISIBLE);
            setViewForIdStatus();
        }
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

    boolean isBigSize = false;
    boolean isDoneAutoCapturing = false;
    CountDownTimer countDownTimer;
    File dir;

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
                if (isScanningDocument) {
                    camera.capturePicture();
                } else {
                    doLiveSelfie();
                }
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
        setResult(RESULT_OK);
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
                tvMsg.setText(R.string.pm_msg_saving_live_selfie);
                tvMsg.setVisibility(View.VISIBLE);
                setResult(RESULT_OK);
                finish();
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


}
