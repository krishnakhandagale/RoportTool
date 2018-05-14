package com.electivechaos.claimsadjuster.interfaces;

import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;

import java.util.ArrayList;
import java.util.List;

public  interface SelectedImagesDataInterface {

     void setSelectedElevationImages(ArrayList<ImageDetailsPOJO> imagesList,int labelPosition) ;
     void setSelectedImages(ArrayList<ImageDetailsPOJO> elevationImagesList, int labelPosition);

}
