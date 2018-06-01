package com.electivechaos.claimsadjuster.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.pojo.Category;

import java.util.ArrayList;

/**
 * Created by nafeesa on 4/17/18.
 */

public class CustomCategoryPopUpAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Category> categoryArrayList;

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
