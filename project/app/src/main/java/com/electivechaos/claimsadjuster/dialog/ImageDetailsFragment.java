package com.electivechaos.claimsadjuster.dialog;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;

/**
 * Created by nafeesa on 7/11/18.
 */

public class ImageDetailsFragment extends DialogFragment{

    private String imageName;
    private String imageDateTime;
    private String imageGeoTag;
    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_info_popup_layout, container, false);

        Bundle passedArgs = getArguments();

        if(passedArgs != null){
            imageName = passedArgs.get("imgName").toString();
            imageDateTime = passedArgs.get("imgDateTime").toString();
            imageGeoTag = passedArgs.get("imgGeoTag").toString();
        }


        TextView imgName, imgDateTime, imgGeoTag;
        imgName = view.findViewById(R.id.imageName);
        imgDateTime = view.findViewById(R.id.imageDateTime);
        imgGeoTag = view.findViewById(R.id.imageGeoTag);
        ImageView closeImageDialog  = view.findViewById(R.id.closeImageDialog);
        closeImageDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        imgName.setText(imageName);
        imgDateTime.setText(imageDateTime);
        if(!imageGeoTag.isEmpty()) {
            imgGeoTag.setText(imageGeoTag);
        }else {
            imgGeoTag.setText("No Geo tag found.");
        }

        return view;
    }
}
