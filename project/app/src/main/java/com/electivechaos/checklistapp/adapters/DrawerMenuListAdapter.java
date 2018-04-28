package com.electivechaos.checklistapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.electivechaos.checklistapp.pojo.Label;
import com.electivechaos.checklistapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public  class  DrawerMenuListAdapter extends BaseExpandableListAdapter {

    Context context;
    HashMap<String,List<Label>> childMenuList;
    ArrayList<String> parentMenuList;
    MyItemClickListener myItemClickListener;

    public interface MyItemClickListener {
        void onItemClick(int position);
        void onEditLabelClick(Label label, int childPosition);
    }

    public DrawerMenuListAdapter(Context context, ArrayList<String> parentMenuList, HashMap<String,List<Label>> childMenuList){
        this.context= context;
        this.parentMenuList = parentMenuList ;
        this.childMenuList = childMenuList;
        this.myItemClickListener = (MyItemClickListener)context;
    }

    public List<Label> getChildList(int groupPosition) {
        return childMenuList.get(parentMenuList.get(groupPosition));
    }

    @Override
    public int getGroupCount() {
        return parentMenuList.size();
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        List<Label> list = childMenuList.get(parentMenuList.get(groupPosition));
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_layout_menu_item, parent, false);
            Button addInspectionView = convertView.findViewById(R.id.addInspection);
            addInspectionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myItemClickListener.onItemClick(groupPosition);
                }
            });
        }

        TextView menuTitle = convertView.findViewById(R.id.menuTitle);
        ImageView imageView = convertView.findViewById(R.id.menuIcon);
        Button addInspectionView = convertView.findViewById(R.id.addInspection);
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
            addInspectionView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_layout_child_menu_item, parent, false);
            Button editLabel = convertView.findViewById(R.id.editLabel);
            editLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Label label = childMenuList.get("Inspection").get(childPosition);
                    myItemClickListener.onEditLabelClick(label, childPosition);
                }
            });
        }

        TextView  menuTitle = convertView.findViewById(R.id.menuTitle);
        menuTitle.setText(childMenuList.get(parentMenuList.get(groupPosition)).get(childPosition).toString());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}