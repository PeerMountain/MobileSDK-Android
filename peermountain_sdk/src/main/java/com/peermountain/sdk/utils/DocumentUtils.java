package com.peermountain.sdk.utils;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ariadnext.android.smartsdk.exception.CaptureApiException;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterface;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentAbstract;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentChip;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentIdentity;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentValidityResult;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTImageResult;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTSdkResult;
import com.peermountain.core.model.guarded.DocumentID;
import com.peermountain.core.model.guarded.ImageResult;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.constants.PeerMountainCoreConstants;

/**
 * Created by Galeen on 10/11/2017.
 */

@Deprecated
public final class DocumentUtils {

    public static DocumentID getScannedData(Intent scannedData) {
        if(PeerMountainCoreConstants.isFake) return getFakeScannedData();
        if(scannedData==null) return null;
        try {
            AXTSdkResult scannedResult = AXTCaptureInterface.INSTANCE.getResultImageFromCapture(scannedData);
            DocumentID document = new DocumentID();
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
            document.setValid(validity == AXTDocumentValidityResult.VALID);
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
            document.setValid(true);
            return document;
    }
    public static void setImage(ImageView iv, ImageResult image, String error, StringBuilder sb) {
        if (image != null && !TextUtils.isEmpty(image.getImageUri())) {
            LogUtils.d("image", Uri.parse(image.getImageUri()).toString());
            iv.setImageURI(Uri.parse(image.getImageUri()));
            iv.setVisibility(View.VISIBLE);
        } else {
            sb.append(error);
            iv.setVisibility(View.GONE);
        }
    }

    public static void setText(TextView tv, String prefix, String value, String error, StringBuilder sb) {
        if (!TextUtils.isEmpty(value) && !value.equalsIgnoreCase("null")) {
            tv.setText(prefix + value);
            tv.setVisibility(View.VISIBLE);
        } else {
            sb.append(error);
            tv.setVisibility(View.GONE);
        }
    }

    public static boolean checkDocumentTextNotEmpty(String value) {
        return  !TextUtils.isEmpty(value) && !value.equalsIgnoreCase("null");
    }

    public static boolean checkDocumentImageNotEmpty(AXTImageResult image) {
        return  image != null && !TextUtils.isEmpty(image.getImageUri());
    }
}
