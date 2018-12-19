package com.electivechaos.claimsadjuster.listeners;

import com.electivechaos.claimsadjuster.pojo.Image;

import java.util.List;

/**
 * Created by krishna on 11/16/17.
 */

public interface ImageClickListener {
    void onImageSelectionChanged(List<Image> selectedImages);
}
