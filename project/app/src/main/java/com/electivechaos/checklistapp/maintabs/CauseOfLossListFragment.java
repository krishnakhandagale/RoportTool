package com.electivechaos.checklistapp.maintabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.database.ReportsListDBHelper;
import com.electivechaos.checklistapp.pojo.CauseOfLoss;
import com.electivechaos.checklistapp.ui.AddEditCauseOfLossActivity;

import java.util.ArrayList;
import java.util.List;

public class CauseOfLossListFragment  extends Fragment {
    public ArrayList<CauseOfLoss> causeOfLosses = new ArrayList<>();
    private RecyclerView recyclerView;
    static ReportsListDBHelper mReportListDBHelper;
    private CauseOfLossListFragment.CauseOfLossListAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cause_of_loss_list_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mAdapter = new CauseOfLossListFragment.CauseOfLossListAdapter(causeOfLosses, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mReportListDBHelper = new ReportsListDBHelper(getContext());
        updateCauseOfLossList();

        FloatingActionButton btnAddReport = view.findViewById(R.id.btnAddCauseOfLoss);
        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCategoryActivity = new Intent(getActivity(), AddEditCauseOfLossActivity.class);
                startActivityForResult(addCategoryActivity, 1);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle dataFromActivity = data.getBundleExtra("data");
                String categoryName = dataFromActivity.get("name").toString();
                String categoryDescription = dataFromActivity.get("desc").toString();
                //add to category database
                CauseOfLoss loss = new CauseOfLoss();
                loss.setName(categoryName);
                loss.setDescription(categoryDescription);
                mReportListDBHelper.addCauseOfLoss(loss);
                updateCauseOfLossList();
            }
        }

        if (requestCode == 2) {
            if (resultCode == 2) {
                Bundle dataFromActivity = data.getBundleExtra("data");
                String name = dataFromActivity.get("name").toString();
                String desc = dataFromActivity.get("desc").toString();
                int id = Integer.parseInt(dataFromActivity.get("id").toString());
                //add to category database
                CauseOfLoss loss = new CauseOfLoss();
                loss.setName(name);
                loss.setDescription(desc);
                loss.setID(id);
                mReportListDBHelper.updateCauseOfLoss(loss);
                updateCauseOfLossList();
            }
        }

        if (requestCode == 3) {
            if (resultCode == 3) {
                Bundle dataFromActivity = data.getBundleExtra("data");
                String id = dataFromActivity.get("id").toString();
                mReportListDBHelper.deleteCauseOfLoss(id);
                updateCauseOfLossList();
            }
        }
    }

    private  void updateCauseOfLossList(){
        causeOfLosses = mReportListDBHelper.getCauseOfLosses();
        mAdapter = new CauseOfLossListFragment.CauseOfLossListAdapter(causeOfLosses, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    public class CauseOfLossListAdapter extends RecyclerView.Adapter<CauseOfLossListFragment.CauseOfLossListAdapter.MyViewHolder> {
        private Context context;
        public List<CauseOfLoss> causeOfLosses;
        public CauseOfLossListAdapter(ArrayList<CauseOfLoss> causeOfLosses, Context context) {
            this.context = context;
            this.causeOfLosses = causeOfLosses;
        }

        @NonNull
        @Override
        public CauseOfLossListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_row_category, parent, false);
            return new CauseOfLossListFragment.CauseOfLossListAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CauseOfLossListAdapter.MyViewHolder holder, int position) {
            final CauseOfLoss loss = causeOfLosses.get(position);
            holder.title.setText(loss.getName());
            holder.desc.setText(loss.getDescription());
            holder.editLoss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent causeOfLossActivity = new Intent(context, AddEditCauseOfLossActivity.class);
                    Bundle data = new Bundle();//create bundle instance
                    data.putString("name", loss.getName());
                    data.putString("desc", loss.getDescription());
                    data.putInt("id", loss.getID());
                    causeOfLossActivity.putExtra("data", data);
                    startActivityForResult(causeOfLossActivity, 2);
                }
            });

            holder.deleteLoss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mReportListDBHelper.deleteCauseOfLoss(String.valueOf(loss.getID()));
                    updateCauseOfLossList();
                }
            });
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, desc;
            public Button editLoss, deleteLoss;
            public MyViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                desc = itemView.findViewById(R.id.desc);
                editLoss = itemView.findViewById(R.id.edit);
                deleteLoss = itemView.findViewById(R.id.delete);
            }

        }
    }

}