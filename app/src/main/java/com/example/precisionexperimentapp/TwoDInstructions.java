/**
 * Created by carmen on 16-09-02.
 * 
 * Modified by Afroza on 2017-04-26
 * 
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
//import android.support.v7.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TwoDInstructions extends Activity {

    Button start;
    int block;
    long startTimeStamp;
    String startTimeStampCalender;
    String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_d_instructions);

        start = (Button) findViewById(R.id.goToTwoD);

        // Start the Finger Calibration Task with a New Block of Trials

        start.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    getPid();  // Get the participant ID
                    
                    startTimeStamp = System.currentTimeMillis();  // Record the TIME Stamp from the System                                      
                    startTimeStampCalender = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());    // get current Time

                	//WriteStartTime_InternalFile();
                	//WriteStartTime_ExternalFile();
                	
                    startActivity(new Intent(TwoDInstructions.this, TwoDFittsTask.class)); // Go to the Two-D Fitts Task
                    updateBlock(); // Update the block file
                }

                return false;
            }
        } );  // End of txtNextBlock.setOnTouchListener
    }
    
    // Write the start time of the 2D Fitts task
    
    public void WriteStartTime_InternalFile(){
        
    	try {

            FileOutputStream file = openFileOutput("PId_" + pid +  "_TwoDFittsData_Internal.csv", Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {

                out.write("TwoD Task Start Time: "+ startTimeStamp + ","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                out.write('\n');
                
                out.write("TwoD Task Start Time Calender: "+ startTimeStampCalender + ","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
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
    
    public void WriteStartTime_ExternalFile(){
	   	 
        String fileName = "PId_" + pid + "_TwoDFittsData_External.csv";

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {   // If the External Storage is Mounted, then write on the file

            File Dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MotionCaptureFile");

            if (!Dir.exists()) {  // If the Directory does not exist, make the directory
                Dir.mkdir();
            }

            File file = new File(Dir, fileName);
   	
            try {

                BufferedWriter out = new BufferedWriter(new FileWriter(file, true));  //  FileWriter(file, true ) appends on the file              
                
                out.write("TwoD Task  Start Time: "+ startTimeStamp + ","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                // the above "," makes it easier to read the header and avoid creating the void arrays
                out.write('\n');

                out.write("TwoD Task Start Time Calender: "+ startTimeStampCalender + ","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");
                // the above "," makes it easier to read the header and avoid creating the void arrays
                out.write('\n');

                out.close();


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }   // End of if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        
        else {  // Otherwise, Toast a message
            Toast.makeText(TwoDInstructions.this, "SD card Not Found", Toast.LENGTH_LONG).show(); // displays a message
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

        getBlock(); // Reading the block number from the "block.txt" file
        block++;    // Increment the block number


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


}
