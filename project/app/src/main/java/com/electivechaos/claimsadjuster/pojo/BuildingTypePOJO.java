package com.electivechaos.claimsadjuster.pojo;

/**
 * Created by nafeesa on 5/28/18.
 */

public class BuildingTypePOJO {
    private int id;
    private String name;

    public BuildingTypePOJO() {
        id = 0;
        name = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
