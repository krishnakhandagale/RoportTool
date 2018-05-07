package com.electivechaos.checklistapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.electivechaos.checklistapp.pojo.Category;
import com.electivechaos.checklistapp.R;

import java.util.ArrayList;

/**
 * Created by nafeea on 4/17/18.
 */

public class CustomCategoryPopUpAdapter extends BaseAdapter {
    Context context;
    ArrayList<Category> categoryArrayList;
    int selectedCategoryPosition;
    public CustomCategoryPopUpAdapter(Context context, ArrayList<Category> categoryArrayList, int selectedCategoryPosition){
        this.categoryArrayList = categoryArrayList;
        this.context = context;
        this.selectedCategoryPosition = selectedCategoryPosition;
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


        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_category_popup_adapter_layout, parent, false);
            holder = new ViewHolder();
            holder.checkedTextView = convertView.findViewById(R.id.category_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.checkedTextView.setText(categoryArrayList.get(position).getCategoryName());

        if(selectedCategoryPosition == position){
            holder.checkedTextView.setChecked(true);
        }else{
            holder.checkedTextView.setChecked(false);
        }

        return convertView;
    }

    static class ViewHolder {
        CheckedTextView checkedTextView;

    }
}
