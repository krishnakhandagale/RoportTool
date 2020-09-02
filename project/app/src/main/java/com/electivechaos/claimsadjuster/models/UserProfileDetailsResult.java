package com.electivechaos.claimsadjuster.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileDetailsResult implements Parcelable {

    @SerializedName("BusinessAccountID")
    @Expose
    private String businessAccountID;
    @SerializedName("FirstName")
    @Expose
    private String firstName;
    @SerializedName("LastName")
    @Expose
    private String lastName;
    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("Thumbnails")
    @Expose
    private UserProfileDetailsThumbnails thumbnails;
    @SerializedName("ChannelID")
    @Expose
    private String channelId;
    @SerializedName("ProjectID")
    @Expose
    private String projectId;
    @SerializedName("Email")
    @Expose
    private String emailId;

    @SerializedName("LibraryID")
    @Expose
    private String libraryId;

    protected UserProfileDetailsResult(Parcel in) {
        businessAccountID = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        userName = in.readString();
        thumbnails = in.readParcelable(UserProfileDetailsThumbnails.class.getClassLoader());
        channelId = in.readString();
        projectId = in.readString();
        emailId = in.readString();
        libraryId = in.readString();
    }

    public static final Creator<UserProfileDetailsResult> CREATOR = new Creator<UserProfileDetailsResult>() {
        @Override
        public UserProfileDetailsResult createFromParcel(Parcel in) {
            return new UserProfileDetailsResult(in);
        }

        @Override
        public UserProfileDetailsResult[] newArray(int size) {
            return new UserProfileDetailsResult[size];
        }
    };

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }



    public String getBusinessAccountID() {
        return businessAccountID;
    }

    public void setBusinessAccountID(String businessAccountID) {
        this.businessAccountID = businessAccountID;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserProfileDetailsThumbnails getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(UserProfileDetailsThumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(businessAccountID);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(userName);
        dest.writeParcelable(thumbnails, flags);
        dest.writeString(channelId);
        dest.writeString(projectId);
        dest.writeString(emailId);
        dest.writeString(libraryId);
    }


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(String libraryId) {
        this.libraryId = libraryId;
    }
}
