package com.peermountain.common.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by Galeen on 10/11/2017.
 */

public class DocumentID implements Parcelable {
    private String lastName, firstName, gender, birthday, docNumber, country, emitDate,
            mrzID, expirationDate, type, errorMessage;
    private ImageResult imageSource,imageSourceBack,imageCropped, imageCroppedBack,imageFace,imageCroppedSmall, imageCroppedBackSmall;
    private boolean dobCheck,mrzCheck,numberCheck,doeCheck;



    public DocumentID() {
    }

//    public DocumentID(DocumentID d) {
//        this.lastName = d.lastName;
//        this.firstName = d.firstName;
//        this.gender = gender;
//        this.birthday = birthday;
//        this.docNumber = docNumber;
//        this.country = country;
//        this.emitDate = emitDate;
//        this.mrzID = mrzID;
//        this.expirationDate = expirationDate;
//        this.type = type;
//        this.errorMessage = errorMessage;
//        this.imageSource = imageSource;
//        this.imageSourceBack = imageSourceBack;
//        this.imageCropped = imageCropped;
//        this.imageCroppedBack = imageCroppedBack;
//        this.imageFace = imageFace;
//        this.imageCroppedSmall = imageCroppedSmall;
//        this.imageCroppedBackSmall = imageCroppedBackSmall;
//        this.valid = valid;
//    }

    public boolean checkIsValid(){
        return !TextUtils.isEmpty(lastName)&&!TextUtils.isEmpty(firstName)&&!TextUtils.isEmpty(gender)&&!TextUtils.isEmpty(docNumber)&&!TextUtils.isEmpty(country)&&!TextUtils.isEmpty(birthday);
    }


    public void deleteDocumentImages() {
        deleteFile(imageCropped);
        deleteFile(imageCroppedBack);
        deleteFile(imageCroppedSmall);
        deleteFile(imageCroppedBackSmall);
        deleteFile(imageFace);
        deleteFile(imageSource);
        deleteFile(imageSourceBack);
    }

    private void deleteFile(ImageResult image) {
        if (image != null) {
            deleteFile(image.getImageUri());
        }
    }

    private void deleteFile(String uri) {
        if (!TextUtils.isEmpty(uri)) {
            File file = new File(Uri.parse(uri).getPath());
            if (file.exists()) file.delete();
        }
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ImageResult getImageSource() {
        return imageSource;
    }

    public void setImageSource(ImageResult imageSource) {
        this.imageSource = imageSource;
    }

    public ImageResult getImageSourceBack() {
        return imageSourceBack;
    }

    public void setImageSourceBack(ImageResult imageSourceBack) {
        this.imageSourceBack = imageSourceBack;
    }

    public ImageResult getImageCropped() {
        return imageCropped;
    }

    public void setImageCropped(ImageResult imageCropped) {
        this.imageCropped = imageCropped;
    }

    public ImageResult getImageCroppedBack() {
        return imageCroppedBack;
    }

    public void setImageCroppedBack(ImageResult imageCroppedBack) {
        this.imageCroppedBack = imageCroppedBack;
    }

    public ImageResult getImageFace() {
        return imageFace;
    }

    public void setImageFace(ImageResult imageFace) {
        this.imageFace = imageFace;
    }

    public ImageResult getImageCroppedSmall() {
        return imageCroppedSmall;
    }

    public void setImageCroppedSmall(ImageResult imageCroppedSmall) {
        this.imageCroppedSmall = imageCroppedSmall;
    }

    public ImageResult getImageCroppedBackSmall() {
        return imageCroppedBackSmall;
    }

    public void setImageCroppedBackSmall(ImageResult imageCroppedBackSmall) {
        this.imageCroppedBackSmall = imageCroppedBackSmall;
    }

    public boolean isDobCheck() {
        return dobCheck;
    }

    public void setDobCheck(boolean dobCheck) {
        this.dobCheck = dobCheck;
    }

    public boolean isMrzCheck() {
        return mrzCheck;
    }

    public void setMrzCheck(boolean mrzCheck) {
        this.mrzCheck = mrzCheck;
    }

    public boolean isNumberCheck() {
        return numberCheck;
    }

    public void setNumberCheck(boolean numberCheck) {
        this.numberCheck = numberCheck;
    }

    public boolean isDoeCheck() {
        return doeCheck;
    }

    public void setDoeCheck(boolean doeCheck) {
        this.doeCheck = doeCheck;
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
        dest.writeString(this.type);
        dest.writeString(this.errorMessage);
        dest.writeSerializable(this.imageSource);
        dest.writeSerializable(this.imageSourceBack);
        dest.writeSerializable(this.imageCropped);
        dest.writeSerializable(this.imageCroppedBack);
        dest.writeSerializable(this.imageFace);
        dest.writeSerializable(this.imageCroppedSmall);
        dest.writeSerializable(this.imageCroppedBackSmall);
        dest.writeByte(this.dobCheck ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mrzCheck ? (byte) 1 : (byte) 0);
        dest.writeByte(this.numberCheck ? (byte) 1 : (byte) 0);
        dest.writeByte(this.doeCheck ? (byte) 1 : (byte) 0);
    }

    protected DocumentID(Parcel in) {
        this.lastName = in.readString();
        this.firstName = in.readString();
        this.gender = in.readString();
        this.birthday = in.readString();
        this.docNumber = in.readString();
        this.country = in.readString();
        this.emitDate = in.readString();
        this.mrzID = in.readString();
        this.expirationDate = in.readString();
        this.type = in.readString();
        this.errorMessage = in.readString();
        this.imageSource = (ImageResult) in.readSerializable();
        this.imageSourceBack = (ImageResult) in.readSerializable();
        this.imageCropped = (ImageResult) in.readSerializable();
        this.imageCroppedBack = (ImageResult) in.readSerializable();
        this.imageFace = (ImageResult) in.readSerializable();
        this.imageCroppedSmall = (ImageResult) in.readSerializable();
        this.imageCroppedBackSmall = (ImageResult) in.readSerializable();
        this.dobCheck = in.readByte() != 0;
        this.mrzCheck = in.readByte() != 0;
        this.numberCheck = in.readByte() != 0;
        this.doeCheck = in.readByte() != 0;
    }

    public static final Parcelable.Creator<DocumentID> CREATOR = new Parcelable.Creator<DocumentID>() {
        @Override
        public DocumentID createFromParcel(Parcel source) {
            return new DocumentID(source);
        }

        @Override
        public DocumentID[] newArray(int size) {
            return new DocumentID[size];
        }
    };
}