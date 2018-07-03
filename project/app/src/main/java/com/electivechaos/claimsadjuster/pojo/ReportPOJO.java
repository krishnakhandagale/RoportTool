package com.electivechaos.claimsadjuster.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Observable;

public class ReportPOJO  extends Observable implements Parcelable  {
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
    private String googleMapSnapShotFilePath;

    private ArrayList labelArrayList;
    private PropertyDetailsPOJO propertyDetailsPOJO;
    private PerilPOJO perilPOJO;

    public ReportPOJO(){

        id = "";
        reportTitle = "";
        reportDescription = "";
        clientName = "";
        claimNumber = "";
        createdDate = "";
        filePath = "";
        locationLat = "";
        locationLong = "";
        addressLine = "";
        googleMapSnapShotFilePath = "";
        labelArrayList = new ArrayList<>();
        propertyDetailsPOJO = new PropertyDetailsPOJO();
        perilPOJO = new PerilPOJO();
    }

    public PropertyDetailsPOJO getPropertyDetailsPOJO() {
        return propertyDetailsPOJO;
    }

    public void setPropertyDetailsPOJO(PropertyDetailsPOJO propertyDetailsPOJO) {
        this.propertyDetailsPOJO = propertyDetailsPOJO;
        setChanged();
        notifyObservers();
    }

    public PerilPOJO getPerilPOJO() {
        return perilPOJO;
    }

    public void setPerilPOJO(PerilPOJO perilPOJO) {

        this.perilPOJO = perilPOJO;
        setChanged();
        notifyObservers();
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
        googleMapSnapShotFilePath = in.readString();
        labelArrayList = in.createTypedArrayList(Label.CREATOR);
        propertyDetailsPOJO = in.readParcelable(PropertyDetailsPOJO.class.getClassLoader());
        perilPOJO = in.readParcelable(PerilPOJO.class.getClassLoader());
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
        setChanged();
        notifyObservers();
    }



    public String getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
        setChanged();
        notifyObservers();
    }

    public String getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(String locationLong) {
        this.locationLong = locationLong;
        setChanged();
        notifyObservers();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        setChanged();
        notifyObservers();
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
        setChanged();
        notifyObservers();
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
        setChanged();
        notifyObservers();
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
        setChanged();
        notifyObservers();
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
        setChanged();
        notifyObservers();
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        setChanged();
        notifyObservers();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        setChanged();
        notifyObservers();
    }

    public ArrayList<Label> getLabelArrayList() {
        return labelArrayList == null ?  new ArrayList<Label>() :  labelArrayList;
    }

    public void setLabelArrayList(ArrayList<Label> labelArrayList) {
        this.labelArrayList = labelArrayList;
        setChanged();
        notifyObservers();
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
        dest.writeString(googleMapSnapShotFilePath);
        dest.writeTypedList(labelArrayList);
        dest.writeParcelable(propertyDetailsPOJO , flags);
        dest.writeParcelable(perilPOJO , flags);
    }

    public String getGoogleMapSnapShotFilePath() {
        return googleMapSnapShotFilePath;
    }

    public void setGoogleMapSnapShotFilePath(String googleMapSnapShotFilePath) {
        this.googleMapSnapShotFilePath = googleMapSnapShotFilePath;
    }
}
