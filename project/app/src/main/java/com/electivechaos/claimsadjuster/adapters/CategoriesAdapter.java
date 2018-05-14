package com.electivechaos.claimsadjuster.adapters;

/**
 * Created by barkhasikka on 24/04/18.
 */
//
//import android.app.Activity;
//import android.content.ContentProvider;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.electivechaos.checklistapp.AddEditCategoryActivity;
//import com.electivechaos.checklistapp.Pojo.Category;
//import com.electivechaos.checklistapp.R;
//
//import java.util.List;
//
//public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {
//    private Context context;
//    public List<Category> categoryList;
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//
//        public TextView title, genre;
//        public Button editCategory, deleteCategory;
//
//
//        public MyViewHolder(View view) {
//            super(view);
//
//            title = view.findViewById(R.id.title);
//            genre = view.findViewById(R.id.desc);
//            editCategory = view.findViewById(R.id.editcategory);
//            deleteCategory = view.findViewById(R.id.deletecategory);
//
//        }
//    }
//
//
//    public CategoriesAdapter(List<Category> categoryList, Context context) {
//        this.categoryList = categoryList;
//        this.context = context;
//    }
//
//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.single_row_category, parent, false);
//
//        return new CategoriesAdapter.MyViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        final Category movie = categoryList.get(position);
//        holder.title.setText(movie.getCategoryName());
//        holder.genre.setText(movie.getCategoryDescription());
//        holder.editCategory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent addCategoryActivity = new Intent(context, AddEditCategoryActivity.class);
//                addCategoryActivity.putExtra("title",movie.getCategoryName());
//                addCategoryActivity.putExtra("description",movie.getCategoryDescription());
//                addCategoryActivity.putExtra("id",movie.getCategoryId());
//
//                Activity origin = (Activity)context;
//                origin.startActivityForResult(addCategoryActivity, 2);
//
////                context.startActivity(addCategoryActivity);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return categoryList.size();
//    }
//}
