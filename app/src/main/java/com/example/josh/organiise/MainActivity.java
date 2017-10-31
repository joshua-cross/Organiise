package com.example.josh.organiise;

import java.util.Timer;
import java.util.TimerTask;
import java.util.*;
import java.io.IOException;
import java.io.Serializable;
import java.io.*;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
//import needed for button
import android.widget.Button;
//importing the intent.
import android.content.Intent;
public class MainActivity extends AppCompatActivity {

    EditText action;
    //A string ArrayList which will save all the unquie actions that the user has saved to the phone..
    ArrayList<String> actionArray = new ArrayList<String>();
    TextView addText;
    Spinner actionMenu;

    //the array that's needed for the Spinner.
    ArrayAdapter<String> spinnerArray;

    //the actionArray needs to be converted to a set to be saved.
    Set<String> actionSet = new HashSet<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startCountdown();

        //creating a reference to the ACTION input.
        action = (EditText) findViewById(R.id.currentAction);

        //the text that tells the user if the data was added.
        addText = (TextView) findViewById(R.id.Add);

        //the drop down menus for the actions.
        actionMenu = (Spinner) findViewById(R.id.ActionMenu);


        Context context = MainActivity.this.getApplicationContext();
        TinyDB tinydb = new TinyDB(context);


        if(tinydb.getListString("actions").size() != 0) {
            actionArray = tinydb.getListString("actions");
            spinnerArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actionArray);
            actionMenu.setAdapter(spinnerArray);
        }



        //reference to the button which will submit the action.
        Button sumbitAction = (Button) findViewById(R.id.submitAction);
        sumbitAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println(action.getText().toString());
                showNotification();

                //a boolean which will return true at the end of the array if there is no element that contrains the same string.
                boolean notDuplicate = false;

                //Have to add this as we do not go around the for loop with an empty array...
                if(actionArray.size() == 0) {
                    notDuplicate = true;
                }

                String currentAction = action.getText().toString();

                //checking to see if the action that we are trying to add is already in the array.
                for(int i = 0; i < actionArray.size(); i = i + 1) {

                    //setting this to true each tick as we have not yet compared.
                    notDuplicate = true;
                    //comparing the current element with what has been enteTesred by the user.
                    if(actionArray.get(i).equals(currentAction)) {
                        //if it is a duplicate then we will set notDuplicate to false, and get out of the for loop so we don't accidently set it to true.
                        notDuplicate = false;
                        //this is where we will increase the count for this element.

                        //making the application tell you that the element is already present so the user knows to use the drop down meny instead, THIS IS NOT FINAL! we will increment instead in the future.
                        addText.setText("Action already entered. Please use drop down menu!");
                        System.out.println("Action " + i + " is: " + actionArray.get(i) + " the typed action is: " + currentAction);
                        //getting out the for loop early.
                        break;
                    }
                }

                if(notDuplicate) {
                    actionArray.add(action.getText().toString());
                    //informing the user that there action has been registered to the array.
                    addText.setText("Action registered!");

                    Context context = MainActivity.this.getApplicationContext();
                    TinyDB tinydb = new TinyDB(context);
                    tinydb.putListString("actions", actionArray);



                    System.out.println("New element detected...");
                }
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