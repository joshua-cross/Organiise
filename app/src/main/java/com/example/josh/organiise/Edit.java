package com.example.josh.organiise;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Edit extends AppCompatActivity {


    boolean mBounded;
    Actions mServer;

    //text view array for the 5 actions.
    TextView[] actionText = new TextView[5];


    //array which will hold 5 buttons to edit each bit of the text.
    Button[] editButtons = new Button[5];

    //ArrayList, which can hold 5 of the previous actions which will be displayed to the user.
    ArrayList<String> previousActions = new ArrayList<String>();

    EditText action;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        android.support.v7.widget.Toolbar toolbar = ( android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar app_bar = getSupportActionBar();

        app_bar.setDisplayShowTitleEnabled(false);

        actionText[0] = (TextView) findViewById((R.id.previousAction));
        actionText[1] = (TextView) findViewById((R.id.previousAction1));
        actionText[2] = (TextView) findViewById((R.id.previousAction2));
        actionText[3] = (TextView) findViewById((R.id.previousAction3));
        actionText[4] = (TextView) findViewById((R.id.previousAction4));

        editButtons[0] = (Button) findViewById((R.id.previousButton));
        editButtons[1] = (Button) findViewById((R.id.previousButton1));
        editButtons[2] = (Button) findViewById((R.id.previousButton2));
        editButtons[3] = (Button) findViewById((R.id.previousButton3));
        editButtons[4] = (Button) findViewById((R.id.previousButton4));

        action = (EditText) findViewById(R.id.editText);


        //if the first edit button has been clicked.
        editButtons[0].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!TextUtils.isEmpty(action.getText().toString())) {

                    mServer.editPressed(0, action.getText().toString());

                    drawPrevious();

                }


            }
        });
        //if the second edit button has been clicked.
        editButtons[1].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!TextUtils.isEmpty(action.getText().toString())) {

                    mServer.editPressed(1, action.getText().toString());


                    drawPrevious();

                }


            }
        });
        //if the third edit button has been clicked.
        editButtons[2].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!TextUtils.isEmpty(action.getText().toString())) {

                    mServer.editPressed(2, action.getText().toString());


                    drawPrevious();
                }

            }
        });
        //if the second forth button has been clicked.
        editButtons[3].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!TextUtils.isEmpty(action.getText().toString())) {

                    mServer.editPressed(3, action.getText().toString());

                    drawPrevious();

                }

            }
        });
        //if the second fifth button has been clicked.
        editButtons[4].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!TextUtils.isEmpty(action.getText().toString())) {
                    mServer.editPressed(4, action.getText().toString());


                    drawPrevious();
                }

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
            Toast.makeText(Edit.this, "Service is disconnected", 1000).show();
            mBounded = false;
            mServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(Edit.this, "Service is connected", 1000).show();
            mBounded = true;
            Actions.LocalBinder mLocalBinder = (Actions.LocalBinder)service;
            mServer = mLocalBinder.getServerInstance();

            Context context = Edit.this.getApplicationContext();
            //Context context = MainActivity.this.getApplicationContext();
            TinyDB tinydb = new TinyDB(context);

            //setContentView(R.layout.activity_main);
            //updateUI();

            if(mServer.getPreviousActions().size() != 0) {
                previousActions = mServer.getPreviousActions();
                drawPrevious();
                updateUI();
            }




        }
    };

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

    //a function that updates the UI.
    private void updateUI()
    {

        //int millis = ((hours*60)+mins)*60000; // Need milliseconds to use Timer
        int millis = 1000;


        //Had to use handler instead of Java timer as this did not allow for the GUI to be updated.
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mServer.getActionCounter().size() != 0) {

                    //previousActions = mServer.getPreviousActions();
                    drawPrevious();

                    //calling a method to reset the timer.
                    resetUI();
                }
            }
        }, millis);
    }

    //resetting the UI
    private void resetUI() {
        updateUI();
    }

    private void drawPrevious() {

        //System.out.println("There are: " + previousActions.size() + " previous actions.");
        int tmpSize = previousActions.size();

        if(tmpSize == 1) {
            actionText[0].setText("5: " + previousActions.get(0));
        } else if (tmpSize == 2) {
            actionText[0].setText("5: " + previousActions.get(0));
            actionText[1].setText("4: " + previousActions.get(1));
        } else if (tmpSize == 3) {
            actionText[0].setText("5: " + previousActions.get(0));
            actionText[1].setText("4: " + previousActions.get(1));
            actionText[2].setText("3: " + previousActions.get(2));
        } else if (tmpSize == 4) {
            actionText[0].setText("5: " + previousActions.get(0));
            actionText[1].setText("4: " + previousActions.get(1));
            actionText[2].setText("3: " + previousActions.get(2));
            actionText[3].setText("2: " + previousActions.get(3));
        } else if(tmpSize == 5) {
            actionText[0].setText("5: " + previousActions.get(0));
            actionText[1].setText("4: " + previousActions.get(1));
            actionText[2].setText("3: " + previousActions.get(2));
            actionText[3].setText("2: " + previousActions.get(3));
            actionText[4].setText("1: " + previousActions.get(4));
        } else if (tmpSize > 5) {
            actionText[0].setText("5: " + previousActions.get(0));
            actionText[1].setText("4: " + previousActions.get(1));
            actionText[2].setText("3: " + previousActions.get(2));
            actionText[3].setText("2: " + previousActions.get(3));
            actionText[4].setText("1: " + previousActions.get(4));
        } else {
            System.out.println("Not sure...");
        }

    }

}
