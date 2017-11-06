package com.example.josh.organiise;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ChartDaily extends AppCompatActivity {

    Button back;
    boolean mBounded;
    Actions mServer;

    ArrayList<String> elementNames;
    ArrayList<Integer> elementData;

    PieChart dailyBarChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_daily);

        back = (Button) findViewById(R.id.Back);

        dailyBarChart = (PieChart) findViewById(R.id.DailyChart);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent home = new Intent(ChartDaily.this, MainActivity.class);
                startActivity(home);

            }
        });
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
            Toast.makeText(ChartDaily.this, "Service is disconnected", 1000).show();
            mBounded = false;
            mServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(ChartDaily.this, "Service is connected", 1000).show();
            mBounded = true;
            Actions.LocalBinder mLocalBinder = (Actions.LocalBinder)service;
            mServer = mLocalBinder.getServerInstance();

            //if there are some actions in the array.
            if(mServer.getActionArray().size() != 0) {
                elementNames = mServer.getActionArray();
            }
            //if there are integers attributed to this data.
            if(mServer.getActionCounter().size() != 0) {
                elementData = mServer.getActionCounter();
            }

            createDailyPieChart();

        }
    };

    private void createDailyPieChart() {
        //list that will fill pie chart.
        List<PieEntry> actions = new ArrayList<>();
        //looping through the actions, and the counters.
        for(int i = 0; i < elementNames.size(); i = i + 1) {
            actions.add(new PieEntry(elementData.get(i), elementNames.get(i)));
        }

        PieDataSet actionData = new PieDataSet(actions, "Pie chart for yesterdays actions.");
        actionData.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(actionData);

        dailyBarChart.setData(data);
        dailyBarChart.animateY(500);
        //dailyBarChart.animateX(500);
        dailyBarChart.invalidate();

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
}
