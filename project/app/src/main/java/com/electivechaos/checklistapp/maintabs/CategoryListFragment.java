package com.electivechaos.checklistapp.maintabs;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.electivechaos.checklistapp.AddEditCategoryActivity;
import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.adapters.CategoriesAdapter;
import com.electivechaos.checklistapp.categories.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment  extends Fragment {
    public List<Category> categoryList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CategoriesAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_list_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mAdapter = new CategoriesAdapter(categoryList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareMovieData();

//        String getArgument = getArguments().getString("categoryName");
//        Log.d("Something===========", "onCreateView: "+getArgument);
//
//        String categoryDescription = getArguments().getString("categoryDesc");
//        Log.d("Something===========", "onCreateView: "+categoryDescription);
//        Category movie = new Category(getArgument, categoryDescription, "2015");
//        categoryList.add(movie);

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
                String categoryName = dataFromActivity.get("categoryName").toString();
                String categoryDescription = dataFromActivity.get("categoryDesc").toString();
                Category movie = new Category(categoryName, categoryDescription, "2015");
                categoryList.add(movie);
                updateCategoryList();
            }
        }
    }

    private  void updateCategoryList(){
        mAdapter = new CategoriesAdapter(categoryList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }


    private void prepareMovieData() {
        Category movie = new Category("Mad Max: Fury Road", "Action & Adventure", "2015");
        categoryList.add(movie);

        movie = new Category("Inside Out", "Animation, Kids & Family", "2015");
        categoryList.add(movie);

        movie = new Category("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        categoryList.add(movie);

        movie = new Category("Shaun the Sheep", "Animation", "2015");
        categoryList.add(movie);

        movie = new Category("The Martian", "Science Fiction & Fantasy", "2015");
        categoryList.add(movie);

        movie = new Category("Mission: Impossible Rogue Nation", "Action", "2015");
        categoryList.add(movie);

        movie = new Category("Up", "Animation", "2009");
        categoryList.add(movie);

        movie = new Category("Star Trek", "Science Fiction", "2009");
        categoryList.add(movie);

        movie = new Category("The LEGO Movie", "Animation", "2014");
        categoryList.add(movie);

        movie = new Category("Iron Man", "Action & Adventure", "2008");
        categoryList.add(movie);

        movie = new Category("Aliens", "Science Fiction", "1986");
        categoryList.add(movie);

        movie = new Category("Chicken Run", "Animation", "2000");
        categoryList.add(movie);

        movie = new Category("Back to the Future", "Science Fiction", "1985");
        categoryList.add(movie);

        movie = new Category("Raiders of the Lost Ark", "Action & Adventure", "1981");
        categoryList.add(movie);

        movie = new Category("Goldfinger", "Action & Adventure", "1965");
        categoryList.add(movie);

        movie = new Category("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014");
        categoryList.add(movie);

        mAdapter.notifyDataSetChanged();
    }
}
