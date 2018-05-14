package com.electivechaos.claimsadjuster.pojo;

import java.io.Serializable;

/**
 * Created by nafeea on 4/24/18.
 */

public class ImageDetailsPOJO implements Serializable {
    private String imageUrl;
    private String title;
    private String description;

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
}
