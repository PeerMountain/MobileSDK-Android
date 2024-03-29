package com.peermountain.core.persistence;

import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import com.peermountain.common.model.DocumentID;
import com.peermountain.common.model.ImageResult;
import com.peermountain.common.utils.DocumentJsonParser;
import com.peermountain.common.utils.Pm_JsonParser;
import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.model.guarded.Contact;
import com.peermountain.core.model.guarded.FileDocument;
import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.model.guarded.PmJob;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.model.guarded.ShareObject;
import com.peermountain.core.model.guarded.VerifySelfie;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.pm_net.network.teleferique.model.Persona;

import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Galeen on 21.10.2016 г..
 */
public class MyJsonParser extends Pm_JsonParser{

    private static final String EMAIL_ADDRESS = "emailAddress";
    private static final String FIRST_NAME = "firstName";
    private static final String PICTURE_URL = "pictureUrl";
    private static final String PHONE = "phone";
    private static final String DOB = "dob";
    private static final String POB = "pob";
    private static final String FACEBOOK = "facebook";
    private static final String LINKEDIN = "linkedin";
    private static final String DOCUMENTS = "documents";
    private static final String LAST_NAME = "lastName";
    private static final String GANDER = "gander";
    private static final String MRZ_ID = "mrzID";

    private static final String ID = "id";
    private static final String HEIGHT = "height";
    private static final String WIDTH = "width";

    private static final String PUBLIC_PROFILES = "publicProfiles";
    private static final String LOGIN_TYPE = "login_type";
    private static final String COMPANY_TITLE = "company-title";
    private static final String PUBLIC_PROFILE_URL = "publicProfileUrl";
    private static final String COMPANY = "company";
    private static final String COMPANY_START_DATE = "company_start_date";
    private static final String COMPANY_END_DATE = "company_end_date";
    public static final String OPERATION = "operation";
    public static final String CONTACT = "contact";
    public static final String VALIDATED = "validated";
    public static final String VALIDATED_IMAGE_URI = "validated_image_uri";
    public static final String RES = "res";
    public static final String TITLE = "title";
    public static final String IS_EMPTY = "isEmpty";
    public static final String URI = "uri";
    public static final String TYPE = "type";
    public static final String FILE_DOCUMENTS = "fileDocuments";
    public static final String FILE_URI = "file_uri";
    public static final String LIVE_SELFIE = "live_selfie";
    public static final String ACTIVITY = "activity";
    public static final String INFORMATION = "information";
    public static final String X_FORM_URI = "xFormUri";
    public static final String OPEN = "open";



    public static PublicUser parseFbUser(JSONObject userJ) {
        PublicUser publicUser = null;
        if (userJ != null) {
            LogUtils.d("fb user", userJ.toString());
            if (!userJ.optString("id").isEmpty()) {
                String id = userJ.optString("id");
                String name = userJ.optString("first_name");
                String lastName = userJ.optString("last_name");
                String email = userJ.optString("email");
                String gender = userJ.optString("gender");
                String picture = userJ.optString("picture");
                if (picture != null) {
                    JSONObject picObj = userJ.optJSONObject("picture");
                    if (picObj != null && picObj.has("data")) {
                        picture = picObj.optJSONObject("data").optString("url");
                    } else {
                        picture = null;
                    }
                }
                publicUser = new PublicUser(id, email, name, lastName, picture);
//                Toast.makeText(getContext(), "FB logged", Toast.LENGTH_LONG).show();
            }
        }
        return publicUser;
    }

    static String writeShareObject(ShareObject shareObject) throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name(OPERATION).value(shareObject.getOperation());
        if (shareObject.getContact() != null) {
            writer.name(CONTACT);
            writeContact(writer, shareObject.getContact(), true);
        }
        writer.endObject();
        writer.close();
        return sw.toString();
    }

    public static Persona readServerPersona(String json) throws IOException {
        if (json == null) return null;
        String name;
        Persona persona = new Persona();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("data")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    name = reader.nextName();
                    if (name.equals("teleferic")) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            name = reader.nextName();
                            if (name.equals("persona")) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    name = reader.nextName();
                                    switch (name) {
                                        case "address" :
                                            persona.setAddress(getString(reader));
                                            break;
                                        case "nickname" :
                                            persona.setNickname(getString(reader));
                                            break;
                                        case "pubkey" :
                                            persona.setPubkey(getString(reader));
                                            break;
                                        default:
                                            reader.skipValue();
                                    }
                                }
                                reader.endObject();
                            }else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        reader.close();
        return persona;
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

    private static Profile readProfile(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.skipValue();
            return null;
        }
        String name = null;
        Profile profile = new Profile();
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            switch (name) {
                case ID:
                    profile.setId(getString(reader));
                    break;
                case VALIDATED_IMAGE_URI:
                    profile.setValidatedImageUri(getString(reader));
                    break;
                case VALIDATED:
                    profile.setValidated(getBoolean(reader));
                    break;
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
                case LIVE_SELFIE:
                    profile.setLiveSelfie(readLiveSelfie(reader));
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
        writeContact(writer, profile, false);
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

    private static void writeContact(JsonWriter writer, Contact contact, boolean justContact) throws IOException {
        if (contact == null) {
            writer.nullValue();
            return;
        }
        writer.beginObject();
        writer.name(ID).value(contact.getId());
        writer.name(VALIDATED_IMAGE_URI).value(contact.getValidatedImageUri());
        writer.name(VALIDATED).value(contact.isValidated());
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
        if (!justContact && contact instanceof Profile) {
            writeDocumentsID(writer, ((Profile) contact).getDocuments());
            writeLiveSelfie(writer, ((Profile) contact).getLiveSelfie());
        }
        writer.endObject();
    }

    private static void writeLiveSelfie(JsonWriter writer, ArrayList<String> images) throws IOException {
        if (images.size() > 0) {
            writer.name(LIVE_SELFIE);
            writer.beginArray();
            for (String image : images) {
                writer.value(image);
            }
            writer.endArray();
        }
    }

    private static void writeDocumentsID(JsonWriter writer, ArrayList<DocumentID> documents) throws IOException {
        if (documents.size() > 0) {
            writer.name(DOCUMENTS);
            writer.beginArray();
            for (DocumentID document : documents) {
                writeDocument(writer, document);
            }
            writer.endArray();
        }
    }

    static String writeAppDocument(AppDocument appDocument) throws IOException {
        if (appDocument == null) return null;
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name(ID).value(appDocument.getId());
        writer.name(RES).value(appDocument.getRes());
        writer.name(TITLE).value(appDocument.getTitle());
        writer.name(IS_EMPTY).value(appDocument.isEmpty());
        writeDocumentsID(writer, appDocument.getDocuments());
        writeFileDocuments(writer, appDocument.getFileDocuments());
        writer.endObject();
        writer.close();
        return sw.toString();
    }

    private static void writeFileDocuments(JsonWriter writer, ArrayList<FileDocument> documents) throws IOException {
        if (documents.size() > 0) {
            writer.name(FILE_DOCUMENTS);
            writer.beginArray();
            for (FileDocument document : documents) {
                writeFileDocument(writer, document);
            }
            writer.endArray();
        }
    }

    private static void writeFileDocument(JsonWriter writer, FileDocument file) throws IOException {
        writer.beginObject();
        if (!TextUtils.isEmpty(file.getImageUri())) {
            writer.name(URI).value(file.getImageUri());
        }
        if (!TextUtils.isEmpty(file.getFileUri())) {
            writer.name(FILE_URI).value(file.getFileUri());
        }
        writer.name(TYPE).value(file.getType());
        writer.endObject();
    }

    public static AppDocument readAppDocument(String json) throws IOException {
        if (json == null) return null;
        JsonReader reader = new JsonReader(new StringReader(json));
        String name = null;
        AppDocument document = new AppDocument();
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            switch (name) {
                case ID:
                    document.setId(getString(reader));
                    break;
                case TITLE:
                    document.setTitle(getString(reader));
                    break;
                case RES:
                    document.setRes(getInt(reader));
                    break;
                case IS_EMPTY:
                    document.setEmpty(getBoolean(reader));
                    break;
                case DOCUMENTS:
                    document.setDocuments(readDocuments(reader));
                    break;
                case FILE_DOCUMENTS:
                    document.setFileDocuments(readFileDocuments(reader));
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return document;
    }

    static ArrayList<FileDocument> readFileDocuments(JsonReader reader) throws IOException {
        ArrayList<FileDocument> contacts = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            FileDocument document = readFileDocument(reader);
            if (document != null) contacts.add(document);
        }
        reader.endArray();
        return contacts;
    }

    private static FileDocument readFileDocument(JsonReader reader) throws IOException {
        String name = null;
        FileDocument fileDocument = null;
        reader.beginObject();
        while (reader.hasNext()) {
            if (fileDocument == null) fileDocument = new FileDocument();
            name = reader.nextName();
            switch (name) {
                case URI:
                    fileDocument.setImageUri(getString(reader));
                    break;
                case FILE_URI:
                    fileDocument.setFileUri(getString(reader));
                    break;
                case TYPE:
                    fileDocument.setType(getString(reader));
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return fileDocument;
    }

    static String writeJobs(ArrayList<PmJob> jobs) throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginArray();
        if (jobs.size() > 0) {
            for (PmJob job : jobs) {
                writeJob(writer, job);
            }
        }
        writer.endArray();
        writer.close();
        return sw.toString();
    }

    private static void writeJob(JsonWriter writer, PmJob job) throws IOException {
        writer.beginObject();
        if (!TextUtils.isEmpty(job.getActivity())) {
            writer.name(ACTIVITY).value(job.getActivity());
        }
        if (!TextUtils.isEmpty(job.getInformation())) {
            writer.name(INFORMATION).value(job.getInformation());
        }
        if (!TextUtils.isEmpty(job.getxFormPath())) {
            writer.name(X_FORM_URI).value(job.getxFormPath());
        }
        writer.name(TYPE).value(job.getType());
        writer.name(OPEN).value(job.isOpen());

        writer.endObject();
    }

    static ArrayList<PmJob> readJobs(String json) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(json));
        ArrayList<PmJob> contacts = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            PmJob jo = readJob(reader);
            if (jo != null) contacts.add(jo);
        }
        reader.endArray();
        return contacts;
    }

    private static PmJob readJob(JsonReader reader) throws IOException {
        String name = null;
        PmJob job = null;
        reader.beginObject();
        while (reader.hasNext()) {
            if (job == null) job = new PmJob();
            name = reader.nextName();
            switch (name) {
                case ACTIVITY:
                    job.setActivity(getString(reader));
                    break;
                case INFORMATION:
                    job.setInformation(getString(reader));
                    break;
                case X_FORM_URI:
                    job.setxFormPath(getString(reader));
                    break;
                case TYPE:
                    job.setType(getInt(reader));
                    break;
                case OPEN:
                    job.setOpen(getBoolean(reader));
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return job;
    }

    static String writeContacts(Set<Contact> contacts) throws IOException {
        if (contacts == null) return null;
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginArray();
        for (Contact contact : contacts) {
            writeContact(writer, contact, false);
        }
        writer.endArray();
        writer.close();
        return sw.toString();
    }

    static String writeContact(Contact contact) throws IOException {
        if (contact == null) return null;
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writeContact(writer, contact, false);
        writer.close();
        return sw.toString();
    }

    static Set<Contact> readContacts(String json) throws IOException {
        Set<Contact> contacts = new HashSet<>();
        if (json == null) return contacts;
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.beginArray();
        while (reader.hasNext()) {
            Contact contact = readProfile(reader);
            if (contact != null) contacts.add(contact);
        }
        reader.endArray();
        reader.close();
        return contacts;
    }

    private static ArrayList<String> readLiveSelfie(JsonReader reader) throws IOException {
        ArrayList<String> images = new ArrayList<String>();
        reader.beginArray();
        while (reader.hasNext()) {
            String image = getString(reader);
            if (image != null) images.add(image);
        }
        reader.endArray();
        return images;
    }

    // TODO: 8/8/2018 move all Document parsing to DocumentJsonParser
    private static ArrayList<DocumentID> readDocuments(JsonReader reader) throws IOException {
        ArrayList<DocumentID> documents = new ArrayList<DocumentID>();
        reader.beginArray();
        while (reader.hasNext()) {
            DocumentID document = DocumentJsonParser.readDocument(reader);
            if (document != null) documents.add(document);
        }
        reader.endArray();
        return documents;
    }

    public static DocumentID readDocument(String json) throws IOException {
        return DocumentJsonParser.readDocument(json);
    }


    private static void writeDocument(JsonWriter writer, DocumentID document) throws IOException {
        if (document == null) {
            writer.nullValue();
            return;
        }
        writer.beginObject();
        writer.name(SEX).value(document.getGender());
        writer.name(GIVEN_NAME).value(document.getFirstName());
        writer.name(SURNAME).value(document.getLastName());
        writer.name(DATE_OF_BIRTH).value(document.getBirthday());
        writer.name(DOC_NUMBER).value(document.getDocNumber());
        writer.name(ISSUING_COUNTRY).value(document.getCountry());
        writer.name(EMIT_DATE).value(document.getEmitDate());
        writer.name(PASSPORT_TYPE).value(document.getType());
        writer.name(EXPIRATION_DATE).value(document.getExpirationDate());
        writer.name(MRZ_CHECK).value(document.isMrzCheck());
        writer.name(NUMBER_CHECK).value(document.isNumberCheck());
        writer.name(DOB_CHECK).value(document.isDobCheck());
        writer.name(DOE_CHECK).value(document.isDoeCheck());
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
        if (checkDocumentImageNotEmpty(document.getImageCroppedSmall())) {
            writer.name(IMAGE_CROPPED_SMALL);
            writeAXTImage(writer, document.getImageCroppedSmall());
        }
        if (checkDocumentImageNotEmpty(document.getImageCroppedBack())) {
            writer.name(IMAGE_CROPPED_BACK);
            writeAXTImage(writer, document.getImageCroppedBack());
        }
        if (checkDocumentImageNotEmpty(document.getImageCroppedBackSmall())) {
            writer.name(IMAGE_CROPPED_BACK_SMALL);
            writeAXTImage(writer, document.getImageCroppedBackSmall());
        }
        if (checkDocumentImageNotEmpty(document.getImageFace())) {
            writer.name(IMAGE_FACE);
            writeAXTImage(writer, document.getImageFace());
        }
        writer.endObject();
    }

    public static VerifySelfie readLiveSelfieResponse(String json) throws IOException {
        if (json == null) return null;
        JsonReader reader = new JsonReader(new StringReader(json));
        String name = null;
        VerifySelfie verifySelfie = null;
        reader.beginObject();
        while (reader.hasNext()) {
            if (verifySelfie == null) verifySelfie = new VerifySelfie();
            name = reader.nextName();
            switch (name) {
                case "liveliness":
                    verifySelfie.setLiveliness(getBoolean(reader));
                    break;
                case "humanFace":
                    verifySelfie.setHumanFace(getBoolean(reader));
                    break;
                case "faceMatch":
                    verifySelfie.setFaceMatch(getBoolean(reader));
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return verifySelfie;
    }


    private static boolean checkDocumentImageNotEmpty(ImageResult image) {
        return image != null && !TextUtils.isEmpty(image.getImageUri());
    }

    private static void writeAXTImage(JsonWriter writer, ImageResult image) throws IOException {
        writer.beginObject();
        writer.name(IMAGE_URI).value(image.getImageUri());
//        writer.name(WIDTH).value(image.getWidth());
//        writer.name(HEIGHT).value(image.getHeight());
        writer.endObject();
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
