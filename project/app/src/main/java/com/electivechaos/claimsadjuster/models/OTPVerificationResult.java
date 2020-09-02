package com.electivechaos.claimsadjuster.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OTPVerificationResult {
    @SerializedName("UserID")
    @Expose
    private String userId;
    @SerializedName("BusinessAccountID")
    @Expose
    private Boolean businessAccountId;
    @SerializedName("ChannelID")
    @Expose
    private String channelId;
    @SerializedName("ProjectID")
    @Expose
    private String projectId;
    @SerializedName("LibraryID")
    @Expose
    private String libraryId;
    @SerializedName("OTPValue")
    @Expose
    private String otpValue;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getBusinessAccountId() {
        return businessAccountId;
    }

    public void setBusinessAccountId(Boolean businessAccountId) {
        this.businessAccountId = businessAccountId;
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

    public String getOtpValue() {
        return otpValue;
    }

    public void setOtpValue(String otpValue) {
        this.otpValue = otpValue;
    }
}
