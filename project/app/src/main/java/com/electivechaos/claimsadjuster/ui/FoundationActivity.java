package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.FoundationPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

public class FoundationActivity extends AppCompatActivity {

    private static final int UNIQUE_CONSTRAINT_FAIL_ERROR_CODE = -111;
    private CategoryListDBHelper categoryListDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foundation);

        categoryListDBHelper = CategoryListDBHelper.getInstance(this);

        Button addFoundationBtn = findViewById(R.id.addFoundation);
        final EditText foundationName = findViewById(R.id.foundationName);
        final View parentLayoutForMessages = findViewById(R.id.parentLayout);


        addFoundationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foundationName.getText().toString().isEmpty()) {
                    CommonUtils.showSnackbarMessage(getString(R.string.please_enter_name), true, true, parentLayoutForMessages, FoundationActivity.this);
                } else {
                    FoundationPOJO foundationPOJO = new FoundationPOJO();
                    foundationPOJO.setName(foundationName.getText().toString());

                    long foundationId = categoryListDBHelper.addFoundation(foundationPOJO);

                    if (foundationId == UNIQUE_CONSTRAINT_FAIL_ERROR_CODE) {
                        Toast.makeText(FoundationActivity.this, "Foundation with same name already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle data = new Bundle();
                        data.putString("foundationName", foundationName.getText().toString());
                        Intent intent = new Intent();
                        intent.putExtra("foundationDetails", data);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
    }
}
