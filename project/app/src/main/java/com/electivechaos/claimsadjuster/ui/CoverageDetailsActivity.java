package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.CoveragePOJO;

import java.util.ArrayList;
import java.util.List;

public class CoverageDetailsActivity extends AppCompatActivity{
    public ArrayList<CoveragePOJO> coveragePOJOS = new ArrayList<>();
    private RecyclerView recyclerView;
    static CategoryListDBHelper mCategoryListDBHelper;
    private CoverageDetailsActivity.CoverageListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coverage_details);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Manage Coverage");
        }
        recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mCategoryListDBHelper = CategoryListDBHelper.getInstance(this);
        updateCoverageDetails();

        FloatingActionButton btnAddCoverage = findViewById(R.id.btnAddCoverage);
        btnAddCoverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addEditCoverageActivity = new Intent(CoverageDetailsActivity.this, AddEditCoverageActivity.class);
                startActivityForResult(addEditCoverageActivity, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                updateCoverageDetails();
            }
        }
        else if(requestCode == 2) {
            updateCoverageDetails();
        }

    }

    private  void updateCoverageDetails(){
        coveragePOJOS = mCategoryListDBHelper.getCoverageList();
        mAdapter = new CoverageDetailsActivity.CoverageListAdapter(coveragePOJOS,CoverageDetailsActivity.this);
        recyclerView.setAdapter(mAdapter);
    }

    public class CoverageListAdapter extends RecyclerView.Adapter<CoverageDetailsActivity.CoverageListAdapter.MyViewHolder> {
        private Context context;
        List<CoveragePOJO> coveragePOJOS;
        CoverageListAdapter(ArrayList<CoveragePOJO> coveragePOJOS, Context context) {
            this.context = context;
            this.coveragePOJOS = coveragePOJOS;
        }

        @NonNull
        @Override
        public CoverageDetailsActivity.CoverageListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_row_category, parent, false);
            return new CoverageDetailsActivity.CoverageListAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final CoverageDetailsActivity.CoverageListAdapter.MyViewHolder holder, int position) {
            final CoveragePOJO coveragePOJO = coveragePOJOS.get(position);
            holder.title.setText(coveragePOJO.getName());
            holder.desc.setText(coveragePOJO.getDescription());

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
                                    Intent coverageActivity = new Intent(context, AddEditCoverageActivity.class);
                                    Bundle data = new Bundle();
                                    data.putString("name", coveragePOJO.getName());
                                    data.putString("description", coveragePOJO.getDescription());
                                    data.putInt("id", coveragePOJO.getID());
                                    coverageActivity.putExtra("coverageDetails", data);
                                    startActivityForResult(coverageActivity, 2);
                                    break;
                                case R.id.delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Delete coverage")
                                            .setMessage("Are you sure you want to delete this coverage ?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mCategoryListDBHelper.deleteCoverage(String.valueOf(coveragePOJO.getID()));
                                                    updateCoverageDetails();
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
            return coveragePOJOS.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, desc;
            public ImageView textViewOptions;
            MyViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.category_name);
                desc = itemView.findViewById(R.id.category_description);
                textViewOptions = itemView.findViewById(R.id.textViewOptions);

            }

        }
    }
}
