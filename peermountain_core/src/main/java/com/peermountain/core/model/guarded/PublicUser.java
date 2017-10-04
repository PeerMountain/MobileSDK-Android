
package com.peermountain.core.model.guarded;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PublicUser implements Parcelable {
    public static final String LOGIN_TYPE_FB = "FACEBOOK";
    public static final String LOGIN_TYPE_LN = "LINKEDIN";

    @SerializedName("linked_in")
    @Expose
    private String linked_in;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("pictureUrl")
    @Expose
    private String pictureUrl;
    @SerializedName("publicProfileUrl")
    @Expose
    private String publicProfileUrl;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("companyTitle")
    @Expose
    private String companyTitle;
    @SerializedName("companyStartDate")
    @Expose
    private String companyStartDate;
    @SerializedName("companyEndDate")
    @Expose
    private String companyEndDate;
    @SerializedName("login_type")
    @Expose
    private String loginType = LOGIN_TYPE_LN;

    public PublicUser() {
    }

    public PublicUser(String linked_in, String email, String firstname, String surname, String pictureUrl) {
        this.linked_in = linked_in;
        this.email = email;
        this.firstname = firstname;
        this.surname = surname;
        this.pictureUrl = pictureUrl;
        loginType = LOGIN_TYPE_FB;
    }

    public String getLinked_in() {
        return linked_in;
    }

    public void setLinked_in(String linked_in) {
        this.linked_in = linked_in;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPublicProfileUrl() {
        return publicProfileUrl;
    }

    public void setPublicProfileUrl(String publicProfileUrl) {
        this.publicProfileUrl = publicProfileUrl;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyTitle() {
        return companyTitle;
    }

    public void setCompanyTitle(String companyTitle) {
        this.companyTitle = companyTitle;
    }

    public String getCompanyStartDate() {
        return companyStartDate;
    }

    public void setCompanyStartDate(String companyStartDate) {
        this.companyStartDate = companyStartDate;
    }

    public String getCompanyEndDate() {
        return companyEndDate;
    }

    public void setCompanyEndDate(String companyEndDate) {
        this.companyEndDate = companyEndDate;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.linked_in);
        dest.writeString(this.email);
        dest.writeString(this.firstname);
        dest.writeString(this.surname);
        dest.writeString(this.pictureUrl);
        dest.writeString(this.publicProfileUrl);
        dest.writeString(this.company);
        dest.writeString(this.companyTitle);
        dest.writeString(this.companyStartDate);
        dest.writeString(this.companyEndDate);
        dest.writeString(this.loginType);
    }

    protected PublicUser(Parcel in) {
        this.linked_in = in.readString();
        this.email = in.readString();
        this.firstname = in.readString();
        this.surname = in.readString();
        this.pictureUrl = in.readString();
        this.publicProfileUrl = in.readString();
        this.company = in.readString();
        this.companyTitle = in.readString();
        this.companyStartDate = in.readString();
        this.companyEndDate = in.readString();
        this.loginType = in.readString();
    }

    public static final Parcelable.Creator<PublicUser> CREATOR = new Parcelable.Creator<PublicUser>() {
        @Override
        public PublicUser createFromParcel(Parcel source) {
            return new PublicUser(source);
        }

        @Override
        public PublicUser[] newArray(int size) {
            return new PublicUser[size];
        }
    };
}
