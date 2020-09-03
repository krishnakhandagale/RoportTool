package com.electivechaos.claimsadjuster.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.electivechaos.claimsadjuster.BaseActivity;
import com.electivechaos.claimsadjuster.Constants;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.application.App;
import com.electivechaos.claimsadjuster.maintabs.AlertFragment;
import com.electivechaos.claimsadjuster.maintabs.CalenderFragment;
import com.electivechaos.claimsadjuster.maintabs.ReportListFragment;
import com.electivechaos.claimsadjuster.modules.login.LoginActivity;
import com.electivechaos.claimsadjuster.router.Routable;
import com.electivechaos.claimsadjuster.utils.CommonUtils;
import com.google.android.material.tabs.TabLayout;

public class MainTabsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.main_tabs_layout_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager viewPager = findViewById(R.id.main_viewpager);

        MainTabsActivityPagerAdapter mainTabsActivityPagerAdapter = new MainTabsActivityPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainTabsActivityPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (CommonUtils.getReportQuality(MainTabsActivity.this).isEmpty()) {
            CommonUtils.setReportQuality(Constants.REPORT_QUALITY_MEDIUM, MainTabsActivity.this);
        }
        if (CommonUtils.getGoogleMap(MainTabsActivity.this).isEmpty()) {
            CommonUtils.setGoogleMap(Constants.MAP_TYPE_ID_SATELLITE, MainTabsActivity.this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_peril_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.Category:
                Intent catIntent = new Intent(this, CategoryDetailsActivity.class);
                startActivity(catIntent);
                break;

            case R.id.Peril:
                Intent perilIntent = new Intent(this, PerilDetailsActivity.class);
                startActivity(perilIntent);
                break;
            case R.id.Coverage:
                Intent coverageIntent = new Intent(this, CoverageDetailsActivity.class);
                startActivity(coverageIntent);
                break;
            case R.id.Settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
//            case R.id.Logout:
//                logOutConfirmation();
//                break;

            default:

        }
        return super.onOptionsItemSelected(item);
    }

    private void logOutConfirmation() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainTabsActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        final TextView txtLogOut = dialogView.findViewById(R.id.txtDone);
        final TextView txtCancel = dialogView.findViewById(R.id.txtCancel);
        final TextView txtMessage = dialogView.findViewById(R.id.txtAlertMessage);
        txtMessage.setText("Are you sure,\n you want to logout ?");
        txtLogOut.setText("Logout");
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        Dialog dialog = dialogBuilder.show();

        txtLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPreferences();
                Routable.loginScreen(MainTabsActivity.this);
                finish();
                dialog.dismiss();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        CommonUtils.unlockOrientation(MainTabsActivity.this);
    }

    private void clearPreferences() {
        CommonUtils.clearUserDefaultValues(App.getContext());
        CommonUtils.clearEmailId(App.getContext());
        CommonUtils.clearReportByField(App.getContext());
    }



    //View pager  for showing two tabs in the welcome activity

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    static public class MainTabsActivityPagerAdapter extends FragmentStatePagerAdapter {

        public MainTabsActivityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                Fragment fragment = new ReportListFragment();
                return fragment;
            } else if (i == 1) {
                Fragment fragment = new CalenderFragment();
                return fragment;
            } else {
                Fragment fragment = new AlertFragment();
                return fragment;
            }

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Report List";
            } else if (position == 1) {
                return "Calender";
            } else {
                return "Alert";
            }
        }
    }
}
