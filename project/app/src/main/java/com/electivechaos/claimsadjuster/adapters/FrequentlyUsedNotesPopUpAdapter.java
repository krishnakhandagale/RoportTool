package com.electivechaos.claimsadjuster.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;

import java.util.ArrayList;

/**
 * Created by nafeesa on 4/17/18.
 */

public class FrequentlyUsedNotesPopUpAdapter extends BaseAdapter {
    private Context context;
    private ArrayList notesList;

    public FrequentlyUsedNotesPopUpAdapter(Context context, ArrayList notesList) {
        this.notesList = notesList;
        this.context = context;
    }

    @Override
    public int getCount() {
        if(notesList != null) {
            return notesList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return notesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.most_frequently_used_notes_popup_adapter, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.note);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(notesList!=null){
            if(notesList.get(position)!=null) {
                holder.textView.setText(notesList.get(position).toString());
                holder.textView.setGravity(Gravity.LEFT);
                holder.textView.setBackgroundColor(0);
            }
        }else {
            holder.textView.setText(R.string.notes_not_found);
        }
        return convertView;

    }
    static class ViewHolder {
        TextView textView;

    }
}
