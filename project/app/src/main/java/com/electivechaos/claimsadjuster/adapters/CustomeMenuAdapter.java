package com.electivechaos.claimsadjuster.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;

import java.util.ArrayList;

/**
 * Created by nafeesa on 5/22/18.
 */

public class CustomeMenuAdapter extends BaseAdapter {
    Context context;
    ArrayList arrayList;

    public CustomeMenuAdapter(Context context, ArrayList arrayList){
        this.arrayList = arrayList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater =  LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.custom_menu_popup_adapter_layout,parent,false);
            holder.checkedTextView = convertView.findViewById(R.id.checked_text_name);
            convertView.setTag(holder);

        } else{
          holder = (ViewHolder) convertView.getTag();
        }

        holder.checkedTextView.setText(arrayList.get(position).toString());
        return convertView;
    }
    public static  class ViewHolder{
        CheckedTextView checkedTextView;
    }

}
