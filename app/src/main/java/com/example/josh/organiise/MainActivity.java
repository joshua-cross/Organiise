package com.example.josh.organiise;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.res.Resources;

//import that's needed for the input boxes.
import android.view.View;
import android.widget.EditText;
//import needed for button
import android.widget.Button;
//importing the intent.
import android.content.Intent;
public class MainActivity extends AppCompatActivity {

    EditText action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startCountdown();

        //creating a reference to the ACTION input.
        action = (EditText) findViewById(R.id.currentAction);
        //reference to the button which will submit the action.
        Button sumbitAction = (Button) findViewById(R.id.submitAction);
        sumbitAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println(action.getText().toString());
                showNotification();
            }
        });
    }


    //TO-DO: need a timer for every day/week/month to show the pie chart, this will need a new notification menu aswell...

    //timer that shows the user every hour that they need to input new data.
    public void startCountdown()
    {

        //int millis = ((hours*60)+mins)*60000; // Need milliseconds to use Timer
        int millis = 10000;

        new Timer().schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                //code that runs when timer is done
                showNotification();
                //calling a method to reset the timer.
                resetTimer();
            }
        }, millis);
    }

    //Method thats only job is to recall startCountdown after the timer has finished.
    private void resetTimer() {
        startCountdown();
    }


    //function that prints the notifications, this will be printed every hour.
    public void showNotification() {

        NotificationManager notificationmgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pintent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        //   PendingIntent pintent = PendingIntent.getActivities(this,(int)System.currentTimeMillis(),intent, 0);


        Notification notif = new Notification.Builder(this)
                //the location of the icon.
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Hello Android Hari")
                .setContentText("Welcome to Notification Service")
                .setContentIntent(pintent)
                .build();


        notificationmgr.notify(0,notif);
    }

}