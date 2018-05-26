package com.electivechaos.claimsadjuster.fragments;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.adapters.DrawerMenuListAdapter;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnGenerateReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;
import com.electivechaos.claimsadjuster.pojo.PerilPOJO;
import com.electivechaos.claimsadjuster.ui.AddEditCauseOfLossActivity;

import java.util.ArrayList;
import java.util.List;

public class PerilListMenuFragment extends Fragment {

    private Boolean isFabOpen = false;
    private FloatingActionButton showFabBtn,fabGoNextBtn, fabAddLabelBtn, fabGenerateReportBtn, fabSaveReportBtn;
    private Animation fab_open, fab_close;
    private NextButtonClickListener nextButtonClickListener;
    private DrawerMenuListAdapter.OnLabelAddClickListener onLabelAddClickListener;
    private OnSaveReportClickListener onSaveReportClickListener;
    private OnGenerateReportClickListener onGenerateReportClickListener;


    public ArrayList<PerilPOJO> perilPOJOS = new ArrayList<>();
    private RecyclerView recyclerView;
    static CategoryListDBHelper mCategoryListDBHelper;
    private PerilListMenuFragment.CauseOfLossListAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.peril_list_fragment, container, false);
        showFabBtn = view.findViewById(R.id.showFab);
        fabGoNextBtn = view.findViewById(R.id.fabGoNext);
        fabAddLabelBtn = view.findViewById(R.id.fabAddLabel);
        fabGenerateReportBtn = view.findViewById(R.id.fabGenerateReport);
        fabSaveReportBtn = view.findViewById(R.id.fabSaveReport);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);


        fabGoNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonClickListener.onNextButtonClick();
                animateFAB();
            }
        });
        showFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });

        fabAddLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLabelAddClickListener.onLabelAddClick();
                animateFAB();
            }
        });

        fabGenerateReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGenerateReportClickListener.onReportGenerateClicked();
                animateFAB();
            }
        });

        fabSaveReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveReportClickListener.onReportSave(true);
                animateFAB();
            }
        });


        recyclerView = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mCategoryListDBHelper = CategoryListDBHelper.getInstance(getActivity());
        updateCauseOfLossList();

        FloatingActionButton btnAddReport = view.findViewById(R.id.btnAddPeril);
        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCategoryActivity = new Intent(getActivity(), AddEditCauseOfLossActivity.class);
                startActivityForResult(addCategoryActivity, 1);
            }
        });
        return view;
    }

    public void animateFAB() {
        if (isFabOpen) {
            showFabBtn.setImageResource(R.drawable.ic_more_vertical_white);
            fabGoNextBtn.startAnimation(fab_close);
            fabAddLabelBtn.startAnimation(fab_close);
            fabGenerateReportBtn.startAnimation(fab_close);
            fabSaveReportBtn.startAnimation(fab_close);
            fabGoNextBtn.setClickable(false);
            fabAddLabelBtn.setClickable(false);
            fabGenerateReportBtn.setClickable(false);
            fabSaveReportBtn.setClickable(false);
            isFabOpen = false;

        } else {
            showFabBtn.setImageResource(R.drawable.ic_close_white);
            fabGoNextBtn.startAnimation(fab_open);
            fabAddLabelBtn.startAnimation(fab_open);
            fabGenerateReportBtn.startAnimation(fab_open);
            fabSaveReportBtn.startAnimation(fab_open);
            fabGoNextBtn.setClickable(true);
            fabAddLabelBtn.setClickable(true);
            fabGenerateReportBtn.setClickable(true);
            fabSaveReportBtn.setClickable(true);
            isFabOpen = true;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle dataFromActivity = data.getBundleExtra("causeOdLossDetails");
                String categoryName = dataFromActivity.get("name").toString();
                String categoryDescription = dataFromActivity.get("description").toString();
                PerilPOJO loss = new PerilPOJO();
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
                PerilPOJO loss = new PerilPOJO();
                loss.setName(name);
                loss.setDescription(desc);
                loss.setID(id);
                mCategoryListDBHelper.updateCauseOfLoss(loss);
                updateCauseOfLossList();
            }
        }
    }

    private  void updateCauseOfLossList(){
        perilPOJOS = mCategoryListDBHelper.getCauseOfLosses();
        mAdapter = new PerilListMenuFragment.CauseOfLossListAdapter(perilPOJOS, getContext());
        recyclerView.setAdapter(mAdapter);
    }

    public class CauseOfLossListAdapter extends RecyclerView.Adapter<PerilListMenuFragment.CauseOfLossListAdapter.MyViewHolder> {
        private Context context;
        public List<PerilPOJO> perilPOJOS;
        public CauseOfLossListAdapter(ArrayList<PerilPOJO> perilPOJOS, Context context) {
            this.context = context;
            this.perilPOJOS = perilPOJOS;
        }

        @NonNull
        @Override
        public CauseOfLossListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_row_category, parent, false);
            return new PerilListMenuFragment.CauseOfLossListAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final CauseOfLossListAdapter.MyViewHolder holder, int position) {
            final PerilPOJO loss = perilPOJOS.get(position);
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
            return perilPOJOS.size();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            nextButtonClickListener = (NextButtonClickListener) getActivity();
            onLabelAddClickListener = (DrawerMenuListAdapter.OnLabelAddClickListener)getActivity();
            onSaveReportClickListener = (OnSaveReportClickListener)getActivity();
            onGenerateReportClickListener = (OnGenerateReportClickListener)getActivity();
        }catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }
}