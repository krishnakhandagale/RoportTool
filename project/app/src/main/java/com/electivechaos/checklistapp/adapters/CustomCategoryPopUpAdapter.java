package com.electivechaos.checklistapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.electivechaos.checklistapp.Pojo.Category;
import com.electivechaos.checklistapp.R;

import java.util.ArrayList;

/**
 * Created by nafeea on 4/17/18.
 */

public class CustomCategoryPopUpAdapter extends BaseAdapter {
    Context context;
    ArrayList<Category> categoryArrayList;
    public CustomCategoryPopUpAdapter(Context context, ArrayList<Category> categoryArrayList){
        this.categoryArrayList = categoryArrayList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return categoryArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       LayoutInflater inflater =  LayoutInflater.from(context);

       View rootView = inflater.inflate(R.layout.custom_category_popup_adapter_layout,parent,false);
       CheckedTextView checkedTextView = rootView.findViewById(R.id.category_name);
       checkedTextView.setText(categoryArrayList.get(position).getCategoryName());
       return rootView;
    }
}
