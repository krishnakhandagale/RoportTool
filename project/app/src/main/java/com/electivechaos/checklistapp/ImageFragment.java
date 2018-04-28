package com.electivechaos.checklistapp;

/**
 * Created by krishna on 11/7/17.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.electivechaos.checklistapp.pojo.Category;
import com.electivechaos.checklistapp.pojo.ImageDetailsPOJO;
import com.electivechaos.checklistapp.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.checklistapp.database.ReportsListDBHelper;

import java.util.ArrayList;
import java.util.Collections;

public class ImageFragment extends Fragment {
    String imageUrl;
    String imgTitle;
    String imgDescription;
    String imgCategory;
    int position;
    MonitorImageDetailsChange monitorImageDetailsChange;
    static ViewPager mPagerInstance;
    static ReportsListDBHelper mReportsListDBHelper;
    private int selectedCategoryPosition = -1;
    private ArrayList<Category> options = null;

    public static ImageFragment init(ImageDetailsPOJO imageDetails, int position, ViewPager mPager, ReportsListDBHelper reportsListDBHelper) {
        ImageFragment imageFragment = new ImageFragment();
        mPagerInstance = mPager;
        mReportsListDBHelper = reportsListDBHelper;
        // Supply val input as an argument.
        Bundle args = new Bundle();

        args.putString("imageUrl", imageDetails.getImageUrl());
        args.putString("title",imageDetails.getTitle());
        args.putString("description",imageDetails.getDescription());
        args.putString("category",imageDetails.getCategory());
        args.putInt("position", position);

        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageUrl = getArguments() != null ? getArguments().getString("imageUrl") : "";
        position = getArguments() != null ? getArguments().getInt("position") : 0;
        imgTitle =getArguments() != null ? getArguments().getString("title") : "";
        imgDescription =getArguments() != null ? getArguments().getString("description") : "";
        imgCategory = getArguments() != null ? getArguments().getString("category") : "";
        options = mReportsListDBHelper.getCategoryList();
        Collections.sort(options, new CustomCategoryComparator());
        if(savedInstanceState != null){
            selectedCategoryPosition = savedInstanceState.getInt("selectedCategoryPosition",-1);
            if(selectedCategoryPosition > -1){
                imgCategory = options.get(selectedCategoryPosition).getCategoryName();
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_image, container,
                false);
        ImageView iv = layoutView.findViewById(R.id.imageView1);
        EditText title = layoutView.findViewById(R.id.image_title);
        title.setText(imgTitle);
        final EditText description = layoutView.findViewById(R.id.image_description);
        description.setText(imgDescription);
        
        final TextView categoryTextView = layoutView.findViewById(R.id.category_selection);
        if(!imgCategory.isEmpty()){
            categoryTextView.setText(imgCategory);
        }

        if(categoryTextView.getText().equals("Select Category"))
        {
            description.setEnabled(false);
        }
        else
        {
            description.setEnabled(true);
        }

        final CustomCategoryPopUpAdapter adapter = new CustomCategoryPopUpAdapter(getActivity(), options);
        categoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setCancelable(true);
                ad.setTitle("Select Category");

                ad.setSingleChoiceItems(adapter, selectedCategoryPosition,  new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        selectedCategoryPosition =  pos;
                        categoryTextView.setText(options.get(pos).getCategoryName());
                        monitorImageDetailsChange.updateImageCategory(options.get(pos).getCategoryName(),position);
                        dialogInterface.dismiss();
                        description.setEnabled(true);
                    }
                });

                ad.show();

            }

        });


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


        description.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                  /* Write your logic here that will be executed when user taps next button */

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
        void updateImageCategory(String category, int position);
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
        outState.putInt("selectedCategoryPosition",selectedCategoryPosition);
    }
}
