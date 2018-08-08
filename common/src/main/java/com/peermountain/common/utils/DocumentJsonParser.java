package com.peermountain.common.utils;

import android.util.JsonReader;

import com.peermountain.common.model.DocumentID;
import com.peermountain.common.model.ImageResult;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Galeen on 8/8/2018.
 */
public class DocumentJsonParser extends Pm_JsonParser {
    public static DocumentID readDocument(String json) throws IOException {
        if (json == null) return null;
        JsonReader reader = new JsonReader(new StringReader(json));
        return readDocument(reader);
    }

    public static DocumentID readDocument(JsonReader reader) throws IOException {
        DocumentID document = null;
        String name;
        reader.beginObject();
        while (reader.hasNext()) {
            if (document == null) document = new DocumentID();
            name = reader.nextName();
            switch (name) {
                case "message":
                    document.setErrorMessage(getString(reader));
                    break;
                case SEX:
                    document.setGender(getString(reader));
                    break;
                case GIVEN_NAME:
                    document.setFirstName(getString(reader));
                    break;
                case SURNAME:
                    document.setLastName(getString(reader));
                    break;
                case DATE_OF_BIRTH:
                    document.setBirthday(getString(reader));
                    break;
                case DOC_NUMBER:
                    document.setDocNumber(getString(reader));
                    break;
                case ISSUING_COUNTRY:
                    document.setCountry(getString(reader));
                    break;
                case EMIT_DATE:
                    document.setEmitDate(getString(reader));
                    break;
                case PASSPORT_TYPE:
                    document.setType(getString(reader));
                    break;
                case EXPIRATION_DATE:
                    document.setExpirationDate(getString(reader));
                    break;
                case MRZ_CHECK:
                    document.setMrzCheck(getBoolean(reader));
                    break;
                case NUMBER_CHECK:
                    document.setNumberCheck(getBoolean(reader));
                    break;
                case DOB_CHECK:
                    document.setDobCheck(getBoolean(reader));
                    break;
                case DOE_CHECK:
                    document.setDoeCheck(getBoolean(reader));
                    break;
                case IMAGE_SOURCE:
                    document.setImageSource(readAXTImageResult(reader));
                    break;
                case IMAGE_SOURCE_BACK:
                    document.setImageSourceBack(readAXTImageResult(reader));
                    break;
                case IMAGE_CROPPED:
                    document.setImageCropped(readAXTImageResult(reader));
                    break;
                case IMAGE_CROPPED_SMALL:
                    document.setImageCroppedSmall(readAXTImageResult(reader));
                    break;
                case IMAGE_CROPPED_BACK:
                    document.setImageCroppedBack(readAXTImageResult(reader));
                    break;
                case IMAGE_CROPPED_BACK_SMALL:
                    document.setImageCroppedBackSmall(readAXTImageResult(reader));
                    break;
                case IMAGE_FACE:
                    document.setImageFace(readAXTImageResult(reader));
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return document;
    }

    protected static ImageResult readAXTImageResult(JsonReader reader) throws IOException {
        String name = null;
        ImageResult axtImageResult = null;
        reader.beginObject();
        while (reader.hasNext()) {
            if (axtImageResult == null) axtImageResult = new ImageResult();
            name = reader.nextName();
            switch (name) {
                case IMAGE_URI:
                    axtImageResult.setImageUri(getString(reader));
                    break;
//                case WIDTH:
//                    axtImageResult.setWidth(getInt(reader));
//                    break;
//                case HEIGHT:
//                    axtImageResult.setHeight(getInt(reader));
//                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return axtImageResult;
    }
}
