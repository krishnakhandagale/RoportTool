package com.electivechaos.checklistapp.pojo;

/**
 * Created by barkhasikka on 26/04/18.
 */

public class Label {
    String name;
    String description;
    long id;

    int categoryID;

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
