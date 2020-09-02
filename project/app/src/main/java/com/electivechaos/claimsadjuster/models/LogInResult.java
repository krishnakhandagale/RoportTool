package com.electivechaos.claimsadjuster.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogInResult implements Parcelable {

    public static final Creator<LogInResult> CREATOR = new Creator<LogInResult>() {
        @Override
        public LogInResult createFromParcel(Parcel in) {
            return new LogInResult(in);
        }

        @Override
        public LogInResult[] newArray(int size) {
            return new LogInResult[size];
        }
    };
    @SerializedName("UserID")
    @Expose
    private String userID;
    @SerializedName("Token")
    @Expose
    private String token;
    @SerializedName("RefreshToken")
    @Expose
    private String refreshToken;
    @SerializedName("DomainID")
    @Expose
    private String domainID;
    @SerializedName("DefaultBusinessAccountID")
    @Expose
    private String defaultBusinessAccountID;
    @SerializedName("DefaultChannelID")
    @Expose
    private String defaultChannelID;
    @SerializedName("DefaultProjectID")
    @Expose
    private String defaultProjectID;
    @SerializedName("DefaultLibraryID")
    @Expose
    private String defaultLibraryID;
    @SerializedName("DeviceID")
    @Expose
    private String deviceID;
    @SerializedName("IsUserNotActive")
    @Expose
    private Boolean isUserNotActive;
    @SerializedName("IsUserVerified")
    @Expose
    private boolean isUserVerified;
    @SerializedName("Products")
    @Expose
    private Object products;
    @SerializedName("UserInfo")
    @Expose
    private UserProfileDetailsResult userInfo;

    protected LogInResult(Parcel in) {
        userID = in.readString();
        token = in.readString();
        refreshToken = in.readString();
        domainID = in.readString();
        defaultBusinessAccountID = in.readString();
        defaultChannelID = in.readString();
        defaultProjectID = in.readString();
        defaultLibraryID = in.readString();
        deviceID = in.readString();
        byte tmpIsUserNotActive = in.readByte();
        isUserNotActive = tmpIsUserNotActive == 0 ? null : tmpIsUserNotActive == 1;
        isUserVerified = in.readByte() != 0;
        userInfo = in.readParcelable(UserProfileDetailsResult.class.getClassLoader());
    }

    public Boolean getVerifiedUser() {
        return isUserVerified;
    }

    public void setVerifiedUser(Boolean verifiedUser) {
        isUserVerified = verifiedUser;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getDomainID() {
        return domainID;
    }

    public void setDomainID(String domainID) {
        this.domainID = domainID;
    }

    public String getDefaultBusinessAccountID() {
        return defaultBusinessAccountID;
    }

    public void setDefaultBusinessAccountID(String defaultBusinessAccountID) {
        this.defaultBusinessAccountID = defaultBusinessAccountID;
    }

    public String getDefaultChannelID() {
        return defaultChannelID;
    }

    public void setDefaultChannelID(String defaultChannelID) {
        this.defaultChannelID = defaultChannelID;
    }

    public String getDefaultProjectID() {
        return defaultProjectID;
    }

    public void setDefaultProjectID(String defaultProjectID) {
        this.defaultProjectID = defaultProjectID;
    }

    public String getDefaultLibraryID() {
        return defaultLibraryID;
    }

    public void setDefaultLibraryID(String defaultLibraryID) {
        this.defaultLibraryID = defaultLibraryID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public Boolean getIsUserNotActive() {
        return isUserNotActive;
    }

    public void setIsUserNotActive(Boolean isUserNotActive) {
        this.isUserNotActive = isUserNotActive;
    }

    public Object getProducts() {
        return products;
    }

    public void setProducts(Object products) {
        this.products = products;
    }

    public UserProfileDetailsResult getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserProfileDetailsResult userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userID);
        parcel.writeString(token);
        parcel.writeString(refreshToken);
        parcel.writeString(domainID);
        parcel.writeString(defaultBusinessAccountID);
        parcel.writeString(defaultChannelID);
        parcel.writeString(defaultProjectID);
        parcel.writeString(defaultLibraryID);
        parcel.writeString(deviceID);
        parcel.writeByte((byte) (isUserNotActive == null ? 0 : isUserNotActive ? 1 : 2));
        parcel.writeByte((byte) (isUserVerified ? 1 : 0));
        parcel.writeParcelable(userInfo, i);
    }
}
