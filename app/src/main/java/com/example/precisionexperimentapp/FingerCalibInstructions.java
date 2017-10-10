/**
 * 
 * Created by Carmen on 16-08-31.
 * Modified by Afroza on 2017-04-24
 * 
 * TO DO: 
 * 1. Write the starting time for the Finger Calibration task (After the participant press "yes") to the External Files
 *    Modify this in the WriteStartTimeInFile() Method
 *  
 */


package com.example.precisionexperimentapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class FingerCalibInstructions extends Activity {
	

    Button start;
    int block;
    String pid;
    long startTimeStamp;
    String startTimeStampCalender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_calib_instructions);


        start = (Button)findViewById(R.id.goToFingerCalib);

        // start the finger Calibration Task with a New Block of Trials
        
        start.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {    //Called when a touch event is dispatched to a view. This allows listeners to get a chance to respond before the target view.

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {    // ACTION_DOWN: A pressed gesture has started, the motion contains the initial starting location.

                    updateBlock(); // directs to the next block, begins the other tasks
                    getPid();  // Get the participant ID
 
                    startTimeStamp = System.currentTimeMillis();  // Record the TIME Stamp from the System                                      
                    startTimeStampCalender = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());    // get current Time

                    WriteStartTime_InternalFile(); // record the startTime when the pressed gesture started, makes it easier for data extraction from Leap Motion as we have the start timestamp in this calibration task
                    WriteStartTime_ExternalFile(); // record the startTime when the pressed gesture started, makes it easier for data extraction from Leap Motion as we have the start timestamp in this calibration task
                
                   	startActivity(new Intent(FingerCalibInstructions.this,FingerCalibTask.class));  // Go to the next activity
                    
                }
                    return false;
            }
        });   // END of start.setOnTouchListener


    }

    // Write the starting time for the Finger calibration task to coordinate with the data from the LEAP MOTION Device
    
    public void WriteStartTime_InternalFile(){

        try {

            FileOutputStream file = openFileOutput("PId_" + pid + "_FingerCalibData_Internal.csv", Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {

                out.write("Finger Calibration Task Start Time: "+ startTimeStamp + ","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                // the above "," makes it easier to read the header and avoid creating the void arrays
                out.write('\n');
                
                out.write("Finger Calibration Task Start Time Calender: "+ startTimeStampCalender + ","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                // the above "," makes it easier to read the header and avoid creating the void arrays
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

    // Write Start time of Finger Calibration Task in the external File
    
    public void WriteStartTime_ExternalFile(){
    	    	   	 
        String fileName = "PId_" + pid + "_FingerCalibData_External.csv";

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {   // If the External Storage is Mounted, then write on the file

            File Dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MotionCaptureFile");

            if (!Dir.exists()) {  // If the Directory does not exist, make the directory
                Dir.mkdir();
            }

            File file = new File(Dir, fileName);
   	
            try {

                BufferedWriter out = new BufferedWriter(new FileWriter(file, true));  //  FileWriter(file, true ) appends on the file              
                
                out.write("Finger Calibration Task Start Time: "+ startTimeStamp + ","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                // the above "," makes it easier to read the header and avoid creating the void arrays
                out.write('\n');

                out.write("Finger Calibration Task Start Time Calender: "+ startTimeStampCalender + ","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                // the above "," makes it easier to read the header and avoid creating the void arrays
                out.write('\n');

                out.close();


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }   // End of if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        
        else {  // Otherwise, Toast a message
            Toast.makeText(FingerCalibInstructions.this, "SD card Not Found", Toast.LENGTH_LONG).show(); // displays a message
        }

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
    
    
    public void updateBlock() {

        getBlock(); // Reading the block number from the "block.txt" file
        block++;    // Increment the block number

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
    
    

    public void getBlock() {
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("block.txt")));
            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append(inputReader.readLine());
            block = Integer.parseInt(stringBuffer.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
