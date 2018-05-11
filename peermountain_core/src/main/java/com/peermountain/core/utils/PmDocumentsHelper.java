package com.peermountain.core.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.ariadnext.android.smartsdk.exception.CaptureApiException;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterface;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterfaceCallback;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentAbstract;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentChip;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentIdentity;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentValidityResult;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTSdkResult;
import com.peermountain.core.R;
import com.peermountain.core.camera.CameraActivity;
import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.model.guarded.DocumentID;
import com.peermountain.core.model.guarded.FileDocument;
import com.peermountain.core.model.guarded.ImageResult;
import com.peermountain.core.persistence.PeerMountainManager;
import com.peermountain.core.utils.constants.PeerMountainCoreConstants;
import com.peermountain.core.utils.constants.PmCoreConstants;
import com.peermountain.core.utils.constants.PmRequestCodes;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Galeen on 10/27/2017.
 */

public class PmDocumentsHelper {
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

    public PmDocumentsHelper(Events callback) {
        this.callback = callback;
        if (callback == null || callback.getActivity() == null) {
            throw new NullPointerException("callback and callback.getActivity() must not be null !");
        }
    }

    public void updateDocument(AppDocument documentToUpdate) {
        this.documentToUpdate = documentToUpdate;
        if (documentToUpdate.isIdentityDocument()) {
            scanId();
        } else {
            PmCoreUtils.browseDocuments(getActivity(), PmRequestCodes.REQUEST_CODE_SELECT_FILE);
        }
    }

    public boolean addDocument(AppDocument documentToUpdate) {
        this.documentToUpdate = documentToUpdate;
        if (documentToUpdate.isID()) {
            scanId();
        } else {
            return PmCoreUtils.browseDocuments(getActivity(), PmRequestCodes.REQUEST_CODE_SELECT_FILE);
        }
        return true;
    }

    private void scanId() {
        if (callback != null) callback.onScanSDKLoading(true);

        if (PeerMountainCoreConstants.isFake) {
            handleIdDocumentData(new Intent());
            if (callback != null) callback.onScanSDKLoading(false);
            return;
        }

        CameraActivity.show(getActivity(), true, PmRequestCodes.REQUEST_SCAN_ID);
//        if (PeerMountainManager.isScanIdSDKReady()) {
//            if (getFragment() != null) {
//                PeerMountainManager.scanId(getFragment(), PmRequestCodes.REQUEST_SCAN_ID);
//            } else {
//                PeerMountainManager.scanId(getActivity(), PmRequestCodes.REQUEST_SCAN_ID);
//            }
//        } else {
//            initScanIdSDK();
//        }
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
                if (callback != null) callback.onScanSDKLoading(false);
            }
        });
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean handled = false;
        switch (requestCode) {
            case PmRequestCodes.REQUEST_CODE_SELECT_FILE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    LogUtils.d("selected file", data.getData().toString());
                    try {
                        ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(data.getData(), "r");
                        createLocalFile(pfd, data);
                        copyFile(pfd);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        showUpdateFileError();
                        onCancelDocument();
                    }
                } else {
                    onCancelDocument();
                }
                handled = true;
                break;
            case PmRequestCodes.REQUEST_SCAN_ID:
                if (callback != null) callback.onScanSDKLoading(false);
                if ((resultCode == Activity.RESULT_OK && CameraActivity.idImages != null
                        && CameraActivity.idImages[0] != null)
                        || PeerMountainCoreConstants.isFake) {
                    handleIdDocumentData(data);
                } else {
                    Toast.makeText(getActivity(), R.string.pm_err_msg_scan_data, Toast.LENGTH_SHORT).show();
                    onCancelDocument();
                }
                handled = true;
                break;
        }
        return handled;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (PmDocumentsHelper.checkPermissionsForScanId(getActivity(), requestCode, permissions, grantResults)) {
//            initScanIdSDK();
//        } else {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), Build.VERSION.SDK_INT >= 22 ? android.R.style.Theme_DeviceDefault_Dialog_Alert :
//                    android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
//            dialog.setCancelable(false);
//            dialog.setMessage(R.string.pm_err_msg_permission_scan_id);
//            dialog.setPositiveButton(R.string.pm_btn_ask_for_permission_again, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    initScanIdSDK();
//                }
//            });
//            dialog.setNegativeButton(R.string.pm_btn_refuse_permission, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            AlertDialog alertDialog = dialog.create();
////        alertDialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
//            alertDialog.show();
////            DialogUtils.showChoiceDialog(getActivity(), -1, R.string.pm_err_msg_permission_scan_id,
////                    new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialogInterface, int i) {
////                            initScanIdSDK();
////                        }
////                    }, new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialogInterface, int i) {
////                            dialogInterface.dismiss();
////                        }
////                    },
////                    R.string.pm_btn_ask_for_permission_again,
////                    R.string.btn_refuse_permission);
//        }

    }

    public ArrayList<AppDocument> getDocumentsForAdapter() {
        return PeerMountainManager.getDocuments();
//        ArrayList<AppDocument> documents = new ArrayList<>();
//        ArrayList<AppDocument> docs = PeerMountainManager.getDocuments();
//        if (docs != null && docs.size() > 0) {//we have saved docs
//            if(docs.size()<4){
//                documents.addAll(docs);
//            }else{
//                return docs;
//            }
////            PeerMountainManager.saveDocuments(null);
//        }
//        AppDocument myID = new AppDocument(getActivity().getString(R.string.pm_document_item_id_title));
//        Profile me = PeerMountainManager.getProfile();
//        if (me != null && me.getDocuments().size() > 0) {
//            myID.getDocuments().add(me.getDocuments().get(0));
//        }
//        documents.add(myID);
//        documents.add(new AppDocument(R.drawable.pm_birther, "Birth Certificate"));
//        documents.add(new AppDocument(R.drawable.pm_employment_contract, "Employment Contract"));
//        documents.add(new AppDocument(R.drawable.pm_income_tax, "Tax Return"));
//        documents.add(new AppDocument(true));
//        PeerMountainManager.saveDocuments(documents);
//        return documents;

    }

    private void onCancelDocument() {
        if (callback != null && documentToUpdate.isShouldAdd()) callback.onAddingDocumentCanceled(documentToUpdate);
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
                        updateFileDocument();
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
                            if (isSuccess) updateFileDocument();
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

    private void updateFileDocument() {
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
            } else {
                fileDocument.setImageUri(null);
            }
        } else {
            fileDocument.setType(FileDocument.TYPE_IMAGE);
            fileDocument.setImageUri(uri.toString());
            fileDocument.setFileUri(null);
        }
        if (documentToUpdate.isEmpty() || TextUtils.isEmpty(documentToUpdate.getTitle())) {
            documentToUpdate.setEmpty(false);
            showSetNameDialog();
        } else {
            onDocumentDone();
        }
    }

    private void onDocumentDone() {
        if (callback != null) {
            callback.refreshAdapter();
        }
        if (documentToUpdate.isShouldAdd()) {
            documentToUpdate.setShouldAdd(false);
            PeerMountainManager.addDocument(documentToUpdate);
        } else {
            PeerMountainManager.updateDocument(documentToUpdate);
        }
    }

    private AlertDialog nameDialog;

    private void showSetNameDialog() {
        if (callback.getActivity() == null) return;
        final AlertDialog.Builder dialog = new AlertDialog.Builder(callback.getActivity());
        dialog.setCancelable(false);
        LayoutInflater vi = (LayoutInflater) callback.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = vi.inflate(R.layout.pm_set_file_title_dialog, null);
        final EditText etDocName = view.findViewById(R.id.etDocName);
        View btnCancel = view.findViewById(R.id.btnCancel);
        View btnDone = view.findViewById(R.id.btnDone);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameDialog.dismiss();
                onDocumentDone();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentToUpdate.setTitle(etDocName.getText().toString());
                nameDialog.dismiss();
                onDocumentDone();
            }
        });
        dialog.setView(view);
        nameDialog = dialog.create();
        nameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        nameDialog.show();
    }

    private int sidesDone;
    private DocumentID documentId;

    private void handleIdDocumentData(Intent scannedData) {
        //save files and send
        new PmLiveSelfieHelper(true, new PmLiveSelfieHelper.Events() {
            @Override
            public void onLiveSelfieReady(ArrayList<String> liveSelfie) {
                if (liveSelfie == null || liveSelfie.size() == 0) return;
                ArrayList<File> files = new ArrayList<>();

                Uri uri = Uri.parse(liveSelfie.get(0));
                File file = new File(uri.getPath());

                if (liveSelfie.size() > 1) {
                    uri = Uri.parse(liveSelfie.get(1));
                    File file2 = new File(uri.getPath());
                    files.add(file2);//this is front ID
                }
                files.add(file);//this is MRZ, if there is front is added before MRZ
                if (callback != null) {
                    callback.ocrId(files);
                } else {
                    for (File file1 : files) {
                        file1.delete();
                    }
                }
//                if (documentToUpdate.isIdentityDocument()) {// update
//                    documentId = documentToUpdate.getDocuments().get(0);
//                    documentId.deleteDocumentImages();
//                    addIDImagesToDocument(files, documentId);
//                    resizeDocumentIdImages();
//                } else {// new document
////                    sendFiles(files);
//                }
            }
        }).saveID();
    }

    /**
     * @param documentID to process
     * @return result is error message , if null no errors
     */
    public String onIdScanResult(DocumentID documentID) {
        if (documentID == null || documentID.getErrorMessage() != null) {//|| !documentID.isValid(), !documentID.checkIsValid()
            String msg;
            if (documentID != null) {
                documentID.deleteDocumentImages();
                msg = documentID.getErrorMessage();
            } else {
                msg = PeerMountainManager.getApplicationContext().getString(R.string.pm_err_msg_scan_data);
            }
            if (callback != null) callback.onAddingDocumentCanceled(documentToUpdate);
            return msg;
        } else {
            this.documentId = documentID;
            resizeDocumentIdImages();
            return null;
        }
    }

    private void resizeDocumentIdImages() {
        if (documentId == null) return;
        sidesDone = 4;
//            if (documentId.getImageCropped() != null) sidesDone+=2;//first make a smaller image, then copy and delete the original
//            if (documentId.getImageCroppedBack() != null) sidesDone+=2;

        resizeIdImages(getActivity(), documentId,
                new SizeImageEventCallback(true, false),
                new SizeImageEventCallback(false, false),
                new SizeImageEventCallback(true, true),
                new SizeImageEventCallback(false, true));
    }

    private void updateIdDocument() {
        sidesDone--;
        if (sidesDone == 0) {
            if (documentToUpdate == null || documentId == null) return;
            if (documentToUpdate.isIdentityDocument()) {//update
                DocumentID oldDocument = documentToUpdate.getDocuments().get(0);
                oldDocument.deleteDocumentImages();
                documentToUpdate.getDocuments().clear();//replace current
            }
            documentToUpdate.getDocuments().add(documentId);

            documentToUpdate.setEmpty(false);
            onDocumentDone();
        }
    }

    private void deleteFile(String uri) {
        if (!TextUtils.isEmpty(uri)) {
            File file = new File(Uri.parse(uri).getPath());
            if (file.exists()) file.delete();
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

    public static void addIDImagesToDocument(ArrayList<File> filesToSend, DocumentID documentID) {
        if (filesToSend != null && documentID != null && documentID.getErrorMessage() == null) {
            if (filesToSend.size() == 1) {
                documentID.setImageCropped(new ImageResult(Uri.fromFile(filesToSend.get(0)).toString()));
            } else {
                documentID.setImageCropped(new ImageResult(Uri.fromFile(filesToSend.get(1)).toString()));
                documentID.setImageCroppedBack(new ImageResult(Uri.fromFile(filesToSend.get(0)).toString()));
            }
        }
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

        void onAddingDocumentCanceled(AppDocument document);

        void ocrId(ArrayList<File> images);
    }

    public static void resizeIdImages(Context context, final DocumentID document, final SizeImageEvent callbackFront, final SizeImageEvent callbackBack, final SizeImageEvent callbackMoveFront, final SizeImageEvent callbackMoveBack) {
        if (document != null) {
            int size = context.getResources().getDimensionPixelSize(R.dimen.pm_id_size);
            processIdImage(context, size, document.getImageCropped(), System.currentTimeMillis() + "", document.getDocNumber(), callbackFront, callbackMoveFront);
            processIdImage(context, size, document.getImageCroppedBack(), System.currentTimeMillis() + 1 + "", document.getDocNumber(), callbackBack, callbackMoveBack);
        }
    }

    private static void processIdImage(final Context context, int size, final ImageResult image, String name, final String idNum, final SizeImageEvent callback, final SizeImageEvent callbackMove) {
        if (image != null) {
            final File originalImage = new File(Uri.parse(image.getImageUri()).getPath());
            File newSmallerImage = PmCoreUtils.createLocalFile(context, idNum, name, PmCoreConstants.FILE_TYPE_IMAGES);

            ImageUtils.rotateAndResizeImageAsync(originalImage, newSmallerImage, size,
                    size / 2, false, false, new ImageUtils.ConvertImageTask.ImageCompressorListener() {
                        @Override
                        public void onImageCompressed(Bitmap bitmap, Uri uri) {
                            ImageResult imageResult = new ImageResult();
                            imageResult.setImageUri(uri.toString());
                            if (callback != null) {
                                callback.onSized(imageResult);
                            }
                            moveIdImage(context, image, System.currentTimeMillis() + "",
                                    idNum, callbackMove);
                        }

                        @Override
                        public void onError() {
                            LogUtils.d("onSized", "error");
                            if (callback != null) {
                                callback.onError();
                            }
                            moveIdImage(context, image, System.currentTimeMillis() + "",
                                    idNum, callbackMove);
                        }
                    }
            );
        } else {
            if (callback != null) {
                callback.onSized(null);
            }
            moveIdImage(context, image, System.currentTimeMillis() + "",
                    idNum, callbackMove);
        }
    }

    private static void moveIdImage(Context context, ImageResult image, String name,
                                    String idNum, final SizeImageEvent callback) {
        if (image != null) {
            File originalImage = new File(Uri.parse(image.getImageUri()).getPath());
            File newImage = PmCoreUtils.createLocalFile(context, idNum, name, PmCoreConstants.FILE_TYPE_IMAGES);
            final Uri uri = Uri.fromFile(newImage);
            FileUtils.copyFileAsync(originalImage, newImage, true, new FileUtils.CopyFileEvents() {
                @Override
                public void onFinish(boolean isSuccess) {
                    if (isSuccess) {
                        LogUtils.d("onMoved", uri.toString());
                        ImageResult image = new ImageResult();
                        image.setImageUri(uri.toString());
                        if (callback != null) {
                            callback.onSized(image);
                        }
                    } else {
                        LogUtils.d("onMoved", "error");
                        if (callback != null) {
                            callback.onError();
                        }
                    }
                }
            });
        } else {
            if (callback != null) {
                callback.onSized(null);
            }
        }
    }

    public interface SizeImageEvent {
        void onSized(ImageResult image);

        void onError();
    }

    private class SizeImageEventCallback implements SizeImageEvent {
        private Boolean isFront, isMoving;

        public SizeImageEventCallback(Boolean isFront, Boolean isMoving) {
            this.isFront = isFront;
            this.isMoving = isMoving;
        }

        @Override
        public void onSized(ImageResult image) {
            if (!isMoving) {
                if (isFront) {
                    documentId.setImageCroppedSmall(image);
                } else {
                    documentId.setImageCroppedBackSmall(image);
                }
            } else {
                if (isFront) {
                    documentId.setImageCropped(image);
                } else {
                    documentId.setImageCroppedBack(image);
                }
            }
            updateIdDocument();
        }

        @Override
        public void onError() {
            updateIdDocument();
        }
    }

    public static DocumentID getScannedData(Intent scannedData) {
        if (PeerMountainCoreConstants.isFake) return getFakeScannedData();
        if (scannedData == null) return null;
        try {
            AXTSdkResult scannedResult = AXTCaptureInterface.INSTANCE.getResultImageFromCapture(scannedData);
            DocumentID document = new DocumentID();
            // TODO: 5/10/2018 fix
//            document.setImageSource(scannedResult.getMapImageSource().get(AXTSdkResult.IMAGES_RECTO));
//            document.setImageSourceBack(scannedResult.getMapImageSource().get(AXTSdkResult.IMAGES_VERSO));
//            document.setImageCropped(scannedResult.getMapImageCropped().get(AXTSdkResult.IMAGES_RECTO));
//            document.setImageCroppedBack(scannedResult.getMapImageCropped().get(AXTSdkResult.IMAGES_VERSO));
//            document.setImageFace(scannedResult.getMapImageFace().get(AXTSdkResult.FACE_CROPPED));
            AXTDocumentIdentity documentID = (AXTDocumentIdentity)
                    scannedResult.getMapDocument().get(AXTSdkResult.IDENTITY_DOCUMENT);
            // Récupération des champs document'un document document'identité
            document.setLastName(documentID.getField(AXTDocumentIdentity.AxtField.LAST_NAMES));
            document.setFirstName(documentID.getField(AXTDocumentIdentity.AxtField.FIRST_NAMES));
            document.setGender(documentID.getField(AXTDocumentIdentity.AxtField.GENDER));
            document.setBirthday(documentID.getField(AXTDocumentIdentity.AxtField.BIRTH_DATE));

            document.setDocNumber(documentID.getField(AXTDocumentIdentity.AxtField.DOCUMENT_NUMBER));
            document.setCountry(documentID.getField(AXTDocumentIdentity.AxtField.EMIT_COUNTRY));
            document.setEmitDate(documentID.getField(AXTDocumentIdentity.AxtField.EMIT_DATE));
            document.setMrzID(documentID.getField(AXTDocumentAbstract.AxtField.CODELINE));
            final AXTDocumentValidityResult validity = documentID.getDocumentValidity();
//            document.setValid(validity == AXTDocumentValidityResult.VALID);
            AXTDocumentChip documentNfc = (AXTDocumentChip)
                    scannedResult.getMapDocument().get(AXTSdkResult.RFID_DOCUMENT);
            if (documentNfc != null) {
                document.setExpirationDate(documentNfc.getField(AXTDocumentChip.AxtField.EXPIRATION_DATE));
            }
            return document;
        } catch (CaptureApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DocumentID getFakeScannedData() {
        DocumentID document = new DocumentID();
//            document.setImageSource(scannedResult.getMapImageSource().get(AXTSdkResult.IMAGES_RECTO));
//            document.setImageSourceBack(scannedResult.getMapImageSource().get(AXTSdkResult.IMAGES_VERSO));
//            document.setImageCropped(scannedResult.getMapImageCropped().get(AXTSdkResult.IMAGES_RECTO));
//            document.setImageCroppedBack(scannedResult.getMapImageCropped().get(AXTSdkResult.IMAGES_VERSO));
//            document.setImageFace(scannedResult.getMapImageFace().get(AXTSdkResult.FACE_CROPPED));
        document.setLastName("fLastName");
        document.setFirstName("fFirstName");
        document.setBirthday("01/04/1990");

        document.setDocNumber("fNumber");
        document.setCountry("fCountry");
        document.setEmitDate("01/04/1990");
        document.setMrzID("kjhsdcaui67yasch");
        document.setMrzCheck(true);
        return document;
    }

    public static boolean checkDocumentTextNotEmpty(String value) {
        return !TextUtils.isEmpty(value) && !value.equalsIgnoreCase("null");
    }

    public static boolean checkDocumentImageNotEmpty(ImageResult image) {
        return image != null && !TextUtils.isEmpty(image.getImageUri());
    }
}
