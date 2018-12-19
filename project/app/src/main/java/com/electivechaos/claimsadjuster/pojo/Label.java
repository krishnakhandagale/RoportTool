package com.electivechaos.claimsadjuster.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by barkhasikka on 26/04/18.
 */

public class Label implements Parcelable {
    public static final Creator<Label> CREATOR = new Creator<Label>() {
        @Override
        public Label createFromParcel(Parcel in) {
            return new Label(in);
        }

        @Override
        public Label[] newArray(int size) {
            return new Label[size];
        }
    };
    private String id;
    private String name;
    private String description;
    private int categoryID;
    private String reportId;
    private ArrayList<ImageDetailsPOJO> selectedImages;
    private ArrayList<ImageDetailsPOJO> selectedElevationImages;
    private String houseNumber;
    private String coverageType;

    public Label() {
        id = "";
        name = "";
        description = "";
        categoryID = -1;
        reportId = "";
        selectedImages = new ArrayList<>();
        selectedElevationImages = new ArrayList<>();
        houseNumber = "";
        coverageType = "";

    }

    protected Label(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        categoryID = in.readInt();
        reportId = in.readString();
        selectedImages = in.createTypedArrayList(ImageDetailsPOJO.CREATOR);
        selectedElevationImages = in.createTypedArrayList(ImageDetailsPOJO.CREATOR);
        houseNumber = in.readString();
        coverageType = in.readString();
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<ImageDetailsPOJO> getSelectedImages() {
        return selectedImages;
    }

    public void setSelectedImages(ArrayList<ImageDetailsPOJO> selectedImages) {
        this.selectedImages = selectedImages;
    }

    public ArrayList<ImageDetailsPOJO> getSelectedElevationImages() {
        return selectedElevationImages;
    }

    public void setSelectedElevationImages(ArrayList<ImageDetailsPOJO> selectedElevationImages) {
        this.selectedElevationImages = selectedElevationImages;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int id) {
        this.categoryID = id;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCoverageType() {
        return coverageType;
    }

    public void setCoverageType(String coverageType) {
        this.coverageType = coverageType;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(categoryID);
        dest.writeString(reportId);
        dest.writeTypedList(selectedImages);
        dest.writeTypedList(selectedElevationImages);
        dest.writeString(houseNumber);
        dest.writeString(coverageType);
    }
}
