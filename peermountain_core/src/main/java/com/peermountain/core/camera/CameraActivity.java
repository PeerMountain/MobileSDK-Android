package com.peermountain.core.camera;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.peermountain.core.R;

import java.io.File;
import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {

    public static final int LIVE_SELFIE_FRAME_INTERVAL = 200;
    private CameraView camera;
    private ImageView btnRecord;
    private ProgressBar progress;
    private TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.pm_activity_camera);
        initView();
        initCamera();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
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
        if (valid && !camera.isStarted()) {
            camera.start();
        }
    }

    private void initCamera() {
        camera.addCameraListener(new CameraListener() {
            public void onCameraOpened(CameraOptions options) {
                onOpened();
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

    private static ArrayList<Bitmap> getBitmaps(){
        if(bitmaps==null){
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
        camera = findViewById(R.id.camera);
        btnRecord = findViewById(R.id.btnRecord);
        progress = findViewById(R.id.progress);
        tvMsg = findViewById(R.id.tvMsg);
    }

    boolean isBigSize = false;
    boolean isDoneAutoCapturing = false;
    CountDownTimer countDownTimer;
    File dir;

    /**
     * type ocl to fast get new setOnClickListener, rr/rc/rs to set ripple
     */
    private void setListeners() {
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRecord.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
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
        });
    }


}
