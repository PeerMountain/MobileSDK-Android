package com.peermountain.core.model.guarded;

import android.os.Parcel;
import android.os.Parcelable;

import com.ariadnext.android.smartsdk.interfaces.bean.AXTImageResult;

/**
 * Created by Galeen on 10/11/2017.
 */

public class Document implements Parcelable {
    private String lastName, firstName, gender, birthday, docNumber, country, emitDate,
            mrzID, expirationDate;
    private AXTImageResult imageSource,imageSourceBack,imageCropped, imageCroppedBack,imageFace,imageCroppedSmall, imageCroppedBackSmall;
    private boolean valid;


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmitDate() {
        return emitDate;
    }

    public void setEmitDate(String emitDate) {
        this.emitDate = emitDate;
    }

    public String getMrzID() {
        return mrzID;
    }

    public void setMrzID(String mrzID) {
        this.mrzID = mrzID;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public AXTImageResult getImageSource() {
        return imageSource;
    }

    public void setImageSource(AXTImageResult imageSource) {
        this.imageSource = imageSource;
    }

    public AXTImageResult getImageSourceBack() {
        return imageSourceBack;
    }

    public void setImageSourceBack(AXTImageResult imageSourceBack) {
        this.imageSourceBack = imageSourceBack;
    }

    public AXTImageResult getImageCropped() {
        return imageCropped;
    }

    public void setImageCropped(AXTImageResult imageCropped) {
        this.imageCropped = imageCropped;
    }

    public AXTImageResult getImageCroppedBack() {
        return imageCroppedBack;
    }

    public void setImageCroppedBack(AXTImageResult imageCroppedBack) {
        this.imageCroppedBack = imageCroppedBack;
    }

    public AXTImageResult getImageFace() {
        return imageFace;
    }

    public void setImageFace(AXTImageResult imageFace) {
        this.imageFace = imageFace;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public AXTImageResult getImageCroppedSmall() {
        return imageCroppedSmall;
    }

    public void setImageCroppedSmall(AXTImageResult imageCroppedSmall) {
        this.imageCroppedSmall = imageCroppedSmall;
    }

    public AXTImageResult getImageCroppedBackSmall() {
        return imageCroppedBackSmall;
    }

    public void setImageCroppedBackSmall(AXTImageResult imageCroppedBackSmall) {
        this.imageCroppedBackSmall = imageCroppedBackSmall;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lastName);
        dest.writeString(this.firstName);
        dest.writeString(this.gender);
        dest.writeString(this.birthday);
        dest.writeString(this.docNumber);
        dest.writeString(this.country);
        dest.writeString(this.emitDate);
        dest.writeString(this.mrzID);
        dest.writeString(this.expirationDate);
        dest.writeSerializable(this.imageSource);
        dest.writeSerializable(this.imageSourceBack);
        dest.writeSerializable(this.imageCropped);
        dest.writeSerializable(this.imageCroppedBack);
        dest.writeSerializable(this.imageFace);
        dest.writeSerializable(this.imageCroppedSmall);
        dest.writeSerializable(this.imageCroppedBackSmall);
        dest.writeByte(this.valid ? (byte) 1 : (byte) 0);
    }

    public Document() {
    }

    protected Document(Parcel in) {
        this.lastName = in.readString();
        this.firstName = in.readString();
        this.gender = in.readString();
        this.birthday = in.readString();
        this.docNumber = in.readString();
        this.country = in.readString();
        this.emitDate = in.readString();
        this.mrzID = in.readString();
        this.expirationDate = in.readString();
        this.imageSource = (AXTImageResult) in.readSerializable();
        this.imageSourceBack = (AXTImageResult) in.readSerializable();
        this.imageCropped = (AXTImageResult) in.readSerializable();
        this.imageCroppedBack = (AXTImageResult) in.readSerializable();
        this.imageFace = (AXTImageResult) in.readSerializable();
        this.imageCroppedSmall = (AXTImageResult) in.readSerializable();
        this.imageCroppedBackSmall = (AXTImageResult) in.readSerializable();
        this.valid = in.readByte() != 0;
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel source) {
            return new Document(source);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };
}
