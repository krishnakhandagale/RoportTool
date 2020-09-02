package com.electivechaos.claimsadjuster.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileDetailsThumbnails implements Parcelable {

    @SerializedName("High")
    @Expose
    private String high;
    @SerializedName("Low")
    @Expose
    private String low;
    @SerializedName("Medium")
    @Expose
    private String medium;

    protected UserProfileDetailsThumbnails(Parcel in) {
        high = in.readString();
        low = in.readString();
        medium = in.readString();
    }

    public static final Creator<UserProfileDetailsThumbnails> CREATOR = new Creator<UserProfileDetailsThumbnails>() {
        @Override
        public UserProfileDetailsThumbnails createFromParcel(Parcel in) {
            return new UserProfileDetailsThumbnails(in);
        }

        @Override
        public UserProfileDetailsThumbnails[] newArray(int size) {
            return new UserProfileDetailsThumbnails[size];
        }
    };

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(high);
        dest.writeString(low);
        dest.writeString(medium);
    }
}
