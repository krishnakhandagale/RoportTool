package com.electivechaos.claimsadjuster;

/**
 * Created by krishna on 11/7/17.
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.electivechaos.claimsadjuster.adapters.CustomMenuAdapter;
import com.electivechaos.claimsadjuster.asynctasks.DBPropertyDetailsListTsk;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.pojo.CoveragePOJO;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.ui.AddEditCoverageActivity;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.electivechaos.claimsadjuster.ui.AddEditCategoryActivity.ADD_COVERAGE_REQUEST_CODE;

public class ImageFragment extends Fragment {
    private String imageUrl;
   // private String imgTitle;
    private String imgDescription;
    private String coverageType;
    private String imgName, imgDateTime, imgGeoTag;
    private boolean imgIsDamage;
    private boolean imgIsOverview;
    private boolean imgIsPointOfOrigin;

    private int position;
    private MonitorImageDetailsChange monitorImageDetailsChange;
    static ViewPager mPagerInstance;
    private static CategoryListDBHelper categoryListDBHelper;
    TextView imageCoverageType;

    TextView imageName, imageDateTime, imageGeoTag;
    ImageButton imageInfo;
    private PopupWindow popWindow;


    public static ImageFragment init(ImageDetailsPOJO imageDetails, int position, ViewPager mPager) {
        ImageFragment imageFragment = new ImageFragment();
        mPagerInstance = mPager;
        categoryListDBHelper = CategoryListDBHelper.getInstance(mPager.getContext());
        Bundle args = new Bundle();

        args.putString("imageUrl", imageDetails.getImageUrl());
       // args.putString("title",imageDetails.getTitle());
        args.putString("description",imageDetails.getDescription());
        args.putString("coverageType",imageDetails.getCoverageTye());
        args.putBoolean("imgIsDamage", imageDetails.isDamage());
        args.putBoolean("imgIsOverview", imageDetails.isOverview());
        args.putBoolean("imgIsPointPofOrigin", imageDetails.isPointOfOrigin());
        args.putString("imgName", imageDetails.getImageName());
        args.putString("imgDateTime", imageDetails.getImageDateTime());
        args.putString("imgGeoTag", imageDetails.getImageGeoTag());
        args.putInt("position", position);

        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageUrl = getArguments() != null ? getArguments().getString("imageUrl") : "";
        position = getArguments() != null ? getArguments().getInt("position") : 0;
      //  imgTitle = getArguments() != null ? getArguments().getString("title") : "";
        imgDescription =getArguments() != null ? getArguments().getString("description") : "";
        coverageType =getArguments() != null ? getArguments().getString("coverageType") : "";
        imgIsDamage =getArguments() != null  && getArguments().getBoolean("imgIsDamage");
        imgIsOverview = getArguments() != null && getArguments().getBoolean("imgIsOverview");
        imgIsPointOfOrigin = getArguments() != null && getArguments().getBoolean("imgIsPointOfOrigin");
        imgName =getArguments() != null ? getArguments().getString("imgName") : "";
        imgDateTime =getArguments() != null ? getArguments().getString("imgDateTime") : "";
        imgGeoTag =getArguments() != null ? getArguments().getString("imgGeoTag") : "";

        if(savedInstanceState != null){
            imgIsDamage = savedInstanceState.getBoolean("imgIsDamage");
            imgIsOverview = savedInstanceState.getBoolean("imgIsOverview");
            imgIsPointOfOrigin = savedInstanceState.getBoolean("imgIsPointOfOrigin");
            coverageType = savedInstanceState.getString("coverageType");
            imgName = savedInstanceState.getString("imgName");
            imgDateTime = savedInstanceState.getString("imgDateTime");
            imgGeoTag = savedInstanceState.getString("imgGeoTag");
        }
  }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layoutView = inflater.inflate(R.layout.fragment_image, container,
                false);
        ImageView iv = layoutView.findViewById(R.id.imageView1);
       // EditText title = layoutView.findViewById(R.id.imageTitle);
        EditText description = layoutView.findViewById(R.id.image_description);



        final CheckedTextView damageTextView = layoutView.findViewById(R.id.damageTextView);
        final CheckedTextView overviewTextView = layoutView.findViewById(R.id.overviewTextView);
        final  CheckedTextView pointOfOriginTextView = layoutView.findViewById(R.id.isPointOfOrigin);
        imageCoverageType = layoutView.findViewById(R.id.imageCoverageType);

        imageInfo = layoutView.findViewById(R.id.imageInfo);


        imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowPopup(v);
            }
        });


        if(coverageType == null || coverageType.isEmpty()){
            imageCoverageType.setText("Coverage Type");
        }else {
            imageCoverageType.setText(coverageType);
        }




        pointOfOriginTextView.setChecked(imgIsPointOfOrigin);
        // For damage text view
        damageTextView.setChecked(imgIsDamage);

        if(imgIsDamage) {
            imgIsDamage = true;
            imgIsOverview = false;
            damageTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_active));
        }else {
            imgIsDamage = false;
            damageTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
        }

        // For over view  text view
        overviewTextView.setChecked(imgIsOverview);

        if(imgIsOverview) {
            imgIsOverview = true;
            imgIsDamage = false;
            overviewTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_active));
        }else {
            imgIsOverview = false;
            overviewTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
        }

       // title.setText(imgTitle);

        description.setText(imgDescription);

        imageCoverageType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DBPropertyDetailsListTsk(categoryListDBHelper, "coverage_type", new AsyncTaskStatusCallback() {
                    @Override
                    public void onPostExecute(Object object, String type) {

                        final ArrayList<CoveragePOJO> coveragePOJOS = (ArrayList<CoveragePOJO>) object;

                        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

                        ad.setCancelable(true);
                        ad.setPositiveButton("Add New", new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getActivity(), AddEditCoverageActivity.class);
                                getActivity().startActivityForResult(intent, ADD_COVERAGE_REQUEST_CODE);
                            }
                        });
                        ad.setTitle("Coverage Type");
                        if(coveragePOJOS.size() == 0){
                            ad.setMessage("No coverage types found.");
                        }
                        CustomMenuAdapter adapter = new CustomMenuAdapter(coveragePOJOS, coverageType, "coverage_type");
                        ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                setCoverageType(coveragePOJOS.get(position).getName());
                                dialogInterface.dismiss();

                            }
                        });

                        ad.show();
                        CommonUtils.unlockOrientation(getActivity());
                    }

                    @Override
                    public void onPreExecute() {
                        CommonUtils.lockOrientation(getActivity());
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }).execute();
            }
        });


        //Here image information
        monitorImageDetailsChange.setImageName(imgName, position);
        monitorImageDetailsChange.setImageDateTime(imgDateTime, position);
        monitorImageDetailsChange.setGeoTag(imgGeoTag, position);

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                monitorImageDetailsChange.updateImageDescription(s.toString(),position);
            }
        });

//        title.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                monitorImageDetailsChange.updateImageTitle(s.toString(),position);
//            }
//        });

        damageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(overviewTextView.isChecked()){
                    overviewTextView.setChecked(false);
                    overviewTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
                    imgIsOverview = false;
                    monitorImageDetailsChange.setUnsetOverview(false,position);
                }

                if(((CheckedTextView)v).isChecked()){
                    ((CheckedTextView)v).setChecked(false);
                    imgIsDamage = false;
                    v.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
                    monitorImageDetailsChange.setUnsetDamage(false,position);
                }else{
                    ((CheckedTextView)v).setChecked(true);
                    imgIsDamage = true;
                    v.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_active));
                    monitorImageDetailsChange.setUnsetDamage(true,position);
                }

            }
        });

        overviewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(damageTextView.isChecked()){
                    damageTextView.setChecked(false);
                    imgIsDamage = false;
                    damageTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
                    monitorImageDetailsChange.setUnsetDamage(false,position);
                }
                if(((CheckedTextView)v).isChecked()){
                    ((CheckedTextView)v).setChecked(false);
                    imgIsOverview = false;
                    v.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
                    monitorImageDetailsChange.setUnsetOverview(false,position);
                }else{

                    ((CheckedTextView)v).setChecked(true);
                    imgIsOverview = true;
                    v.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_active));
                    monitorImageDetailsChange.setUnsetOverview(true,position);
                }
            }
        });

        pointOfOriginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pointOfOriginTextView.isChecked()){
                    imgIsPointOfOrigin = false;
                    pointOfOriginTextView.setChecked(false);
                    pointOfOriginTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
                    monitorImageDetailsChange.setUnsetPointOfOrigin(false,position);
                }else{
                    imgIsPointOfOrigin = true;
                    pointOfOriginTextView.setChecked(true);
                    pointOfOriginTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_active));
                    monitorImageDetailsChange.setUnsetPointOfOrigin(true,position);
                }
            }
        });




        description.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mPagerInstance.setCurrentItem(position+1);
                    handled = true;
                }
                return handled;
            }
        });

        Glide.with(this).load("file://"+imageUrl).into(iv);

        return layoutView;
    }

    public void setCoverageType(String name) {
        coverageType = name;
        imageCoverageType.setText(coverageType);
        monitorImageDetailsChange.setUnsetCoverageType(coverageType,position);
    }


    public interface MonitorImageDetailsChange{
       // void updateImageTitle(String title, int position);
        void updateImageDescription(String description, int position);
        void setUnsetDamage(boolean isDamage, int position);
        void setUnsetOverview(boolean isOverview, int position);
        void setUnsetPointOfOrigin(boolean isPointOfOrigin, int position);
        void setUnsetCoverageType(String coverageType, int position);
        void setImageName(String name,int position);
        void setImageDateTime(String dateTime, int position);
        void setGeoTag(String geoTag, int position);

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onShowPopup(View v){

        LayoutInflater layoutInflater = (LayoutInflater)getLayoutInflater();

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.image_info_popup_layout, null,false);
        // find the ListView in the popup layout

        imageName = inflatedView.findViewById(R.id.imageName);
        imageDateTime = inflatedView.findViewById(R.id.imageDateTime);


        imageName.setText(imgName);
        imageDateTime.setText(imgDateTime);
        // get device size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, size.x - 50,size.y - 1000, true );

        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_image_info));

        popWindow.setElevation(10.0f);
        // make it outside touchable to dismiss the popup window
        popWindow.setOutsideTouchable(true);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            monitorImageDetailsChange = (ImageFragment.MonitorImageDetailsChange) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("imgIsDamage",imgIsDamage);
        outState.putBoolean("imgIsOverview",imgIsOverview);
        outState.putBoolean("imgIsPointOfOrigin",imgIsPointOfOrigin);
        outState.putString("coverageType",coverageType);
        outState.putString("imgName",imgName);
        outState.putString("imgDateTime",imgDateTime);
        outState.putString("imgGeoTag",imgGeoTag);
    }


}
