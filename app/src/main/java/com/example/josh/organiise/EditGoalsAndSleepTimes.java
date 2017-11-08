package com.example.josh.organiise;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class EditGoalsAndSleepTimes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goals_and_sleep_times);

        android.support.v7.widget.Toolbar toolbar = ( android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar app_bar = getSupportActionBar();

        app_bar.setDisplayShowTitleEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String menu1 = getResources().getString(R.string.menuText1);
        String menu2 = getResources().getString(R.string.menuText2);
        String menu3 = getResources().getString(R.string.menuText3);
        String menu4 = getResources().getString(R.string.menuText4);
        String menu5 = getResources().getString(R.string.menuText5);

        if(item.toString().equals(menu1)) {
            Intent editPage = new Intent (this, ChartDailyPreview.class);
            startActivity(editPage);
        } else if(item.toString().equals(menu2)) {
            Intent editPage = new Intent (this, ChartMonthlyPreview.class);
            startActivity(editPage);
        } else if(item.toString().equals(menu3)) {
            Intent editPage = new Intent (this, ChartYearlyPreview.class);
            startActivity(editPage);
        } else if(item.toString().equals(menu4)) {
            Intent editPage = new Intent (this, Edit.class);
            startActivity(editPage);
        } else if(item.toString().equals(menu5)) {
            Intent editPage = new Intent (this, EditGoalsAndSleepTimes.class);
            startActivity(editPage);
        }
        return super.onOptionsItemSelected(item);
    }
}
