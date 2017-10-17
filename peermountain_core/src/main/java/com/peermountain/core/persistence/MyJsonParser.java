package com.peermountain.core.persistence;

import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import com.ariadnext.android.smartsdk.interfaces.bean.AXTImageResult;
import com.peermountain.core.model.guarded.Contact;
import com.peermountain.core.model.guarded.Document;
import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.model.guarded.ShareObject;

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

    private static final String EMAIL_ADDRESS = "emailAddress";
    private static final String FIRST_NAME = "firstName";
    private static final String PICTURE_URL = "pictureUrl";
    private static final String PHONE = "phone";
    private static final String IMAGE_URI = "imageUri";
    private static final String DOB = "dob";
    private static final String POB = "pob";
    private static final String FACEBOOK = "facebook";
    private static final String LINKEDIN = "linkedin";
    private static final String DOCUMENTS = "documents";
    private static final String LAST_NAME = "lastName";
    private static final String GANDER = "gander";
    private static final String DOC_NUMBER = "docNumber";
    private static final String EMIT_DATE = "emitDate";
    private static final String MRZ_ID = "mrzID";
    private static final String EXPIRATION_DATE = "expirationDate";
    private static final String VALID = "valid";
    private static final String ID = "id";
    private static final String HEIGHT = "height";
    private static final String WIDTH = "width";
    private static final String IMAGE_FACE = "imageFace";
    private static final String IMAGE_CROPPED_BACK = "imageCroppedBack";
    private static final String IMAGE_CROPPED = "imageCropped";
    private static final String IMAGE_SOURCE_BACK = "imageSourceBack";
    private static final String IMAGE_SOURCE = "imageSource";
    private static final String PUBLIC_PROFILES = "publicProfiles";
    private static final String LOGIN_TYPE = "login_type";
    private static final String COMPANY_TITLE = "company-title";
    private static final String PUBLIC_PROFILE_URL = "publicProfileUrl";
    private static final String COMPANY = "company";
    private static final String COMPANY_START_DATE = "company_start_date";
    private static final String COMPANY_END_DATE = "company_end_date";
    public static final String OPERATION = "operation";
    public static final String CONTACT = "contact";

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


    static String writeShareObject(ShareObject shareObject) throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name(OPERATION).value(shareObject.getOperation());
        writer.name(CONTACT);
        writeContact(writer,shareObject.getContact(),null);
        writer.endObject();
        writer.close();
        return sw.toString();
    }

    static ShareObject readShareObject(String json) throws IOException {
        if (json == null) return null;
        String name;
        ShareObject shareObject = new ShareObject();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            switch (name) {
                case OPERATION:
                    shareObject.setOperation(getString(reader));
                    break;
                case CONTACT:
                    shareObject.setContact(readProfile(reader));
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        reader.close();
        return shareObject;
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
        return readProfile(reader);
//        reader.beginObject();
//        while (reader.hasNext()) {
//            name = reader.nextName();
//            switch (name) {
//                case EMAIL_ADDRESS:
//                    profile.setMail(getString(reader));
//                    break;
//                case FIRST_NAME:
//                    profile.setNames(getString(reader));
//                    break;
//                case PICTURE_URL:
//                    profile.setPictureUrl(getString(reader));
//                    break;
//                case PHONE:
//                    profile.setPhone(getString(reader));
//                    break;
//                case IMAGE_URI:
//                    profile.setImageUri(getString(reader));
//                    break;
//                case DOB:
//                    profile.setDob(getString(reader));
//                    break;
//                case POB:
//                    profile.setPob(getString(reader));
//                    break;
//                case PUBLIC_PROFILES:
//                    profile.setPublicProfiles(readPublicUsers(reader));
//                    break;
////                case FACEBOOK:
////                    profile.setFbProfile(readPublicUser(reader));
////                    break;
////                case LINKEDIN:
////                    profile.setLnProfile(readPublicUser(reader));
////                    break;
//                case DOCUMENTS:
//                    profile.setDocuments(readDocuments(reader));
//                    break;
//                default:
//                    reader.skipValue();
//            }
//        }
//        reader.endObject();
//        reader.close();
//        return profile;
    }

    static Profile readProfile(JsonReader reader) throws IOException {
        String name = null;
        Profile profile = new Profile();
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
                case PUBLIC_PROFILES:
                    profile.setPublicProfiles(readPublicUsers(reader));
                    break;
//                case FACEBOOK:
//                    profile.setFbProfile(readPublicUser(reader));
//                    break;
//                case LINKEDIN:
//                    profile.setLnProfile(readPublicUser(reader));
//                    break;
                case DOCUMENTS:
                    profile.setDocuments(readDocuments(reader));
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return profile;
    }


    static String writeProfile(Profile profile) throws IOException {
        if (profile == null) return null;
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writeContact(writer,profile,profile);
//        writer.beginObject();
//        writer.name(EMAIL_ADDRESS).value(profile.getMail());
//        writer.name(FIRST_NAME).value(profile.getNames());
//        writer.name(PHONE).value(profile.getPhone());
//        writer.name(IMAGE_URI).value(profile.getImageUri());
//        writer.name(PICTURE_URL).value(profile.getPictureUrl());
//        writer.name(DOB).value(profile.getDob());
//        writer.name(POB).value(profile.getPob());
//
//        if (profile.getPublicProfiles() != null && profile.getPublicProfiles().size() > 0) {
//            writer.name(PUBLIC_PROFILES);
//            writer.beginArray();
//            for (PublicUser publicUser : profile.getPublicProfiles()) {
//                writePublicUser(writer, publicUser);
//            }
//            writer.endArray();
//        }
////        if(profile.getFbProfile()!=null) {
////            writer.name(FACEBOOK);
////            writePublicUser(writer, profile.getFbProfile());
////        }
////
////        if(profile.getLnProfile()!=null) {
////            writer.name(LINKEDIN);
////            writePublicUser(writer, profile.getLnProfile());
////        }
//
//        if (profile.getDocuments().size() > 0) {
//            writer.name(DOCUMENTS);
//            writer.beginArray();
//            for (Document document : profile.getDocuments()) {
//                writeDocument(writer, document);
//            }
//            writer.endArray();
//        }
//        writer.endObject();
        writer.close();
        return sw.toString();
    }

    private static void writeContact(JsonWriter writer, Contact contact, Profile profile) throws IOException {
        if (contact == null) return;
        writer.beginObject();
        writer.name(EMAIL_ADDRESS).value(contact.getMail());
        writer.name(FIRST_NAME).value(contact.getNames());
        writer.name(PHONE).value(contact.getPhone());
        writer.name(IMAGE_URI).value(contact.getImageUri());
        writer.name(PICTURE_URL).value(contact.getPictureUrl());
        writer.name(DOB).value(contact.getDob());
        writer.name(POB).value(contact.getPob());

        if (contact.getPublicProfiles() != null && contact.getPublicProfiles().size() > 0) {
            writer.name(PUBLIC_PROFILES);
            writer.beginArray();
            for (PublicUser publicUser : contact.getPublicProfiles()) {
                writePublicUser(writer, publicUser);
            }
            writer.endArray();
        }
        if (profile != null) writeProfileDocuments(writer, profile);
        writer.endObject();
    }

    private static void writeProfileDocuments(JsonWriter writer, Profile contact) throws IOException {
        if (contact.getDocuments().size() > 0) {
            writer.name(DOCUMENTS);
            writer.beginArray();
            for (Document document : contact.getDocuments()) {
                writeDocument(writer, document);
            }
            writer.endArray();
        }
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
        if (document == null) return;
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
        if (checkDocumentImageNotEmpty(document.getImageSource())) {
            writer.name(IMAGE_SOURCE);
            writeAXTImage(writer, document.getImageSource());
        }
        if (checkDocumentImageNotEmpty(document.getImageSourceBack())) {
            writer.name(IMAGE_SOURCE_BACK);
            writeAXTImage(writer, document.getImageSourceBack());
        }
        if (checkDocumentImageNotEmpty(document.getImageCropped())) {
            writer.name(IMAGE_CROPPED);
            writeAXTImage(writer, document.getImageCropped());
        }
        if (checkDocumentImageNotEmpty(document.getImageCroppedBack())) {
            writer.name(IMAGE_CROPPED_BACK);
            writeAXTImage(writer, document.getImageCroppedBack());
        }
        if (checkDocumentImageNotEmpty(document.getImageFace())) {
            writer.name(IMAGE_FACE);
            writeAXTImage(writer, document.getImageFace());
        }
        writer.endObject();
    }

    private static boolean checkDocumentImageNotEmpty(AXTImageResult image) {
        return image != null && !TextUtils.isEmpty(image.getImageUri());
    }

    private static void writeAXTImage(JsonWriter writer, AXTImageResult image) throws IOException {
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
            if (axtImageResult == null) axtImageResult = new AXTImageResult();
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

    private static ArrayList<PublicUser> readPublicUsers(JsonReader reader) throws IOException {
        ArrayList<PublicUser> publicUsers = new ArrayList<PublicUser>();
        reader.beginArray();
        while (reader.hasNext()) {
            PublicUser publicUser = readPublicUser(reader);
            if (publicUser != null) publicUsers.add(publicUser);
        }
        reader.endArray();
        return publicUsers;
    }

    private static PublicUser readPublicUser(JsonReader reader) throws IOException {
        String name = null;
        PublicUser liUser = new PublicUser();
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            switch (name) {
                case LOGIN_TYPE:
                    liUser.setLoginType(getString(reader));
                    break;
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
                case PUBLIC_PROFILE_URL:
                    liUser.setPublicProfileUrl(getString(reader));
                    break;
                case COMPANY:
                    liUser.setCompany(getString(reader));
                    break;
                case COMPANY_TITLE:
                    liUser.setCompanyTitle(getString(reader));
                    break;
                case COMPANY_START_DATE:
                    liUser.setCompanyStartDate(getString(reader));
                    break;
                case COMPANY_END_DATE:
                    liUser.setCompanyEndDate(getString(reader));
                    break;
                case "positions"://this is read from LinkedIn profile only
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
        if (liUser == null) return null;
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name(LOGIN_TYPE).value(liUser.getLoginType());
        writer.name(EMAIL_ADDRESS).value(liUser.getEmail());
        writer.name(FIRST_NAME).value(liUser.getFirstname());
        writer.name(LAST_NAME).value(liUser.getSurname());
        writer.name(ID).value(liUser.getLinked_in());
        writer.name(PICTURE_URL).value(liUser.getPictureUrl());
        writer.name(COMPANY_TITLE).value(liUser.getCompanyTitle());
        writer.name(PUBLIC_PROFILE_URL).value(liUser.getPublicProfileUrl());
        writer.endObject();
        writer.close();
        return sw.toString();
    }

    private static void writePublicUser(JsonWriter writer, PublicUser liUser) throws IOException {
        writer.beginObject();
        writer.name(LOGIN_TYPE).value(liUser.getLoginType());
        writer.name(EMAIL_ADDRESS).value(liUser.getEmail());
        writer.name(FIRST_NAME).value(liUser.getFirstname());
        writer.name(LAST_NAME).value(liUser.getSurname());
        writer.name(ID).value(liUser.getLinked_in());
        writer.name(PICTURE_URL).value(liUser.getPictureUrl());
        writer.name(COMPANY_TITLE).value(liUser.getCompanyTitle());
        writer.name(COMPANY).value(liUser.getCompany());
        writer.name(COMPANY_START_DATE).value(liUser.getCompanyStartDate());
        writer.name(COMPANY_END_DATE).value(liUser.getCompanyEndDate());
        writer.name(PUBLIC_PROFILE_URL).value(liUser.getPublicProfileUrl());
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
