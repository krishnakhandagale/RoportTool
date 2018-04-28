package com.electivechaos.checklistapp.maintabs;

import android.content.Context;
import android.content.Intent;
import android.media.MediaExtractor;
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

import com.electivechaos.checklistapp.AddEditCategoryActivity;
import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.database.CategoryListDBHelper;
import com.electivechaos.checklistapp.database.ReportsListDBHelper;
import com.electivechaos.checklistapp.pojo.Category;
import com.electivechaos.checklistapp.pojo.CauseOfLoss;

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
        updateCategoryList();

        FloatingActionButton btnAddReport = view.findViewById(R.id.btnAddCategory);
        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCategoryActivity = new Intent(getActivity(), AddEditCategoryActivity.class);
                startActivityForResult(addCategoryActivity, 1);
            }
        });
        return view;
    }

    private  void updateCategoryList(){
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
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull CauseOfLossListAdapter.MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public MyViewHolder(View itemView) {
                super(itemView);
            }

        }
    }

}