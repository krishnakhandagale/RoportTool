package com.electivechaos.claimsadjuster;

/**
 * Created by krishna on 11/7/17.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;

public class ImageFragment extends Fragment {
    String imageUrl;
    String imgTitle;
    String imgDescription;
    boolean imgIsDamage;
    boolean imgIsOverview;

    boolean isDamage;
    boolean isOverview;

    int position;
    MonitorImageDetailsChange monitorImageDetailsChange;
    static ViewPager mPagerInstance;

    public static ImageFragment init(ImageDetailsPOJO imageDetails, int position, ViewPager mPager) {
        ImageFragment imageFragment = new ImageFragment();
        mPagerInstance = mPager;

        Bundle args = new Bundle();

        args.putString("imageUrl", imageDetails.getImageUrl());
        args.putString("title",imageDetails.getTitle());
        args.putString("description",imageDetails.getDescription());
        args.putBoolean("imgIsDamage", imageDetails.isDamage());
        args.putBoolean("imgIsOverview", imageDetails.isOverview());
        args.putInt("position", position);

        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageUrl = getArguments() != null ? getArguments().getString("imageUrl") : "";
        position = getArguments() != null ? getArguments().getInt("position") : 0;
        imgTitle = getArguments() != null ? getArguments().getString("title") : "";
        imgDescription =getArguments() != null ? getArguments().getString("description") : "";
        imgIsDamage =getArguments() != null  && getArguments().getBoolean("imgIsDamage");
        imgIsOverview = getArguments() != null && getArguments().getBoolean("imgIsOverview");

        if(savedInstanceState != null){
            imgIsDamage = savedInstanceState.getBoolean("isDamage");
            imgIsOverview = savedInstanceState.getBoolean("isOverview");
        }
  }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_image, container,
                false);
        ImageView iv = layoutView.findViewById(R.id.imageView1);
        EditText title = layoutView.findViewById(R.id.imageTitle);
        EditText description = layoutView.findViewById(R.id.image_description);


        final CheckedTextView damageTextView = layoutView.findViewById(R.id.damageTextView);
        final CheckedTextView overviewTextView = layoutView.findViewById(R.id.overviewTextView);

        // For damage text view
        damageTextView.setChecked(imgIsDamage);

        if(imgIsDamage) {
            isDamage = true;
            isOverview = false;
            damageTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_active));
        }else {
            isDamage = false;
            damageTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
        }

        // For over view  text view
        overviewTextView.setChecked(imgIsOverview);

        if(imgIsOverview) {
            isOverview = true;
            isDamage = false;
            overviewTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_active));
        }else {
            isOverview = false;
            overviewTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
        }

        title.setText(imgTitle);

        description.setText(imgDescription);


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

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                monitorImageDetailsChange.updateImageTitle(s.toString(),position);
            }
        });

        damageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(overviewTextView.isChecked()){
                    overviewTextView.setChecked(false);
                    overviewTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
                    isOverview = false;
                    monitorImageDetailsChange.setUnsetOverview(false,position);
                }

                if(((CheckedTextView)v).isChecked()){
                    ((CheckedTextView)v).setChecked(false);
                    isDamage = false;
                    v.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
                    monitorImageDetailsChange.setUnsetDamage(false,position);
                }else{
                    ((CheckedTextView)v).setChecked(true);
                    isDamage = true;
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
                    isDamage = false;
                    damageTextView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
                    monitorImageDetailsChange.setUnsetDamage(false,position);
                }
                if(((CheckedTextView)v).isChecked()){
                    ((CheckedTextView)v).setChecked(false);
                    isOverview = false;
                    v.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_gray));
                    monitorImageDetailsChange.setUnsetOverview(false,position);
                }else{

                    ((CheckedTextView)v).setChecked(true);
                    isOverview = true;
                    v.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.shape_chip_drawable_active));
                    monitorImageDetailsChange.setUnsetOverview(true,position);
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
    public interface MonitorImageDetailsChange{
        void updateImageTitle(String title, int position);
        void updateImageDescription(String description, int position);
        void setUnsetDamage(boolean isDamage, int position);
        void setUnsetOverview(boolean isOverview, int position);

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
        outState.putBoolean("isDamage",isDamage);
        outState.putBoolean("isOverview",isOverview);
    }
}
