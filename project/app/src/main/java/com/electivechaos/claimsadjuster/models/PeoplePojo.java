package com.electivechaos.claimsadjuster.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PeoplePojo implements Parcelable {
    public static final Creator<PeoplePojo> CREATOR = new Creator<PeoplePojo>() {
        @Override
        public PeoplePojo createFromParcel(Parcel in) {
            return new PeoplePojo(in);
        }

        @Override
        public PeoplePojo[] newArray(int size) {
            return new PeoplePojo[size];
        }
    };
    boolean isSelected = false;
    @SerializedName("UserID")
    @Expose
    private String userID;
    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("FirstName")
    @Expose
    private String firstName;
    @SerializedName("LastName")
    @Expose
    private String lastName;
    @SerializedName("PrimaryEmail")
    @Expose
    private String primaryEmail;
    @SerializedName("ContactNumber")
    @Expose
    private String contactNumber;
    @SerializedName("PermittedTo")
    @Expose
    private String permittedTo;
    @SerializedName("IsProjectUser")
    @Expose
    private Boolean isProjectUser;
    @SerializedName("Thumbnails")
    @Expose
    private PeopleGetAllThumbnails thumbnails;
    private boolean isClickable = true;
    @SerializedName("IsSystemUser")
    @Expose
    private boolean isVmUser = true;
    private String password;

    protected PeoplePojo(Parcel in) {
        userID = in.readString();
        userName = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        primaryEmail = in.readString();
        contactNumber = in.readString();
        permittedTo = in.readString();
        byte tmpIsProjectUser = in.readByte();
        isProjectUser = tmpIsProjectUser == 0 ? null : tmpIsProjectUser == 1;
        thumbnails = in.readParcelable(PeoplePojo.class.getClassLoader());
        isSelected = in.readByte() != 0;
        isClickable = in.readByte() != 0;
        isVmUser = in.readByte() != 0;

    }

    public PeoplePojo() {

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVmUser() {
        return isVmUser;
    }

    public void setVmUser(boolean vmUser) {
        isVmUser = vmUser;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPermittedTo() {
        return permittedTo;
    }

    public void setPermittedTo(String permittedTo) {
        this.permittedTo = permittedTo;
    }

    public Boolean getIsProjectUser() {
        return isProjectUser;
    }

    public void setIsProjectUser(Boolean isProjectUser) {
        this.isProjectUser = isProjectUser;
    }

    public PeopleGetAllThumbnails getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(PeopleGetAllThumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(userName);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(primaryEmail);
        dest.writeString(contactNumber);
        dest.writeString(permittedTo);
        dest.writeByte((byte) (isProjectUser == null ? 0 : isProjectUser ? 1 : 2));
        dest.writeParcelable(thumbnails, flags);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (isClickable ? 1 : 0));
        dest.writeByte((byte) (isVmUser ? 1 : 0));

    }
}
