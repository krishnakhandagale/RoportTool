package com.electivechaos.checklistapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.electivechaos.checklistapp.R;

public class AddEditCauseOfLossActivity extends AppCompatActivity {
    private  String causeOfLossTitle = null;
    private  String causeOfLossDescription = null;
    private  int causeOfLossID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_cause_of_loss);

        if(getIntent().getExtras()!= null){
            Bundle dataFromActivity = getIntent().getBundleExtra("data");
            causeOfLossTitle = dataFromActivity.get("name").toString();
            causeOfLossDescription = dataFromActivity.get("desc").toString();
            causeOfLossID = Integer.parseInt(dataFromActivity.get("id").toString());

            Button updateCategoryButton = findViewById(R.id.updateCauseOfLoss);
            updateCategoryButton.setText("Update Cause Of Loss");

            EditText categoryName = (EditText) findViewById(R.id.name);
            categoryName.setText(causeOfLossTitle.toString());

            EditText categoryDescription = (EditText) findViewById(R.id.description);
            categoryDescription.setText(causeOfLossDescription.toString());
        }

        Button updateCategoryButton = findViewById(R.id.updateCauseOfLoss);
        updateCategoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            EditText categoryName = (EditText) findViewById(R.id.name);
            String categoryNameString = categoryName.getText().toString();

            EditText categoryDescription = (EditText) findViewById(R.id.description);
            String categoryDescriptionString = categoryDescription.getText().toString();
            Bundle data = new Bundle();//create bundle instance
            if(causeOfLossID == -1) {
                Log.d("------------->>", "onClick: THIS IS ADD CAUSE OF LOSS EVENT");

                data.putString("categoryName", categoryNameString);//put string to pass with a key value
                data.putString("categoryDesc", categoryDescriptionString);//put string to pass with a key value
                Intent intent = new Intent();
                intent.putExtra("data", data);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }else {
                Log.d("------------->>", "onClick: THIS IS EDIT EDIT EDIT CAUSE OF LOSS EVENT");
                Intent intent = new Intent();
                data.putString("categoryName", categoryNameString);//put string to pass with a key value
                data.putString("categoryDesc", categoryDescriptionString);//put string to pass with a key value
                data.putInt("categoryId", causeOfLossID);//put string to pass with a key value
                intent.putExtra("data", data);
                setResult(2, intent);
                finish();
            }
            }
        });
    }
}
