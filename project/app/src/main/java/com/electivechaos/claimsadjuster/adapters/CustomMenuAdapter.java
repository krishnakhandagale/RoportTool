package com.electivechaos.claimsadjuster.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.pojo.BuildingTypePOJO;
import com.electivechaos.claimsadjuster.pojo.FoundationPOJO;
import com.electivechaos.claimsadjuster.pojo.RoofSystemPOJO;
import com.electivechaos.claimsadjuster.pojo.SidingPOJO;

import java.util.ArrayList;

/**
 * Created by nafeesa on 5/22/18.
 */

public class CustomMenuAdapter extends BaseAdapter {
    private Context context;
    private ArrayList arrayList;
    private int selectedPosition = -1;
    private String value;
    private String type;

    public CustomMenuAdapter(Context context, ArrayList arrayList, String value, String type){
        this.arrayList = arrayList;
        this.context = context;
        this.value = value;
        this.type = type;
        this.selectedPosition =  findSelectedPosition();;
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

    public int findSelectedPosition(){

        for(int i=0;i<arrayList.size();i++){
            if(type.equalsIgnoreCase("roof_system")) {
                if (((RoofSystemPOJO)arrayList.get(i)).getName().equals(value)){
                    return  i;
                }
                else if(type.equalsIgnoreCase("siding")) {
                    if (((SidingPOJO)arrayList.get(i)).getName().equals(value)){
                        return  i;
                    }
                }
                else if(type.equalsIgnoreCase("foundation")) {
                    if (((FoundationPOJO)arrayList.get(i)).getName().equals(value)){
                        return  i;
                    }
                }
                else if(type.equalsIgnoreCase("building_type")) {
                    if (((BuildingTypePOJO)arrayList.get(i)).getName().equals(value)){
                        return  i;
                    }
                }
          }
        }
        return  -1;
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

       if(type.equalsIgnoreCase("roof_system")) {

           RoofSystemPOJO roofSystemPOJO=(RoofSystemPOJO)arrayList.get(position);
           if(selectedPosition == position || roofSystemPOJO.getName().toString().equals(value)) {
               holder.checkedTextView.setChecked(true);
           }else {
               holder.checkedTextView.setChecked(false);
           }
           holder.checkedTextView.setText(roofSystemPOJO.getName());
       }
       else if(type.equalsIgnoreCase("siding")){

           SidingPOJO sidingPOJO=(SidingPOJO) arrayList.get(position);
           if(selectedPosition == position || sidingPOJO.getName().toString().equals(value)) {
               holder.checkedTextView.setChecked(true);
           }else {
               holder.checkedTextView.setChecked(false);
           }

           holder.checkedTextView.setText(sidingPOJO.getName());
       }
       else if(type.equalsIgnoreCase("foundation")){
           FoundationPOJO foundationPOJO=(FoundationPOJO) arrayList.get(position);

           if(selectedPosition == position || foundationPOJO.getName().toString().equals(value)) {
               holder.checkedTextView.setChecked(true);
           }else {
               holder.checkedTextView.setChecked(false);
           }

           holder.checkedTextView.setText(foundationPOJO.getName());
       }
       else if(type.equalsIgnoreCase("building_type")){
           BuildingTypePOJO buildingTypePOJO=(BuildingTypePOJO) arrayList.get(position);

           if(selectedPosition == position || buildingTypePOJO.getName().toString().equals(value)) {
               holder.checkedTextView.setChecked(true);
           }else {
               holder.checkedTextView.setChecked(false);
           }

           holder.checkedTextView.setText(buildingTypePOJO.getName());
       }

        return convertView;
    }
    public static  class ViewHolder{
        CheckedTextView checkedTextView;
    }

}
