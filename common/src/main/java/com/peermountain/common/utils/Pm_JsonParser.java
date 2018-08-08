package com.peermountain.common.utils;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Galeen on 8/8/2018.
 */
public class Pm_JsonParser {
    protected static final String SEX = "Sex";
    protected static final String GIVEN_NAME = "GivenName";
    protected static final String SURNAME = "Surname";
    protected static final String DATE_OF_BIRTH = "DateOfBirth";
    protected static final String ISSUING_COUNTRY = "IssuingCountry";
    protected static final String PASSPORT_TYPE = "PassportType";
    protected static final String DOC_NUMBER = "PassportNumber";
    protected static final String EMIT_DATE = "emitDate";
    protected static final String EXPIRATION_DATE = "DateOfExpiration";
    protected static final String MRZ_CHECK = "MRZCheck";
    protected static final String DOE_CHECK = "doeCheck";
    protected static final String NUMBER_CHECK = "numberCheck";
    protected static final String DOB_CHECK = "dobCheck";
    protected static final String IMAGE_FACE = "imageFace";
    protected static final String IMAGE_CROPPED_BACK = "imageCroppedBack";
    protected static final String IMAGE_CROPPED_BACK_SMALL = "imageCroppedBackSmall";
    protected static final String IMAGE_CROPPED = "imageCropped";
    protected static final String IMAGE_CROPPED_SMALL = "imageCroppedSmall";
    protected static final String IMAGE_SOURCE_BACK = "imageSourceBack";
    protected static final String IMAGE_SOURCE = "imageSource";
    protected static final String IMAGE_URI = "imageUri";
    
    
    protected static String getString(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            return reader.nextString();
        else
            reader.skipValue();
        return null;
    }

    protected static Date getDate(JsonReader reader, SimpleDateFormat dateFormat) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            try {
                return dateFormat.parse(reader.nextString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        else
            reader.skipValue();
        return null;
    }

    protected static Integer getInt(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            return reader.nextInt();
        else
            reader.skipValue();
        return -1;
    }

    protected static boolean getBoolean(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            return reader.nextBoolean();
        else
            reader.skipValue();
        return false;
    }

    protected static double getDouble(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            return reader.nextDouble();
        else
            reader.skipValue();
        return -1;
    }


}
