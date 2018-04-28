package com.electivechaos.checklistapp.pojo;

/**
 * Created by barkhasikka on 28/04/18.
 */

public class CauseOfLoss {
    String name;
    String description;
    int id;

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
}
