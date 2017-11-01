package com.example.josh.organiise;

import java.util.Timer;
import java.util.TimerTask;
import java.util.*;
import java.io.IOException;
import java.io.Serializable;
import java.io.*;
import android.text.TextUtils;
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

    //an int arrayList that will keep track of how many times each action has been chosen.
    ArrayList<Integer> actionCounter = new ArrayList<Integer>();



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

        if(tinydb.getListInt("counter").size() != 0) {
            actionCounter = tinydb.getListInt("counter");
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
                if (actionArray.size() == 0) {
                    notDuplicate = true;
                }

                String currentAction = action.getText().toString();

                //only do this if the user has not left the box blank.
                if (!TextUtils.isEmpty(action.getText().toString())) {
                    //checking to see if the action that we are trying to add is already in the array.
                    for (int i = 0; i < actionArray.size(); i = i + 1) {

                        //setting this to true each tick as we have not yet compared.
                        notDuplicate = true;
                        //comparing the current element with what has been enteTesred by the user.
                        if (actionArray.get(i).equals(currentAction)) {
                            //if it is a duplicate then we will set notDuplicate to false, and get out of the for loop so we don't accidently set it to true.
                            notDuplicate = false;
                            //this is where we will increase the count for this element.
                            //Firstly, we will increment the current value by one.
                            int newActionCounter = actionCounter.get(i) + 1;
                            //then we will set the current action counter to be this.
                            actionCounter.set(i, newActionCounter);

                            //making the application tell you that the element is already present so the user knows to use the drop down meny instead, THIS IS NOT FINAL! we will increment instead in the future.
                            addText.setText("Action already entered. Please use drop down menu!");
                            System.out.println("Action: " + actionArray.get(i) + " has been selected " + actionCounter.get(i) + " times");
                            //getting out the for loop early.
                            break;
                        }
                    }

                    if (notDuplicate) {
                        actionArray.add(action.getText().toString());
                        //informing the user that there action has been registered to the array.
                        addText.setText("Action registered!");

                        Context context = MainActivity.this.getApplicationContext();


                        actionCounter.add(1);

                        TinyDB tinydb = new TinyDB(context);
                        tinydb.putListString("actions", actionArray);
                        tinydb.putListInt("counter", actionCounter);

                        System.out.println("New element detected...");
                    }

                    //resetting the edit text.
                    action.setText(null);
                }
                //or if the action array is 0 this means that the user has never typed anything in the box, and there is no history, in this case do nothing
                else if (TextUtils.isEmpty(action.getText().toString()) && actionArray.size() == 0) {
                    addText.setText("Please type something in the text box above.");

                } else {
                    String dropDownMenuText = actionMenu.getSelectedItem().toString();

                    incrementArray(dropDownMenuText);

                    addText.setText("Action registered!");
                    System.out.println("Drop down menu: " + dropDownMenuText);

                    //resetting the edit text.
                    action.setText(null);

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
                //showing the notification.
                showNotification();

                //if, after an hour a hour the user has not chosen a new action, then we will choose the last action that was chosen.
                //if the array size is 0 then the array is empty so don't do anyrhing.
                if(getLastAction(1).equals(null)) {

                }
                //else there is something in the array so we will increment the last chosen item by one.
                else {
                    //incrementing the last action in the array.
                    incrementArray(getLastAction(1));
                }

                //calling a method to reset the timer.
                resetTimer();
            }
        }, millis);
    }

    //Getting the a specific integer in the array (1 being the last, 2 being the second 2 last etc.) and returning it to be used elsewhere.
    private String getLastAction(int arraySelector) {

        //if there is currently nothing in the array, then we will return null to save errors.
        if(actionArray.size() == 0) {
            return null;
        } else {
            System.out.println("last action was: " + actionArray.get(actionArray.size() - arraySelector));
            //returning the last element in the array.
            return actionArray.get(actionArray.size() - arraySelector);
        }

    }


    //method that compares 2 integers (1 from the input, 1 from the action array) and increments the corresponding element in actionCounter
    private void incrementArray(String actionInput) {
        for(int i = 0; i < actionArray.size(); i = i + 1) {
            if (actionArray.get(i).equals(actionInput)) {
                //Firstly, we will increment the current value by one.
                int newActionCounter = actionCounter.get(i) + 1;
                //then we will set the current action counter to be this.
                actionCounter.set(i, newActionCounter);

                System.out.println("Action: " + actionArray.get(i) + " has been selected " + actionCounter.get(i) + " times");

                break;
            }
        }

        Context context = MainActivity.this.getApplicationContext();
        TinyDB tinydb = new TinyDB(context);
        tinydb.putListInt("counter", actionCounter);
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