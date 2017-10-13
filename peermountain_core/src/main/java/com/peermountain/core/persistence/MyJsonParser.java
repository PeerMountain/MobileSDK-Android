package com.peermountain.core.persistence;

import android.content.Context;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import com.ariadnext.android.smartsdk.interfaces.bean.AXTImageResult;
import com.peermountain.core.model.guarded.Document;
import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.model.guarded.PublicUser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Galeen on 21.10.2016 г..
 */
class MyJsonParser {

    public static final String EMAIL_ADDRESS = "emailAddress";
    public static final String FIRST_NAME = "firstName";
    public static final String PICTURE_URL = "pictureUrl";
    public static final String PHONE = "phone";
    public static final String IMAGE_URI = "imageUri";
    public static final String DOB = "dob";
    public static final String POB = "pob";
    public static final String FACEBOOK = "facebook";
    public static final String LINKEDIN = "linkedin";
    public static final String DOCUMENTS = "documents";
    public static final String LAST_NAME = "lastName";
    public static final String GANDER = "gander";
    public static final String DOC_NUMBER = "docNumber";
    public static final String EMIT_DATE = "emitDate";
    public static final String MRZ_ID = "mrzID";
    public static final String EXPIRATION_DATE = "expirationDate";
    public static final String VALID = "valid";
    public static final String ID = "id";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String IMAGE_FACE = "imageFace";
    public static final String IMAGE_CROPPED_BACK = "imageCroppedBack";
    public static final String IMAGE_CROPPED = "imageCropped";
    public static final String IMAGE_SOURCE_BACK = "imageSourceBack";
    public static final String IMAGE_SOURCE = "imageSource";

    private static String getString(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            return reader.nextString();
        else
            reader.skipValue();
        return null;
    }

    private static Date getDate(JsonReader reader, SimpleDateFormat dateFormat) throws IOException {
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

    private static Integer getInt(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            return reader.nextInt();
        else
            reader.skipValue();
        return -1;
    }

    private static boolean getBoolean(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            return reader.nextBoolean();
        else
            reader.skipValue();
        return false;
    }

    private static double getDouble(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            return reader.nextDouble();
        else
            reader.skipValue();
        return -1;
    }


    static String writeLoginProvider(Context ctx, String token, boolean isFB) throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name(isFB ? "sessionToken" : "accessToken").value(token);
        writer.endObject();
        writer.close();
        return sw.toString();
    }

    private static final String DEBUG = "debug";
    private static final String LICENSE = "license";

    static String writeConfig(PeerMountainConfig config) throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name(DEBUG).value(config.isDebug());
        writer.name(LICENSE).value(config.getIdCheckLicense());
        writer.endObject();
        writer.close();
        return sw.toString();
    }

    static PeerMountainConfig readConfig(String json) throws IOException {
        if (json == null) return null;
        String name;
        PeerMountainConfig config = new PeerMountainConfig();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            switch (name) {
                case DEBUG:
                    config.setDebug(getBoolean(reader));
                    break;
                case LICENSE:
                    config.setIdCheckLicense(getString(reader));
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        reader.close();
        return config;
    }

    //    public static Login readLogin(String response) throws IOException {
//        if (response == null) return null;
//        GsonBuilder builder = new GsonBuilder().serializeNulls();
//        Gson gson = builder.create();
//        return gson.fromJson(response, Login.class);
//    }
    static Profile readProfile(String json) throws IOException {
//        Gson gson = new Gson();
//        return gson.fromJson(json, LiUser.class);
        if (json == null) return null;
        String name = null;
        Profile profile = new Profile();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            switch (name) {
                case EMAIL_ADDRESS:
                    profile.setMail(getString(reader));
                    break;
                case FIRST_NAME:
                    profile.setNames(getString(reader));
                    break;
                case PICTURE_URL:
                    profile.setPictureUrl(getString(reader));
                    break;
                case PHONE:
                    profile.setPhone(getString(reader));
                    break;
                case IMAGE_URI:
                    profile.setImageUri(getString(reader));
                    break;
                case DOB:
                    profile.setDob(getString(reader));
                    break;
                case POB:
                    profile.setPob(getString(reader));
                    break;
                case FACEBOOK:
                    profile.setFbProfile(readPublicUser(reader));
                    break;
                case LINKEDIN:
                    profile.setLnProfile(readPublicUser(reader));
                    break;
                case DOCUMENTS:
                    profile.setDocuments(readDocuments(reader));
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        reader.close();
        return profile;
    }

    static String writeProfile(Profile profile) throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name(EMAIL_ADDRESS).value(profile.getMail());
        writer.name(FIRST_NAME).value(profile.getNames());
        writer.name(PHONE).value(profile.getPhone());
        writer.name(IMAGE_URI).value(profile.getImageUri());
        writer.name(PICTURE_URL).value(profile.getPictureUrl());
        writer.name(DOB).value(profile.getDob());
        writer.name(POB).value(profile.getPob());

        if(profile.getFbProfile()!=null) {
            writer.name(FACEBOOK);
            writePublicUser(writer, profile.getFbProfile());
        }

        if(profile.getLnProfile()!=null) {
            writer.name(LINKEDIN);
            writePublicUser(writer, profile.getLnProfile());
        }

        if(profile.getDocuments().size()>0){
            writer.name(DOCUMENTS);
            writer.beginArray();
            for (Document document : profile.getDocuments()) {
                writeDocument(writer,document);
            }
            writer.endArray();
        }
        writer.endObject();
        writer.close();
        return sw.toString();
    }

    private static ArrayList<Document> readDocuments(JsonReader reader) throws IOException {
        ArrayList<Document> documents = new ArrayList<Document>();
        reader.beginArray();
        while (reader.hasNext()) {
            Document document = readDocument(reader);
            if (document != null) documents.add(document);
        }
        reader.endArray();
        return documents;
    }

    private static Document readDocument(JsonReader reader) throws IOException {
        Document document = null;
        String name;
        reader.beginObject();
        while (reader.hasNext()) {
            if (document == null) document = new Document();
            name = reader.nextName();
            switch (name) {
                case GANDER:
                    document.setGender(getString(reader));
                    break;
                case FIRST_NAME:
                    document.setFirstName(getString(reader));
                    break;
                case LAST_NAME:
                    document.setLastName(getString(reader));
                    break;
                case DOB:
                    document.setBirthday(getString(reader));
                    break;
                case DOC_NUMBER:
                    document.setDocNumber(getString(reader));
                    break;
                case POB:
                    document.setCountry(getString(reader));
                    break;
                case EMIT_DATE:
                    document.setEmitDate(getString(reader));
                    break;
                case MRZ_ID:
                    document.setMrzID(getString(reader));
                    break;
                case EXPIRATION_DATE:
                    document.setExpirationDate(getString(reader));
                    break;
                case VALID:
                    document.setValid(getBoolean(reader));
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
                case IMAGE_CROPPED_BACK:
                    document.setImageCroppedBack(readAXTImageResult(reader));
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

    private static void writeDocument(JsonWriter writer, Document document) throws IOException {
        if(document==null) return ;
        writer.beginObject();
        writer.name(GANDER).value(document.getGender());
        writer.name(FIRST_NAME).value(document.getFirstName());
        writer.name(LAST_NAME).value(document.getLastName());
        writer.name(DOB).value(document.getBirthday());
        writer.name(DOC_NUMBER).value(document.getDocNumber());
        writer.name(EMIT_DATE).value(document.getEmitDate());
        writer.name(MRZ_ID).value(document.getMrzID());
        writer.name(EXPIRATION_DATE).value(document.getExpirationDate());
        writer.name(VALID).value(document.isValid());
        if(checkDocumentImageNotEmpty(document.getImageSource())) {
            writer.name(IMAGE_SOURCE);
            writeAXTImage(writer, document.getImageSource());
        }
        if(checkDocumentImageNotEmpty(document.getImageSourceBack())) {
            writer.name(IMAGE_SOURCE_BACK);
            writeAXTImage(writer, document.getImageSourceBack());
        }
        if(checkDocumentImageNotEmpty(document.getImageCropped())) {
            writer.name(IMAGE_CROPPED);
            writeAXTImage(writer, document.getImageCropped());
        }
        if(checkDocumentImageNotEmpty(document.getImageCroppedBack())) {
            writer.name(IMAGE_CROPPED_BACK);
            writeAXTImage(writer, document.getImageCroppedBack());
        }
        if(checkDocumentImageNotEmpty(document.getImageFace())) {
            writer.name(IMAGE_FACE);
            writeAXTImage(writer, document.getImageFace());
        }
        writer.endObject();
    }
    private static boolean checkDocumentImageNotEmpty(AXTImageResult image) {
        return  image != null && !TextUtils.isEmpty(image.getImageUri());
    }

    private static void writeAXTImage(JsonWriter writer,AXTImageResult image) throws IOException {
        writer.beginObject();
        writer.name(IMAGE_URI).value(image.getImageUri());
        writer.name(WIDTH).value(image.getWidth());
        writer.name(HEIGHT).value(image.getHeight());
        writer.endObject();
    }

    private static AXTImageResult readAXTImageResult(JsonReader reader) throws IOException {
        String name = null;
        AXTImageResult axtImageResult = null;
        reader.beginObject();
        while (reader.hasNext()) {
            if(axtImageResult==null) axtImageResult = new AXTImageResult();
            name = reader.nextName();
            switch (name) {
                case IMAGE_URI:
                    axtImageResult.setImageUri(getString(reader));
                    break;
                case WIDTH:
                    axtImageResult.setWidth(getInt(reader));
                    break;
                case HEIGHT:
                    axtImageResult.setHeight(getInt(reader));
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return axtImageResult;
    }

    private static PublicUser readPublicUser(JsonReader reader) throws IOException {
        String name = null;
        PublicUser liUser = new PublicUser();
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            switch (name) {
                case EMAIL_ADDRESS:
                    liUser.setEmail(getString(reader));
                    break;
                case FIRST_NAME:
                    liUser.setFirstname(getString(reader));
                    break;
                case LAST_NAME:
                    liUser.setSurname(getString(reader));
                    break;
                case ID:
                    liUser.setLinked_in(getString(reader));
                    break;
                case PICTURE_URL:
                    liUser.setPictureUrl(getString(reader));
                    break;
                case "publicProfileUrl":
                    liUser.setPublicProfileUrl(getString(reader));
                    break;
                case "positions":
                    readCompanies(liUser, reader);
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return liUser;
    }

    static PublicUser readPublicUser(String json) throws IOException {
//        Gson gson = new Gson();
//        return gson.fromJson(json, LiUser.class);
        if (json == null) return null;
        JsonReader reader = new JsonReader(new StringReader(json));
        PublicUser liUser = readPublicUser(reader);
        reader.close();
        return liUser;
    }

    // TODO: 10/3/2017 update to populate the right fields
    static String writePublicUser(PublicUser liUser) throws IOException {
        if(liUser==null) return null;
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name(EMAIL_ADDRESS).value(liUser.getEmail());
        writer.name(FIRST_NAME).value(liUser.getFirstname());
        writer.name(LAST_NAME).value(liUser.getSurname());
        writer.name(ID).value(liUser.getLinked_in());
        writer.name(PICTURE_URL).value(liUser.getPictureUrl());
        writer.endObject();
        writer.close();
        return sw.toString();
    }
    private static void writePublicUser(JsonWriter writer,PublicUser liUser) throws IOException {
        writer.beginObject();
        writer.name(EMAIL_ADDRESS).value(liUser.getEmail());
        writer.name(FIRST_NAME).value(liUser.getFirstname());
        writer.name(LAST_NAME).value(liUser.getSurname());
        writer.name(ID).value(liUser.getLinked_in());
        writer.name(PICTURE_URL).value(liUser.getPictureUrl());
        writer.endObject();
    }

    private static void readCompanies(PublicUser liUser, JsonReader reader) throws IOException {
        reader.beginObject();
        String name = null;
        while (reader.hasNext()) {
            name = reader.nextName();
            switch (name) {
                case "values":
                    reader.beginArray();
                    boolean found = false;
                    while (reader.hasNext()) {
                        if (found)//for now just gets the 1st one
                            reader.skipValue();
                        else
                            found = readCompany(liUser, reader, name);
                    }
                    reader.endArray();
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
    }

    private static boolean readCompany(PublicUser liUser, JsonReader reader, String name) throws IOException {
        while (reader.hasNext()) {

            reader.beginObject();
            String[] company = new String[4];
            while (reader.hasNext()) {
                name = reader.nextName();
                switch (name) {
                    case "company":
                        if (reader.hasNext()) {
                            reader.beginObject();
                            while (reader.hasNext()) {
                                name = reader.nextName();
                                if (name.equals("name")) {
                                    company[0] = getString(reader);
                                } else
                                    reader.skipValue();
                            }
                            reader.endObject();
                        }
                        break;
                    case "title":
                        company[1] = getString(reader);
                        break;
                    case "companyStartDate":
                        company[2] = getString(reader);
                        break;
                    case "companyEndDate":
                        company[3] = getString(reader);
                        break;
                    default:
                        reader.skipValue();
                }
            }
            reader.endObject();
            liUser.setCompany(company[0]);
            liUser.setCompanyTitle(company[1]);
            liUser.setCompanyStartDate(company[2]);
            liUser.setCompanyEndDate(company[3]);
        }
        return true;
    }


}
