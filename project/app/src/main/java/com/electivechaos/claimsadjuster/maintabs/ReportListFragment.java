package com.electivechaos.claimsadjuster.maintabs;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.ReportItemPOJO;
import com.electivechaos.claimsadjuster.ui.AddEditReportActivity;

import java.util.ArrayList;

public class ReportListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ReportListAdapter mAdapter;
    private CategoryListDBHelper mCategoryListDBHelper;
    private  ArrayList<ReportItemPOJO> reportItemPOJOS;


    @Override
    public void onStart() {
        super.onStart();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_list_fragment, container, false);


        mCategoryListDBHelper = CategoryListDBHelper.getInstance(getActivity());

        recyclerView = view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton  btnAddReport = view.findViewById(R.id.btnAddReport);


        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent addReportActivity = new Intent(getActivity(), AddEditReportActivity.class);
                startActivity(addReportActivity);
            }
        });


        setReportListAdapter();

        return view;
    }


    public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.MyViewHolder> {
        private Context context;
        private ArrayList<ReportItemPOJO> reportItemPOJOArrayList;


        ReportListAdapter(Context context, ArrayList<ReportItemPOJO> reportItemPOJOArrayList){
            this.context = context;
            this.reportItemPOJOArrayList = reportItemPOJOArrayList;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView reportName, reportDescription;
            public ImageView textViewOptions;


            public MyViewHolder(View view) {
                super(view);

                reportName = view.findViewById(R.id.reportName);
                reportDescription = view.findViewById(R.id.reportDescription);
                textViewOptions = view.findViewById(R.id.textViewOptions);

            }
        }




        @Override
        public ReportListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_row_report, parent, false);

            return new ReportListAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ReportListAdapter.MyViewHolder holder, final int position) {
            final ReportItemPOJO reportItemPOJO = reportItemPOJOArrayList.get(position);
            if(reportItemPOJO.getReportTitle().isEmpty() || reportItemPOJO.getReportTitle()==null) {

                holder.reportName.setText(reportItemPOJO.getCreatedDate());
            }else{
                holder.reportName.setText(reportItemPOJO.getReportTitle());
            }

            holder.reportDescription.setText(reportItemPOJO.getReportDescription());

            holder.textViewOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, holder.textViewOptions);
                    popup.inflate(R.menu.action_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                        Intent intent=new Intent(context,AddEditReportActivity.class);
                                        intent.putExtra("reportId",reportItemPOJO.getId());
                                        startActivityForResult(intent,1);
                                        notifyDataSetChanged();
                                    break;
                                case R.id.delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle(R.string.delete_report_dialog_title)
                                            .setMessage(R.string.delete_report_msg)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mCategoryListDBHelper. deleteReportEntry(reportItemPOJO.getId());
                                                    reportItemPOJOArrayList.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeChanged(position, reportItemPOJOArrayList.size());
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
                                    negativeButton.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
                                    Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                    positiveButton.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return reportItemPOJOArrayList.size();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setReportListAdapter();
    }

    private void setReportListAdapter() {
        reportItemPOJOS = mCategoryListDBHelper.getReports();
        mAdapter =  new ReportListAdapter(getActivity(), reportItemPOJOS);
        recyclerView.setAdapter(mAdapter);
    }
}
