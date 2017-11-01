package com.example.josh.organiise;

import java.util.Timer;
import java.util.TimerTask;
import java.util.*;
import java.io.IOException;
import java.io.Serializable;
import java.io.*;
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

    //array which will hold 5 buttons to edit each bit of the text.
    Button[] editButtons = new Button[5];


    //the array that's needed for the Spinner.
    ArrayAdapter<String> spinnerArray;

    //the actionArray needs to be converted to a set to be saved.
    Set<String> actionSet = new HashSet<String>();

    //ArrayList, which can hold 5 of the previous actions which will be displayed to the user.
    ArrayList<String> previousActions = new ArrayList<String>();


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

                        Context context = MainActivity.this.getApplicationContext();
                        setPreviousActions(action.getText().toString());


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
                //showing the notification.
                showNotification();

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