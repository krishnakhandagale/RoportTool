package com.electivechaos.checklistapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

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



        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater =  LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.custom_category_popup_adapter_layout, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.category_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(categoryArrayList.get(position).getCategoryName());


        return convertView;
    }

    static class ViewHolder {
        TextView textView;

    }
}
