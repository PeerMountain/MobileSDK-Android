package com.peermountain.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.peermountain.core.model.guarded.FileDocument;
import com.peermountain.core.odk.utils.Collect;

import java.io.File;

/**
 * Created by Galeen on 10/26/2017.
 */

public class PmCoreUtils {
    /**
     * @param context Context
     * @param type    PmCoreConstants.FILE_TYPE_PDF is for document ,LOCAL_XFORM_DIR for XForms, other is for images
     * @return new empty file
     */
    public static File createLocalFile(Context context, int type) {
        return createLocalFile(context, null, type);
    }

    /**
     * @param context Context
     * @param type    PmCoreConstants.FILE_TYPE_PDF is for document ,LOCAL_XFORM_DIR FOR XFORMS , other is for images
     * @param name    File name without extension
     * @return new empty file
     */
    public static File createLocalFile(Context context, String name, int type) {
        return createLocalFile(context,null, name, type);
//        String dir, ext;
//        switch (type) {
//            case PmCoreConstants.FILE_TYPE_PDF:
//                dir = PmCoreConstants.LOCAL_DOCUMENTS_DIR;
//                ext = ".pdf";
//                break;
//            default:
//                dir = PmCoreConstants.LOCAL_IMAGE_DIR;
//                ext = ".jpg";
//        }
//        File path = new File(context.getFilesDir(), dir);
//        path.mkdirs();
//        if (name == null) {
//            name = System.currentTimeMillis() + "";
//        }
//        File file = new File(path, name + ext);
//        return file;
    }

    /**
     * @param context Context
     * @param type    PmCoreConstants.FILE_TYPE_PDF is for document ,LOCAL_XFORM_DIR FOR XFORMS , other is for images
     * @param url    will use last content after / for File name without extension
     * @return new empty file
     */
    public static File createLocalFileFromUrl(Context context, String url, int type) {
        return createLocalFile(context,null, url.substring(url.lastIndexOf("/") + 1,url.length()), type);
    }

    public static File createLocalFile(Context context,String dirName, String name, int type) {
        String dir, ext;
        switch (type) {
            case PmCoreConstants.FILE_TYPE_PDF:
                dir = PmCoreConstants.LOCAL_DOCUMENTS_DIR+(dirName!=null?"/"+dirName:"");
                ext = ".pdf";
                break;
            case PmCoreConstants.FILE_TYPE_XFORM:
                dir = Collect.SHORT_FORMS_PATH+(dirName!=null?"/"+dirName:"");
                ext = ".xml";
                break;
            default:
                dir = PmCoreConstants.LOCAL_IMAGE_DIR+(dirName!=null?"/"+dirName:"");
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
