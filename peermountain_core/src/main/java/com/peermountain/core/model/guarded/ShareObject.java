package com.peermountain.core.model.guarded;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by Galeen on 10/17/2017.
 */

public class ShareObject implements Parcelable {
    public static final String OPERATION_SHARE_CONTACT_DATA = "SHARE_CONTACT_DATA";
    public static final String OPERATION_SHARE_FINISH = "SHARE_FINISH";
    public static final String OPERATION_SHARE_CONFIRM_FINISH = "SHARE_CONFIRM_FINISH";
    public static final String OPERATION_SHARE_CONTACT_IMAGE_FILE = "SHARE_CONTACT_IMAGE_FILE";

    private String operation;
    private Contact contact;

    private transient File receivedFile;

    public ShareObject() {
    }

    public ShareObject(String operation, File receivedFile) {
        this.operation = operation;
        this.receivedFile = receivedFile;
    }

    public ShareObject(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public File getReceivedFile() {
        return receivedFile;
    }

    public void setReceivedFile(File receivedFile) {
        this.receivedFile = receivedFile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.operation);
        dest.writeParcelable(this.contact, flags);
    }

    protected ShareObject(Parcel in) {
        this.operation = in.readString();
        this.contact = in.readParcelable(Contact.class.getClassLoader());
    }

    public static final Parcelable.Creator<ShareObject> CREATOR = new Parcelable.Creator<ShareObject>() {
        @Override
        public ShareObject createFromParcel(Parcel source) {
            return new ShareObject(source);
        }

        @Override
        public ShareObject[] newArray(int size) {
            return new ShareObject[size];
        }
    };
}
