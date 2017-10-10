/**
 * Created by carmen on 16-08-31.
 * 
 * Modified by Afroza 2016-04-26
 * 
 * TO DO:
 * 
 * 1. For testing we are going directly to the 2D Fitts Task, skipping the 2D calibration task. Change back the new activity to TwoDCalibInstructions.class  
 */

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


public class FingerCalibToTwoDCalib extends Activity {
	
    TextView goToTwoD;    // Button to start a new block of Finger Calibration Task
    TextView txtScore;

    String score;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_calib_to_two_d);

        goToTwoD = (TextView) findViewById(R.id.txtGoToTwoDCalib); // setting up the Login button for start
        txtScore = (TextView) findViewById(R.id.fingerCalibScore2);

        getScore(); // Get the score from previous block
        txtScore.setText("Your Score is: " + score );  // Display the score #

        goToTwoD.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    
                	/*****
                	 * 
                	 * FOR THE TIME BEING TESTING ONLY THE FINGER CALIBRATION AND 2D FITTS TASK
                	 * THEREFORE, SKIPPING THE 2D CALIBRATION TASK              
                	 * 
                	 */
                    //startActivity(new Intent(FingerCalibToTwoDCalib.this, TwoDCalibInstructions.class)); // switch to new layout
                          	
                	
                	startActivity(new Intent(FingerCalibToTwoDCalib.this, TwoDInstructions.class)); // switch to new layout
                    
                    resetScore();
                }

                return false;
            }

        } );  // End of goToTwoD.setOnTouchListener

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
