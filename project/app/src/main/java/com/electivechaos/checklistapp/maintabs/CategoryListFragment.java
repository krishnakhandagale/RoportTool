package com.electivechaos.checklistapp.maintabs;


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

import com.electivechaos.checklistapp.AddEditReportActivity;
import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.adapters.CategoriesAdapter;
import com.electivechaos.checklistapp.categories.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment  extends Fragment {
    private List<Category> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CategoriesAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_list_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mAdapter = new CategoriesAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareMovieData();

        FloatingActionButton btnAddReport = view.findViewById(R.id.btnAddCategory);
        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addReportActivity = new Intent(getActivity(), AddEditReportActivity.class);
                startActivity(addReportActivity);
            }
        });

        return view;
    }

    private void prepareMovieData() {
        Category movie = new Category("Mad Max: Fury Road", "Action & Adventure", "2015");
        movieList.add(movie);

        movie = new Category("Inside Out", "Animation, Kids & Family", "2015");
        movieList.add(movie);

        movie = new Category("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        movieList.add(movie);

        movie = new Category("Shaun the Sheep", "Animation", "2015");
        movieList.add(movie);

        movie = new Category("The Martian", "Science Fiction & Fantasy", "2015");
        movieList.add(movie);

        movie = new Category("Mission: Impossible Rogue Nation", "Action", "2015");
        movieList.add(movie);

        movie = new Category("Up", "Animation", "2009");
        movieList.add(movie);

        movie = new Category("Star Trek", "Science Fiction", "2009");
        movieList.add(movie);

        movie = new Category("The LEGO Movie", "Animation", "2014");
        movieList.add(movie);

        movie = new Category("Iron Man", "Action & Adventure", "2008");
        movieList.add(movie);

        movie = new Category("Aliens", "Science Fiction", "1986");
        movieList.add(movie);

        movie = new Category("Chicken Run", "Animation", "2000");
        movieList.add(movie);

        movie = new Category("Back to the Future", "Science Fiction", "1985");
        movieList.add(movie);

        movie = new Category("Raiders of the Lost Ark", "Action & Adventure", "1981");
        movieList.add(movie);

        movie = new Category("Goldfinger", "Action & Adventure", "1965");
        movieList.add(movie);

        movie = new Category("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014");
        movieList.add(movie);

        mAdapter.notifyDataSetChanged();
    }
}
