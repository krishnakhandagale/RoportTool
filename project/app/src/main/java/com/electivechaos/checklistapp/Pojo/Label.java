package com.electivechaos.checklistapp.Pojo;

/**
 * Created by barkhasikka on 26/04/18.
 */

public class Label {
    String name;
    String description;
    int id;
    int categoryID;

    public int getID() {
        return id;
    }

    public void setID(int id) {
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

}
