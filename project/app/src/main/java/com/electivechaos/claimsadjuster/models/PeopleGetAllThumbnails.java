package com.electivechaos.claimsadjuster.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PeopleGetAllThumbnails implements Parcelable {
    @SerializedName("High")
    @Expose
    private String high;
    @SerializedName("Medium")
    @Expose
    private String medium;
    @SerializedName("Low")
    @Expose
    private String low;

    protected PeopleGetAllThumbnails(Parcel in) {
        high = in.readString();
        medium = in.readString();
        low = in.readString();
    }

    public PeopleGetAllThumbnails(String high, String medium, String low){
        this.high = high;
        this.medium = medium;
        this.low = low;

    }

    public static final Creator<PeopleGetAllThumbnails> CREATOR = new Creator<PeopleGetAllThumbnails>() {
        @Override
        public PeopleGetAllThumbnails createFromParcel(Parcel in) {
            return new PeopleGetAllThumbnails(in);
        }

        @Override
        public PeopleGetAllThumbnails[] newArray(int size) {
            return new PeopleGetAllThumbnails[size];
        }
    };

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(high);
        dest.writeString(medium);
        dest.writeString(low);
    }
}
