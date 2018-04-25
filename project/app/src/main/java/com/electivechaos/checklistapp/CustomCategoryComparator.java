package com.electivechaos.checklistapp;

import com.electivechaos.checklistapp.Pojo.Category;

import java.util.Comparator;

public class CustomCategoryComparator implements Comparator<Category> {

    @Override
    public int compare(Category o1, Category o2) {
        return o1.getCategoryName().toLowerCase().compareTo(o2.getCategoryName().toLowerCase());
    }
}