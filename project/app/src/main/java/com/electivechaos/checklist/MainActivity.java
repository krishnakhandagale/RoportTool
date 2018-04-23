package com.electivechaos.checklist;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.electivechaos.checklist.fragments.CauseOfLossFragment;
import com.electivechaos.checklist.fragments.ClaimDetailsFragment;
import com.electivechaos.checklist.fragments.InspectionFragment;
import com.electivechaos.checklist.fragments.PointOfOriginFragment;
import com.electivechaos.checklist.fragments.TitleFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //default fragment
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, new ClaimDetailsFragment());
        tx.addToBackStack(null);
        tx.commit();


        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavView = findViewById(R.id.nav_view);
        mNavView.setNavigationItemSelectedListener(this);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_claim_details) {
            fragment = new ClaimDetailsFragment();
        }
        if (id == R.id.nav_cause_of_loss) {
            fragment = new CauseOfLossFragment();
        }
        if (id == R.id.nav_point_of_origin) {
            fragment = new PointOfOriginFragment();
        }
        if (id == R.id.nav_inspection) {
            fragment = new InspectionFragment();
        }
        if(fragment !=  null){

            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.content_frame, new ClaimDetailsFragment());
            tx.addToBackStack(null);
            tx.commit();


        }
        mDrawerLayout.closeDrawers();
        return false;
    }
}
