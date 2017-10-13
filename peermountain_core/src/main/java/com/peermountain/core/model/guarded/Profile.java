package com.peermountain.core.model.guarded;

import java.util.ArrayList;

/**
 * Created by Galeen on 10/11/2017.
 */

public class Profile extends Contact{
    private ArrayList<Document> documents = new ArrayList<>();
    public ArrayList<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<Document> documents) {
        this.documents = documents;
    }
//
//    private String mail, phone, names, dob, pob, imageUri, pictureUrl;
//    private PublicUser fbProfile, lnProfile;
//
//
//
//    public String getMail() {
//        return mail;
//    }
//
//    public void setMail(String mail) {
//        this.mail = mail;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public PublicUser getFbProfile() {
//        return fbProfile;
//    }
//
//    public void setFbProfile(PublicUser fbProfile) {
//        this.fbProfile = fbProfile;
//    }
//
//    public PublicUser getLnProfile() {
//        return lnProfile;
//    }
//
//    public void setLnProfile(PublicUser lnProfile) {
//        this.lnProfile = lnProfile;
//    }
//
//    public String getNames() {
//        return names;
//    }
//
//    public void setNames(String names) {
//        this.names = names;
//    }
//
//    public String getDob() {
//        return dob;
//    }
//
//    public void setDob(String dob) {
//        this.dob = dob;
//    }
//
//    public String getPob() {
//        return pob;
//    }
//
//    public void setPob(String pob) {
//        this.pob = pob;
//    }
//
//    public String getImageUri() {
//        return imageUri;
//    }
//
//    public void setImageUri(String imageUri) {
//        this.imageUri = imageUri;
//    }
//
//    public String getPictureUrl() {
//        return pictureUrl;
//    }
//
//    public void setPictureUrl(String pictureUrl) {
//        this.pictureUrl = pictureUrl;
//    }
}
