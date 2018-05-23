package com.electivechaos.claimsadjuster.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.pojo.ReportPOJO;

import java.util.ArrayList;
import java.util.Collections;

public class CustomisationActivity extends AppCompatActivity {
    ReportPOJO reportPOJO = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customise_report_activity);

        reportPOJO = getIntent().getParcelableExtra("reportDetails");

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        RecyclerView recyclerView = findViewById(R.id.customLabelPositionView);
        ImageButton button = findViewById(R.id.doneBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("modified_report", reportPOJO);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ArrayList<Label> labels =  reportPOJO.getLabelArrayList();

        if(labels != null){
            final Adapter adapter = new Adapter(this,labels);
            recyclerView.setAdapter(adapter);


            ItemTouchHelper.Callback ithCallback = new ItemTouchHelper.Callback() {
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    Collections.swap(reportPOJO.getLabelArrayList(), viewHolder.getAdapterPosition(), target.getAdapterPosition());
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
            holder.label.setText(labelArrayList.get(position).getName());

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
                return true;
        }
        return  true;
    }
}
