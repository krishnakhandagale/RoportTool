package com.electivechaos.checklistapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.electivechaos.checklistapp.AddEditReportActivity;
import com.electivechaos.checklistapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public  class  DrawerMenuListAdapter extends BaseExpandableListAdapter {

    Context context;
    HashMap<String,List<String>> childMenuList;
    ArrayList<String> parentMenuList;

    public DrawerMenuListAdapter(Context context, ArrayList<String> parentMenuList, HashMap<String,List<String>> childMenuList){
        this.context= context;
        this.parentMenuList = parentMenuList ;
        this.childMenuList = childMenuList;
    }

    @Override
    public int getGroupCount() {
        return parentMenuList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<String> list = childMenuList.get(parentMenuList.get(groupPosition));
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentMenuList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childMenuList.get(parentMenuList.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //Button addBtn=convertView.findViewById(R.id.addButton);

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_layout_menu_item, parent, false);
            Button addBtn=convertView.findViewById(R.id.addButton);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"My Labels",Toast.LENGTH_SHORT).show();
//                    Intent addLabelActivity = new Intent(context, AddEditReportActivity.class);
//                    context.startActivity(addLabelActivity);
                }
            });
        }

        TextView menuTitle = convertView.findViewById(R.id.menuTitle);
        ImageView imageView = convertView.findViewById(R.id.menuIcon);
        Button addBtn=convertView.findViewById(R.id.addButton);

        String parentMenuString = parentMenuList.get(groupPosition);
        menuTitle.setText(parentMenuString);
        if(parentMenuString.equals("Claim Details")){
            imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_reports));
        }else if(parentMenuString.equals("Cause Of Loss")){
            imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_location));

        }else if(parentMenuString.equals("Point Of Origin")){
            imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_point_of_origin));

        }else{
            imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_damage));
           addBtn.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_layout_menu_item, parent, false);
        }

        TextView  menuTitle = convertView.findViewById(R.id.menuTitle);
        convertView.findViewById(R.id.menuIcon).setVisibility(View.INVISIBLE);
        menuTitle.setText(childMenuList.get(parentMenuList.get(groupPosition)).get(childPosition).toString());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}