package com.electivechaos.claimsadjuster.pojo;

import java.util.ArrayList;

/**
 * Created by barkhasikka on 26/04/18.
 */

public class Label {
    private String id;
    private String name;
    private String description;
    private int categoryID;
    private String reportId;
    private ArrayList<ImageDetailsPOJO> selectedImages;
    private ArrayList<ImageDetailsPOJO> selectedElevationImages;


    public Label(){
        id = "";
        name = "";
        description = "";
        categoryID = -1;
        selectedImages = new ArrayList<>();
        selectedElevationImages = new ArrayList<>();
        reportId = "";
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
}
