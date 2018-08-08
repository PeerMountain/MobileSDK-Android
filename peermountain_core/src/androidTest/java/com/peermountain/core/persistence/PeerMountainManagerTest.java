package com.peermountain.core.persistence;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.zxing.Result;
import com.peermountain.core.BuildConfig;
import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.model.guarded.Contact;
import com.peermountain.common.model.DocumentID;
import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.model.guarded.PmAccessToken;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.model.guarded.PublicUser;
import com.peermountain.core.model.guarded.ShareObject;
import com.peermountain.core.model.unguarded.Keyword;
import com.peermountain.core.model.unguarded.Keywords;
import com.peermountain.core.utils.constants.PmCoreConstants;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Galeen on 12/15/2017.
 */
@RunWith(AndroidJUnit4.class)
public class PeerMountainManagerTest extends PmBaseInstrumentedTest {
    @Test
    public void testAppContext() throws Exception {
        Log.e("id",BuildConfig.APPLICATION_ID);
        assertEquals("com.peermountain.core.test", appContext.getPackageName());
    }

    @Test
    public void getLastPeerMountainConfig() throws Exception {
        //init has been called in onPrepare
        PeerMountainConfig pmc = PeerMountainManager.getLastPeerMountainConfig(appContext);
        Assert.assertNotNull("No PeerMountainConfig", pmc);
        Assert.assertNotNull("Not set license in PeerMountainConfig", pmc.getIdCheckLicense());
        Assert.assertNotSame("Not set license in PeerMountainConfig", "",
                pmc.getIdCheckLicense());
    }

    @Test
    public void testPin() throws Exception {
        PeerMountainManager.savePin("12345");
        assertEquals("not same pin", "12345",
                PeerMountainManager.getPin());
        Cache.getInstance().clearCache();
        assertEquals("not same pin DB", "12345",
                PeerMountainManager.getPin());
    }

    @Test
    public void testLiAccessToken() throws Exception {
        PmAccessToken token = new PmAccessToken("token", 1000);
        PeerMountainManager.saveLiAccessToken(token);
        assertEquals("not same LiAccessToken", token,
                PeerMountainManager.getLiAccessToken());
        Cache.getInstance().clearCache();
        assertNotNull("no LiAccessToken in DB",
                PeerMountainManager.getLiAccessToken());
        assertEquals("not same LiAccessToken in DB", token.getAccessTokenValue(),
                PeerMountainManager.getLiAccessToken().getAccessTokenValue());
        assertEquals("not same LiAccessToken expire in DB", token.getExpiresOn(),
                PeerMountainManager.getLiAccessToken().getExpiresOn());
    }

    @Test
    public void testProfile() throws Exception {
        Profile profile = new Profile();
        DocumentID doc = new DocumentID();
        doc.setFirstName("Name");
        doc.setBirthday("Dob");
        profile.getDocuments().add(doc);
        profile.setNames("Name");
        profile.setDob("Dob");
        profile.setPob("Pob");
        profile.setMail("aa@aa.aa");
        profile.setPhone("+888888");
        profile.setImageUri("ImageUri");
        profile.setLiveSelfie(new ArrayList<String>(Arrays.asList("London", "Tokyo", "New York")));
        profile.setPictureUrl("PictureUrl");
        profile.getPublicProfiles().add(new PublicUser("id", "mail", "fName", "lName", "picUrl"));

        PeerMountainManager.saveProfile(profile);
        assertEquals("not same profile", profile,
                PeerMountainManager.getProfile());
        Cache.getInstance().clearCache();

        assertEquals("not same profile in DB docs", profile.getDocuments().size(),
                PeerMountainManager.getProfile().getDocuments().size());
        assertEquals("not same profile in DB selfie", profile.getLiveSelfie().size(),
                PeerMountainManager.getProfile().getLiveSelfie().size());
        assertEquals("not same profile in DB mail", profile.getMail(),
                PeerMountainManager.getProfile().getMail());
    }

    @Test
    public void testShareObject() throws Exception {
        ShareObject so = new ShareObject(ShareObject.OPERATION_SHARE_FINISH);
        Contact contact = new Contact();
        contact.setNames("Name");
        contact.setDob("Dob");
        contact.setPob("Pob");
        contact.setMail("aa@aa.aa");
        contact.setPhone("+888888");
        contact.setImageUri("ImageUri");
        contact.setPictureUrl("PictureUrl");
        contact.getPublicProfiles().add(new PublicUser("id", "mail", "fName", "lName", "picUrl"));
        so.setContact(contact);

        String jsonShareObject = PeerMountainManager.shareObjectToJson(so);

        ShareObject parsedShareObject = PeerMountainManager.parseSharedObject(jsonShareObject);
        String parsedJsonShareObject = PeerMountainManager.shareObjectToJson(parsedShareObject);

        assertEquals("not same ShareObject, problem in parsing", jsonShareObject,
                parsedJsonShareObject);

    }

    @Test
    public void testTutoSeen() throws Exception {
        PeerMountainManager.saveTutoSeen();
        assertTrue("tutoSeen not saved",
                PeerMountainManager.isTutoSeen());
    }

    @Test
    public void testAddContact() throws Exception {
        int countBefore = PeerMountainManager.getContacts().size();
        PeerMountainManager.addContact(new Contact());
        assertEquals("contact not added", countBefore + 1,
                PeerMountainManager.getContacts().size());

        Cache.getInstance().clearCache();
        assertEquals("contact not added in DB", countBefore + 1,
                PeerMountainManager.getContacts().size());
    }

    @Test
    public void testRemoveContact() throws Exception {
        Contact contact = new Contact();
        PeerMountainManager.addContact(contact);
        int countBefore = PeerMountainManager.getContacts().size();
        PeerMountainManager.removeContact(contact);
        assertEquals("contact not removed", countBefore - 1,
                PeerMountainManager.getContacts().size());

        Cache.getInstance().clearCache();
        assertEquals("contact not removed in DB", countBefore - 1,
                PeerMountainManager.getContacts().size());
    }

    @Test
    public void testUpdateContact() throws Exception {
        Contact contact = new Contact();
        contact.setNames("name");
        PeerMountainManager.addContact(contact);
        contact.setNames("nameNew");
        PeerMountainManager.updateContact(contact);

        boolean found = false;
        for (Contact contact1 : PeerMountainManager.getContacts()) {
            if (contact1.getNames() != null && contact1.getNames().equals("nameNew")) {
                found = true;
                break;
            }
        }
        assertTrue("contact not updated", found);

        Cache.getInstance().clearCache();
        found = false;
        for (Contact contact1 : PeerMountainManager.getContacts()) {
            if (contact1.getNames() != null && contact1.getNames().equals("nameNew")) {
                found = true;
                break;
            }
        }
        assertTrue("contact not updated in DB", found);
    }

    @Test
    public void testSaveDocuments() throws Exception {
        PeerMountainManager.saveDocuments(null);
        assertEquals("docs not empty", 0,
                PeerMountainManager.getDocuments().size());

        Cache.getInstance().clearCache();
        assertEquals("docs not empty in DB", 0,
                PeerMountainManager.getDocuments().size());

        PeerMountainManager.saveDocuments(new ArrayList<AppDocument>(Arrays.asList(new AppDocument("title"))));
        assertEquals("docs not saved", 1,
                PeerMountainManager.getDocuments().size());

        Cache.getInstance().clearCache();
        assertEquals("docs not saved in DB", 1,
                PeerMountainManager.getDocuments().size());
    }

    @Test
    public void testAddDocument() throws Exception {
        int countBefore = PeerMountainManager.getDocuments().size();
        PeerMountainManager.addDocument(new AppDocument("titleAdd"));
        assertEquals("doc not added", countBefore + 1,
                PeerMountainManager.getDocuments().size());

        Cache.getInstance().clearCache();
        assertEquals("doc not added in DB", countBefore + 1,
                PeerMountainManager.getDocuments().size());
    }

    @Test
    public void testRemoveDocument() throws Exception {
        AppDocument doc = new AppDocument("titleToRemove");
        PeerMountainManager.addDocument(doc);
        int countBefore = PeerMountainManager.getDocuments().size();
        PeerMountainManager.removeDocument(doc);
        assertEquals("doc not removed", countBefore - 1,
                PeerMountainManager.getDocuments().size());

        Cache.getInstance().clearCache();
        assertEquals("doc not removed in DB", countBefore - 1,
                PeerMountainManager.getDocuments().size());
    }

    @Test
    public void testUpdateDocument() throws Exception {
        AppDocument doc = new AppDocument("titleInit");
        PeerMountainManager.addDocument(doc);
        doc.setTitle("titleUpdated");
        PeerMountainManager.updateDocument(doc);

        boolean found = false;
        for (AppDocument doc1 : PeerMountainManager.getDocuments()) {
            if (doc1.getTitle() != null && doc1.getTitle().equals("titleUpdated")) {
                found = true;
                break;
            }
        }
        assertTrue("doc not updated", found);

        Cache.getInstance().clearCache();
        found = false;
        for (AppDocument doc1 : PeerMountainManager.getDocuments()) {
            if (doc1.getTitle() != null && doc1.getTitle().equals("titleUpdated")) {
                found = true;
                break;
            }
        }
        assertTrue("doc not updated", found);
    }

    @Test
    public void testKeywords() throws Exception {
        saveMyKeywords();
        assertEquals("MyKeywords not saved", 6,
                PeerMountainManager.getSavedKeywordsObject().getKeywords().size());
    }

    private Keywords saveMyKeywords() {
        Keywords keywords = new Keywords(new ArrayList<Keyword>(Arrays.asList(
                new Keyword("fomes"),
                new Keyword("femurs"),
                new Keyword("belam"),
                new Keyword("picks"),
                new Keyword("uparm"),
                new Keyword("outlot")
        )));
        PeerMountainManager.saveKeywordsObject(keywords);
        return keywords;
    }

    @Test
    public void testRandomKeywords() throws Exception {
        assertEquals("random Keywords are not the right size", PmCoreConstants.KEYWORDS_SHOW_COUNT,
                PeerMountainManager.getRandomKeywords(appContext).getKeywords().size());
    }

    @Test
    public void testRandomKeywordsWithMineIncluded() throws Exception {
        Keywords keywords = saveMyKeywords();

        Keywords randomKeywords = PeerMountainManager.getRandomKeywordsWithSavedIncluded(appContext);
        assertEquals("RandomKeywords with mine not the right size",
                PmCoreConstants.KEYWORDS_SHOW_TO_VALIDATE_COUNT,
                randomKeywords.getKeywords().size());

        boolean found = false;
        for (Keyword keyword : keywords.getKeywords()) {
            found = false;
            for (Keyword keywordRandom : randomKeywords.getKeywords()) {
                if (keyword.getValue().equals(keywordRandom.getValue())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                break;
            }
        }
        assertTrue("my keywords are not in the random one", found);
    }

    @Test
    public void getQrCode() throws Exception {
        Contact contact = generateContact();
        assertNotNull("no QR code generated",
                PeerMountainManager.getQrCode(contact,100, Color.BLUE));
    }

//    @Test //moved to unit test
//    public void getSetDataForQrCode() throws Exception {
//        Contact contact = generateContact();
//        assertNotNull("no Data set for QR code",
//                ShareHelper.setData(contact));
//    }

    @Test
    public void handleQrScanResult() throws Exception {
        Contact contact = generateContact();
        String data =  ShareHelper.setData(contact);
        Result scanResult = new Result(data,null,null,null);
        Contact contactScanned = ShareHelper.handleResult(scanResult);
        assertNotNull("can't parse scanned contact",contactScanned);
        assertEquals("not same dob",contact.getDob(),contactScanned.getDob());
        assertEquals("not same mail",contact.getMail(),contactScanned.getMail());
        assertEquals("not same phone",contact.getPhone(),contactScanned.getPhone());
        assertEquals("not same pob",contact.getPob(),contactScanned.getPob());
        assertEquals("not same names",contact.getNames(),contactScanned.getNames());
//        assertEquals("not same ids",contact,contactScanned);// ids are not sent
    }

    @NonNull
    private Contact generateContact() {
        Contact contact = new Contact();
        contact.setNames("some names");
        contact.setPob("pob");
        contact.setDob("dob");
        contact.setPhone("phone");
        contact.setMail("mail");
        return contact;
    }

    @Test
    public void parseFbPublicProfile() throws Exception {
        JSONObject userJ = new JSONObject("{\"id\":\"1936131533371586\",\"first_name\":\"Andro\",\"last_name\":\"Andro\",\"gender\":\"female\",\"picture\":{\"data\":{\"height\":50,\"is_silhouette\":false,\"url\":\"https:\\/\\/scontent.xx.fbcdn.net\\/v\\/t1.0-1\\/c2.0.50.50\\/p50x50\\/11008790_1419054108412667_4556725290994166576_n.jpg?oh=e12f9e34d187632b9e5742eebe64987b&oe=5AC9926F\",\"width\":50}}}");
        PublicUser fbUser = PeerMountainManager.parseFbPublicProfile(userJ);
        assertNotNull("no fb user parsed",fbUser);
        assertEquals("not same name","Andro",fbUser.getFirstname());
        assertEquals("not same id","1936131533371586",fbUser.getLinked_in());
    }
}