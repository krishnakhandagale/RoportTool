package com.electivechaos.checklistapp.maintabs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.database.CategoryListDBHelper;
import com.electivechaos.checklistapp.database.ReportsListDBHelper;
import com.electivechaos.checklistapp.pojo.CauseOfLoss;
import com.electivechaos.checklistapp.ui.AddEditCategoryActivity;
import com.electivechaos.checklistapp.ui.AddEditCauseOfLossActivity;

import java.util.ArrayList;
import java.util.List;

public class CauseOfLossListFragment  extends Fragment {
    public ArrayList<CauseOfLoss> causeOfLosses = new ArrayList<>();
    private RecyclerView recyclerView;
    static CategoryListDBHelper mCategoryListDBHelper;
    private CauseOfLossListFragment.CauseOfLossListAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cause_of_loss_list_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mCategoryListDBHelper = new CategoryListDBHelper(getContext());
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
                Bundle dataFromActivity = data.getBundleExtra("causeOdLossDetails");
                String categoryName = dataFromActivity.get("name").toString();
                String categoryDescription = dataFromActivity.get("description").toString();
                CauseOfLoss loss = new CauseOfLoss();
                loss.setName(categoryName);
                loss.setDescription(categoryDescription);
                mCategoryListDBHelper.addCauseOfLoss(loss);
                updateCauseOfLossList();
            }
        }

        if (requestCode == 2) {
            if (resultCode == 2) {
                Bundle dataFromActivity = data.getBundleExtra("causeOdLossDetails");
                String name = dataFromActivity.get("name").toString();
                String desc = dataFromActivity.get("description").toString();
                int id = Integer.parseInt(dataFromActivity.get("id").toString());
                CauseOfLoss loss = new CauseOfLoss();
                loss.setName(name);
                loss.setDescription(desc);
                loss.setID(id);
                mCategoryListDBHelper.updateCauseOfLoss(loss);
                updateCauseOfLossList();
            }
        }
    }

    private  void updateCauseOfLossList(){
        causeOfLosses = mCategoryListDBHelper.getCauseOfLosses();
        mAdapter = new CauseOfLossListFragment.CauseOfLossListAdapter(causeOfLosses, getContext());
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
        public void onBindViewHolder(@NonNull final CauseOfLossListAdapter.MyViewHolder holder, int position) {
            final CauseOfLoss loss = causeOfLosses.get(position);
            holder.title.setText(loss.getName());
            holder.desc.setText(loss.getDescription());

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
                                    Intent causeOfLossActivity = new Intent(context, AddEditCauseOfLossActivity.class);
                                    Bundle data = new Bundle();
                                    data.putString("name", loss.getName());
                                    data.putString("description", loss.getDescription());
                                    data.putInt("id", loss.getID());
                                    causeOfLossActivity.putExtra("causeOdLossDetails", data);
                                    startActivityForResult(causeOfLossActivity, 2);
                                    break;
                                case R.id.delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Delete Cause")
                                            .setMessage("Are you sure you want to delete this cause of loss ?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mCategoryListDBHelper.deleteCauseOfLoss(String.valueOf(loss.getID()));
                                                    updateCauseOfLossList();
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
            return causeOfLosses.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, desc;
            public ImageView textViewOptions;
            public MyViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.category_name);
                desc = itemView.findViewById(R.id.category_description);
                textViewOptions = itemView.findViewById(R.id.textViewOptions);

            }

        }
    }

}