package com.electivechaos.claimsadjuster.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by nafeea on 4/24/18.
 */

public class ImageDetailsPOJO implements Parcelable {
    private String imageUrl;
    private String title;
    private String description;
    private boolean isDamage;
    private boolean isOverview;

    protected ImageDetailsPOJO(Parcel in) {
        imageUrl = in.readString();
        title = in.readString();
        description = in.readString();
        isDamage = in.readByte() != 0;
        isOverview = in.readByte() != 0;
    }

    public static final Creator<ImageDetailsPOJO> CREATOR = new Creator<ImageDetailsPOJO>() {
        @Override
        public ImageDetailsPOJO createFromParcel(Parcel in) {
            return new ImageDetailsPOJO(in);
        }

        @Override
        public ImageDetailsPOJO[] newArray(int size) {
            return new ImageDetailsPOJO[size];
        }
    };

    public boolean isDamage() {
        return isDamage;
    }

    public void setIsDamage(boolean damage) {
        isDamage = damage;
    }

    public ImageDetailsPOJO(){
        this.imageUrl = "";
        this.title = "";
        this.description = "";
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDamage(boolean damage) {
        isDamage = damage;
    }

    public boolean isOverview() {
        return isOverview;
    }

    public void setOverview(boolean overview) {
        isOverview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByte((byte)(isDamage ? 1 : 0));
        dest.writeByte((byte)(isOverview ? 1 : 0));
    }
}
