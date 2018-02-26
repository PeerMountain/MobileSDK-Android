package com.peermountain.core.odk.utils;

import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.peermountain.core.odk.FormController;
import com.peermountain.core.persistence.PeerMountainManager;

import java.io.File;

/**
 * Created by Galeen on 1/25/2018.
 */

public class Collect {
    // Storage paths  /data/user/0/com.peermountain.dev/files/pm_xforms/
    public static final String ROOT_POSTFIX = File.separator + "pm_xforms";
    public static final String ODK_ROOT = PeerMountainManager.getApplicationContext().getFilesDir()
            + ROOT_POSTFIX;
    public static final String FORMS_PATH = ODK_ROOT + File.separator + "forms";
    public static final String SHORT_FORMS_PATH = ROOT_POSTFIX + File.separator + "forms";
    public static final String INSTANCES_PATH = ODK_ROOT + File.separator + "instances";
    public static final String CACHE_PATH = ODK_ROOT + File.separator + ".cache";
    public static final String METADATA_PATH = ODK_ROOT + File.separator + "metadata";
    public static final String TMPFILE_PATH = CACHE_PATH + File.separator + "tmp.jpg";
    public static final String TMPDRAWFILE_PATH = CACHE_PATH + File.separator + "tmpDraw.jpg";
    public static final String LOG_PATH = ODK_ROOT + File.separator + "log";
    public static final String DEFAULT_FONTSIZE = "14";
    public static final int DEFAULT_FONTSIZE_INT = 14;
    public static final String OFFLINE_LAYERS = ODK_ROOT + File.separator + "layers";
    public static final String SETTINGS = ODK_ROOT + File.separator + "settings";

    public static String defaultSysLanguage;

    private static Collect singleton = null;

    public static Collect getInstance() {
        if (singleton == null) {
            singleton = new Collect();
        }
        return singleton;
    }

    @Nullable
    private FormController formController = null;

    /**
     * Creates required directories on the SDCard (or other external storage)
     *
     * @throws RuntimeException if there is no SDCard or the directory exists as a non directory
     */
    public static void createODKDirs() throws RuntimeException {
//        String cardstatus = Environment.getExternalStorageState();
//        if (!cardstatus.equals(Environment.MEDIA_MOUNTED)) {
//            throw new RuntimeException(
//                    PeerMountainManager.getApplicationContext().getString(R.string.pm_err_sdcard_unmounted, cardstatus));
//        }

        String[] dirs = {
                ODK_ROOT, FORMS_PATH, INSTANCES_PATH, CACHE_PATH, METADATA_PATH, OFFLINE_LAYERS
        };

        for (String dirName : dirs) {
            File dir = new File(dirName);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new RuntimeException("ODK reports :: Cannot create directory: "
                            + dirName);
                }
            } else {
                if (!dir.isDirectory()) {
                    throw new RuntimeException("ODK reports :: " + dirName
                            + " exists, but is not a directory");
                }
            }
        }
    }

    /**
     * Predicate that tests whether a directory path might refer to an
     * ODK Tables instance data directory (e.g., for media attachments).
     */
    public static boolean isODKTablesInstanceDataDirectory(File directory) {
        /*
         * Special check to prevent deletion of files that
         * could be in use by ODK Tables.
         */
        String dirPath = directory.getAbsolutePath();
        if (dirPath.startsWith(Collect.ODK_ROOT)) {
            dirPath = dirPath.substring(Collect.ODK_ROOT.length());
            String[] parts = dirPath.split(File.separatorChar == '\\' ? "\\\\" : File.separator);
            // [appName, instances, tableId, instanceId ]
            if (parts.length == 4 && parts[1].equals("instances")) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public FormController getFormController() {
        return formController;
    }

    public void setFormController(@Nullable FormController controller) {
        formController = controller;
        // TODO: 2/26/18 serialize it
    }

    private static long lastClickTime;

    // Preventing multiple clicks, using threshold of 500 ms
    public static boolean allowClick() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        boolean allowClick = (lastClickTime == 0 || lastClickTime == elapsedRealtime) // just for tests
                || elapsedRealtime - lastClickTime > 500;
        if (allowClick) {
            lastClickTime = elapsedRealtime;
        }
        return allowClick;
    }

//    public static int getQuestionFontSize() {
//        if (PeerMountainManager.getPeerMountainConfig() == null) {
//            return Collect.DEFAULT_FONTSIZE_INT;
//        }
//
//        return PeerMountainManager.getPeerMountainConfig().getFontSize();
//    }

}
