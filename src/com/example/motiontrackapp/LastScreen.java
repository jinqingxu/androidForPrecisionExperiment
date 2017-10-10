package com.example.precisionexperimentapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by carmen on 16-09-03.
 */

public class LastScreen extends Activity {
	
    TextView goToLogin;  // When touched, start the Login Screen Activity(A new participant ID log in)
    TextView txtScore;
    TextView touchDownAverage, liftUpAverage;// To show the average time as feedback
    String score, pid;
    int touchDownTime,liftUpTime;//Record the average time
    long endTimeStamp;

    //Everything starts here
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_screen);

        WriteEndTime(); // makes it easier to extract data in Leap Motion file according to the end timestamp
        goToLogin = (TextView) findViewById(R.id.txtGoToLogin); // setting up the button for start
        txtScore = (TextView) findViewById(R.id.twoDScore2);
        touchDownAverage = (TextView) findViewById(R.id.LastTouchDownAverage);// TouchDown Average time
        liftUpAverage = (TextView) findViewById(R.id.LastLiftUpAverage);// LiftUp Average time

        getScore(); // Get the score from previous block
        txtScore.setText("Your Score is: " + score );  // Display the score #

        // calculate the average time
        touchDownTime = safeLongToInt(TwoDFittsTask.touchDownAll/TwoDFittsTask.maxTrial);
        liftUpTime =safeLongToInt(TwoDFittsTask.liftUpAll/TwoDFittsTask.maxTrial);

        touchDownAverage.setText("Your touch-down average time is:" + touchDownTime + "ms");
        liftUpAverage.setText("Your lift-up average time is:"+ liftUpTime +"ms");


        goToLogin.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    startActivity(new Intent(LastScreen.this, LoginScreen.class)); // Go to the Finger Calibration Task
                    resetScore();

                }

                return false;
            }
        } );

    }

    private void getPid() {

        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("pid.txt")));

            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append(inputReader.readLine());
            pid = stringBuffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void WriteEndTime(){
        getPid();
        endTimeStamp = System.currentTimeMillis();
        try {

            FileOutputStream file = openFileOutput("PId_" + pid +  "_2D_Fitts_Detailed_Trial_Data_Internal.csv", Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {

                out.write("End Time: "+ endTimeStamp + ",");

                out.write('\n');
                out.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }

    }


    // Safely cast long to int
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }


    // Reading the score of previous trial from the "score.txt" file
    public void getScore(){

        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("score.txt")));
            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append(inputReader.readLine());
            score = stringBuffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Reset the score.txt to ""
    public void resetScore(){

        try{

            FileOutputStream file = openFileOutput( "score.txt", Context.MODE_PRIVATE | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {
                out.write("");
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }  // End of resetScore()
}
