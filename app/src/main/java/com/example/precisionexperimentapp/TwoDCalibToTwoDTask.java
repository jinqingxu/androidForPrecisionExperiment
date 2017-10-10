package com.example.precisionexperimentapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by carmen on 16-11-03.
 */

public class TwoDCalibToTwoDTask extends Activity   {
	
    TextView goToTwoD;    // Button to start a new block of Finger Calibration Task


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_d_calib_to_two_d_task);

        goToTwoD = (TextView) findViewById(R.id.txtGoToTwoDTask); // setting up the Login button for start


        goToTwoD.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    startActivity(new Intent(TwoDCalibToTwoDTask.this, TwoDInstructions.class)); // switch to new layout

                }

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    startActivity(new Intent(TwoDCalibToTwoDTask.this, TwoDInstructions.class)); // switch to new layout

                }

                return false;
            }

        });  // End of goToTwoD.setOnTouchListener

    }


}

