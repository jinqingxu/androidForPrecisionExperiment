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
 * Created by carmen on 16-08-31.
 */


public class NextBlockFingerCalib extends Activity {
	
	 TextView txtNextBlock;    // Button to start a new block of Finger Calibration Task
	    TextView txtScore;       // Number of blocks finished
	    int block;
	    String score;

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.new_block_finger_calib);

	        txtNextBlock = (TextView) findViewById(R.id.txtNextBlock); // setting up the Login button for start
	        txtScore = (TextView) findViewById(R.id.fingerCalibScore);

	        getBlock(); // Reading the block number from the "block.txt" file
	        block++;    // Increment the block number
	        getScore(); // Get the score from previous block
	        txtScore.setText("Your Score is: " + score );  // Display the score #


	        // Start Button Actions


	        // Start the Finger Calibration Task with a New Block of Trials

	        txtNextBlock.setOnTouchListener(new View.OnTouchListener() {

	            @Override
	            public boolean onTouch(View v, MotionEvent event) {
	                // TODO Auto-generated method stub

	                if (event.getAction() == MotionEvent.ACTION_DOWN){

	                    startActivity(new Intent(NextBlockFingerCalib.this, FingerCalibTask.class)); // Go to the Finger Calibration Task
	                    updateBlock(); // Update the block file
	                    resetScore();  // Reset the score to ""
	                }

	                return false;
	            }
	        } );  // End of txtNextBlock.setOnTouchListener
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

	        // Update the block file

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
