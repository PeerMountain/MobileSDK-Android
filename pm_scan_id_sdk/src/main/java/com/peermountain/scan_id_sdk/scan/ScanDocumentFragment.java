package com.peermountain.scan_id_sdk.scan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Flash;
import com.peermountain.common.utils.PmDialogUtils;
import com.peermountain.pm_livecycle.base.BaseFragment;
import com.peermountain.scan_id_sdk.R;

import java.io.File;
import java.util.ArrayList;


public class ScanDocumentFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ScanDocumentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanDicumentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanDocumentFragment newInstance(String param1, String param2) {
        ScanDocumentFragment fragment = new ScanDocumentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private CameraView camera;
    private ImageView btnRecord;
    private Button btnDone, btnFlash;
    private ProgressBar progress;
    private TextView tvMsg;
//    private IdentityDocumentViewModel viewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_dicument, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initCamera();
        setListeners();
    }

    private boolean permissionRejected = false;

    @Override
    public void onResume() {
        super.onResume();
        if (!permissionRejected) camera.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    public void onDetach() {
        camera.destroy();
        super.onDetach();
        mListener = null;
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
            PmDialogUtils.showChoiceDialog(getActivity(), -1, R.string.pm_ask_for_permission_again_camera,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            camera.start();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mListener != null) mListener.onPermissionRefused();
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


    private void initView(View view) {
        btnDone = view.findViewById(R.id.btnDone);
        camera = view.findViewById(R.id.camera);
        btnRecord = view.findViewById(R.id.btnRecord);
        progress = view.findViewById(R.id.progress);
        tvMsg = view.findViewById(R.id.tvMsg);
        btnFlash = view.findViewById(R.id.btnFlash);
        btnFlash.setVisibility(View.VISIBLE);
        idImages = new Bitmap[2];
        camera.setPlaySounds(true);
        tvMsg.setVisibility(View.VISIBLE);
        setViewForIdStatus();
    }

    private void setViewForIdStatus() {
        if (idImages[0] == null) {//take first image
            tvMsg.setText(R.string.pm_msg_capture_mrz);
            btnDone.setVisibility(View.GONE);
        } else {//take second image
            tvMsg.setText(R.string.pm_msg_capture_not_mrz);
            btnDone.setVisibility(View.VISIBLE);
        }
        onOpened();
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
        btnDone.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);

        new PmScanIdentityDocumentHelper(new PmScanIdentityDocumentHelper.Events() {
            @Override
            public void onID_Ready(ArrayList<File> files) {
                if (mListener != null) mListener.onID_Ready(files);
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

    public void resetScanning() {
        idImages = new Bitmap[2];
        camera.setVisibility(View.VISIBLE);
        if (!permissionRejected) camera.start();
        setViewForIdStatus();
    }

    public interface OnFragmentInteractionListener {
        void onID_Ready(ArrayList<File> files);

        void onPermissionRefused();
    }
}
