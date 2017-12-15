package com.peermountain.core.persistence;

import android.support.annotation.NonNull;

import com.peermountain.core.model.guarded.Contact;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Galeen on 12/15/2017.
 */
public class ShareHelperTest {
    @Test
    public void setData() throws Exception {
        Contact contact = generateContact();
        assertNotNull("no Data set for QR code",
                ShareHelper.setData(contact));
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
}