package com.peermountain.core.odk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.peermountain.core.R;
import com.peermountain.core.network.BaseEvents;
import com.peermountain.core.network.MainCallback;
import com.peermountain.core.network.NetworkResponse;
import com.peermountain.core.odk.model.FormController;
import com.peermountain.core.odk.tasks.FormLoaderTask;
import com.peermountain.core.odk.utils.Collect;
import com.peermountain.core.odk.utils.TimerLogger;
import com.peermountain.core.odk.views.widgets.base.QuestionWidget;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PmCoreUtils;

import java.io.File;

public class XFormActivity extends AppCompatActivity implements XFormFragment.OnFragmentInteractionListener, QuestionWidget.Events {

    public static final String EXTRA_URL = "URL";

    public static void show(Context context, String xFormUrl) {
        Intent starter = new Intent(context, XFormActivity.class);
        starter.putExtra(EXTRA_URL, xFormUrl);
        context.startActivity(starter);
    }

    private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xform);
        progress = findViewById(R.id.progress);
        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url != null) {
            PeerMountainManager.downloadXForm(new DownloadXFormCallback(null, MainCallback.TYPE_NO_PROGRESS), url);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent mv) {
        if (fragment == null || !fragment.dispatchTouchEvent(mv)) {
            return super.dispatchTouchEvent(mv);
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(fragment!=null){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private int waitingPermissionRequestCode;
    private QuestionWidget.PermissionCallback waitingPermissionCallback;
    @Override
    public void requestPermission(String[] permissions,int requestCode,boolean isMandatory, QuestionWidget.PermissionCallback callback) {
        waitingPermissionCallback = callback;
        waitingPermissionRequestCode = requestCode;
        ActivityCompat.requestPermissions(this,permissions,requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == waitingPermissionRequestCode && waitingPermissionCallback!=null){
            waitingPermissionCallback.onPermission(grantResults);
        }
    }

    private XFormFragment fragment = null;

    private void loadXForm(File file) {
        PeerMountainManager.loadXForm(file, new FormLoaderTask.FormLoaderListener() {
            @Override
            public void loadingComplete(FormLoaderTask task) {
                LogUtils.d("ttt", "ttt " + task.getRequestCode());
                fragment = new XFormFragment();
                FormController formController = task.getFormController();
                Collect.getInstance().setFormController(formController);
                if (formController.getInstancePath() == null) {
                    // Create new answer folder.
//                    String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
//                            Locale.ENGLISH).format(Calendar.getInstance().getTime());
                    String file = task.formPath.substring(task.formPath.lastIndexOf('/') + 1,
                            task.formPath.lastIndexOf('.'));
//                    String path = Collect.INSTANCES_PATH + File.separator + file;
////                            + "_" + time;
//                    if (FileUtils.createFolder(path)) {
//                        File instanceFile = new File(path + File.separator + file+ ".xml");
////                                + "_" + time + ".xml");
//                        formController.setInstancePath(instanceFile);
//                    }
                    formController.setInstancePath(PmCoreUtils.getAnswersForXForm(file));
                    formController.getTimerLogger().logTimerEvent(TimerLogger.EventTypes.FORM_START, 0, null, false, true);
                }

                android.support.v4.app.FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.container, fragment);
                fr.commit();
                progress.setVisibility(View.GONE);
            }

            @Override
            public void loadingError(String errorMsg) {
                LogUtils.d("eee", "eee " + errorMsg);
            }
        });
    }

    private class DownloadXFormCallback extends MainCallback {
        // TODO: 2/12/18 add media file links
        DownloadXFormCallback(BaseEvents presenterCallback, int progressType) {
            super(presenterCallback, progressType);
        }

        @Override
        public void inTheEndOfDoInBackground(NetworkResponse networkResponse) {
            super.inTheEndOfDoInBackground(networkResponse);
            // TODO: 2/12/18 download media files
        }

        @Override
        public void onPostExecute(NetworkResponse networkResponse) {
            super.onPostExecute(networkResponse);
            if (networkResponse.file != null) {
                loadXForm(networkResponse.file);
            }
        }

        @Override
        public void onError(String msg, NetworkResponse networkResponse) {
            super.onError(msg, networkResponse);
            progress.setVisibility(View.GONE);
        }
    }
}
