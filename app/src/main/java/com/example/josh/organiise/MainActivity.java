package com.example.josh.organiise;

import java.util.*;

import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.ComponentName;
import android.text.TextUtils;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

import com.example.josh.organiise.Actions.LocalBinder;



public class MainActivity extends AppCompatActivity {

    boolean mBounded;
    Actions mServer;


    EditText action;
    //A string ArrayList which will save all the unquie actions that the user has saved to the phone..
    ArrayList<String> actionArray = new ArrayList<String>();

    //an int arrayList that will keep track of how many times each action has been chosen.
    ArrayList<Integer> actionCounter = new ArrayList<Integer>();

    TextView addText;
    Spinner actionMenu;

    //text view array for the 5 actions.
    TextView[] actionText = new TextView[5];

    //text view for the current time.
    TextView Time;

    //array which will hold 5 buttons to edit each bit of the text.
    Button[] editButtons = new Button[5];

    //button which takes user to the chart page.
    Button chartPage;

    //the array that's needed for the Spinner.
    ArrayAdapter<String> spinnerArray;

    //the actionArray needs to be converted to a set to be saved.
    Set<String> actionSet = new HashSet<String>();

    //ArrayList, which can hold 5 of the previous actions which will be displayed to the user.
    ArrayList<String> previousActions = new ArrayList<String>();

    TextView hasEnteredText;


    //TODO: add button that makes user wakeup/sleep variables equal to a time of there choosing


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //updateUI();

        Context context = MainActivity.this.getApplicationContext();
        /***** For start Service  ****/
        //Intent myIntent = new Intent(context, Actions.class);
        //context.startService(myIntent);

        //startCountdown();
        //secondCounter();

        //creating a reference to the ACTION input.
        action = (EditText) findViewById(R.id.currentAction);

        //the text that tells the user if the data was added.
        addText = (TextView) findViewById(R.id.Add);

        //the drop down menus for the actions.
        actionMenu = (Spinner) findViewById(R.id.ActionMenu);
        spinnerArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actionArray);


        hasEnteredText = (TextView) findViewById(R.id.AlreadyEntered);

        /*
        //setting the actions array.
        actionText[0] = (TextView) findViewById((R.id.previousAction));
        actionText[1] = (TextView) findViewById((R.id.PreviousAction1));
        actionText[2] = (TextView) findViewById((R.id.previousAction3));
        actionText[3] = (TextView) findViewById((R.id.previousAction3));
        actionText[4] = (TextView) findViewById((R.id.previousAction4));

        editButtons[0] = (Button) findViewById((R.id.previousButton));
        editButtons[1] = (Button) findViewById((R.id.previousButton1));
        editButtons[2] = (Button) findViewById((R.id.previousButton2));
        editButtons[3] = (Button) findViewById((R.id.previousButton3));
        editButtons[4] = (Button) findViewById((R.id.previousButton4));
        */



        android.support.v7.widget.Toolbar toolbar = ( android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar app_bar = getSupportActionBar();

        //app_bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //app_bar.setCustomView(R.layout.app_bar);

        //getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setTitle("Canteen Home");
        //getSupportActionBar().setHomeButtonEnabled(true);

        //app_bar.setDisplayShowCustomEnabled(true);
        app_bar.setDisplayShowTitleEnabled(false);
        //app_bar.setDisplayHomeAsUpEnabled(true);
        //app_bar.setHomeButtonEnabled(true);


        //reference to the button which will submit the action.
        Button sumbitAction = (Button) findViewById(R.id.submitAction);
        sumbitAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //only do any of the following if we have not confirmed an action is the last hour.
                if(!mServer.getHasSelected()) {
                    //mServer.message();
                    System.out.println(action.getText().toString());
                    //showNotification();

                    //The confirm button has been pressed so change this to true; this will stop the last element being updated when we have already chosen an update for the hour.
                    mServer.setConfirmButtonPressed(true);

                    //a boolean which will return true at the end of the array if there is no element that contrains the same string.
                    boolean notDuplicate = false;

                    //Have to add this as we do not go around the for loop with an empty array...
                    if (actionArray.size() == 0) {
                        notDuplicate = true;
                    }

                    String currentAction = action.getText().toString();

                    //only do this if the user has not left the box blank.
                    if (!TextUtils.isEmpty(action.getText().toString())) {

                        //caling action added from actions.java, this adds to the counter array, and the action array and returns a bool which we will use to decide what to print.
                        boolean isAction = mServer.actionAdded(currentAction);
                        //setting hasSelected to true in the server so we know not to add the previous action to the array.
                        mServer.setHasSelected(true);
                        //if it's a new action print action registered.
                        if (isAction) {
                            addText.setText("Action registered!");
                        }
                        //else it's an existing action.
                        else {
                            addText.setText("Action already entered. Please use drop down menu!");
                        }
                        //resetting the edit text.
                        action.setText(null);
                    }
                    //or if the action array is 0 this means that the user has never typed anything in the box, and there is no history, in this case do nothing
                    else if (TextUtils.isEmpty(action.getText().toString()) && actionArray.size() == 0) {
                        addText.setText("Please type something in the text box above.");

                    } else {
                        String dropDownMenuText = actionMenu.getSelectedItem().toString();

                        mServer.incrementArray(dropDownMenuText);

                        addText.setText("Action registered!");
                        System.out.println("Drop down menu: " + dropDownMenuText);

                        //rearranging the array so we know that dropDownMenuText was the last element selected.
                        mServer.rearrangeArray(dropDownMenuText);
                        mServer.setPreviousActions(dropDownMenuText);

                        //resetting the edit text.
                        action.setText(null);

                    }
                }
                //else we have selected something in the last hour
                else {
                    //reset edit text.
                    action.setText(null);
                    addText.setText(null);
                    //informing the user they have already typed an action in the last hour.
                    hasEnteredText.setVisibility(View.VISIBLE);
                }
            }

        });



        /*

        //if the first edit button has been clicked.
        editButtons[0].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!TextUtils.isEmpty(action.getText().toString())) {

                    mServer.editPressed(0, action.getText().toString());

                    //drawPrevious();

                }


            }
        });
        //if the second edit button has been clicked.
        editButtons[1].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!TextUtils.isEmpty(action.getText().toString())) {

                    mServer.editPressed(1, action.getText().toString());


                    //drawPrevious();

                }


            }
        });
        //if the third edit button has been clicked.
        editButtons[2].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!TextUtils.isEmpty(action.getText().toString())) {

                    mServer.editPressed(2, action.getText().toString());


                    //drawPrevious();
                }

            }
        });
        //if the second forth button has been clicked.
        editButtons[3].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!TextUtils.isEmpty(action.getText().toString())) {

                    mServer.editPressed(3, action.getText().toString());

                    //drawPrevious();

                }

            }
        });
        //if the second fifth button has been clicked.
        editButtons[4].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!TextUtils.isEmpty(action.getText().toString())) {
                    mServer.editPressed(4, action.getText().toString());


                    //drawPrevious();
                }

            }
        });
        */
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
            Toast.makeText(MainActivity.this, "Service is disconnected", 1000).show();
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            System.out.println("Not Working :(");
            mBounded = false;
            mServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainActivity.this, "Service is connected", 1000).show();
            mBounded = true;
            LocalBinder mLocalBinder = (LocalBinder)service;
            mServer = mLocalBinder.getServerInstance();

            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");
            System.out.println("Working!");

            Context context = MainActivity.this.getApplicationContext();
            //Context context = MainActivity.this.getApplicationContext();
            TinyDB tinydb = new TinyDB(context);

            //setContentView(R.layout.activity_main);
            updateUI();

            //if there are some previous actions.
            if(mServer.getActionArray().size() != 0) {
                actionArray = mServer.getActionArray();
                spinnerArray = new ArrayAdapter<String>(context, R.layout.spinner_item, actionArray);
                actionMenu.setAdapter(spinnerArray);
            }

            if(mServer.getActionCounter().size() != 0) {
                actionCounter = mServer.getActionCounter();
            }

            if(mServer.getPreviousActions().size() != 0) {
                previousActions = mServer.previousActions;
            }

            if(mServer.getHasSelected() == true) {
                hasEnteredText.setVisibility(View.VISIBLE);
            }



            //setting the previousText boxes initially.
            //drawPrevious();
        }
    };

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


    /*
    private void drawPrevious() {

        //System.out.println("There are: " + previousActions.size() + " previous actions.");
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
    */


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

                    actionArray = mServer.getActionArray();
                    previousActions = mServer.getPreviousActions();
                    //spinnerArray = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actionArray);
                    //actionMenu.setAdapter(spinnerArray);
                    spinnerArray.notifyDataSetChanged();
                    //drawPrevious();

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