package com.electivechaos.claimsadjuster.interfaces;

import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;

import java.util.ArrayList;

public  interface SelectedImagesDataInterface {

     void setSelectedElevationImages(ArrayList<ImageDetailsPOJO> imagesList,int labelPosition) ;
     void setSelectedImages(ArrayList<ImageDetailsPOJO> elevationImagesList, int labelPosition);
     void setSwapedSelectedImages(ArrayList<ImageDetailsPOJO> imagesList, int labelPosition);


}
