package com.electivechaos.claimsadjuster.adapters;

import android.content.Context;
import android.content.DialogInterface;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.AddEditLabelInterface;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.pojo.ParentMenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrawerMenuListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private HashMap<String, List<Label>> childMenuList;
    private ArrayList<ParentMenuItem> parentMenuList;
    private OnLabelAddClickListener onLabelAddClickListener;
    private CategoryListDBHelper mCategoryList;
    private AddEditLabelInterface addEditLabelInterface;

    public DrawerMenuListAdapter(Context context, ArrayList<ParentMenuItem> parentMenuList, HashMap<String, List<Label>> childMenuList) {
        this.context = context;
        this.parentMenuList = parentMenuList;
        this.childMenuList = childMenuList;
        this.onLabelAddClickListener = (OnLabelAddClickListener) context;
        this.addEditLabelInterface = (AddEditLabelInterface) context;
        mCategoryList = CategoryListDBHelper.getInstance(context);
    }

    @Override
    public int getGroupCount() {
        return parentMenuList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<Label> list = childMenuList.get(parentMenuList.get(groupPosition).getTitle());
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentMenuList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childMenuList.get(parentMenuList.get(groupPosition).getTitle()).get(childPosition);
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

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_layout_menu_item, parent, false);
            holder = new ParentViewHolder();

            holder.menuTitle = convertView.findViewById(R.id.menuTitle);
            holder.imageView = convertView.findViewById(R.id.menuIcon);
            holder.addInspectionView = convertView.findViewById(R.id.addInspection);
            holder.checked = convertView.findViewById(R.id.checked);
            convertView.setTag(holder);
        } else {
            holder = (ParentViewHolder) convertView.getTag();
        }


        String parentMenuString = parentMenuList.get(groupPosition).getTitle();

        holder.menuTitle.setText(parentMenuString);

        holder.addInspectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLabelAddClickListener.onLabelAddClick();
            }
        });

        if (parentMenuList.get(groupPosition).isChecked()) {
            holder.checked.setVisibility(View.VISIBLE);
        } else {
            holder.checked.setVisibility(View.GONE);
        }
        if (parentMenuString.equals("Claim Details")) {
            holder.imageView.setImageResource(R.drawable.ic_reports);
        } else if (parentMenuString.equals("Property Details")) {
            holder.imageView.setImageResource(R.drawable.ic_building);
        } else if (parentMenuString.equals("Peril")) {
            holder.imageView.setImageResource(R.drawable.ic_warning);
        } else if (parentMenuString.equals("Point Of Origin")) {
            holder.imageView.setImageResource(R.drawable.ic_point_of_origin);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_damage);
            holder.addInspectionView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.drawer_layout_child_menu_item, parent, false);
            holder = new ChildViewHolder();
            holder.menuTitle = convertView.findViewById(R.id.menuTitle);
            holder.labelDeleteBtn = convertView.findViewById(R.id.labelDeleteBtn);
            convertView.setTag(holder);

        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        String menuTitle = childMenuList.get(parentMenuList.get(groupPosition).getTitle()).get(childPosition).toString();

        if (menuTitle.equals("Risk Overview")) {
            holder.labelDeleteBtn.setVisibility(View.GONE);
        } else {
            holder.labelDeleteBtn.setVisibility(View.VISIBLE);
        }
        holder.menuTitle.setText(menuTitle);
        holder.labelDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Remove Image")
                            .setMessage("Are you sure wanna remove label ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String id = childMenuList.get(parentMenuList.get(groupPosition).getTitle()).get(childPosition).getId();
                                    int result = mCategoryList.deleteLabel(id);
                                    if (result > 0) {
                                        addEditLabelInterface.onLabelDeleted(childPosition);
                                    }
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    negativeButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    positiveButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                }
            }
        });


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }

    public interface OnLabelAddClickListener {
        void onLabelAddClick();
    }

    static class ChildViewHolder {
        TextView menuTitle;
        Button labelDeleteBtn;
    }

    static class ParentViewHolder {
        TextView menuTitle;
        ImageView imageView;
        ImageView checked;
        Button addInspectionView;
    }


}