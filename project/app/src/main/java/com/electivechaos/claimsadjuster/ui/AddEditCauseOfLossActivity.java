package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

public class AddEditCauseOfLossActivity extends AppCompatActivity {
    private  String causeOfLossTitle = null;
    private  String causeOfLossDescription = null;
    private  int causeOfLossID = -1;

    private  EditText name;
    private  EditText description;
    private Button updateCategoryButton;
    private  View addEditCauseParentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_cause_of_loss);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        updateCategoryButton = findViewById(R.id.updateCauseOfLoss);

        addEditCauseParentLayout = findViewById(R.id.addEditCauseParentLayout);


        if(getIntent().getExtras()!= null){
            Bundle dataFromActivity = getIntent().getBundleExtra("causeOdLossDetails");
            causeOfLossTitle = dataFromActivity.get("name").toString();
            causeOfLossDescription = dataFromActivity.get("description").toString();
            causeOfLossID = Integer.parseInt(dataFromActivity.get("id").toString());


            updateCategoryButton.setText("Update Cause Of Loss");


            name.setText(causeOfLossTitle.toString());

            description.setText(causeOfLossDescription.toString());
        }

        updateCategoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


            name = findViewById(R.id.name);
            String nameString = name.getText().toString().trim();

            description = findViewById(R.id.description);
            String descriptionString = description.getText().toString().trim();


            if(nameString.isEmpty()){
                CommonUtils.hideKeyboard(AddEditCauseOfLossActivity.this);
                CommonUtils.showSnackbarMessage("Please enter name.", true,true, addEditCauseParentLayout, AddEditCauseOfLossActivity.this);
                return;
            }else if(descriptionString.isEmpty()){
                CommonUtils.hideKeyboard(AddEditCauseOfLossActivity.this);
                CommonUtils.showSnackbarMessage("Please enter description.", true,true, addEditCauseParentLayout, AddEditCauseOfLossActivity.this);
                return;
            }

            Bundle data = new Bundle();
            if(causeOfLossID == -1) {
                data.putString("name", nameString);
                data.putString("description", descriptionString);
                Intent intent = new Intent();
                intent.putExtra("causeOdLossDetails", data);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }else {
                Intent intent = new Intent();
                data.putString("name", nameString);
                data.putString("description", descriptionString);
                data.putInt("id", causeOfLossID);
                intent.putExtra("causeOdLossDetails", data);
                setResult(2, intent);
                finish();
            }
            }
        });
    }
}
