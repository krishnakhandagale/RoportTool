package com.electivechaos.claimsadjuster.interfaces;

import android.graphics.Bitmap;

public interface LossLocationDataInterface {
    void setLocationLat(String locationLat);

    void setLocationLong(String locationLong);

    void setAddressLine(String addressLine);

    void setMapSnapshot(Bitmap bitmap);

}
