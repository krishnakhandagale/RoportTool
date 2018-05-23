package com.electivechaos.claimsadjuster.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.pojo.Label;

import java.util.ArrayList;
import java.util.Collections;

public class CustomisationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customise_report_activity);



        RecyclerView recyclerView = findViewById(R.id.customLabelPositionView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final ArrayList<Label> labels = new ArrayList<>();
        labels.add(new Label());
        labels.add(new Label());
        labels.add(new Label());

        final Adapter adapter = new Adapter(this,labels);
        recyclerView.setAdapter(adapter);


        ItemTouchHelper.Callback ithCallback = new ItemTouchHelper.Callback() {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(labels, viewHolder.getAdapterPosition(), target.getAdapterPosition());
//                selectedImagesDataInterface.setSelectedImages(selectedImageList,labelPosition);
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        };
        ItemTouchHelper ith = new ItemTouchHelper(ithCallback);
        ith.attachToRecyclerView(recyclerView);


    }



    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
        Context context;
        ArrayList<Label> labelArrayList;
        Adapter(Context context, ArrayList<Label> labelArrayList){
            this.context = context;
            this.labelArrayList = labelArrayList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater =  LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.label_rearragement_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.label.setText("Krishna" + position);

        }

        @Override
        public int getItemCount() {
            return labelArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView label;
            public ViewHolder(View itemView) {
                super(itemView);
                label = itemView.findViewById(R.id.label);
            }
        }
    }


}
