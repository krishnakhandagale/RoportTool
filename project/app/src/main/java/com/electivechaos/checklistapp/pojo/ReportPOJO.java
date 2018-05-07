package com.electivechaos.checklistapp.pojo;

import java.util.ArrayList;

public class ReportPOJO {
    private String id;
    private String reportTitle;
    private String reportDescription;
    private String clientName;
    private String claimNumber;
    private String address;
    private String locationLat;
    private String locationLong;

    private String createdDate;
    private String filePath;
    private int causeOfLossId;

    private ArrayList<Label> labelArrayList;

    public ReportPOJO(){
        id= "";
        reportTitle = "";
        reportDescription = "";
        clientName = "";
        claimNumber = "";
        address = "";
        createdDate = "";
        filePath = "";
        causeOfLossId = -1;
        locationLat = "";
        locationLong = "";
        labelArrayList = new ArrayList<>();
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public int getCauseOfLossId() {
        return causeOfLossId;
    }

    public void setCauseOfLossId(int causeOfLossId) {
        this.causeOfLossId = causeOfLossId;
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
}
