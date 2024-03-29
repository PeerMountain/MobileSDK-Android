package com.peermountain.core.persistence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.ariadnext.android.smartsdk.bean.enums.AXTSdkParameters;
import com.ariadnext.android.smartsdk.exception.CaptureApiException;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterface;
import com.ariadnext.android.smartsdk.interfaces.AXTCaptureInterfaceCallback;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDataExtractionRequirement;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTDocumentType;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTSdkInit;
import com.ariadnext.android.smartsdk.interfaces.bean.AXTSdkParams;
import com.google.zxing.Result;
import com.peermountain.common.PmBaseConfig;
import com.peermountain.core.R;
import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.model.guarded.Contact;
import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.model.guarded.PmAccessToken;
import com.peermountain.core.model.guarded.PmJob;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.model.guarded.ShareObject;
import com.peermountain.core.model.unguarded.Keywords;
import com.peermountain.pm_net.network.MainCallback;
import com.peermountain.pm_net.network.NetworkManager;
import com.peermountain.pm_net.network.NetworkResponse;
import com.peermountain.core.odk.tasks.FormLoaderTask;
import com.peermountain.core.odk.utils.Collect;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.constants.PmCoreConstants;
import com.peermountain.core.utils.PmCoreUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Created by Galeen on 28.9.2016 г..
 */
public class PeerMountainManager {
    // TODO: 6.1.2017 г. create a method to update the cache if is dirty
    private static Context applicationContext = null;

    public static Context getApplicationContext() {
        return applicationContext;
    }

    /**
     * This method must be called in Application.onCreate
     *
     * @param config PeerMountainConfig
     */
    public static void init(PeerMountainConfig config) {
        PeerMountainManager.applicationContext = config.getApplicationContext();
        config.setApplicationContext(null);
        new PmBaseConfig.Builder()
                .setApplicationContext(applicationContext)
                .setDebug(config.isDebug())
                .setApiScanKey(config.getApiScanKey())
                .init();

        Cache.getInstance().setConfig(config);
        SharedPreferenceManager.saveConfig(config);
        Collect.createODKDirs();
    }

    /**
     * Get last saved Config
     *
     * @param context any context works to get the value
     * @return PeerMountainConfig object without applicationContext in it
     */
    public static PeerMountainConfig getLastPeerMountainConfig(Context context) {
        if (Cache.getInstance().getConfig() == null) {
            Cache.getInstance().setConfig(SharedPreferenceManager.getConfig(context));
        }
        return Cache.getInstance().getConfig();
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static PeerMountainConfig getPeerMountainConfig() {
        if (Cache.getInstance().getConfig() == null) {
            Cache.getInstance().setConfig(SharedPreferenceManager.getConfig(null));
        }
        return Cache.getInstance().getConfig();
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static long getUserValidTime() {
        long mills = new PeerMountainConfig().getUserValidTime();//default time
        if (getPeerMountainConfig() != null)
            mills = getPeerMountainConfig().getUserValidTime();//user's set time
        return mills;
    }

    public static PmAccessToken getLiAccessToken() {
        if (Cache.getInstance().getAccessToken() == null) {
            Cache.getInstance().setAccessToken(SharedPreferenceManager.getPmAccessToken());
        }
        return Cache.getInstance().getAccessToken();
    }

    public static void saveLiAccessToken(PmAccessToken accessToken) {
        Cache.getInstance().setAccessToken(accessToken);
        SharedPreferenceManager.savePmAccessToken(accessToken);
    }


    public static String getDeviceId() {
        return SharedPreferenceManager.getDeviceId();
    }

    public static PublicUser readPublicUser(String publicUserJson) {
        PublicUser liUser = null;
        try {
            liUser = MyJsonParser.readPublicUser(publicUserJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return liUser;
    }
//    public static PublicUser savePublicUser(String publicUserJson) {
//        PublicUser liUser = null;
//        try {
//            liUser = MyJsonParser.readPublicUser(publicUserJson);
//            Cache.getInstance().setPublicUser(liUser);
//            SharedPreferenceManager.savePublicUser(publicUserJson);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return liUser;
//    }

//    public static void savePublicUser(PublicUser publicUser) {
//        Cache.getInstance().setPublicUser(publicUser);
//        try {
//            SharedPreferenceManager.savePublicUser(MyJsonParser.writePublicUser(publicUser));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static PublicUser getPublicUser() {
//        if (Cache.getInstance().getPublicUser() == null)
//            Cache.getInstance().setPublicUser(SharedPreferenceManager.getPublicUser());
//        return Cache.getInstance().getPublicUser();
//    }

    public static void logoutPublicProfile() {
        Cache.getInstance().clearPublicProfileCache();
        SharedPreferenceManager.logoutPublicProfile();
//        Messenger.clearAll();
    }

    public static void saveProfile(Profile profile) {
        Cache.getInstance().setProfile(profile);
        try {
            SharedPreferenceManager.saveProfile(MyJsonParser.writeProfile(profile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveCurrentProfile() {
        try {
            SharedPreferenceManager.saveProfile(MyJsonParser.writeProfile(getProfile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Profile getProfile() {
        if (Cache.getInstance().getProfile() == null)
            Cache.getInstance().setProfile(SharedPreferenceManager.getProfile());
        return Cache.getInstance().getProfile();
    }

    public static void saveJobs(ArrayList<PmJob> jobs) {
        Cache.getInstance().setJobs(jobs);
        try {
            SharedPreferenceManager.saveJobs(MyJsonParser.writeJobs(jobs));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<PmJob> getJobs() {
        if (Cache.getInstance().getJobs() == null)
            Cache.getInstance().setJobs(SharedPreferenceManager.getJobs());
        return Cache.getInstance().getJobs();
    }

    public static String shareObjectToJson(ShareObject shareObject) {
        try {
            return MyJsonParser.writeShareObject(shareObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ShareObject parseSharedObject(String json) {
        try {
            return MyJsonParser.readShareObject(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveTutoSeen() {
        SharedPreferenceManager.saveTutoSeen();
    }

    public static boolean isTutoSeen() {
        return SharedPreferenceManager.isTutoSeen();
    }

    public static void addContact(Contact contact) {
        getContacts().add(contact);
        // TODO: 10/17/2017 probably create contact.id and save each contact by key=id
        SharedPreferenceManager.saveContacts(Cache.getInstance().getContacts());
    }

    public static void updateContact(Contact contact) {
        getContacts().remove(contact);
        getContacts().add(contact);
        // TODO: 10/17/2017 probably create contact.id and save each contact by key=id
        SharedPreferenceManager.saveContacts(Cache.getInstance().getContacts());
    }

    public static void removeContact(Contact contact) {
        getContacts().remove(contact);
        // TODO: 10/17/2017 probably create contact.id and save each contact by key=id
        SharedPreferenceManager.saveContacts(Cache.getInstance().getContacts());
    }

    public static HashSet<Contact> getContacts() {
        if (Cache.getInstance().getContacts() == null)
            Cache.getInstance().setContacts((HashSet<Contact>) SharedPreferenceManager.getContacts());
        return Cache.getInstance().getContacts();
    }

    public static AppDocument getDocument(String id) {
        if (id == null) return null;
        ArrayList<AppDocument> documents = getDocuments();
        if (documents != null) {
            for (AppDocument document : documents) {
                if (document.getId().equals(id)) {
                    return document;
                }
            }
        }
        return null;
    }


    public static void addDocument(AppDocument document) {
        getDocuments().add(document);
        SharedPreferenceManager.addDocument(document);
    }

    public static void updateDocument(AppDocument document) {
        getDocuments().remove(document);
        getDocuments().add(document);

        SharedPreferenceManager.saveDocument(document);
    }

    public static void removeDocument(AppDocument document) {
        getDocuments().remove(document);
        SharedPreferenceManager.removeDocument(document.getId());
    }

    public static ArrayList<AppDocument> getDocuments() {
        if (Cache.getInstance().getDocuments() == null)
            Cache.getInstance().setDocuments(SharedPreferenceManager.getDocuments());
        ArrayList<AppDocument> docs = Cache.getInstance().getDocuments();
        ArrayList<AppDocument> documents;
        if (docs != null && docs.size() > 0) {//we have saved docs
            if(docs.size()<4){
                documents = new ArrayList<>(docs);
            }else{
                return docs;
            }
        }else{
            documents = new ArrayList<>();
        }
        AppDocument myID = new AppDocument(getApplicationContext().getString(R.string.pm_document_item_id_title));
        Profile me = PeerMountainManager.getProfile();
        if (me != null && me.getDocuments().size() > 0) {
            myID.getDocuments().add(me.getDocuments().get(0));
        }
        documents.add(myID);
        documents.add(new AppDocument(R.drawable.pm_birther, "Birth Certificate"));
        documents.add(new AppDocument(R.drawable.pm_employment_contract, "Employment Contract"));
        documents.add(new AppDocument(R.drawable.pm_income_tax, "Tax Return"));
        documents.add(new AppDocument(true));
        PeerMountainManager.saveDocuments(documents);
        return documents;
    }

    public static void saveDocuments(ArrayList<AppDocument> documents) {
        Cache.getInstance().setDocuments(documents);
        SharedPreferenceManager.saveDocuments(documents);
    }

    public static void savePin(String pin) {
        Cache.getInstance().setPin(pin);
        SharedPreferenceManager.savePin(pin);
    }

    public static String getPin() {
        if (Cache.getInstance().getPin() == null)
            Cache.getInstance().setPin(SharedPreferenceManager.getPin());
        return Cache.getInstance().getPin();
    }

    public static void clearLastTimeActive() {
        Cache.getInstance().setLastTimeActive(0);
    }

    public static void saveLastTimeActive() {
        Cache.getInstance().setLastTimeActive(System.currentTimeMillis());
    }

    /**
     * @return if the time for valid user set in PeerMountainConfig has past since was authorization
     */
    public static boolean shouldAuthorize() {
        return System.currentTimeMillis() > Cache.getInstance().getLastTimeActive() + getPeerMountainConfig().getUserValidTime();
    }

    public static void saveFingerprint(boolean enabled) {
        SharedPreferenceManager.saveFingerprint(enabled);
    }

    public static boolean getFingerprint() {
        return SharedPreferenceManager.getFingerprint();
    }

    public static void saveKeywords(String keywords) {
        SharedPreferenceManager.saveKeywords(keywords);
    }

    public static String getKeywords() {
        return SharedPreferenceManager.getKeywords();
    }

    public static void saveKeywordsObject(Keywords keywords) {
        SharedPreferenceManager.saveKeywords(keywords);
    }

    public static Keywords getSavedKeywordsObject() {
        return SharedPreferenceManager.getKeywordsAsObject();
    }

    public static Keywords getRandomKeywords(Context context) {
        return KeywordsHelper.getRandomKeywords(context);
    }

    public static Keywords getRandomKeywordsWithSavedIncluded(Context context) {
        return KeywordsHelper.getRandomKeywordsWithSavedIncluded(context);
    }

    public static void resetProfile() {
        Cache.getInstance().clearCache();
        SharedPreferenceManager.logout();
    }

    /**
     * This method works in background and takes a few seconds
     * also needs network to verify license
     *
     * @param activity caller
     * @param callback return onSuccess and onError events
     */
    public static void initScanSDK(Activity activity, AXTCaptureInterfaceCallback callback) {
        if (getPeerMountainConfig() == null ||
                TextUtils.isEmpty(getPeerMountainConfig().getIdCheckLicense())) {
            LogUtils.e("initScanSDK", "no license");
            return;
        }
        // TODO: 10/5/2017 check for internet

        AXTSdkInit sdkInit = new AXTSdkInit(getPeerMountainConfig().getIdCheckLicense());

        sdkInit.setUseImeiForActivation(true);
        // Initialisation of SDK
        try {
            // TODO: 10/5/2017 implement inner callback and save in cache if sdk is initialized
            AXTCaptureInterface.INSTANCE.initCaptureSdk(activity, sdkInit, callback);
        } catch (CaptureApiException e) {
            e.printStackTrace();
            LogUtils.e("initScanSDK", "An exception occured during SmartSdk initialization \n" + e.getMessage());
        }
    }

    public static boolean scanId(Activity activity, int requestCode) {
        if (!AXTCaptureInterface.INSTANCE.sdkIsActivated()) {
            return false;
        } else {
            startScanningId(activity, null, requestCode);
            return true;
        }
    }

    public static boolean scanId(Fragment fragment, int requestCode) {
        if (!AXTCaptureInterface.INSTANCE.sdkIsActivated()) {
            return false;
        } else {
            startScanningId(null, fragment, requestCode);
            return true;
        }
    }

    public static boolean isScanIdSDKReady() {
        return AXTCaptureInterface.INSTANCE.sdkIsActivated();
    }


    private static void startScanningId(Activity activity, Fragment fragment, int requestCode) {
        if (activity == null && fragment == null) return;
        try {
            AXTSdkParams sdkParams = createAxtSdkParamsForID();
            final Intent intentSDK = AXTCaptureInterface.INSTANCE.
                    getIntentCapture(activity != null ? activity : fragment.getContext(), sdkParams);
            if (activity != null) {
                activity.startActivityForResult(intentSDK, requestCode);
            } else {
                fragment.startActivityForResult(intentSDK, requestCode);
            }
        } catch (final CaptureApiException ex) {
            ex.printStackTrace();
            LogUtils.e("startScan", ex.getMessage());
        }
    }

    @NonNull
    private static AXTSdkParams createAxtSdkParamsForID() {
        AXTSdkParams sdkParams = new AXTSdkParams();
        sdkParams.addParameters(AXTSdkParameters.DISPLAY_CAPTURE, true);
        sdkParams.setDocType(AXTDocumentType.ID);
        sdkParams.addParameters(AXTSdkParameters.USE_FRONT_CAMERA, false);
        sdkParams.addParameters(AXTSdkParameters.EXTRACT_DATA, true);
        sdkParams.addParameters(AXTSdkParameters.SCAN_RECTO_VERSO, true);
        sdkParams.addParameters(AXTSdkParameters.READ_RFID, true);
        sdkParams.addParameters(AXTSdkParameters.USE_HD, true);
        sdkParams.addParameters(AXTSdkParameters.DATA_EXTRACTION_REQUIREMENT,
                AXTDataExtractionRequirement.MRZ_FOUND);
        return sdkParams;
    }

    @WorkerThread
    public static Bitmap getQrCode(Contact contact, int imageSize, int qrColor) {
        return ShareHelper.getQrCode(contact, imageSize, qrColor);
    }

    public static Contact handleQrScanResult(Result rawResult) {
        return ShareHelper.handleResult(rawResult);
    }

    public static PublicUser parseFbPublicProfile(JSONObject userJ) {
        return MyJsonParser.parseFbUser(userJ);
    }

    /**
     * If local file exist then this XForm is downloaded already and will be returned the file
     *
     * @param mCallback callback to return the file and notify for events
     * @param url       location to download the file
     */
    public static void downloadXForm(MainCallback mCallback, String url) {
        // TODO: 1/26/2018 downloadManifestAndMediaFiles
        File file = PmCoreUtils.createLocalFileFromUrl(applicationContext, url,
                PmCoreConstants.FILE_TYPE_XFORM);
        if (file.exists()) {//it is downloaded
            if (mCallback != null) mCallback.onPostExecute(new NetworkResponse(NetworkResponse.FIXE_EXIST, file));
        } else {
            NetworkManager.downloadXForm(mCallback, url, file);
        }
    }

    public static void loadXForm(File file, FormLoaderTask.FormLoaderListener callback) {
        FormLoaderTask task = new FormLoaderTask(null, null, null);
        task.execute(file.getAbsolutePath());
        task.setFormLoaderListener(callback);
    }
}
