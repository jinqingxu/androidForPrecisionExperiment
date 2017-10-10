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
 * Created by carmen on 16-09-02.
 */

public class NextBlockTwoD extends Activity  {

    TextView txtNextBlock;    // Button to start a new block of Finger Calibration Task
    TextView txtScore;       // Number of blocks finished
    TextView touchDownAverage, liftUpAverage; // to show the average time as feedback
    int block;
    int touchDownTime,liftUpTime; //record the average time

    String score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_block_two_d_fitts);

        txtNextBlock = (TextView) findViewById(R.id.txtNextBlockTwoD); // Setting up the Login button for start
        txtScore = (TextView) findViewById(R.id.twoDScore);
        touchDownAverage = (TextView) findViewById(R.id.twoDTouchDownAverage); // Show the touchDownAverage Time to give efficient feedback
        liftUpAverage = (TextView) findViewById(R.id.twoDLiftUpAverage); // Show the liftUpAverage Time to give efficient feedback



        getBlock(); // Reading the block number from the "block.txt" file
        block++;    // Then Increment the block number

        getScore(); // Get the score from previous block
        txtScore.setText("Your Score is: " + score );  // Display the score #


        // calculate the average time
        touchDownTime = safeLongToInt(TwoDFittsTask.touchDownAll/TwoDFittsTask.maxTrial);
        liftUpTime =safeLongToInt(TwoDFittsTask.liftUpAll/TwoDFittsTask.maxTrial);

        touchDownAverage.setText("Your touch-down average time is:" + touchDownTime + "ms");
        liftUpAverage.setText("Your lift-up average time is:"+ liftUpTime +"ms");


        // Start Button Actions
        // Start the 2-D Fitts Task with a New Block of Trials

        txtNextBlock.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    startActivity(new Intent(NextBlockTwoD.this, TwoDFittsTask.class)); // Go to the 2-D Fitts Task
                    updateBlock(); // Update the block file
                    resetScore();  // Reset the score to ""


                }

                return false;
            }

        } );  // End of txtNextBlock.setOnTouchListener

    }   // End of onCreate(Bundle savedInstanceState)


    // safely convert long to int
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }



    // Reading the block number from the "block.txt" file

    public void getBlock(){

        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("block.txt")));
            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append(inputReader.readLine());
            block = Integer.parseInt(stringBuffer.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // Update the block file

    public void updateBlock(){

        try{
            // Create the block file everytime there is a new block and write the current block number

            FileOutputStream file = openFileOutput( "block.txt", Context.MODE_PRIVATE | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {

                out.write("" + block);
                out.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (FileNotFoundException e){

            e.printStackTrace();

        }
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
