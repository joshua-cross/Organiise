package com.example.josh.organiise;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ChartMonthlyPreview extends AppCompatActivity {

    Button back;
    boolean mBounded;
    Actions mServer;

    ArrayList<String> elementNames;
    ArrayList<Integer> elementData;

    PieChart dailyBarChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_monthly_preview);

        back = (Button) findViewById(R.id.Back);

        dailyBarChart = (PieChart) findViewById(R.id.DailyChartPreview);

        android.support.v7.widget.Toolbar toolbar = ( android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar app_bar = getSupportActionBar();

        app_bar.setDisplayShowTitleEnabled(false);


        /*
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent home = new Intent(ChartDailyPreview.this, MainActivity.class);
                startActivity(home);

            }
        });
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, Actions.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    };

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(ChartMonthlyPreview.this, "Service is disconnected", 1000).show();
            mBounded = false;
            mServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(ChartMonthlyPreview.this, "Service is connected", 1000).show();
            mBounded = true;
            Actions.LocalBinder mLocalBinder = (Actions.LocalBinder)service;
            mServer = mLocalBinder.getServerInstance();

            //if there are some actions in the array.
            if(mServer.getActionArray().size() != 0) {
                elementNames = mServer.getMonthlyActionArray();
            }
            //if there are integers attributed to this data.
            if(mServer.getActionCounter().size() != 0) {
                elementData = mServer.getMonthlyActionCounter();
            }

            createDailyPieChart();

        }
    };

    private void createDailyPieChart() {
        if(dailyBarChart != null) {
            System.out.println("WARNING!: chart null, program will crash...");

            //list that will fill pie chart.
            List<PieEntry> actions = new ArrayList<>();
            //looping through the actions, and the counters.
            for (int i = 0; i < elementNames.size(); i = i + 1) {
                actions.add(new PieEntry(elementData.get(i), elementNames.get(i)));
            }

            ArrayList<Integer> colours = new ArrayList<>();


            //for loop that loops through each of the elements in the actions array, from there it creates a random color to each one which is assigned to the colours array list.
            //this is different for each of the elements in the chart.
            for (int i = 0; i < elementNames.size(); i = i + 1) {
                //creating random RGB elements between 0 and 255.
                int r = ThreadLocalRandom.current().nextInt(0, 255 + 1);
                int g = ThreadLocalRandom.current().nextInt(0, 255 + 1);
                int b = ThreadLocalRandom.current().nextInt(0, 255 + 1);

                //System.out.println("R: " + r + " G: " + g + " B: " + b);
                //System.out.println(r);
                int colour = Color.rgb(r, g, b);
                colours.add(colour);
            }

            PieDataSet actionData = new PieDataSet(actions, "Pie chart for yesterdays actions.");
            actionData.setColors(colours);
            PieData data = new PieData(actionData);

            //dailyBarChart.getAxisLeft().setTextColor(R.color.white); // left y-axis
            //dailyBarChart.getXAxis().setTextColor(R.color.white);
            //dailyBarChart.animateX(500);
            Legend l = dailyBarChart.getLegend();
            l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
            dailyBarChart.getLegend().setWordWrapEnabled(true);
            dailyBarChart.getLegend().setXEntrySpace(15f); // set the space between the legend entries on the x-axis
            dailyBarChart.getLegend().setYEntrySpace(3f);
            dailyBarChart.setData(data);
            dailyBarChart.animateY(500);
            dailyBarChart.invalidate();
            //dailyBarChart.getLegend().setWordWrapEnabled(true);
            dailyBarChart.getLegend().setTextColor(Color.WHITE);
            dailyBarChart.getLegend().setTextSize(12.0f);

            //dailyBarChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);


            // set custom labels and colors
            //l.setCustom(ColorTemplate.VORDIPLOM_COLORS, actions);
        }

    }

    /*
    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    };
    */

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

        } else if(item.toString().equals(menu3)) {

        } else if(item.toString().equals(menu4)) {
            Intent editPage = new Intent (this, Edit.class);
            startActivity(editPage);
        } else if(item.toString().equals(menu5)) {

        }
        return super.onOptionsItemSelected(item);
    }
}
