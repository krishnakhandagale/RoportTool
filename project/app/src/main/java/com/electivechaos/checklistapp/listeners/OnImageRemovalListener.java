package com.electivechaos.checklistapp.listeners;



import com.electivechaos.checklistapp.Pojo.ImageDetailsPOJO;

import java.util.List;

/**
 * Created by krishna on 11/17/17.
 */

public interface OnImageRemovalListener {
    void onImageSelectionChanged(List<ImageDetailsPOJO> selctedImages);
}
