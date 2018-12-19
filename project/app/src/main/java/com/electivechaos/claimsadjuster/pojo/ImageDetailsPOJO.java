package com.electivechaos.claimsadjuster.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nafeea on 4/24/18.
 */

public class ImageDetailsPOJO implements Parcelable {

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
    private String imageId;
    private String imageUrl;
    private String title;
    private String description;
    private boolean isDamage;
    private boolean isOverview;
    private boolean isPointOfOrigin;
    private String coverageTye; // Default value from label
    private String imageName;
    private String imageDateTime;
    private String imageGeoTag;

    protected ImageDetailsPOJO(Parcel in) {
        imageId = in.readString();
        imageUrl = in.readString();
        title = in.readString();
        description = in.readString();
        isDamage = in.readByte() != 0;
        isOverview = in.readByte() != 0;
        isPointOfOrigin = in.readByte() != 0;
        coverageTye = in.readString();
        imageName = in.readString();
        imageDateTime = in.readString();
        imageGeoTag = in.readString();

    }

    public ImageDetailsPOJO() {
        this.imageId = "";
        this.imageUrl = "";
        this.title = "";
        this.description = "";
        this.coverageTye = "";
        this.imageName = "";
        this.imageDateTime = "";
        this.imageGeoTag = "";
    }

    public boolean isPointOfOrigin() {
        return isPointOfOrigin;
    }

    public void setPointOfOrigin(boolean pointOfOrigin) {
        isPointOfOrigin = pointOfOrigin;
    }

    public boolean isDamage() {
        return isDamage;
    }

    public void setDamage(boolean damage) {
        isDamage = damage;
    }

    public void setIsDamage(boolean damage) {
        isDamage = damage;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
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

    public boolean isOverview() {
        return isOverview;
    }

    public void setOverview(boolean overview) {
        isOverview = overview;
    }

    public String getCoverageTye() {
        return coverageTye;
    }

    public void setCoverageTye(String coverageTye) {
        this.coverageTye = coverageTye;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageDateTime() {
        return imageDateTime;
    }

    public void setImageDateTime(String imageDateTime) {
        this.imageDateTime = imageDateTime;
    }

    public String getImageGeoTag() {
        return imageGeoTag;
    }

    public void setImageGeoTag(String imageGeoTag) {
        this.imageGeoTag = imageGeoTag;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageId);
        dest.writeString(imageUrl);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByte((byte) (isDamage ? 1 : 0));
        dest.writeByte((byte) (isOverview ? 1 : 0));
        dest.writeByte((byte) (isPointOfOrigin ? 1 : 0));
        dest.writeString(coverageTye);
        dest.writeString(imageName);
        dest.writeString(imageDateTime);
        dest.writeString(imageGeoTag);
    }
}
