package com.electivechaos.claimsadjuster.maintabs;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.ui.AddEditCategoryActivity;

import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment  extends Fragment {
    public ArrayList<Category> categoryList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CategoriesAdapter mAdapter;
    static CategoryListDBHelper mCategoryListDBHelper;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_list_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        mAdapter = new CategoriesAdapter(categoryList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mCategoryListDBHelper = CategoryListDBHelper.getInstance(getActivity());
        getCategoryList();

        FloatingActionButton btnAddReport = view.findViewById(R.id.btnAddCategory);
        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCategoryActivity = new Intent(getActivity(), AddEditCategoryActivity.class);
                startActivityForResult(addCategoryActivity, 1);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle dataFromActivity =  data.getExtras().getBundle("categoryDetails");
                String categoryName = dataFromActivity.get("categoryName").toString();
                String categoryDescription = dataFromActivity.get("categoryDescription").toString();
                Category category = new Category();
                category.setCategoryName(categoryName);
                category.setCategoryDescription(categoryDescription);
                mCategoryListDBHelper.addCategory(category);
                getCategoryList();
            }
        }

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle dataFromActivity = data.getExtras().getBundle("categoryDetails");
                String categoryName = dataFromActivity.get("categoryName").toString();
                String categoryDescription = dataFromActivity.get("categoryDescription").toString();
                int categoryID = Integer.parseInt(dataFromActivity.get("categoryID").toString());
                Category category = new Category();
                category.setCategoryName(categoryName);
                category.setCategoryDescription(categoryDescription);
                category.setCategoryId(categoryID);
                mCategoryListDBHelper.updateCategory(category);
                getCategoryList();
            }
        }

    }

    private  void getCategoryList(){
        categoryList = mCategoryListDBHelper.getCategoryList();
        mAdapter = new CategoriesAdapter(categoryList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }


    public class CategoriesAdapter extends RecyclerView.Adapter<CategoryListFragment.CategoriesAdapter.MyViewHolder> {
        private Context context;
        public List<Category> categoryList;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView categoryName, categoryDescription;
            public ImageView textViewOptions;


            public MyViewHolder(View view) {
                super(view);

                categoryName = view.findViewById(R.id.category_name);
                categoryDescription = view.findViewById(R.id.category_description);
                textViewOptions = view.findViewById(R.id.textViewOptions);

            }
        }


        public CategoriesAdapter(List<Category> categoryList, Context context) {
            this.categoryList = categoryList;
            this.context = context;
        }

        @Override
        public CategoryListFragment.CategoriesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_row_category, parent, false);

            return new CategoryListFragment.CategoriesAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final CategoryListFragment.CategoriesAdapter.MyViewHolder holder, int position) {
            final Category category = categoryList.get(position);
            holder.categoryName.setText(category.getCategoryName());
            holder.categoryDescription.setText(category.getCategoryDescription());

            holder.textViewOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, holder.textViewOptions);
                    popup.inflate(R.menu.action_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    Intent addCategoryActivity = new Intent(context, AddEditCategoryActivity.class);
                                    Bundle data = new Bundle();
                                    data.putString("categoryName",category.getCategoryName());
                                    data.putString("categoryDescription",category.getCategoryDescription());
                                    data.putInt("categoryID",category.getCategoryId());
                                    addCategoryActivity.putExtra("categoryDetails", data);
                                    startActivityForResult(addCategoryActivity, 2);
                                    break;
                                case R.id.delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Delete Category")
                                            .setMessage("Are you sure you want to delete this category ?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mCategoryListDBHelper.deleteCategoryEntry(String.valueOf(category.getCategoryId()));
                                                    getCategoryList();
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                    negativeButton.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
                                    Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                    positiveButton.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }
    }
}
