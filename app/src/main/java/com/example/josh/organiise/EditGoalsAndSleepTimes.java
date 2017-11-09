package com.example.josh.organiise;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

public class EditGoalsAndSleepTimes extends AppCompatActivity {

    //the integers the user submits.
    EditText sleepHour, sleepMinute, wakeupHour, wakeupMinute;

    //the confirm button
    Button confirmTimes;

    //The message that will display if the user has entered something that is not correct e.g. 34 in the hour slot.
    TextView errorMsg;


    boolean mBounded;
    Actions mServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goals_and_sleep_times);

        android.support.v7.widget.Toolbar toolbar = ( android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        sleepHour = (EditText) findViewById(R.id.hours);
        sleepMinute = (EditText) findViewById(R.id.minutes);
        wakeupHour = (EditText) findViewById(R.id.wakeUpHour);
        wakeupMinute = (EditText) findViewById(R.id.wakeupMinutes);

        confirmTimes = (Button) findViewById(R.id.submitTime);

        errorMsg = (TextView) findViewById(R.id.error);

        ActionBar app_bar = getSupportActionBar();

        app_bar.setDisplayShowTitleEnabled(false);

        //boolean for if the user wakes up the same day they sleep.

        //if the submitTimes button has been pressed.
        confirmTimes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //ints which the values of the EditTexts will be placed.
                int sHour = 0;
                int sMinute = 0;
                int wHour = 0;
                int wMinute = 0;

                boolean sameDay = false;



                //only fill out the time variables if they all have values, else we will be parsing nothing thus crashing program.
                if(!TextUtils.isEmpty(sleepHour.getText().toString()) || !TextUtils.isEmpty(sleepMinute.getText().toString())
                        || !TextUtils.isEmpty(wakeupHour.getText().toString()) || !TextUtils.isEmpty(wakeupMinute.getText().toString())) {

                    //getting value from edit texts and converting it to an int for the prgram.
                    sHour = Integer.parseInt(sleepHour.getText().toString());
                    sMinute = Integer.parseInt(sleepMinute.getText().toString());
                    wHour = Integer.parseInt(wakeupHour.getText().toString());
                    wMinute = Integer.parseInt(wakeupMinute.getText().toString());
                    System.out.println("sleep hour: " + sHour + " sleep minute: " + sMinute + " wakeup hour: " + wHour + " wakeupMinute: " + wMinute);

                } else {
                    errorMsg.setText("Please fill out all boxes.");
                }



                //if either sleephour or wakeup hour is something higher than 24 then it is not a real time, so we will not sent it.
                if(sHour > 23 || wHour > 23) {
                    errorMsg.setText("Time not valid.");
                }
                //if either the sleep minute or wakeup minute is above 59 then it's not a valid time.
                else if(sMinute > 59 || wMinute > 59) {
                    errorMsg.setText("Time not valid.");
                }
                //if the user enters 24:00 we will convert this to 00 hours instead.
                else if(sHour == 24 || wHour == 24) {
                    if(sHour == 24) {
                        sHour = 0;
                    }

                    if(wHour == 24) {
                        wHour = 0;
                    }

                } else {
                    //if wakeup hour is more than sleep hour this means that the user has gone to sleep the same day as they wake up e.g. 01:00 to 08:00
                    if(wHour > sHour) {
                        sameDay = true;
                        mServer.setSleepAndWakeupTimes(sameDay, sHour, sMinute,wHour,wMinute);
                        //now we have to determine if this is the same day as today or tomorrow.
                        errorMsg.setText("Thank you :)");

                    }
                    //else the wakeuptime is less than the sleep time e.g. 23:00 sleep and 07:00 wakeup, so they are seperate days.
                    else {
                        sameDay = false;
                        mServer.setSleepAndWakeupTimes(sameDay, sHour, sMinute,wHour,wMinute);
                        errorMsg.setText("Thank you :)");
                    }
                }

            }

        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");
        System.out.println("Started");


        Intent mIntent = new Intent(this, Actions.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    };

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(EditGoalsAndSleepTimes.this, "Service is disconnected", 1000).show();

            mBounded = false;
            mServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(EditGoalsAndSleepTimes.this, "Service is connected", 1000).show();
            mBounded = true;
            Actions.LocalBinder mLocalBinder = (Actions.LocalBinder)service;
            mServer = mLocalBinder.getServerInstance();

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
}
