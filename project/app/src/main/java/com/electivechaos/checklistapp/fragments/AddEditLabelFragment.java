package com.electivechaos.checklistapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.checklistapp.database.CategoryListDBHelper;
import com.electivechaos.checklistapp.pojo.Category;
import com.electivechaos.checklistapp.pojo.ImageDetailsPOJO;
import com.electivechaos.checklistapp.pojo.Label;
import com.electivechaos.checklistapp.utils.CommonUtils;

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
    int childPosition = -1;

    AddEditLabelInterface mCallback;


    public interface AddEditLabelInterface {
        void onLabelAdded(Label label);
        void onLabelEdited(Label label, int childPosition);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.activity_add_edit_label, container,
                false);
         final TextView categoryTextView = layoutView.findViewById(R.id.category_selection);
         final TextView labelName = layoutView.findViewById(R.id.name);
         final TextView labelDescription = layoutView.findViewById(R.id.description);
         final View parentLayout = layoutView.findViewById(R.id.addEditLabelParentLayout);
         final Button saveLabelBtn = layoutView.findViewById(R.id.addLabel);

         mCategoryList = new CategoryListDBHelper(getContext());
         categories = mCategoryList.getCategoryList();


        if(getArguments() != null) {
            labelName.setText( getArguments().getString("labelName",""));
            labelDescription.setText(getArguments().getString("labelDesc",""));
            editLabelID = getArguments().getLong("labelID", -1);
            selectedCategoryID = getArguments().getInt("categoryID", -1);
            childPosition = getArguments().getInt("childPosition", -1);
            Category selectedCategory = mCategoryList.getCategory(String.valueOf(selectedCategoryID));
            categoryTextView.setText(selectedCategory.getCategoryName());
        }



            if(editLabelID != -1){
                saveLabelBtn.setText(R.string.edit_label);
            }

            categoryTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(categories != null && categories.size() > 0){
                    final CustomCategoryPopUpAdapter adapter = new CustomCategoryPopUpAdapter(getContext(), categories);
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

                }else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("There are no categories present.")
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }});





        saveLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(labelName.getText().toString().trim().isEmpty()){
                    CommonUtils.hideKeyboard(getActivity());
                    CommonUtils.showSnackbarMessage("Please enter label name.", true, true, parentLayout, getActivity());
                    return;
                }
                if(labelDescription.getText().toString().trim().isEmpty()){
                    CommonUtils.hideKeyboard(getActivity());
                    CommonUtils.showSnackbarMessage("Please enter label description.", true, true, parentLayout,getActivity());
                    return;
                }
                if(categoryTextView.getText().toString().trim().isEmpty() || categoryTextView.getText().toString().trim().equals("Select Category") == true){
                    CommonUtils.hideKeyboard(getActivity());
                    CommonUtils.showSnackbarMessage("Please select category.", true, true, parentLayout,getActivity());
                    return;
                }
                Label label = new Label();

                label.setCategoryID(selectedCategoryID);
                label.setName(labelName.getText().toString());
                label.setDescription(labelDescription.getText().toString());

                if(editLabelID != -1) {
                    label.setId(editLabelID);
                    mCategoryList.updateLabel(label);
                    mCallback.onLabelEdited(label, childPosition);
                }else {
                    long id = mCategoryList.addLabel(label);
                    label.setId(id);
                    mCallback.onLabelAdded(label);
                }
            }
        });
        return layoutView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
