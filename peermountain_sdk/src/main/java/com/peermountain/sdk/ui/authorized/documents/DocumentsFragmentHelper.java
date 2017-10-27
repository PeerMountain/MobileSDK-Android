package com.peermountain.sdk.ui.authorized.documents;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.model.guarded.FileDocument;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.FileUtils;
import com.peermountain.core.utils.ImageUtils;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PmCoreConstants;
import com.peermountain.core.utils.PmCoreUtils;
import com.peermountain.sdk.R;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Galeen on 10/27/2017.
 */

public class DocumentsFragmentHelper {
    public static final int REQUEST_CODE_SELECT_FILE = 533;
    private AppDocument documentToUpdate;

    private File localFile, documentImageFile;
    private Events callback;

    public DocumentsFragmentHelper(Events callback) {
        this.callback = callback;
        if(callback==null || callback.getActivity()==null){
            throw new NullPointerException("callback and callback.getActivity() must not be null !");
        }
    }

    public void updateDocument(AppDocument documentToUpdate) {
        this.documentToUpdate = documentToUpdate;
        if (documentToUpdate.isIdentityDocument()) {
            // TODO: 10/27/2017 show scanId frag and get data
        } else {
            PmCoreUtils.browseDocuments(getActivity(), REQUEST_CODE_SELECT_FILE);
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean handled = false;
        switch (requestCode) {
            case REQUEST_CODE_SELECT_FILE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    LogUtils.d("selected file", data.getData().toString());
                    try {
                        ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(data.getData(), "r");
                        createLocalFile(pfd,data);
                        copyFile(pfd);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        showUpdateFileError();
                    }
                }
                handled = true;
                break;
        }
        return handled;
    }

    public ArrayList<AppDocument> getDocumentsForAdapter() {
        ArrayList<AppDocument> documents = new ArrayList<>();
        ArrayList<AppDocument> docs = PeerMountainManager.getDocuments();
        if (docs != null && docs.size() > 0) {//we have saved docs
            documents.addAll(docs);
//            PeerMountainManager.saveDocuments(null);
        } else {
            AppDocument myID = new AppDocument(getActivity().getString(R.string.pm_document_item_id_title));
            Profile me = PeerMountainManager.getProfile();
            if (me != null && me.getDocuments().size() > 0) {
                myID.getDocuments().add(me.getDocuments().get(0));
            }
            documents.add(myID);
            documents.add(new AppDocument(R.drawable.pm_birther, "Birth Certificate"));
            documents.add(new AppDocument(R.drawable.pm_employment_contract, "Employment Contract"));
            documents.add(new AppDocument(R.drawable.pm_income_tax, "Tax Return"));
            PeerMountainManager.saveDocuments(documents);
        }
        return documents;
    }

    private void copyFile(ParcelFileDescriptor pfd) {
        FileUtils.copyFileAsync(pfd, localFile, new FileUtils.CopyFileEvents() {
            @Override
            public void onFinish(boolean isSuccess) {
                LogUtils.d("selected file", "onFinish " + isSuccess);
                if (isSuccess) {
                    if(localFile.getName().endsWith("pdf")){
                        try {
                            makeThumbnail(getActivity().getContentResolver().openFileDescriptor(Uri.fromFile(localFile), "r"));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        };
                    }else {
                        updateDocument();
                    }
                } else {
                    showUpdateFileError();
                }
            }
        });
    }

    private void createLocalFile( ParcelFileDescriptor pfd,Intent data) {
        String ext = "pdf";
        //if we support other types than image and pdf , better check against */* and check the result
        String[] types = FileUtils.getMimeTypes(getActivity(),data.getData());
        if (types!=null && types.length>0 && types[0].endsWith("pdf")){
            localFile = PmCoreUtils.createLocalFile(getActivity(), PmCoreConstants.FILE_TYPE_PDF);
        }else {
            ext = "jpg";
            localFile = PmCoreUtils.createLocalFile(getActivity(), PmCoreConstants.FILE_TYPE_IMAGES);
        }
        LogUtils.d("selected file", "extension : " + ext +"\nLocal file : "+localFile.toString());
    }

    private void makeThumbnail(ParcelFileDescriptor pfd) {
        Bitmap pageBitmap = getPdfPage(pfd,0);
        if(pageBitmap!=null){
            documentImageFile = PmCoreUtils.createLocalFile(getActivity(), PmCoreConstants.FILE_TYPE_IMAGES);
            LogUtils.d("documentImageFile",documentImageFile.toString());
            ImageUtils.saveImageAsync(documentImageFile, pageBitmap,
                    new ImageUtils.SaveImageEvents() {
                        @Override
                        public void onFinish(boolean isSuccess) {
                            LogUtils.d("saveImageAsync","result "+isSuccess);
                            if(isSuccess) updateDocument();
                        }
                    });
        }
    }

    private Bitmap getPdfPage(ParcelFileDescriptor fd,int pageNum) {
        PdfiumCore pdfiumCore = new PdfiumCore(getActivity());
        Bitmap bitmap = null;
        try {
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);

            pdfiumCore.openPage(pdfDocument, pageNum);

            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);

            // ARGB_8888 - best quality, high memory usage, higher possibility of OutOfMemoryError
            // RGB_565 - little worse quality, twice less memory usage
            bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.RGB_565);
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0,
                    width, height);

            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }

    private void showUpdateFileError() {
        Toast.makeText(getActivity(), "There was a problem", Toast.LENGTH_SHORT).show();
    }

    private void updateDocument() {
        if (documentToUpdate == null || localFile == null)
            return;
        FileDocument fileDocument = getFileDocument();
        Uri uri = Uri.fromFile(localFile);

        if (FileUtils.getExtension(uri.toString()).endsWith("pdf")) {
            fileDocument.setType(FileDocument.TYPE_PDF);
            fileDocument.setFileUri(uri.toString());
            if(documentImageFile!=null){
                fileDocument.setImageUri(Uri.fromFile(documentImageFile).toString());
            }
        } else {
            fileDocument.setType(FileDocument.TYPE_IMAGE);
            fileDocument.setImageUri(uri.toString());
            fileDocument.setFileUri(null);
        }

        if (callback != null) {
            callback.refreshAdapter();
        }
        PeerMountainManager.updateDocument(documentToUpdate);
    }

    private FileDocument getFileDocument() {
        FileDocument fileDocument;
        if (documentToUpdate.getFileDocuments().size() > 0
                && documentToUpdate.getFileDocuments().get(0) != null) {
            fileDocument = documentToUpdate.getFileDocuments().get(0);
        } else {
            fileDocument = new FileDocument();
            documentToUpdate.getFileDocuments().add(fileDocument);
            documentToUpdate.setRes(0);
        }
        return fileDocument;
    }

    private Activity getActivity(){
        return callback!=null?callback.getActivity():null;
    }

    public interface Events{
        void refreshAdapter();
        Activity getActivity();
    }
}
