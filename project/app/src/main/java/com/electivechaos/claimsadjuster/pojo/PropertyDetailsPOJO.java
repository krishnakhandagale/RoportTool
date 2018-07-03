package com.electivechaos.claimsadjuster.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Observable;

/**
 * Created by nafeesa on 5/23/18.
 */

public class PropertyDetailsPOJO extends Observable implements Parcelable {

    private String propertyDate;
    private String squareFootage;
    private String roofSystem;
    private String siding;
    private String foundation;
    private String buildingType;
    private String reportId;

    public String getPropertyDate() {
        return propertyDate;
    }


    public void setPropertyDate(String propertyDate) {
        this.propertyDate = propertyDate;
    }

    public String getSquareFootage() {
        return squareFootage;
    }


    public void setSquareFootage(String squareFootage) {
        this.squareFootage = squareFootage;
        setChanged();
        notifyObservers();
    }

    public String getRoofSystem() {
        return roofSystem;
    }

    public void setRoofSystem(String roofSystem) {
        this.roofSystem = roofSystem;
    }

    public String getSiding() {
        return siding;
    }

    public void setSiding(String siding) {
        this.siding = siding;
    }

    public String getFoundation() {
        return foundation;
    }

    public void setFoundation(String foundation) {
        this.foundation = foundation;
    }
    public String getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
    }


    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;

    }


    public PropertyDetailsPOJO()
    {
        propertyDate = "";
        squareFootage = "";
        roofSystem = "";
        siding = "";
        foundation = "";
        buildingType = "";
        reportId = "";
    }

    protected PropertyDetailsPOJO(Parcel in) {
        propertyDate = in.readString();
        squareFootage = in.readString();
        roofSystem = in.readString();
        siding = in.readString();
        foundation = in.readString();
        buildingType = in.readString();
        reportId = in.readString();
    }

    public static final Creator<PropertyDetailsPOJO> CREATOR = new Creator<PropertyDetailsPOJO>() {
        @Override
        public PropertyDetailsPOJO createFromParcel(Parcel in) {
            return new PropertyDetailsPOJO(in);
        }

        @Override
        public PropertyDetailsPOJO[] newArray(int size) {
            return new PropertyDetailsPOJO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(propertyDate);
        dest.writeString(squareFootage);
        dest.writeString(roofSystem);
        dest.writeString(siding);
        dest.writeString(foundation);
        dest.writeString(buildingType);
        dest.writeString(reportId);
    }
}
