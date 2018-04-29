package com.electivechaos.checklistapp.maintabs;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.electivechaos.checklistapp.ui.AddEditCategoryActivity;
import com.electivechaos.checklistapp.pojo.Category;
import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.database.CategoryListDBHelper;

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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mCategoryListDBHelper = new CategoryListDBHelper(getContext());
        updateCategoryList();

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
                Bundle dataFromActivity = data.getBundleExtra("data");
                String categoryName = dataFromActivity.get("name").toString();
                String categoryDescription = dataFromActivity.get("desc").toString();
                Category category = new Category();
                category.setCategoryName(categoryName);
                category.setCategoryDescription(categoryDescription);
                mCategoryListDBHelper.addCategory(category);
                updateCategoryList();
            }
        }

        if (requestCode == 2) {
            if (resultCode == 2) {
                Bundle dataFromActivity = data.getBundleExtra("data");
                String categoryName = dataFromActivity.get("name").toString();
                String categoryDescription = dataFromActivity.get("desc").toString();
                int categoryID = Integer.parseInt(dataFromActivity.get("id").toString());
                //add to category database
                Category category = new Category();
                category.setCategoryName(categoryName);
                category.setCategoryDescription(categoryDescription);
                category.setCategoryId(categoryID);
                mCategoryListDBHelper.updateCategory(category);
                updateCategoryList();
            }
        }

        if (requestCode == 3) {
            if (resultCode == 3) {
                Bundle dataFromActivity = data.getBundleExtra("data");
                String categoryID = dataFromActivity.get("id").toString();
                mCategoryListDBHelper.deleteCategoryEntry(categoryID);
                updateCategoryList();
            }
        }
    }

    private  void updateCategoryList(){
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

            public TextView title, genre;
            public Button editCategory, deleteCategory;


            public MyViewHolder(View view) {
                super(view);

                title = view.findViewById(R.id.title);
                genre = view.findViewById(R.id.desc);
                editCategory = view.findViewById(R.id.editcategory);
                deleteCategory = view.findViewById(R.id.deletecategory);

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
        public void onBindViewHolder(CategoryListFragment.CategoriesAdapter.MyViewHolder holder, int position) {
            final Category movie = categoryList.get(position);
            holder.title.setText(movie.getCategoryName());
            holder.genre.setText(movie.getCategoryDescription());
            holder.editCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addCategoryActivity = new Intent(context, AddEditCategoryActivity.class);
                    Bundle data = new Bundle();//create bundle instance
                    data.putString("title",movie.getCategoryName());
                    data.putString("description",movie.getCategoryDescription());
                    data.putInt("id",movie.getCategoryId());
                    addCategoryActivity.putExtra("data", data);
                    startActivityForResult(addCategoryActivity, 2);
                    }
            });

            holder.deleteCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCategoryListDBHelper.deleteCategoryEntry(String.valueOf(movie.getCategoryId()));
                    updateCategoryList();
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }
    }
}
