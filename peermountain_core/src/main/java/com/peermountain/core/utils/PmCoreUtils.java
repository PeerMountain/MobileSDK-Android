package com.peermountain.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.peermountain.core.model.guarded.FileDocument;

import java.io.File;

/**
 * Created by Galeen on 10/26/2017.
 */

public class PmCoreUtils {
    /**
     * @param context Context
     * @param type    PmCoreConstants.FILE_TYPE_DOCUMENTS is for document , other is for images
     * @return new empty file
     */
    public static File createLocalFile(Context context, int type) {
        return createLocalFile(context, null, type);
    }

    /**
     * @param context Context
     * @param type    PmCoreConstants.FILE_TYPE_DOCUMENTS is for document , other is for images
     * @param name    File name without extension
     * @return new empty file
     */
    public static File createLocalFile(Context context, String name, int type) {
        String dir, ext;
        switch (type) {
            case PmCoreConstants.FILE_TYPE_DOCUMENTS:
                dir = PmCoreConstants.LOCAL_DOCUMENTS_DIR;
                ext = ".pdf";
                break;
            default:
                dir = PmCoreConstants.LOCAL_IMAGE_DIR;
                ext = ".jpg";
        }
        File path = new File(context.getFilesDir(), dir);
        path.mkdirs();
        if (name == null) {
            name = System.currentTimeMillis() + "";
        }
        File file = new File(path, name + ext);
        return file;
    }

    public static void browseDocuments(Activity activity, int requestCode) {
        String[] mimeTypes = {
                FileDocument.TYPE_IMAGE,
                FileDocument.TYPE_PDF
        };
//                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
//                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
//                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
//                        "text/plain",
//                        "application/pdf",
//                        "application/zip"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        setMimeTypes(mimeTypes, intent);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(Intent.createChooser(intent, "ChooseFile"), requestCode);
        }else {
            mimeTypes = new String[]{
                    FileDocument.TYPE_IMAGE
            };
            setMimeTypes(mimeTypes, intent);
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(Intent.createChooser(intent, "ChooseFile"), requestCode);
            }else {
                mimeTypes = new String[]{
                        FileDocument.TYPE_PDF
                };
                setMimeTypes(mimeTypes, intent);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivityForResult(Intent.createChooser(intent, "ChooseFile"), requestCode);
                }else {
                    // TODO: 10/26/2017 show error
                }
            }
        }
    }

    private static void setMimeTypes(String[] mimeTypes, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
    }

}
