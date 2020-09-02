package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.PerilPOJO;

import java.util.ArrayList;
import java.util.List;

public class PerilDetailsActivity extends AppCompatActivity {

    static CategoryListDBHelper mCategoryListDBHelper;
    public ArrayList<PerilPOJO> perilPOJOS = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peril_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage Peril");


        recyclerView = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mCategoryListDBHelper = CategoryListDBHelper.getInstance(this);
        updatePerilDetails();

        FloatingActionButton btnAddReport = findViewById(R.id.btnAddPeril);
        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCategoryActivity = new Intent(PerilDetailsActivity.this, AddEditPerilActivity.class);
                startActivityForResult(addCategoryActivity, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                updatePerilDetails();
            }
        } else if (requestCode == 2) {
            updatePerilDetails();
        }

    }

    private void updatePerilDetails() {
        perilPOJOS = mCategoryListDBHelper.getPeril();
        PerilListAdapter mAdapter = new PerilListAdapter(perilPOJOS, PerilDetailsActivity.this);
        recyclerView.setAdapter(mAdapter);
    }

    public class PerilListAdapter extends RecyclerView.Adapter<PerilListAdapter.MyViewHolder> {
        List<PerilPOJO> perilPOJOS;
        private Context context;

        PerilListAdapter(ArrayList<PerilPOJO> perilPOJOS, Context context) {
            this.context = context;
            this.perilPOJOS = perilPOJOS;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_row_category, parent, false);
            return new PerilListAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final PerilListAdapter.MyViewHolder holder, int position) {
            final PerilPOJO perilPOJO = perilPOJOS.get(position);
            holder.title.setText(perilPOJO.getName());
            holder.desc.setText(perilPOJO.getDescription());

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
                                    Intent perilActivity = new Intent(context, AddEditPerilActivity.class);
                                    Bundle data = new Bundle();
                                    data.putString("name", perilPOJO.getName());
                                    data.putString("description", perilPOJO.getDescription());
                                    data.putInt("id", perilPOJO.getID());
                                    perilActivity.putExtra("perilDetails", data);
                                    startActivityForResult(perilActivity, 2);
                                    break;
                                case R.id.delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Delete peril")
                                            .setMessage("Are you sure you want to delete this peril ?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mCategoryListDBHelper.deletePeril(String.valueOf(perilPOJO.getID()));
                                                    updatePerilDetails();
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
                                    negativeButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                                    Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                    positiveButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                                    break;
                                default:
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
}
