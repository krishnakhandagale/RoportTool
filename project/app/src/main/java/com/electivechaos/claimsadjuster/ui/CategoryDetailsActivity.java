package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.List;

public class CategoryDetailsActivity extends AppCompatActivity {

    public ArrayList<Category> categoryList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CategoriesAdapter mAdapter;
    static CategoryListDBHelper mCategoryListDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Category Details");

        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new CategoriesAdapter(categoryList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mCategoryListDBHelper = CategoryListDBHelper.getInstance(this);
        getCategoryList();

        FloatingActionButton btnAddReport = findViewById(R.id.btnAddCategory);
        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCategoryActivity = new Intent(CategoryDetailsActivity.this, AddEditCategoryActivity.class);
                startActivityForResult(addCategoryActivity, 1);
            }
        });
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
        mAdapter = new CategoriesAdapter(categoryList, CategoryDetailsActivity.this);
        recyclerView.setAdapter(mAdapter);
    }


    public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {
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


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_row_category, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
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
