/**
 * 
 * Modified by Afroza on 2017-06-14
 *  
 */


package com.example.precisionexperimentapp;

import android.app.Activity;
import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;


import com.leapmotion.leap.*;


public class LoginScreen extends Activity {
	
    Button login;
    EditText pidInput;
    String pid;
    String date, time, deviceModel, androidVersion;
    int screenWidth, screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        login = (Button) findViewById(R.id.btnLogin);  // setting up the button for start
        pidInput = (EditText) findViewById(R.id.inpPID); // setting up the content of pid

        // after clicking on the button
        login.setOnClickListener(new View.OnClickListener() {

 
        	@Override
            public void onClick(View view) {
                pid = pidInput.getText().toString(); // get the text in pidInput and convert it into string

                if (verifyLogin(pid)) {    // if the pid is valid, proceed. otherwise re-enter it.

                    FetchSystemInformation();    // Fetch the system information from the Mobile Device to write on the header
                    createAllFiles();            // Create all the internal, external and other necessary Files

                    //System.out.println("Go To LEAP MOtion");
                    //initializeLeapMotion();   // LEAP MOTION CODE STOPS THE ANDROID APP NEED TO FIX
 
                   // startActivity(new Intent(LoginScreen.this,FingerCalibInstructions.class)); // switch to next activity
                    startActivity(new Intent(LoginScreen.this,TwoDCalibInstructions.class)); // switch to next activity

                }

                else{

                    Toast.makeText(LoginScreen.this, "Please Enter a Valid Participant ID", Toast.LENGTH_SHORT).show();   //Toast.makeText(context, text, duration).show();
                    pid = pidInput.getText().toString();

                }
            }


        });


    }

    public void initializeLeapMotion(){
    	
        LeapListener listener = new LeapListener();     // Initialize a new Leap Listener to collect data from Leap Motion
		Controller controller = new Controller();		// Initialize a new Controller to collect data from Leap Motion

		controller.addListener(listener);		
		controller.removeListener(listener);

    }
   
    public void createAllFiles(){
    	
        // create internal data file for Finger Calibration Task, Two-D Fitts Task, and Two-D detailed Data, then write the system info and data headers on the files
        createInternalFile("PId_" + pid + "_FingerCalibData_Internal.csv", "Finger Calibration Task", "PID,Block,Trial,Select,Width(mm),Pressure,Touch Down X-cor,Touch Down Y-cor,Lift Up X-cor,Lift Up Y-cor,Touch Down Time Stamp, Lift Up Time Stamp");
        createInternalFile("PId_" + pid + "_TwoDFittsData_Internal.csv", "Two Dimensional Fitts Task", "Group,PId,Block,Trial,Amplitude (mm),Width (mm),Direction (degree),Select,Attempt,Error,Slip Error, Narrow Slip Error, Moderate Slip Error, Large Slip Error, Very Large Slip Error, Miss Error, Near Miss Error, Not So Near Miss Error, Other Error, Accidental Tap, Accidental Hit, Pressure,Touch-Down X-cor,Touch-Down Y-cor, Lift-Up X-cor,Lift-Up Y-cor,First Touch-Down Timestamp,First Touch-Down Time Taken,First Lift-Up Timestamp,First Lift-Up Time Taken, Final Touch-Down Timestamp, Final Touch-Down Time Taken(ms),Final Lift-Up Timestamp, Final Lift-Up Time (ms),startTime,First Re-Entry,TRE(/attempt)");
        createInternalFile("PId_" + pid +  "_2D_Fitts_Detailed_Trial_Data_Internal.csv", "Two Dimensional Fitts Task Details", "Group,PId,Block,Trial,Amplitude (mm),Width (mm),Direction (degree),Attempt,Error,Slip Error, Narrow Slip Error, Moderate Slip Error, Large Slip Error, Very Large Slip Error, Miss Error, Near Miss Error, Not So Near Miss Error, Other Error, Accidental Tap, Accidental Hit, Pressure, Target X, Target Y, Touch-Down X-cor, Relative Touch-Down X From Target,Touch-Down Y-cor, Relative Touch-Down Y From Target, Lift-Up X-cor,   Relative Lift-Up X From Start, Relative Lift-Up X From Target,Lift-Up Y-cor,  Relative Lift-Up Y From Start, Relative Lift-Up Y From Target, Final Touch-Down Distance From Target, Final Lift-Up Distance From Target,Current Touch-Down Time Taken,Current Lift-Up Time Taken, Touch-Down TimeStamp,Lift-Up TimeStamp,start Time,startX,startY,Re-entry");

        // create external data file for Finger Calibration Task, Two-D Fitts Task, and Two-D detailed Data, then write the system info and data headers on the files
        createExternalFile("PId_" + pid + "_FingerCalibData_External.csv", "Finger Calibration Task", "PId,Block,Trial,Select,Width (mm),Pressure,Touch Down X-cor,Touch Down Y-cor,Lift Up X-cor,Lift Up Y-cor,Touch Down Time Stamp, Lift Up Time Stamp");
        createExternalFile("PId_" + pid + "_TwoDFittsData_External.csv", "Two Dimensional Fitts Task", "Group,PId,Block,Trial,Amplitude (mm),Width (mm),Direction (degree),Select,Attempt,Error,Slip Error, Narrow Slip Error, Moderate Slip Error, Large Slip Error, Very Large Slip Error, Miss Error, Near Miss Error, Not So Near Miss Error, Other Error, Accidental Tap, Accidental Hit, Pressure,Touch-Down X-cor,Touch-Down Y-cor, Lift-Up X-cor,Lift-Up Y-cor,First Touch-Down Timestamp,First Touch-Down Time Taken,First Lift-Up Timestamp,First Lift-Up Time Taken, Final Touch-Down Timestamp, Final Touch-Down Time Taken(ms),Final Lift-Up Timestamp, Final Lift-Up Time (ms),startTime,First Re-Entry,TRE(/attempt)");
        createExternalFile("PId_" + pid +  "_2D_FittsDetailedTrialData_External.csv", "Two Dimensional Fitts Task Details", "Group,PId,Block,Trial,Amplitude (mm),Width (mm),Direction (degree),Attempt,Error,Slip Error, Narrow Slip Error, Moderate Slip Error, Large Slip Error, Very Large Slip Error, Miss Error, Near Miss Error, Not So Near Miss Error, Other Error, Accidental Tap, Accidental Hit, Pressure, Target X, Target Y, Touch-Down X-cor, Relative Touch-Down X From Target,Touch-Down Y-cor, Relative Touch-Down Y From Target, Lift-Up X-cor,   Relative Lift-Up X From Start, Relative Lift-Up X From Target,Lift-Up Y-cor,  Relative Lift-Up Y From Start, Relative Lift-Up Y From Target, Final Touch-Down Distance From Target, Final Lift-Up Distance From Target, Current Touch-Down Time Taken,Current Lift-Up Time Taken, Touch-Down TimeStamp,Lift-Up TimeStamp, start Time,startX,startY,Re-entry");

        createBlockFile();    // Create a file to track the number of Blocks
        createPIdFile();      // Create a file to track PId
        createScoreFile();	// Create a file to track the score

    }

    // record the scores the user get
    private void createScoreFile() {
        try{

            FileOutputStream file = openFileOutput( "score.txt", Context.MODE_PRIVATE | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

        } catch (FileNotFoundException e){

            e.printStackTrace();

        }

    }

    // record the user pid
    private void createPIdFile() {

        try{

            FileOutputStream file = openFileOutput( "pid.txt", Context.MODE_PRIVATE | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {
                out.write(pid);
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    // record the blocks
    private void createBlockFile() {
        try{

            FileOutputStream file = openFileOutput( "block.txt", Context.MODE_PRIVATE | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {
                out.write("-1");  // To indicate the creation of the block file, as block = -1 before the beginning of the trials. Block = 0 are for trial blocks
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e){

            e.printStackTrace();

        }
    }


    // Create a file in the EXTERNAL STORAGE to record trial data

    private void createExternalFile(String fileName, String taskName, String dataHeading) {

        String state = Environment.getExternalStorageState(); //Returns the current state of the primary shared/external storage media.

        if(Environment.MEDIA_MOUNTED.equals(state)){    // MEDIA_MOUNTED: constant, storage state if the media is present and mounted at its mount point with read/write access.

            File Root = Environment.getExternalStorageDirectory();  // Return the primary shared/external storage directory
            //File Dir = new File(Root.getAbsolutePath() + "/MotionCaptureFile");
            File Dir = new File(Root.getAbsolutePath() + "/MotionTracAppFile");
            
            if(!Dir.exists()){
                Dir.mkdir();   // Creates the directory named by this abstract pathname if not existed
            }

            File file = new File(Dir, fileName);

            try {
                BufferedWriter out = new BufferedWriter((new FileWriter((file))));

                // the  "," helps to read the header and avoid creating the void arrays
                out.write("Participant ID: " + pid +" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");  // avoid ArrayIndexOutOfBoundsException Error when reading file and analyzing File  (here to AQ)
                out.write('\n');
                out.write("Task: " + taskName +" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write("Date: " + date +" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write("Time: " + time +" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write("Device: " + deviceModel +" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write("Screen Size: "+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write("Screen Resolution: " + screenHeight + " x " + screenWidth+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write("Operating System: " + androidVersion+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write('\n');
                out.write(dataHeading);
                out.write('\n');
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else {
            Toast.makeText(LoginScreen.this,"SD card Not Found",Toast.LENGTH_SHORT).show(); //A toast provides simple feedback about an operation in a small popup
        }

    }


    // create INTERNAL files within the APP Directory with given file name, task name and data heading
    
    private void createInternalFile(String fileName, String taskName, String dataHeading) {
        try {


             FileOutputStream file = openFileOutput(fileName, Context.MODE_PRIVATE|Context.MODE_WORLD_READABLE);
             OutputStreamWriter out = new OutputStreamWriter(file);

            try {
                // the  "," helps to read the header and avoid creating the void arrays
                out.write("Participant ID: " + pid+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");  // avoid ArrayIndexOutOfBoundsException Error when reading file and analyzing File  (here to AQ)
                out.write('\n');
                out.write("Task: " + taskName+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write("Date: " + date+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write("Time: " + time+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write("Device: " + deviceModel+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write("Screen Size: "+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write("Screen Resolution: " + screenHeight + " x " + screenWidth+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write("Operating System: " + androidVersion+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                out.write('\n');
                out.write(dataHeading);
                out.write('\n');
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    // fetch system information to write on the files

    private void FetchSystemInformation() {

        date = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());  // get current Date
        time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());    // get current Time

        deviceModel = (Build.MANUFACTURER).toUpperCase() + " " + Build.MODEL; // get device model
        androidVersion =  "Android " + Build.VERSION.RELEASE; // get running android version


        // get screen resolution
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics); //describing general information about a display
        screenHeight = metrics.heightPixels; // get screen height in pixel
        screenWidth = metrics.widthPixels;

    }


    // verify login

    private boolean verifyLogin(String pid) {

        if (pid.equals(""))
            return false;   // if didn't input anything, that is invalid pid.
        
        /* Verify the IDs as Number: 
        
        	1. Convert the pid in to integer then check if 0 < pid < 999
        	
        	pid.Integer.parseInt() -> This is not possible, because first we need to verify pid.Integer.parseInt() is an integer. 
        	What if pid = "abc123"? That will generate and error.
        	
        */
        
        
        
        else return true;
    }	

}
