package com.electivechaos.checklistapp.pojo;

import java.util.ArrayList;

/**
 * Created by krishna on 11/20/17.
 */

public class ReportItemPOJO {
    String id;
    String reportTitle;
    String reportDescription;
    String clientName;
    String claimNumber;
    String address;
    String createdDate;
    String filePath;
    ArrayList<ImageDetailsPOJO> selectedImagesList;

    public ArrayList<ImageDetailsPOJO> getSelectedElevationImagesList() {
        return selectedElevationImagesList;
    }

    public void setSelectedElevationImagesList(ArrayList<ImageDetailsPOJO> selectedElevationImagesList) {
        this.selectedElevationImagesList = selectedElevationImagesList;
    }

    ArrayList<ImageDetailsPOJO> selectedElevationImagesList;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }



    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
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

    public ArrayList<ImageDetailsPOJO> getSelectedImagesList() {
        return selectedImagesList;
    }

    public void setSelectedImagesList(ArrayList<ImageDetailsPOJO> selectedImagesList) {
        this.selectedImagesList = selectedImagesList;
    }
}
