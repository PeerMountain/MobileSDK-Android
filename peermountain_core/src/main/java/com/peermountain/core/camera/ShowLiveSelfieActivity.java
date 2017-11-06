package com.peermountain.core.camera;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.peermountain.core.R;
import com.peermountain.core.persistence.PeerMountainManager;

public class ShowLiveSelfieActivity extends AppCompatActivity {

    private ImageView ivLiveSelfie;
    AnimationDrawable liveSelfieAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pm_activity_show_live_selfie);
        initView();
        prepareLiveSelfie();
    }

    private void prepareLiveSelfie() {
        //        if (CameraActivity.bitmaps != null && CameraActivity.bitmaps.size() > 0) {
//            liveSelfieAnimation = new AnimationDrawable();
//            for (int i = 0; i < CameraActivity.bitmaps.size(); i++) {
//                liveSelfieAnimation.addFrame(
//                        new BitmapDrawable(getResources(), CameraActivity.bitmaps.get(i)), CameraActivity.LIVE_SELFIE_FRAME_INTERVAL);
//            }
//            liveSelfieAnimation.setOneShot(true);
//            ivLiveSelfie.setImageDrawable(liveSelfieAnimation);
//        } else {
        if (PeerMountainManager.getProfile() != null
                && PeerMountainManager.getProfile().hasLiveSelfie()) {
            liveSelfieAnimation = new AnimationDrawable();
            for (int i = 0; i < PeerMountainManager.getProfile().getLiveSelfie().size(); i++) {
                String path = Uri.parse(PeerMountainManager.getProfile().getLiveSelfie().get(i)).getPath();
                liveSelfieAnimation.addFrame(
                        new BitmapDrawable(getResources(), path), CameraActivity.LIVE_SELFIE_FRAME_INTERVAL);
            }
            liveSelfieAnimation.setOneShot(true);
            ivLiveSelfie.setImageDrawable(liveSelfieAnimation);

            ivLiveSelfie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (liveSelfieAnimation != null) {
                        ivLiveSelfie.setImageDrawable(null);
                        ivLiveSelfie.clearAnimation();
                        ivLiveSelfie.setImageDrawable(liveSelfieAnimation);
                        liveSelfieAnimation.start();
                    }
                }
            });
        }
//        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && liveSelfieAnimation != null) liveSelfieAnimation.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initView() {
        ivLiveSelfie = findViewById(R.id.ivLiveSelfie);
    }
}
