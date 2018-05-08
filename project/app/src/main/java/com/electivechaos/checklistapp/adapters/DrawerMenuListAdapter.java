package com.electivechaos.checklistapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.electivechaos.checklistapp.pojo.Label;
import com.electivechaos.checklistapp.R;

import org.w3c.dom.Text;

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
        return childMenuList.get(parentMenuList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentViewHolder holder;

        if(convertView == null){

            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_layout_menu_item, parent, false);
            holder = new ParentViewHolder();

            holder.menuTitle =  convertView.findViewById(R.id.menuTitle);
            holder.imageView = convertView.findViewById(R.id.menuIcon);
            holder.addInspectionView = convertView.findViewById(R.id.addInspection);

            convertView.setTag(holder);
        }else{
            holder = (ParentViewHolder) convertView.getTag();
        }



        String parentMenuString = parentMenuList.get(groupPosition);

        holder.menuTitle.setText(parentMenuString);

        holder.addInspectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myItemClickListener.onItemClick(groupPosition);


            }
        });

        if(parentMenuString.equals("Claim Details")){
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_reports));
        }else if(parentMenuString.equals("Cause Of Loss")){
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_location));
        }else if(parentMenuString.equals("Point Of Origin")){
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_point_of_origin));
        }else{

            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_damage));
            holder.addInspectionView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_layout_child_menu_item, parent, false);
            holder = new ChildViewHolder();
            holder.menuTitle = convertView.findViewById(R.id.menuTitle);
            holder.editLabel = convertView.findViewById(R.id.editLabel);
            convertView.setTag(holder);

        }else{
            holder = (ChildViewHolder) convertView.getTag();
        }



        holder.editLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Label label = childMenuList.get("Inspection").get(childPosition);
                myItemClickListener.onEditLabelClick(label, childPosition);

            }
        });
        holder.menuTitle.setText(childMenuList.get(parentMenuList.get(groupPosition)).get(childPosition).toString());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }


    static class ChildViewHolder{
        TextView menuTitle;
        Button editLabel;
    }

    static class ParentViewHolder{
        TextView menuTitle;
        ImageView imageView;
        Button addInspectionView;
    }



}