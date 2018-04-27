package com.electivechaos.checklistapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.electivechaos.checklistapp.AddEditLabelActivity;
import com.electivechaos.checklistapp.AddEditReportSelectedImagesFragment;
import com.electivechaos.checklistapp.Pojo.Category;
import com.electivechaos.checklistapp.Pojo.ImageDetailsPOJO;
import com.electivechaos.checklistapp.Pojo.Label;
import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.checklistapp.database.CategoryListDBHelper;

import java.util.ArrayList;

/**
 * Created by barkhasikka on 27/04/18.
 */

public class AddEditLabelFragment extends Fragment {
    private ArrayList<Category> categories = null;
    static CategoryListDBHelper mCategoryList;
    private int selectedCategoryPosition = -1;
    int selectedCategoryID;

    private String reportId = null;
    private String reportPath = null;
    private ArrayList<ImageDetailsPOJO> selectedImagesList = null;
    private ArrayList<ImageDetailsPOJO> selectedElevationImagesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.activity_add_edit_label, container,
                false);
        final TextView categoryTextView = layoutView.findViewById(R.id.category_selection);
        final TextView labelName = layoutView.findViewById(R.id.name);
        final TextView labelDescription = layoutView.findViewById(R.id.description);

        mCategoryList = new CategoryListDBHelper(getContext());
        categories = mCategoryList.getCategoryList();
        final CustomCategoryPopUpAdapter adapter = new CustomCategoryPopUpAdapter(getContext(), categories);
        categoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                ad.setCancelable(true);
                ad.setTitle("Select Category");

                ad.setSingleChoiceItems(adapter, selectedCategoryPosition, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        selectedCategoryPosition = pos;
                        categoryTextView.setText(categories.get(pos).getCategoryName());
                        selectedCategoryID = categories.get(pos).getCategoryId();
                        dialogInterface.dismiss();
                    }
                });

                ad.show();

            }

        });

        Button addInspectionView = layoutView.findViewById(R.id.addLabel);
        addInspectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Label label = new Label();
                label.setCategoryID(selectedCategoryID);
                label.setName(labelName.toString());
                label.setDescription(labelDescription.toString());
                mCategoryList.addLabel(label);

                FragmentManager fragmentManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                AddEditReportSelectedImagesFragment df=new AddEditReportSelectedImagesFragment();
                fragmentTransaction.replace(R.id.content_frame, new AddEditReportSelectedImagesFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return layoutView;
    }
    @Override
    public void onSaveInstanceState (@NonNull Bundle outState){
        super.onSaveInstanceState(outState);

    }
}
