package com.example.precisionexperimentapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * Created by carmen on 16-11-03.
 */

public class TwoDCalibInstructions extends Activity {
	
    Button start;
    int block;
    String pid;
    long startTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_d_calib_instructions);

        // Get the initial data
        start = (Button)findViewById(R.id.goToFingerTwoDCalib);

        // start the finger Calibration Task with a New Block of Trials
        start.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {    //Called when a touch event is dispatched to a view. This allows listeners to get a chance to respond before the target view.

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {    // ACTION_DOWN: A pressed gesture has started, the motion contains the initial starting location.

                    WriteStartTime();// record the startTime when the pressed gesture started, helps data extraction from Leap Motion as we have the start timestamp in this 2D calibration task
                    startActivity(new Intent(TwoDCalibInstructions.this,TwoDCalibTask.class));

                }
                return false;
            }
        });


    }

    public void WriteStartTime(){
        getPid();
        startTimeStamp = System.currentTimeMillis();
        try {

            FileOutputStream file = openFileOutput("PId_" + pid + "_2D_Fitts_Detailed_Trial_Data_Internal.csv", Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {

                out.write("TwoD Calib Start Time: "+ startTimeStamp + ","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" "+","+" ");

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

}
