package com.electivechaos.claimsadjuster.pojo;

import java.io.Serializable;

/**
 * Created by nafeea on 4/24/18.
 */

public class ImageDetailsPOJO implements Serializable {
    private String imageUrl;
    private String title;
    private String description;
    private boolean isDamage;
    private boolean isOverview;

    public boolean isDamage() {
        return isDamage;
    }

    public void setIsDamage(boolean damage) {
        isDamage = damage;
    }

    public ImageDetailsPOJO(){
        this.imageUrl = "";
        this.title = "";
        this.description = "";
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDamage(boolean damage) {
        isDamage = damage;
    }

    public boolean isOverview() {
        return isOverview;
    }

    public void setOverview(boolean overview) {
        isOverview = overview;
    }
}
