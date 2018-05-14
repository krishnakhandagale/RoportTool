package com.electivechaos.claimsadjuster.listeners;



import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;

import java.util.List;

/**
 * Created by krishna on 11/17/17.
 */

public interface OnImageRemovalListener {
    void onImageSelectionChanged(List<ImageDetailsPOJO> selctedImages);
}
