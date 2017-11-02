package com.example.josh.organiise;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Actions extends Service {


    EditText action;
    //A string ArrayList which will save all the unquie actions that the user has saved to the phone..
    ArrayList<String> actionArray = new ArrayList<String>();

    //an int arrayList that will keep track of how many times each action has been chosen.
    ArrayList<Integer> actionCounter = new ArrayList<Integer>();

    //BOOL WHICH DETERMINES IF THE BUTTON HAS BEEN PRESSED OR NOT,.
    public boolean confirmButtonPressed = false;


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

    //to be called when the application loads.
    protected void onLoad() {

        secondCounter();


        Context context = Actions.this.getApplicationContext();
        TinyDB tinydb = new TinyDB(context);


        if(tinydb.getListString("actions").size() != 0) {
            actionArray = tinydb.getListString("actions");
            spinnerArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actionArray);
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
        //drawPrevious();

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
                    Context context = Actions.this.getApplicationContext();
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
                    Context context = Actions.this.getApplicationContext();
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
                            setPreviousActions("sleep");
                            actionCounter.add(1);
                            System.out.println("New element detected...");
                            SleptBefore = true;
                        }

                        Context context = Actions.this.getApplicationContext();
                        TinyDB tinydb = new TinyDB(context);
                        tinydb.putListString("actions", actionArray);
                        tinydb.putListInt("counter", actionCounter);

                    }
                    //else if the time is after we have slept we are going to recalculate the times, adding one to tomorrow, so we know when the next time the user will sleep is.
                } else if(now.after(cWakeup.getTime())) {
                    System.out.println("Awoken!");
                    Context context = Actions.this.getApplicationContext();
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
        Context context = Actions.this.getApplicationContext();
        TinyDB tinydb = new TinyDB(context);
        tinydb.putListString("previous", previousActions);

        //drawPrevious();

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

        Context context = Actions.this.getApplicationContext();
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

        Context context = Actions.this.getApplicationContext();
        TinyDB tinydb = new TinyDB(context);
        tinydb.putListInt("counter", actionCounter);
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

        Context context = Actions.this.getApplicationContext();
        TinyDB tinydb = new TinyDB(context);
        tinydb.putListString("actions", actionArray);
        tinydb.putListInt("counter", actionCounter);
        tinydb.putListString("previous", previousActions);
    }

}
