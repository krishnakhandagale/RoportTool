package com.electivechaos.checklistapp;

import android.os.Bundle;
import android.app.Activity;

/**
 * Created by barkhasikka on 25/04/18.
 */

public class AddEditCategoryActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.add_edit_category);
    }
}
