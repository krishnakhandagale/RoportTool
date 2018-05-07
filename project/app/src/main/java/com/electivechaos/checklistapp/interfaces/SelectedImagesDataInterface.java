package com.electivechaos.checklistapp.interfaces;

import com.electivechaos.checklistapp.pojo.ImageDetailsPOJO;

import java.util.ArrayList;
import java.util.List;

public  interface SelectedImagesDataInterface {

     void setSelectedElevationImages(ArrayList<ImageDetailsPOJO> imagesList,int labelPosition) ;
     void setSelectedImages(ArrayList<ImageDetailsPOJO> elevationImagesList, int labelPosition);

}
