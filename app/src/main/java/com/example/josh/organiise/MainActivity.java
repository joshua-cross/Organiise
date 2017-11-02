package com.example.josh.organiise;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;
import java.io.IOException;
import java.io.Serializable;
import java.io.*;
import java.text.ParseException;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import android.text.TextUtils;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
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

    //BOOL WHICH DETERMINES IF THE BUTTON HAS BEEN PRESSED OR NOT,.
    public boolean confirmButtonPressed = false;

    TextView addText;
    Spinner actionMenu;

    //text view array for the 5 actions.
    TextView[] actionText = new TextView[5];

    //text view for the current time.
    TextView Time;

    //array which will hold 5 buttons to edit each bit of the text.
    Button[] editButtons = new Button[5];


    //the array that's needed for the Spinner.
    ArrayAdapter<String> spinnerArray;

    //the actionArray needs to be converted to a set to be saved.
    Set<String> actionSet = new HashSet<String>();

    //ArrayList, which can hold 5 of the previous actions which will be displayed to the user.
    ArrayList<String> previousActions = new ArrayList<String>();

    //integers for the times that we assume the user is sleeping (will be stored in a tinyDB).
    int tDate, tToday, tYear, tMonth, tSleepHour, tSleepMinute, tSleepSecond, tWakeHour, tWakeMinute, tWakeSecond;
    //the time the daily chart will appear.
    int tChartHour, tChartMinute, tChartSecond;

    //integers for when the monthly chart will show up.
    int mYear, mMonth, mDate, mHour, mMinute, mSecond;

    //integers for when the yearly chart will show up.
    int yYear, yMonth, yDate, yHour, yMinute, ySecond;

    //boolean to check if the user has been asleep today.
    boolean isAsleep = false;

    //boolean to check if we have already displayed the monthly graphs.
    boolean midnight = false;

    //TODO: add button that makes user wakeup/sleep variables equal to a time of there choosing
    int userWake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startCountdown();
        secondCounter();

        //creating a reference to the ACTION input.
        action = (EditText) findViewById(R.id.currentAction);

        //the text that tells the user if the data was added.
        addText = (TextView) findViewById(R.id.Add);

        //the drop down menus for the actions.
        actionMenu = (Spinner) findViewById(R.id.ActionMenu);

        //setting the actions array.
        actionText[0] = (TextView) findViewById((R.id.previousAction));
        actionText[1] = (TextView) findViewById((R.id.PreviousAction1));
        actionText[2] = (TextView) findViewById((R.id.previousAction2));
        actionText[3] = (TextView) findViewById((R.id.previousAction3));
        actionText[4] = (TextView) findViewById((R.id.previousAction4));

        editButtons[0] = (Button) findViewById((R.id.previousButton));
        editButtons[1] = (Button) findViewById((R.id.previousButton1));
        editButtons[2] = (Button) findViewById((R.id.previousButton2));
        editButtons[3] = (Button) findViewById((R.id.previousButton3));
        editButtons[4] = (Button) findViewById((R.id.previousButton4));


        Time = (TextView) findViewById((R.id.Time));


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

        if(tinydb.getListString("previous").size() != 0) {
            previousActions = tinydb.getListString("previous");
        }


        //checking if the daily times have a value. if they don't we will assign them a value here.
        if(tinydb.getInt("todayYear") == 0 && tinydb.getInt("todayMonth") == 0 && tinydb.getInt("todayDate") == 0) {
            //SETTING THE WAKEUP TIME TO BE 7:00 the next day, and the sleep time to be 23:00 today.
            Calendar cal = Calendar.getInstance();
            tMonth = cal.get(Calendar.MONTH);
            tDate = cal.get(Calendar.DATE) + 1;
            tToday = cal.get(Calendar.DATE);
            tYear = cal.get(Calendar.YEAR);
            tSleepHour = 23;
            tSleepMinute = 0;
            tSleepSecond = 0;
            tWakeHour = 07;
            tWakeMinute = 0;
            tWakeSecond = 0;
            //saving this to the tinydb.
            tinydb.putInt("todayMonth", tMonth);
            tinydb.putInt("tomorrowDate", tDate);
            tinydb.putInt("todayDate", tToday);
            tinydb.putInt("todayYear", tYear);
            tinydb.putInt("todaySleepHour", tSleepHour);
            tinydb.putInt("todaySleepMinute", tSleepMinute);
            tinydb.putInt("todaySleepSecond", tSleepSecond);
            tinydb.putInt("todayWakeSecond", tWakeSecond);
            tinydb.putInt("todayWakeMinute", tWakeMinute);
            tinydb.putInt("todayWakeHour", tWakeHour);
        }
        //else they have already been given a value.
        else {
            tYear = tinydb.getInt("todayYear");
            tMonth = tinydb.getInt("todayMonth");
            tDate = tinydb.getInt("tomorrowDate");
            tToday = tinydb.getInt("todayDate");
            tSleepHour = tinydb.getInt("todaySleepHour");
            tSleepMinute = tinydb.getInt("todaySleepMinute");
            tSleepSecond = tinydb.getInt("todaySleepSecond");
            tWakeSecond = tinydb.getInt("todayWakeSecond");
            tWakeMinute = tinydb.getInt("todayWakeMinute");
            tWakeHour = tinydb.getInt("todayWakeHour");
        }

        //if we have not yet set the date of the next month.
        if (tinydb.getInt("monthYear") == 0 && tinydb.getInt("monthMonth") == 0 && tinydb.getInt("monthDate") == 0) {
            //SETTING THE WAKEUP TIME TO BE 7:00 the next day, and the sleep time to be 23:00 today.
            Calendar cal = Calendar.getInstance();
            mMonth = cal.get(Calendar.MONTH) + 1;
            mYear = cal.get(Calendar.YEAR);
            mDate = cal.get(Calendar.DATE);
            tinydb.putInt("monthMonth", mMonth);
            tinydb.putInt("monthDate", mDate);
            tinydb.putInt("monthYear", mYear);
        } else {
            mMonth = tinydb.getInt("monthMonth");
            mDate = tinydb.getInt("monthDate");
            mYear = tinydb.getInt("monthYear");
        }

        if (tinydb.getInt("yearYear") == 0 && tinydb.getInt("yearMonth") == 0 && tinydb.getInt("yearDate") == 0) {
            //SETTING THE WAKEUP TIME TO BE 7:00 the next day, and the sleep time to be 23:00 today.
            Calendar cal = Calendar.getInstance();
            yMonth = cal.get(Calendar.MONTH);
            yYear = cal.get(Calendar.YEAR) + 1;
            yDate = cal.get(Calendar.DATE);
            tinydb.putInt("yearMonth", yMonth);
            tinydb.putInt("yearDate", yDate);
            tinydb.putInt("yearYear", yYear);
        } else {
            yMonth = tinydb.getInt("yearMonth");
            yDate = tinydb.getInt("yearDate");
            yYear = tinydb.getInt("yearYear");
        }

            //setting the previousText boxes initially.
        drawPrevious();


        //reference to the button which will submit the action.
        Button sumbitAction = (Button) findViewById(R.id.submitAction);
        sumbitAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println(action.getText().toString());
                showNotification();

                //The confirm button has been pressed so change this to true; this will stop the last element being updated when we have already chosen an update for the hour.
                confirmButtonPressed = true;

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

                            //setting one of the previous actions.
                            setPreviousActions(actionArray.get(i));

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

                        setPreviousActions(action.getText().toString());


                        actionCounter.add(1);

                        System.out.println("New element detected...");
                    }

                    Context context = MainActivity.this.getApplicationContext();
                    TinyDB tinydb = new TinyDB(context);
                    tinydb.putListString("actions", actionArray);
                    tinydb.putListInt("counter", actionCounter);

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

                    //rearranging the array so we know that dropDownMenuText was the last element selected.
                    rearrangeArray(dropDownMenuText);
                    setPreviousActions(dropDownMenuText);

                    //resetting the edit text.
                    action.setText(null);

                }
            }

        });

        //if the first edit button has been clicked.
        editButtons[0].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editPressed(0);

                drawPrevious();


            }
        });
        //if the second edit button has been clicked.
        editButtons[1].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editPressed(1);


                drawPrevious();


            }
        });
        //if the third edit button has been clicked.
        editButtons[2].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editPressed(2);


                drawPrevious();

            }
        });
        //if the second forth button has been clicked.
        editButtons[3].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editPressed(3);

                drawPrevious();

            }
        });
        //if the second fifth button has been clicked.
        editButtons[4].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editPressed(4);


                drawPrevious();

            }
        });

    }

    //timer for every second so we can see if it's past sleep time.
    public void secondCounter()
    {

        //int millis = ((hours*60)+mins)*60000; // Need milliseconds to use Timer
        int millis = 1000;


        //Had to use handler instead of Java timer as this did not allow for the GUI to be updated.
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //a calender for when we will wake up.
                Calendar cWakeup = Calendar.getInstance();
                cWakeup.set(tYear, tMonth, tDate, tWakeHour, tWakeMinute, tWakeSecond);

                //a calendar for when we go to sleep.
                Calendar cSleep = Calendar.getInstance();
                cSleep.set(tYear, tMonth, tToday, tSleepHour, tSleepMinute, tSleepSecond);

                //a calendar for midnight so we can create a chart for the user.
                Calendar midnightAfter = Calendar.getInstance();
                midnightAfter.set(tYear, tMonth,tDate, 00, 00, 00);

                // a second calendar for midnight so we can check if it is between the 2 midnight times.
                Calendar midnightBefore = Calendar.getInstance();
                midnightBefore.set(tYear, tMonth, tDate, 00, 05, 00);

                Calendar month = Calendar.getInstance();
                month.set(mYear, mMonth, mDate, 00, 00, 00);

                Calendar year = Calendar.getInstance();
                year.set(yYear, yMonth, yDate, 00, 00, 00);

                Date now = new Date();
                System.out.println(now);

                System.out.println("Wake: " + cWakeup.getTime());
                System.out.println("Sleep: " + cSleep.getTime());
                System.out.println("now: " + now);
                System.out.println("month: " + month.getTime());
                System.out.println("year: " + year.getTime());


                //TODO: charts for both yearly, and monthly.
                //checking to see if it's been a year after we first used the application.
                if(now.after(year.getTime())) {
                    Context context = MainActivity.this.getApplicationContext();
                    TinyDB tinydb = new TinyDB(context);
                    //SETTING THE WAKEUP TIME TO BE 7:00 the next day, and the sleep time to be 23:00 today.
                    Calendar cal = Calendar.getInstance();
                    yMonth = cal.get(Calendar.MONTH);
                    yYear = cal.get(Calendar.YEAR) + 1;
                    yDate = cal.get(Calendar.DATE);
                    tinydb.putInt("yearMonth", yMonth);
                    tinydb.putInt("yearDate", yDate);
                    tinydb.putInt("yearYear", yYear);
                }

                if(now.after(month.getTime())) {
                    Context context = MainActivity.this.getApplicationContext();
                    TinyDB tinydb = new TinyDB(context);
                    //SETTING THE WAKEUP TIME TO BE 7:00 the next day, and the sleep time to be 23:00 today.
                    Calendar cal = Calendar.getInstance();
                    mMonth = cal.get(Calendar.MONTH) + 1;
                    mYear = cal.get(Calendar.YEAR);
                    mDate = cal.get(Calendar.DATE);
                    tinydb.putInt("monthMonth", mMonth);
                    tinydb.putInt("monthDate", mDate);
                    tinydb.putInt("monthYear", mYear);
                }


                //Checking if the time is midnight so we can display the users daily graphs.
                if(now.before(midnightBefore.getTime()) && now.after(midnightAfter.getTime())) {
                    //checking to see if we have displayed the graphs to the user before.
                    if(!midnight) {
                        midnight = true;
                        //TODO: create notification here.

                        //TODO: link to graph here
                    }
                }

                //if the time is currently the time before we go to sleep then do nothing.
                if (now.before(cSleep.getTime())) {
                    //System.out.println("Before bed time");
                //else if the time is whilst we are asleep we are going to check firstly, if we have slept before, if not we are going to add this to the array, and the previous actions so this happens
                //whilst the user is asleep, and secondly, if we have already checked, if we do this continuously every second we are going to fill up the counterArray very quickly.
                } else if (now.after(cSleep.getTime()) && now.before(cWakeup.getTime())) {
                    //stopping the program from constantly adding 1 to sleep every second.
                    if(!isAsleep) {
                        //System.out.println("Asleep. ZZZ");
                        isAsleep = true;
                        //boolean that checks if the user has used the app whilst he/she has slept before.
                        boolean SleptBefore = false;
                        for(int i = 0; i < actionArray.size(); i = i + 1) {
                            //if sleep is already an action, then we do not want to add it..
                            if(actionArray.get(i) == "sleep") {
                                //we have slept before so do not go into the if statement below.
                                SleptBefore = true;
                                int newActionCounter = actionCounter.get(i) + 1;
                                //then we will set the current action counter to be this.
                                actionCounter.set(i, newActionCounter);
                                //setting one of the previous actions.
                                setPreviousActions(actionArray.get(i));

                                    break;
                                }
                            }

                            if(!SleptBefore) {
                                actionArray.add("sleep");
                                //informing the user that there action has been registered to the array.
                                addText.setText("Action registered!");
                                setPreviousActions("sleep");
                                actionCounter.add(1);
                                System.out.println("New element detected...");
                                SleptBefore = true;
                            }

                            Context context = MainActivity.this.getApplicationContext();
                            TinyDB tinydb = new TinyDB(context);
                            tinydb.putListString("actions", actionArray);
                            tinydb.putListInt("counter", actionCounter);

                        }
                    //else if the time is after we have slept we are going to recalculate the times, adding one to tomorrow, so we know when the next time the user will sleep is.
                    } else if(now.after(cWakeup.getTime())) {
                        System.out.println("Awoken!");
                        Context context = MainActivity.this.getApplicationContext();
                        TinyDB tinydb = new TinyDB(context);
                        isAsleep = false;
                        midnight = false;
                        //SETTING THE WAKEUP TIME TO BE 7:00 the next day, and the sleep time to be 23:00 today.
                        Calendar cal = Calendar.getInstance();
                        tMonth = cal.get(Calendar.MONTH);
                        tDate = cal.get(Calendar.DATE) + 1;
                        tToday = cal.get(Calendar.DATE);
                        tYear = cal.get(Calendar.YEAR);
                        tSleepHour = 23;
                        tSleepMinute = 0;
                        tSleepSecond = 0;
                        tWakeHour = 07;
                        tWakeMinute = 0;
                        tWakeSecond = 0;
                        //saving this to the tinydb.
                        tinydb.putInt("todayMonth", tMonth);
                        tinydb.putInt("tomorrowDate", tDate);
                        tinydb.putInt("todayDate", tToday);
                        tinydb.putInt("todayYear", tYear);
                        tinydb.putInt("todaySleepHour", tSleepHour);
                        tinydb.putInt("todaySleepMinute", tSleepMinute);
                        tinydb.putInt("todaySleepSecond", tSleepSecond);
                        tinydb.putInt("todayWakeSecond", tWakeSecond);
                        tinydb.putInt("todayWakeMinute", tWakeMinute);
                        tinydb.putInt("todayWakeHour", tWakeHour);
                    }


                resetTimerSecond();
            }

        }, millis);
    }


    //TO-DO: need a timer for every day/week/month to show the pie chart, this will need a new notification menu aswell...

    //timer that shows the user every hour that they need to input new data.
    public void startCountdown()
    {

        //int millis = ((hours*60)+mins)*60000; // Need milliseconds to use Timer
        int millis = 10000;


        //Had to use handler instead of Java timer as this did not allow for the GUI to be updated.
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //code that runs when timer is done
                //only send notifications if user is not asleep.
                if(!isAsleep) {
                    //showing the notifications
                    showNotification();
                }

                //only do the following if the confirm button has not been pressed.
                if(confirmButtonPressed == false) {
                    //if, after an hour a hour the user has not chosen a new action, then we will choose the last action that was chosen.
                    //if the array size is 0 then the array is empty so don't do anyrhing.
                    if (getLastAction(1).equals(null)) {

                    }
                    //else there is something in the array so we will increment the last chosen item by one.
                    else {

                        String currLast = getLastAction(1);
                        //incrementing the last action in the array.
                        incrementArray(currLast);
                        //setPreviousActions(currLast);
                        setPreviousActions(currLast);
                    }
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

    //method that checks the current length of the previousActions array, and if the length is less than 5, it adds a new element, else it removes the first and adds the new one.
    private void setPreviousActions(String actionInput) {
        //System.out.println("There are: " + previousActions.size() + " previous actions.");
        //if there are currently less than 5 elements, then we will simply add an element.
        if(previousActions.size() < 4) {
            previousActions.add(actionInput);
        }
        //else there are more than 5 elements so we will remove the first and add the new element.
        else {
            //in case something goes wrong we will ensure there is enough room to add a new element.
            while(previousActions.size() != 4) {
                //removing the first action from the array.
                previousActions.remove(0);
            }
            previousActions.add(actionInput);
        }

        //ADD the tinyDB here.
        Context context = MainActivity.this.getApplicationContext();
        TinyDB tinydb = new TinyDB(context);
        tinydb.putListString("previous", previousActions);

        drawPrevious();

    }

    private void drawPrevious() {

        System.out.println("There are: " + previousActions.size() + " previous actions.");
        int tmpSize = previousActions.size();

        if(tmpSize == 1) {
            actionText[0].setText(previousActions.get(0));
        } else if (tmpSize == 2) {
            actionText[0].setText(previousActions.get(0));
            actionText[1].setText(previousActions.get(1));
        } else if (tmpSize == 3) {
            actionText[0].setText(previousActions.get(0));
            actionText[1].setText(previousActions.get(1));
            actionText[2].setText(previousActions.get(2));
        } else if (tmpSize == 4) {
            actionText[0].setText(previousActions.get(0));
            actionText[1].setText(previousActions.get(1));
            actionText[2].setText(previousActions.get(2));
            actionText[3].setText(previousActions.get(3));
        } else if(tmpSize == 5) {
            actionText[0].setText(previousActions.get(0));
            actionText[1].setText(previousActions.get(1));
            actionText[2].setText(previousActions.get(2));
            actionText[3].setText(previousActions.get(3));
            actionText[4].setText(previousActions.get(4));
        } else if (tmpSize > 5) {
            actionText[0].setText(previousActions.get(0));
            actionText[1].setText(previousActions.get(1));
            actionText[2].setText(previousActions.get(2));
            actionText[3].setText(previousActions.get(3));
            actionText[4].setText(previousActions.get(4));
        } else {
            System.out.println("Not sure...");
        }

    }

    //a method that rearranges the actionArray ArrayList (and counterArray ArrayList), and have it so the selected elements are at the end.
    private void rearrangeArray(String actionInput) {

        //integer for the current value of counterArray(i) so we can add it to the end of the ArrayList.
        int currCounter;
        //value for the current value of actionArray(i) so we can add it to the end of the arrayList.
        String currAction;

        //looping through the actionArray ArrayList.
        for(int i = 0; i < actionArray.size(); i = i + 1) {
            //if the actionArray and the input are identical we will 1. assing
            if (actionArray.get(i).equals(actionInput)) {
                //setting currCounter to be the current value of counterArray.
                currCounter = actionCounter.get(i);

                //setting currAction to be the current value of actionArray.
                currAction = actionArray.get(i);

                //removing the selected elements from the arrays.
                actionCounter.remove(i);
                actionArray.remove(i);
                //readding them at the end.
                actionCounter.add(currCounter);
                actionArray.add(currAction);

                System.out.println("Action: " + actionArray.get(actionArray.size() - 1) + " was moved to end with value: " + actionCounter.get(actionCounter.size() - 1) + " times");

                break;
            }
        }

        Context context = MainActivity.this.getApplicationContext();
        TinyDB tinydb = new TinyDB(context);
        tinydb.putListInt("counter", actionCounter);
        tinydb.putListString("actions", actionArray);

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
        //reset the button as it is a new hour and we don't know if the button has been pressed yet.
        confirmButtonPressed = false;
        startCountdown();
    }

    public void resetTimerSecond() {
        secondCounter();
    }

    private void editPressed(int buttonNumber) {

        System.out.println("Edit: Button: " + buttonNumber + " has been selected");
        //if the text box is not empty.
        if (!TextUtils.isEmpty(action.getText().toString())) {

            //getting what was typed in the text box.
            String currAction = action.getText().toString();

            //boolean that checks if the new edit is a new string.
            boolean isNew = false;

            //finding the

            //looping through the actionArray to find the selected item.
            for (int i = 0; i < actionArray.size(); i = i + 1) {
                //if this is not a new string then we will declare isNew as false.
                if (actionArray.get(i).equals(previousActions.get(buttonNumber))) {
                    System.out.println("Edit: Action: " + previousActions.get(buttonNumber) + " already exists");
                    //if this was the only example of this action in the array, then remove it.
                    if (actionCounter.get(i) <= 1) {
                        System.out.println("Edit: Action: " + previousActions.get(buttonNumber) + " already exists, removing.");
                        actionCounter.remove(i);
                        actionArray.remove(i);
                    } else {
                        //the new value for the counter.
                        int newVal = actionCounter.get(i) - 1;
                        //taking 1 away from this action, as we have no longer did this.
                        actionCounter.set(i, newVal);
                        System.out.println("Edit: EDITED - actionCounter: " + i + " to: " + actionCounter.get(i));
                    }

                    //after we have either taken 1 away, or deleted the old value.
                    //then loop through actionArray again and find the newly entered action.
                    for (int x = 0; x < actionArray.size(); x = x + 1) {
                        //if the action i equals what was typed in the textbox.
                        if (actionArray.get(x).equals(currAction)) {
                            System.out.println("Edit: Action: " + currAction + " already exists.");
                            isNew = false;
                            //adding 1 to the edited action.
                            int newVal = actionCounter.get(x) + 1;
                            actionCounter.set(x, newVal);
                            //setting whichever button it is to the new value.
                            previousActions.set(buttonNumber, currAction);
                            System.out.println("Edit: EDITED + actionCounter: " + x + " to: " + actionCounter.get(x));

                            break;
                        } else {
                            isNew = true;
                        }
                    }

                    //if after coming out of that for loop we see that the info from the textbox is new, we will add this to the array..
                    if (isNew) {
                        System.out.println("Edit: Action: " + currAction + " is new.");
                        actionArray.add(currAction);
                        actionCounter.add(1);
                        //setting whichever button it is to the new value.
                        previousActions.set(buttonNumber, currAction);

                    }

                    break;
                }

            }

        }

        Context context = MainActivity.this.getApplicationContext();
        TinyDB tinydb = new TinyDB(context);
        tinydb.putListString("actions", actionArray);
        tinydb.putListInt("counter", actionCounter);
        tinydb.putListString("previous", previousActions);
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