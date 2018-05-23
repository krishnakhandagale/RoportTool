package com.electivechaos.claimsadjuster.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ReportPOJO  implements Parcelable{
    private String id;
    private String reportTitle;
    private String reportDescription;
    private String clientName;
    private String claimNumber;
    private String locationLat;
    private String locationLong;
    private String addressLine;

    private String createdDate;
    private String filePath;
    private String causeOfLoss;
    private String googleMapSnapShotFilePath;

    private ArrayList<Label> labelArrayList;

    public ReportPOJO(){
        id = "";
        reportTitle = "";
        reportDescription = "";
        clientName = "";
        claimNumber = "";
        createdDate = "";
        filePath = "";
        causeOfLoss = "";
        locationLat = "";
        locationLong = "";
        addressLine = "";
        googleMapSnapShotFilePath = "";
        labelArrayList = new ArrayList<>();
    }

    protected ReportPOJO(Parcel in) {
        id = in.readString();
        reportTitle = in.readString();
        reportDescription = in.readString();
        clientName = in.readString();
        claimNumber = in.readString();
        locationLat = in.readString();
        locationLong = in.readString();
        addressLine = in.readString();
        createdDate = in.readString();
        filePath = in.readString();
        causeOfLoss = in.readString();
        googleMapSnapShotFilePath = in.readString();
        in.readList(labelArrayList, Label.class.getClassLoader());
    }

    public static final Creator<ReportPOJO> CREATOR = new Creator<ReportPOJO>() {
        @Override
        public ReportPOJO createFromParcel(Parcel in) {
            return new ReportPOJO(in);
        }

        @Override
        public ReportPOJO[] newArray(int size) {
            return new ReportPOJO[size];
        }
    };

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public void setCauseOfLoss(String causeOfLoss) {
        this.causeOfLoss = causeOfLoss;
    }



    public String getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(String locationLong) {
        this.locationLong = locationLong;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCauseOfLoss() {
        return causeOfLoss;
    }

    public ArrayList<Label> getLabelArrayList() {
        return labelArrayList;
    }

    public void setLabelArrayList(ArrayList<Label> labelArrayList) {
        this.labelArrayList = labelArrayList;
    }

    @Override
    public String toString() {
        return this.getReportTitle();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(reportTitle);
        dest.writeString(reportDescription);
        dest.writeString(clientName);
        dest.writeString(claimNumber);
        dest.writeString(locationLat);
        dest.writeString(locationLong);
        dest.writeString(addressLine);
        dest.writeString(createdDate);
        dest.writeString(filePath);
        dest.writeString(causeOfLoss);
        dest.writeString(filePath);
        dest.writeString(googleMapSnapShotFilePath);
        dest.writeList(labelArrayList);
    }

    public String getGoogleMapSnapShotFilePath() {
        return googleMapSnapShotFilePath;
    }

    public void setGoogleMapSnapShotFilePath(String googleMapSnapShotFilePath) {
        this.googleMapSnapShotFilePath = googleMapSnapShotFilePath;
    }
}
