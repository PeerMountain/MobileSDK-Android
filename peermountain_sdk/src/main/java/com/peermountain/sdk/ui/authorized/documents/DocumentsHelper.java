package com.peermountain.sdk.ui.authorized.documents;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterface;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterfaceCallback;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTImageResult;
import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.model.guarded.DocumentID;
import com.peermountain.core.model.guarded.FileDocument;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.FileUtils;
import com.peermountain.core.utils.ImageUtils;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PmCoreConstants;
import com.peermountain.core.utils.PmCoreUtils;
import com.peermountain.sdk.R;
import com.peermountain.sdk.utils.DialogUtils;
import com.peermountain.sdk.utils.DocumentUtils;
import com.peermountain.sdk.utils.PeerMountainSdkConstants;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Galeen on 10/27/2017.
 */

public class DocumentsHelper {
    public static final int REQUEST_CODE_SELECT_FILE = 533;
    public static final int REQUEST_SCAN_ID = 633;
    private AppDocument documentToUpdate;

    private File localFile, documentImageFile;
    private Events callback;

    public static Boolean checkPermissionsForScanId(Context context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            final boolean permissionsAllowed =
                    AXTCaptureInterface.INSTANCE.verifyPermissions(requestCode, permissions, grantResults) && ContextCompat.checkSelfPermission(context,
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
            if (permissionsAllowed) {
                return true;
            } else {
                return false;
            }
        }
        return null;
    }

    public DocumentsHelper(Events callback) {
        this.callback = callback;
        if (callback == null || callback.getActivity() == null) {
            throw new NullPointerException("callback and callback.getActivity() must not be null !");
        }
    }

    public void updateDocument(AppDocument documentToUpdate) {
        this.documentToUpdate = documentToUpdate;
        if (documentToUpdate.isIdentityDocument()) {
//            scanId();
        } else {
            PmCoreUtils.browseDocuments(getActivity(), REQUEST_CODE_SELECT_FILE);
        }
    }

    private void scanId() {
        if(callback!=null) callback.onScanSDKLoading(true);
        if (PeerMountainManager.isScanIdSDKReady()) {
            if (getFragment() != null) {
                PeerMountainManager.scanId(getFragment(), REQUEST_SCAN_ID);
            } else {
                PeerMountainManager.scanId(getActivity(), REQUEST_SCAN_ID);
            }
        } else {
            initScanIdSDK();
        }
    }

    private void initScanIdSDK() {
        PeerMountainManager.initScanSDK(getActivity(), new AXTCaptureInterfaceCallback() {
            @Override
            public void onInitSuccess() {
                scanId();
            }

            @Override
            public void onInitError() {
                // TODO: 10/27/2017 show error
                if(callback!=null) callback.onScanSDKLoading(false);
            }
        });
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean handled = false;
        switch (requestCode) {
            case REQUEST_CODE_SELECT_FILE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    LogUtils.d("selected file", data.getData().toString());
                    try {
                        ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(data.getData(), "r");
                        createLocalFile(pfd, data);
                        copyFile(pfd);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        showUpdateFileError();
                    }
                }
                handled = true;
                break;
            case REQUEST_SCAN_ID:
                if(callback!=null) callback.onScanSDKLoading(false);
                if (resultCode == Activity.RESULT_OK
                        || PeerMountainSdkConstants.isFake) {
                    handleIdDocumentData(data);
                } else {
                    Toast.makeText(getActivity(), R.string.pm_err_msg_scan_data, Toast.LENGTH_SHORT).show();
                }
                handled = true;
                break;
        }
        return handled;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (DocumentsHelper.checkPermissionsForScanId(getActivity(), requestCode, permissions, grantResults)) {
            initScanIdSDK();
        } else {
            DialogUtils.showChoiceDialog(getActivity(), -1, R.string.pm_err_msg_permission_scan_id,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            initScanIdSDK();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }, R.string.pm_btn_ask_for_permission_again, R.string.btn_refuse_permission);
        }

    }

    public ArrayList<AppDocument> getDocumentsForAdapter() {
        ArrayList<AppDocument> docs = PeerMountainManager.getDocuments();
        if (docs != null && docs.size() > 0) {//we have saved docs
            return docs;
//            PeerMountainManager.saveDocuments(null);
        } else {
            ArrayList<AppDocument> documents = new ArrayList<>();
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
            return documents;
        }
    }

    private void copyFile(ParcelFileDescriptor pfd) {
        FileUtils.copyFileAsync(pfd, localFile, new FileUtils.CopyFileEvents() {
            @Override
            public void onFinish(boolean isSuccess) {
                LogUtils.d("selected file", "onFinish " + isSuccess);
                if (isSuccess) {
                    if (localFile.getName().endsWith("pdf")) {
                        try {
                            makeThumbnail(getActivity().getContentResolver().openFileDescriptor(Uri.fromFile(localFile), "r"));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        ;
                    } else {
                        updateDocument();
                    }
                } else {
                    showUpdateFileError();
                }
            }
        });
    }

    private void createLocalFile(ParcelFileDescriptor pfd, Intent data) {
        String ext = "pdf";
        //if we support other types than image and pdf , better check against */* and check the result
        String[] types = FileUtils.getMimeTypes(getActivity(), data.getData());
        if (types != null && types.length > 0 && types[0].endsWith("pdf")) {
            localFile = PmCoreUtils.createLocalFile(getActivity(), PmCoreConstants.FILE_TYPE_PDF);
        } else {
            ext = "jpg";
            localFile = PmCoreUtils.createLocalFile(getActivity(), PmCoreConstants.FILE_TYPE_IMAGES);
        }
        LogUtils.d("selected file", "extension : " + ext + "\nLocal file : " + localFile.toString());
    }

    private void makeThumbnail(ParcelFileDescriptor pfd) {
        Bitmap pageBitmap = getPdfPage(pfd, 0);
        if (pageBitmap != null) {
            documentImageFile = PmCoreUtils.createLocalFile(getActivity(), PmCoreConstants.FILE_TYPE_IMAGES);
            LogUtils.d("documentImageFile", documentImageFile.toString());
            ImageUtils.saveImageAsync(documentImageFile, pageBitmap,
                    new ImageUtils.SaveImageEvents() {
                        @Override
                        public void onFinish(boolean isSuccess) {
                            LogUtils.d("saveImageAsync", "result " + isSuccess);
                            if (isSuccess) updateDocument();
                        }
                    });
        }
    }

    private Bitmap getPdfPage(ParcelFileDescriptor fd, int pageNum) {
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
        //delete old files
        deleteFile(fileDocument.getImageUri());
        deleteFile(fileDocument.getFileUri());

        if (FileUtils.getExtension(uri.toString()).endsWith("pdf")) {
            fileDocument.setType(FileDocument.TYPE_PDF);
            fileDocument.setFileUri(uri.toString());
            if (documentImageFile != null) {
                fileDocument.setImageUri(Uri.fromFile(documentImageFile).toString());
            }else{
                fileDocument.setImageUri(null);
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

    private int sidesDone;
    private DocumentID documentId;

    private void handleIdDocumentData(Intent scannedData) {
        if (scannedData != null) {
            documentId = DocumentUtils.getScannedData(scannedData);
            if (documentId == null) return;
            sidesDone = 0;
            if (documentId.getImageCropped() != null) sidesDone++;
            if (documentId.getImageCroppedBack() != null) sidesDone++;

            resizeIdImages(getActivity(), documentId,
                    new SizeImageEventCallback(true),
                    new SizeImageEventCallback(false));
        }
    }

    private void updateIdDocument() {
        sidesDone--;
        if (sidesDone == 0) {
            if (documentToUpdate == null || documentId == null) return;
            if (documentToUpdate.getDocuments().size() > 0) {
                //don't delete document images, because the SDK override them it self and is the same uri
//                DocumentID oldDocument = documentToUpdate.getDocuments().get(0);
//                deleteDocumentImages(oldDocument);
                documentToUpdate.getDocuments().clear();//replace current
            }
            documentToUpdate.getDocuments().add(documentId);

            if (callback != null) {
                callback.refreshAdapter();
            }
            PeerMountainManager.updateDocument(documentToUpdate);
        }
    }

    private void deleteDocumentImages(DocumentID oldDocument) {
        deleteFile(oldDocument.getImageCropped());
        deleteFile(oldDocument.getImageCroppedBack());
        deleteFile(oldDocument.getImageCroppedSmall());
        deleteFile(oldDocument.getImageCroppedBackSmall());
        deleteFile(oldDocument.getImageFace());
        deleteFile(oldDocument.getImageSource());
        deleteFile(oldDocument.getImageSourceBack());
    }

    private void deleteFile(AXTImageResult image) {
        if(image!=null){
            deleteFile(image.getImageUri());
        }
    }

    private void deleteFile(String uri) {
        if(!TextUtils.isEmpty(uri)){
            File file = new File(Uri.parse(uri).getPath());
            if(file.exists()) file.delete();
        }
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

    @NonNull
    private Activity getActivity() {
        return callback != null ? callback.getActivity() : null;
    }

    private Fragment getFragment() {
        return callback != null ? callback.getFragment() : null;
    }

    public interface Events {
        void refreshAdapter();

        Activity getActivity();

        Fragment getFragment();

        void onScanSDKLoading(boolean loading);
    }

    public static void resizeIdImages(Context context, final DocumentID document, final SizeImageEventCallback callbackFront, final SizeImageEvent callbackBack) {
        if (document != null) {
            int size = context.getResources().getDimensionPixelSize(R.dimen.pm_id_size);
            processIdImage(context, size, document.getImageCropped(), System.currentTimeMillis() + "", callbackFront);
            processIdImage(context, size, document.getImageCroppedBack(), System.currentTimeMillis() + 1 + "", callbackBack);
        }
    }

    private static void processIdImage(Context context, int size, AXTImageResult image, String name, final SizeImageEvent callback) {
        if (image != null && image.getWidth() > size) {
            File originalImage = new File(Uri.parse(image.getImageUri()).getPath());
            File newSmallerImage = PmCoreUtils.createLocalFile(context, name, PmCoreConstants.FILE_TYPE_IMAGES);

            ImageUtils.rotateAndResizeImageAsync(originalImage, newSmallerImage, size,
                    size / 2, false, false, new ImageUtils.ConvertImageTask.ImageCompressorListener() {
                        @Override
                        public void onImageCompressed(Bitmap bitmap, Uri uri) {
                            AXTImageResult image = new AXTImageResult();
                            image.setImageUri(uri.toString());
                            if (callback != null) {
                                callback.onSized(image);
                            }
                        }

                        @Override
                        public void onError() {
                            LogUtils.d("onSized", "error");
                            if (callback != null) {
                                callback.onError();
                            }
                        }
                    }
            );
        } else {
            if (callback != null) {
                callback.onSized(null);
            }
        }
    }

    public interface SizeImageEvent {
        void onSized(AXTImageResult image);

        void onError();
    }

    private class SizeImageEventCallback implements SizeImageEvent {
        private boolean isFront;

        public SizeImageEventCallback(boolean isFront) {
            this.isFront = isFront;
        }

        @Override
        public void onSized(AXTImageResult image) {
            if (isFront) {
                documentId.setImageCroppedSmall(image);
            } else {
                documentId.setImageCroppedBackSmall(image);
            }
            updateIdDocument();
        }

        @Override
        public void onError() {
            updateIdDocument();
        }
    }
}
