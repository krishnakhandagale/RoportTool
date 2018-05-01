package com.electivechaos.checklistapp.pojo;

import java.util.ArrayList;

/**
 * Created by barkhasikka on 26/04/18.
 */

public class Label {
    String name;
    String description;
    long id;
    int categoryID;
    ArrayList<ImageDetailsPOJO> selectedImages;
    ArrayList<ImageDetailsPOJO> selectedElevationImages;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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



    public long getID() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
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
