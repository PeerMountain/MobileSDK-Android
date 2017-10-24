package com.peermountain.core.model.guarded;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Galeen on 10/13/2017.
 */

public class Contact implements Parcelable {
    private String id;
    private String mail, phone, names, dob, pob, imageUri, pictureUrl, position, placeOfWork, validatedImageUri;
    private ArrayList<PublicUser> publicProfiles = new ArrayList<>();
    private boolean validated;

    public Contact() {
        id = UUID.randomUUID().toString();
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPob() {
        return pob;
    }

    public void setPob(String pob) {
        this.pob = pob;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPlaceOfWork() {
        return placeOfWork;
    }

    public void setPlaceOfWork(String placeOfWork) {
        this.placeOfWork = placeOfWork;
    }

    public ArrayList<PublicUser> getPublicProfiles() {
        return publicProfiles;
    }

    public void setPublicProfiles(ArrayList<PublicUser> publicProfiles) {
        this.publicProfiles = publicProfiles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public String getValidatedImageUri() {
        return validatedImageUri;
    }

    public void setValidatedImageUri(String validatedImageUri) {
        this.validatedImageUri = validatedImageUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.mail);
        dest.writeString(this.phone);
        dest.writeString(this.names);
        dest.writeString(this.dob);
        dest.writeString(this.pob);
        dest.writeString(this.imageUri);
        dest.writeString(this.pictureUrl);
        dest.writeString(this.position);
        dest.writeString(this.placeOfWork);
        dest.writeString(this.validatedImageUri);
        dest.writeTypedList(this.publicProfiles);
        dest.writeByte(this.validated ? (byte) 1 : (byte) 0);
    }

    protected Contact(Parcel in) {
        this.id = in.readString();
        this.mail = in.readString();
        this.phone = in.readString();
        this.names = in.readString();
        this.dob = in.readString();
        this.pob = in.readString();
        this.imageUri = in.readString();
        this.pictureUrl = in.readString();
        this.position = in.readString();
        this.placeOfWork = in.readString();
        this.validatedImageUri = in.readString();
        this.publicProfiles = in.createTypedArrayList(PublicUser.CREATOR);
        this.validated = in.readByte() != 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
