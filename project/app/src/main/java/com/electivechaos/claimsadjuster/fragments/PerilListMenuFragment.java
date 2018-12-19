package com.electivechaos.claimsadjuster.fragments;

import android.content.Context;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.adapters.DrawerMenuListAdapter;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.BackButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnGenerateReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnPerilSelectionListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;
import com.electivechaos.claimsadjuster.pojo.PerilPOJO;

import java.util.ArrayList;
import java.util.List;

public class PerilListMenuFragment extends Fragment {

    public ArrayList<PerilPOJO> perilPOJOS = new ArrayList<>();
    CategoryListDBHelper mCategoryListDBHelper;
    private Boolean isFabOpen = false;
    private FloatingActionButton showFabBtn, fabGoNextBtn, fabGoBackBtn, fabAddLabelBtn, fabGenerateReportBtn, fabSaveReportBtn;
    private Animation fab_open, fab_close;
    private NextButtonClickListener nextButtonClickListener;
    private BackButtonClickListener backButtonClickListener;
    private DrawerMenuListAdapter.OnLabelAddClickListener onLabelAddClickListener;
    private OnSaveReportClickListener onSaveReportClickListener;
    private OnGenerateReportClickListener onGenerateReportClickListener;
    private OnPerilSelectionListener onPerilSelectionListener;
    private RecyclerView recyclerView;
    private PerilListMenuFragment.PerilListAdapter mAdapter;

    private PerilPOJO perilPOJODetails;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        perilPOJODetails = getArguments() != null ? (PerilPOJO) getArguments().getParcelable("perilDetails") : new PerilPOJO();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.peril_list_menu_fragment, container, false);
        showFabBtn = view.findViewById(R.id.showFab);
        fabGoNextBtn = view.findViewById(R.id.fabGoNext);
        fabGoBackBtn = view.findViewById(R.id.fabGoBack);
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

        fabGoBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonClickListener.onBackButtonClick();
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
        updatePerilDetails();
        return view;
    }

    public void animateFAB() {
        if (isFabOpen) {
            showFabBtn.setImageResource(R.drawable.ic_more_vertical_white);
            fabGoNextBtn.startAnimation(fab_close);
            fabGoBackBtn.startAnimation(fab_close);
            fabAddLabelBtn.startAnimation(fab_close);
            fabGenerateReportBtn.startAnimation(fab_close);
            fabSaveReportBtn.startAnimation(fab_close);
            fabGoNextBtn.setClickable(false);
            fabGoBackBtn.setClickable(false);
            fabAddLabelBtn.setClickable(false);
            fabGenerateReportBtn.setClickable(false);
            fabSaveReportBtn.setClickable(false);
            isFabOpen = false;

        } else {
            showFabBtn.setImageResource(R.drawable.ic_close_white);
            fabGoNextBtn.startAnimation(fab_open);
            fabGoBackBtn.startAnimation(fab_open);
            fabAddLabelBtn.startAnimation(fab_open);
            fabGenerateReportBtn.startAnimation(fab_open);
            fabSaveReportBtn.startAnimation(fab_open);
            fabGoNextBtn.setClickable(true);
            fabGoBackBtn.setClickable(true);
            fabAddLabelBtn.setClickable(true);
            fabGenerateReportBtn.setClickable(true);
            fabSaveReportBtn.setClickable(true);
            isFabOpen = true;
        }

    }


    private void updatePerilDetails() {
        perilPOJOS = mCategoryListDBHelper.getPeril();
        mAdapter = new PerilListMenuFragment.PerilListAdapter(perilPOJOS, perilPOJODetails);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            nextButtonClickListener = (NextButtonClickListener) getActivity();
            backButtonClickListener = (BackButtonClickListener) getActivity();
            onLabelAddClickListener = (DrawerMenuListAdapter.OnLabelAddClickListener) getActivity();
            onSaveReportClickListener = (OnSaveReportClickListener) getActivity();
            onGenerateReportClickListener = (OnGenerateReportClickListener) getActivity();
            onPerilSelectionListener = (OnPerilSelectionListener) getActivity();
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }

    public class PerilListAdapter extends RecyclerView.Adapter<PerilListMenuFragment.PerilListAdapter.MyViewHolder> {
        List<PerilPOJO> perilPOJOS;
        PerilPOJO perilPOJODetails;
        private int sSelected;
        private CheckBox lastSelectedCheckbox;

        PerilListAdapter(ArrayList<PerilPOJO> perilPOJOS, PerilPOJO perilPOJODetails) {
            this.perilPOJOS = perilPOJOS;
            this.perilPOJODetails = perilPOJODetails;
            this.sSelected = getSelectedPosition();
        }

        int getSelectedPosition() {
            for (int i = 0; i < perilPOJOS.size(); i++) {
                if (perilPOJOS.get(i).getName().equals(perilPOJODetails.getName())) {
                    return i;
                }
            }
            return -1;
        }

        @NonNull
        @Override
        public PerilListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_row_peril_menu_layout, parent, false);
            return new PerilListMenuFragment.PerilListAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final PerilListAdapter.MyViewHolder holder, final int position) {
            final PerilPOJO perilPOJO = perilPOJOS.get(holder.getAdapterPosition());

            holder.title.setText(perilPOJO.getName());
            holder.desc.setText(perilPOJO.getDescription());
            holder.chkItem.setChecked(sSelected == holder.getAdapterPosition());
            holder.chkItem.setTag(holder.getAdapterPosition());

            if (sSelected == holder.getAdapterPosition()) {
                lastSelectedCheckbox = holder.chkItem;

            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCheckChange(holder, perilPOJO);
                }
            });
            holder.chkItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCheckChangeFromCheckbox(holder, perilPOJO);
                }
            });

        }

        void onCheckChange(MyViewHolder holder, PerilPOJO perilPOJO) {

            holder.chkItem.setChecked(!holder.chkItem.isChecked());
            if (holder.chkItem.isChecked()) {
                sSelected = holder.getAdapterPosition();
                onPerilSelectionListener.setPeril(perilPOJO);
                if (lastSelectedCheckbox != null && (int) lastSelectedCheckbox.getTag() != holder.getAdapterPosition()) {
                    lastSelectedCheckbox.setChecked(false);
                    lastSelectedCheckbox = holder.chkItem;
                } else {
                    lastSelectedCheckbox = holder.chkItem;
                }

            } else {
                lastSelectedCheckbox = null;
                sSelected = -1;
                onPerilSelectionListener.setPeril(new PerilPOJO());
            }
        }

        void onCheckChangeFromCheckbox(MyViewHolder holder, PerilPOJO perilPOJO) {

            if (holder.chkItem.isChecked()) {
                sSelected = holder.getAdapterPosition();
                onPerilSelectionListener.setPeril(perilPOJO);
                if (lastSelectedCheckbox != null && (int) lastSelectedCheckbox.getTag() != holder.getAdapterPosition()) {
                    lastSelectedCheckbox.setChecked(false);
                    lastSelectedCheckbox = holder.chkItem;
                } else {
                    lastSelectedCheckbox = holder.chkItem;
                }

            } else {
                lastSelectedCheckbox = null;
                sSelected = -1;
                onPerilSelectionListener.setPeril(new PerilPOJO());
            }
        }

        @Override
        public int getItemCount() {
            return perilPOJOS.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, desc;
            CheckBox chkItem;

            MyViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.category_name);
                desc = itemView.findViewById(R.id.category_description);
                chkItem = itemView.findViewById(R.id.perilSelect);

            }

        }


    }
}