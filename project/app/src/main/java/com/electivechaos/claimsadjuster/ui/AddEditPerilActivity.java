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

public class AddEditPerilActivity extends AppCompatActivity {
    private  String perilTitle = null;
    private  String perilDescription = null;
    private  int perilID = -1;

    private  EditText name;
    private  EditText description;
    private Button updateCategoryButton;
    private  View addEditPerilParentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_peril);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        updateCategoryButton = findViewById(R.id.updatePeril);

        addEditPerilParentLayout = findViewById(R.id.addEditPerilParentLayout);


        if(getIntent().getExtras()!= null){
            Bundle dataFromActivity = getIntent().getBundleExtra("perilDetails");
            perilTitle = dataFromActivity.get("name").toString();
            perilDescription = dataFromActivity.get("description").toString();
            perilID = Integer.parseInt(dataFromActivity.get("id").toString());


            updateCategoryButton.setText("Update Peril");


            name.setText(perilTitle.toString());

            description.setText(perilDescription.toString());
        }

        updateCategoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


            name = findViewById(R.id.name);
            String nameString = name.getText().toString().trim();

            description = findViewById(R.id.description);
            String descriptionString = description.getText().toString().trim();


            if(nameString.isEmpty()){
                CommonUtils.hideKeyboard(AddEditPerilActivity.this);
                CommonUtils.showSnackbarMessage("Please enter name.", true,true, addEditPerilParentLayout, AddEditPerilActivity.this);
                return;
            }else if(descriptionString.isEmpty()){
                CommonUtils.hideKeyboard(AddEditPerilActivity.this);
                CommonUtils.showSnackbarMessage("Please enter description.", true,true, addEditPerilParentLayout, AddEditPerilActivity.this);
                return;
            }

            Bundle data = new Bundle();
            if(perilID == -1) {
                data.putString("name", nameString);
                data.putString("description", descriptionString);
                Intent intent = new Intent();
                intent.putExtra("perilDetails", data);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }else {
                Intent intent = new Intent();
                data.putString("name", nameString);
                data.putString("description", descriptionString);
                data.putInt("id", perilID);
                intent.putExtra("perilDetails", data);
                setResult(2, intent);
                finish();
            }
            }
        });
    }
}
