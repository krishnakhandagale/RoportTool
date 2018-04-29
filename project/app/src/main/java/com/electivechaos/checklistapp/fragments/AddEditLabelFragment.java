package com.electivechaos.checklistapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.electivechaos.checklistapp.AddEditReportSelectedImagesFragment;
import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.checklistapp.database.CategoryListDBHelper;
import com.electivechaos.checklistapp.pojo.Category;
import com.electivechaos.checklistapp.pojo.ImageDetailsPOJO;
import com.electivechaos.checklistapp.pojo.Label;

import java.util.ArrayList;

/**
 * Created by barkha sikka on 27/04/18.
 */

public class AddEditLabelFragment extends Fragment {
    private ArrayList<Category> categories = null;
    static CategoryListDBHelper mCategoryList;
    private int selectedCategoryPosition = -1;
    int selectedCategoryID;
    long editLabelID = -1;
    int childPosition;

    AddEditLabelInterface mCallback;


    public interface AddEditLabelInterface {
        void onLabelDataReceive(Label label);
        void onLabelDataEdited(Label label, int childPosition);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

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

        if(getArguments() != null) {
            labelName.setText( getArguments().getString("labelName"));
            labelDescription.setText(getArguments().getString("labelDesc"));
            editLabelID = getArguments().getLong("labelID");
            selectedCategoryID = getArguments().getInt("categoryID");
            childPosition = getArguments().getInt("childPosition");
            Category selectedCategory = mCategoryList.getCategory(String.valueOf(selectedCategoryID));
            categoryTextView.setText(selectedCategory.getCategoryName());
        }



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
                label.setName(labelName.getText().toString());
                label.setDescription(labelDescription.getText().toString());
                if(editLabelID != -1) {
                    label.setID(editLabelID);
                    mCategoryList.updateLabel(label);
                    mCallback.onLabelDataEdited(label, childPosition);
                }else {
                    label.setID(mCategoryList.addLabel(label));
                    mCallback.onLabelDataReceive(label);
                }

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, new AddEditReportSelectedImagesFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return layoutView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (AddEditLabelFragment.AddEditLabelInterface) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onSaveInstanceState (@NonNull Bundle outState){
        super.onSaveInstanceState(outState);

    }
}
