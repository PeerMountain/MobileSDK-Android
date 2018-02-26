package com.peermountain.core.odk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.peermountain.core.R;
import com.peermountain.core.network.BaseEvents;
import com.peermountain.core.network.MainCallback;
import com.peermountain.core.network.NetworkResponse;
import com.peermountain.core.odk.tasks.FormLoaderTask;
import com.peermountain.core.odk.utils.Collect;
import com.peermountain.core.odk.utils.FileUtils;
import com.peermountain.core.odk.utils.TimerLogger;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class XFormActivity extends AppCompatActivity implements XFormFragment.OnFragmentInteractionListener{

    public static final String EXTRA_URL = "URL";//"https://www.dropbox.com/s/9kj12067gqhst42/Sample%20Form.xml?dl=1"

    public static void show(Context context, String xFormUrl) {
        Intent starter = new Intent(context, XFormActivity.class);
        starter.putExtra(EXTRA_URL,xFormUrl);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xform);
        String url = getIntent().getStringExtra(EXTRA_URL);
        if(url!=null){
            PeerMountainManager.downloadXForm(new DownloadXFormCallback(null,MainCallback.TYPE_NO_PROGRESS),url);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent mv) {
        if (fragment==null || !fragment.dispatchTouchEvent(mv)) {
            return super.dispatchTouchEvent(mv);
        }else{
            return true;
        }
    }

    private XFormFragment fragment = null;
    private void loadXForm(File file) {
        PeerMountainManager.loadXForm(file,new FormLoaderTask.FormLoaderListener() {
            @Override
            public void loadingComplete(FormLoaderTask task) {
                LogUtils.d("ttt","ttt "+ task.getRequestCode());
                fragment = new XFormFragment();
                FormController formController = task.getFormController();
                Collect.getInstance().setFormController(formController);
                if (formController.getInstancePath() == null) {

                    // Create new answer folder.
                    String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
                            Locale.ENGLISH).format(Calendar.getInstance().getTime());
                    String file = task.formPath.substring(task.formPath.lastIndexOf('/') + 1,
                            task.formPath.lastIndexOf('.'));
                    String path = Collect.INSTANCES_PATH + File.separator + file + "_"
                            + time;
                    if (FileUtils.createFolder(path)) {
                        File instanceFile = new File(path + File.separator + file + "_" + time + ".xml");
                        formController.setInstancePath(instanceFile);
                    }

                    formController.getTimerLogger().logTimerEvent(TimerLogger.EventTypes.FORM_START, 0, null, false, true);
                }

                android.support.v4.app.FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.container,fragment);
                fr.commit();
            }

            @Override
            public void loadingError(String errorMsg) {
                LogUtils.d("eee","eee "+ errorMsg);
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
            if(networkResponse.file!=null){
                loadXForm(networkResponse.file);
            }
        }
    }
}
