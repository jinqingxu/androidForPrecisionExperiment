/**
 * Created by carmen on 16-08-31.
 * 
 * Modified by Afroza on 2017-04-24
 * 
 * TO DO: 
 * 
 * 1. CHECK THE TARGET WIDTH OF 91 PIXCEL INTO MM. WE NEED THE TARGET WIDTH TO BE 4.88 MM
 * 
 * 2. FIX THE emailResult(). However, we have written the external files, so even though emailResult() does not work, we still have the data in the tablet
 * 
 * 
 */

package com.example.precisionexperimentapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import android.support.v7.app.AppCompatActivity;
//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.common.api.GoogleApiClient;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class FingerCalibTask extends Activity{

    TextView displayTrial;       // Displays the scores, gives the user feedback on their performance
    ImageView imgRedCircle;        // Displays the Target
    FrameLayout frame;        // The Total Screen Space
    LinearLayout linLayout;   // Touchable Screen Space

    int max_trial = 0;  // Initial value of max_trail. This value will change according to the nature of the block (trial or the full block)
    int trial_block_max_trial = 1; // the number of trials in a Trial block is 6
    int full_block_max_trial = 1;  // the number of trials in a Full block is 48
    int max_block = 1; // the number of total blocks is 3

    private SoundPool sp;  // plays audio resources, sp is the soundpool
    private int rightSound, wrongSound; // 2 audio resources, the right tone and the wrong tone


    // int block = 0 ;
    int block;
    int trial = 0;
    int score = 0;
    int select = 0;
    double direction = 0;
    double pressure = 0;
    double targetWidth = 91;  // Nexus: mm to px in xxhdpi: 91 px = 4.88 mm - Take only The Smallest Target


    long touchDownTimeStamp, liftUpTimeStamp;
    double touchDownX, touchDownY, liftUpX, liftUpY, targetX, targetY;

    // In calibration task, we use the same target widths
    //double[] targetWidths = {91, 91, 91};   // Nexus: mm to px in xxhdpi - Take only The Smallest Target
    
    //double [] targetWidths = {9.2, 7.2, 4.8};  // TARGET WIDTHS IN MM
    //double [] targetWidths = {174, 136, 91};   // Nexus: mm to px in xxhdpi
    //double [] targetWidths = {110, 86, 58};   // Samsung: mm to px in xxhdpi

    String pid;

    int screenWidth;
    int screenHeight;

    Canvas canvas; // this variable is used for drawing target
    Paint paint;

    
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     
    //private GoogleApiClient client;   

    //static Context obj;
    //File fileDir;

    //LinearLayout.LayoutParams par;

     */
    
    
    // Initialize Page
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_calib_task);

        initializeEverything();    // Initialize all the necessary variables and prepare the screen
        drawTarget();   // Draw the target on the screen


        
        // Target Selection Trial calculated by the touch down action, whether successful or not
        imgRedCircle.setOnTouchListener(new View.OnTouchListener() {



            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {  //A pressed gesture has started, the motion contains the initial starting location.

                    // Get the screenWidth and screenHeight of the Touchable Area of the screen
                    screenWidth = linLayout.getRight() - linLayout.getLeft();
                    screenHeight = linLayout.getBottom() - linLayout.getTop();

                    // Calculate the targetX and targetY
                    targetX = screenWidth / 2;
                    targetY = screenHeight / 2;

                    touchDownX = motionEvent.getX();   // Record the Touch Down x- coordinate Location
                    touchDownY = motionEvent.getY();   // Record the Touch Down y- coordinate Location

                    pressure = motionEvent.getPressure(); // Record the pressure when touched down
                    touchDownTimeStamp = System.currentTimeMillis();  // Record the time stamp of touch down
                     
                }  // END OF if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)


                // When lift up:
                // If it is successful(Finger lift up location is inside the certain radius of the target),increment the scores,play the right tone
                // If it is unsuccessful(Finger lift up location is outside the target),scores remain the same, play the wrong tone

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {  //A pressed gesture has finished, the motion contains the final release location
                    // as well as any intermediate points since the last down or move event.

                    liftUpX = motionEvent.getX();   // Record the Lift Up x- coordinate Location
                    liftUpY = motionEvent.getY();   // Record the Lift Up y- coordinate Location
                    liftUpTimeStamp = System.currentTimeMillis();    // Record the time stamp of Lift Up
                                        
                    // Successful Target Selection:

                    if (isSelectionInsideTarget(liftUpX, liftUpY) ) {

                        sp.play(rightSound, 1, 1, 0, 0, 1); //play the right tone

                        if(trial < max_trial) {
                            score++;    // Increment Score
                            select = 1;  // Record the successful selection
                        }
                    } 
                    
                    
                    // Unsuccessful Target Selection - Tap on the rest of the screen:
                    
                    else {              
                        screenWidth = linLayout.getRight() - linLayout.getLeft();
                        screenHeight = linLayout.getBottom() - linLayout.getTop();

                        // Set the targetX and targetY as the center of the screen because of unsuccessful selection
                        // IS THIS NECESSARY?????                        
                        targetX = screenWidth / 2;
                        targetY = screenHeight / 2;
                        
                        select = 0;      // Record an unsuccessful selection
                        sp.play(wrongSound, 1, 1, 0, 0, 1); //play the wrong tone
                    }

                    doAfterTouch(); //after the target selection, do other things
                }

                return true;
            }


        });

    }

    // Do these common series of tasks after a successful or unsuccessful target selection
    
    private void doAfterTouch() {

        // If more trials left in the block: Increment trial, display Trial and Score, and write trial data in the "data.txt" file
        if (trial < max_trial) {

            trial++;    // Increment Trial #
            displayTrial.setText("Score: " + score + "/" + trial);
            drawTarget();
            writeDataInternal();  // Write Trial data in the internal "FingerCalibData.csv" file
            writeDataExternal();  // Write Trial data in the External "FingerCalibData_External.csv" file

        }

        // No More Trials Left: Write the Score on the score.txt file and Go to the Next Block Screen
        // If more blocks left, then go to the Next Block Screen, Otherwise go to the 2-D Fitts Task

        else {


            // Write the score in the score.txt file
            try {

                FileOutputStream file = openFileOutput("score.txt", Context.MODE_PRIVATE | Context.MODE_WORLD_READABLE);
                OutputStreamWriter out = new OutputStreamWriter(file);

                try {

                    out.write(score + "/" + trial);
                    out.close();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            }


            // If More Block left: Write the Score on the score.txt file and Go to the Next Block Screen
            if (block < max_block) {

                startActivity(new Intent(FingerCalibTask.this, NextBlockFingerCalib.class)); // switch to new layout
                
 
            }


            // Otherwise, Reset the block to -1, write score on the score.txt file, and Go to the 2-D fitts task
            else {

//                startActivity(new Intent(FingerCalibTask.this, FingerCalibToTwoDCalib.class)); // Go to the Next Block for 2-D Task sScreen
      
                startActivity(new Intent(FingerCalibTask.this, FingerCalibToTwoDCalib.class)); // Go to the Next Block for 2-D Task sScreen

                
                //Toast.makeText(FingerCalibTask.this, "Reset Block ... ", Toast.LENGTH_SHORT).show(); // displays a message

                resetBlock();  // Reset the block to -1
                
                //Toast.makeText(FingerCalibTask.this, "Call Email Result ... ", Toast.LENGTH_SHORT).show(); // displays a message

                /*****************
                 * EMAIL RESULT DOES NOT WORK
                 * 
                 ************/
                
                //emailResult();  // Email the Finger Calibration Task data to the researcher

            }

        }
    }

    // Write Trial data in the Internal "FingerCalibData.csv" file
    private void writeDataInternal() {

        try {

            FileOutputStream file = openFileOutput("PId_" + pid + "_FingerCalibData_Internal.csv", Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {

                out.write(pid + "," + block + "," + trial + "," + select + "," + targetWidth + "," + pressure + "," + touchDownX + "," + touchDownY + "," + liftUpX + "," + liftUpY + "," + touchDownTimeStamp + "," + liftUpTimeStamp);
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

   
    // Write Trial data in the External "FingerCalibData.csv" file
    public void writeDataExternal() {

        String fileName = "PId_" + pid + "_FingerCalibData_External.csv";

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {   // If the External Storage is Mounted, then write on the file

            File Dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MotionTracAppFile");

            if (!Dir.exists()) {  // If the Directory does not exist, make the directory
                Dir.mkdir();
            }

            File file = new File(Dir, fileName);

            try {

                BufferedWriter out = new BufferedWriter(new FileWriter(file, true));  //  FileWriter(file, true ) appends on the file

                out.write(pid + "," + block + "," + trial + "," + select + "," + targetWidth + "," + pressure + "," + touchDownX + "," + touchDownY + "," + liftUpX + "," + liftUpY + "," + touchDownTimeStamp + "," + liftUpTimeStamp);
                out.write('\n');
                out.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }   // End of if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))

        else {  // Otherwise, Toast a message
            Toast.makeText(FingerCalibTask.this, "SD card Not Found", Toast.LENGTH_LONG).show(); // displays a message
        }

    }

    // reset the block and turn into the twoD task
    private void resetBlock() {

        try {

            FileOutputStream file = openFileOutput("block.txt", Context.MODE_PRIVATE | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {

                out.write("-1");
                out.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }
    }

    // Emails the trial data to the Researchers
    
    
    
    //***************************************/
    //
    // FIX THE EMAIL PROBLEM
    //
    //**************************************/

    
    
    private void emailResult() {

        Toast.makeText(FingerCalibTask.this, "Inside Email Result ... ", Toast.LENGTH_SHORT).show(); // displays a message

        
        Mail email = new Mail("actlab.targetselection", "FittsTask3661", "Finger Calibration Task PId " + pid, "Results of Finger Calibration Task from PId " + pid + ".");

        Toast.makeText(FingerCalibTask.this, "Declared new email ... ", Toast.LENGTH_SHORT).show(); // displays a message
        
        
         
        // Add the "PId_" + pid + "_FingerCalibData.csv" file as an Attachment

        try {

            email.addAttachment(getFilesDir().getAbsolutePath() + "/PId_" + pid + "_FingerCalibData_Internal.csv");

        } catch (Exception e1) {
            e1.printStackTrace();
            Toast.makeText(FingerCalibTask.this, "Cannot Attach a File ... ", Toast.LENGTH_LONG).show(); // displays a message
        }


        try {
            if (email.send())
                Toast.makeText(FingerCalibTask.this, "Sending Results ... ", Toast.LENGTH_LONG).show(); // displays a message

            else
                Toast.makeText(FingerCalibTask.this, "No Luck ... ", Toast.LENGTH_LONG).show(); // displays a message

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(FingerCalibTask.this, "Cannot Send Results ... ", Toast.LENGTH_LONG).show(); // displays a message
        }
        
        
    } 

    
    // Check if the touch point (selection) is inside the target
    private boolean isSelectionInsideTarget(double liftUpX, double liftUpY) {

        if (distance(liftUpX, liftUpY, targetX, targetY) <= targetWidth / 2)
            return true;

        return false;
    } 
    
    

    private double distance(double liftUpX, double liftUpY, double targetX, double targetY) {

        return Math.pow((targetX - liftUpX) * (targetX - liftUpX) + (targetY - liftUpY) * (targetY - liftUpY), 0.5);
    }

    // Draw the target at the centre of the screen to select
    
    private void drawTarget() {

        targetX = screenWidth / 2;
        targetY = screenHeight / 2;

        // Draw the Target
        Bitmap bmp = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);    // Create a bitmap
        canvas = new Canvas(bmp);                 // create the canvas
        imgRedCircle.setImageBitmap(bmp);         // associate imgRedCircle View with the bitmap
        paint = new Paint();                     // Initialize paint
        paint.setColor(Color.RED);                 // Set the color of the target
        canvas.drawCircle((float) targetX, (float) targetY, (float) targetWidth / 2, paint);   // Draw the target on the canvas given the coordinates of the target center, target radius, and the color

    }
    
    // initialize everything
    private void initializeEverything() {

        // Linking with variables of the xml layout page
        frame = (FrameLayout) findViewById(R.id.frameLayoutFingerCalib);
        linLayout = (LinearLayout) findViewById(R.id.linLayoutFingerCalib);
        displayTrial = (TextView) findViewById(R.id.txtTrialNum);

        createInitialTargetView();

        // Get Screen Resolution
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;    // Screen Width in pixel
        screenHeight = metrics.heightPixels;  // Screen Height in pixel


        // For Sending Email
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // For playing the audio resources
        
       sp = new SoundPool(10, AudioManager.STREAM_ALARM, 5);  //SoundPool(int maxStreams, int streamType, int srcQuality)
       wrongSound = sp.load(this, R.raw.wrong, 1);  //Play sound at the incorrect selection. load(String path, int priority)
       rightSound = sp.load(this, R.raw.right, 1);  //Play sound at the correct selection.

        getBlock();    // Read the block number from the "block.txt" file

        // Set the max_trial number for each block
        if (block == 0) {
            max_trial = trial_block_max_trial;
        } else {
            max_trial = full_block_max_trial;
        }

        getPid();       // Read the participant id (pid) from the "pid.txt" file
    }

    
    // Create the initial View of the Target with a White Background Image
    private void createInitialTargetView() {

        // add a new element to the FrameLayout
        imgRedCircle = new ImageView(this);
        imgRedCircle.setImageResource(R.drawable.white_background);  //Sets a drawable as the content of this ImageView.
        frame.addView(imgRedCircle);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 0;
        lp.topMargin = 0;
        imgRedCircle.setLayoutParams(lp);
    }

    // Reading the pid from the "pid.txt" file
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

    // Reading the pid from the "block.txt" file
    private void getBlock() {

        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("block.txt")));
            String inputString;
            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append(inputReader.readLine());
            block = Integer.parseInt(stringBuffer.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

 } // end of the class