/**
 * Created by carmen on 16-09-02.

 * 
 * Modified by Afroza 2017-04-26
 * 
 * TO DO:
 * 	1. Fix calculate TRE
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
import android.os.SystemClock;
//import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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


public class TwoDFittsTask extends Activity  {

    ImageButton btnStart;     // Button to start the Finger Calibration Task
    TextView txtExit;         // Link to go back to the Login Page
    TextView  displayTrial;   // Displays the touch points
    ImageView imgRedCircle;   // Display the Target
    Chronometer chronoMeter;  // Display the Timer
    FrameLayout frame;        // The Total Screen Space
    RelativeLayout relLayout;

    int screenWidth, screenHeight;
    Bitmap bmp;
    Canvas canvas;
    Paint paint;

    private SoundPool sp;  // plays audio resources
    private int rightSound, wrongSound; // 2 kinds of audio resources

    TaskConditionsArray targetArray;
    int [] targetAngles = {0,45,90,135,180,225,270,315};
    // int [] targetDistances = {20, 30};
    int [] targetDistances = {270, 378};   //  Nexus: mm to px in xxhdpi: 20 mm, 30 mm
    //int [] targetDistances = {378, 567};   //  Nexus: mm to px in xxhdpi
    //int [] targetDistances = {240, 290};   //  Samsung: mm to px in xxhdpi

    double [] targetWidths = {86, 110, 158};   // Nexus: mm to px in xxhdpi: 4.88 mm, 7.22 mm, 9.22 mm
    //double [] targetWidths = {4.8, 7.2, 9.2};
    //double [] targetWidths = {58, 86, 110};   // Samsung: mm to px in xxhdpi

    //int max_trial = targetAngles.length * targetDistances.length * targetWidths.length;
    public static int maxTrial = 0; // record the max trial for each block, static because it should be calculated in another class
    int trialBlock_maxTrial = 1;
    int fullBlock_maxTrial = 10;
    int maxBlock = 2;

    int group = 0; // 1 represents older adults, while 2 represents young people
    int error ,SlipError , NarrowSlipError ,ModerateSlipError , LargeSlipError, VeryLargeSlipError, MissError,NearMissError,NotSoNearMissError,AccidentalTap,OtherError, AccidentalHit;
    int error1,SlipError1 , NarrowSlipError1 ,ModerateSlipError1 , LargeSlipError1, VeryLargeSlipError1, MissError1,NearMissError1,NotSoNearMissError1,AccidentalTap1,OtherError1, AccidentalHit1;
    
    int block,trial,score,select,attempt,entry,reEntry,firstreEntry;
    double targetWidth, targetDistance, pressure, targetAngle;
    
    long firstTrialTouchDownTimeStamp, firstTrialLiftUpTimeStamp, finalTrialTouchDownTimeStamp, finalTrialLiftUpTimeStamp;  // various timestamp and time taken
    long firstTrialTouchDownTimeTaken, firstTrialLiftUpTimeTaken, finalTrialTouchDownTimeTaken, finalTrialLiftUpTimeTaken;
    long currentTrialTouchDownTimeStamp, currentTrialLiftUpTimeStamp,currentTrialTouchDownTimeTaken, currentTrialLiftUpTimeTaken;
    long startChronometer, startTime;
    double targetX, targetY, startX, startY, touchDownX, touchDownY, liftUpX, liftUpY; // Coordinates of the center of the target
    double RelativeLiftUpXfromTarget,RelativeLiftUpYfromTarget,RelativeLiftUpXfromStart,RelativeLiftUpYfromStart,RelativeTouchDownXfromTarget,RelativeTouchDownYfromTarget,FinalTouchDownFromTarget,FinalLiftUpFromTarget;
    double CurrentX,CurrentY,lastX,lastY;
    double TRE; // target Re-entry per attempt
    public static long touchDownAll,  liftUpAll; // record the total time needed, set static so that it can be used in another class
    String pid;
    boolean ongoingTrial = false;

 

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_d_fitts_task);

        initializeEverything();         // Initialize Everything in the Layout and Activity

        // Start a 2-D Fitts Task

        // Actions Taken on Start Button Touch Down
        
        btnStart.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

            	if (event.getAction() == MotionEvent.ACTION_UP){

                	initializeTrial();
                    btnStart.setEnabled(false);   // Disable Start Button
                    btnStart.setImageResource(R.drawable.start_button_grey);  // Load Disabled Button Image
                    
                  	// Set up the Target drawing area and draw the target                
                    calculateStartCenter();  	   // Calculates the coordinate of the center of the start button
                    calculateScreenProperties();   // Calculates width and height of target drawing area
                    drawTarget();   			   // Draw the circlular target on the screen in the given position
                   
               }
                return true;
            }
        });


        // Actions Taken on Tap on the screen (when try to select a target)

        imgRedCircle.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Gather the Re-entry data

            	if (event.getAction() == MotionEvent.ACTION_MOVE){

                	//calculateTRE(event.getX(), event.getY());  // compare the current X Y and the last X Y to ensure if this is a reEntry                    
               	}

            	/******************************************************
            	 * 
            	 * When finger touch-down:
            	 *  1. Collect touch-down X-Y coordinates
            	 *  2. Collect pressure data
            	 *  3. Increase number of attempts
            	 *  4. Collect the Touch-Down Time Stamp
            	 * 
            	 *******************************************************/
            	
                if (event.getAction() == MotionEvent.ACTION_DOWN){

                    currentTrialTouchDownTimeStamp = System.currentTimeMillis(); // Record the time stamp of touch down

                	touchDownX = event.getX();   // Record the Touch Down x- coordinate Location
                    touchDownY = event.getY();   // Record the Touch Down y- coordinate Location
                    pressure = event.getPressure();                    
                    attempt++;  // increment the # of attempts

                    // Set all the Time Stamps                  
                    currentTrialTouchDownTimeTaken = currentTrialTouchDownTimeStamp - startTime;  // Calculate the current trial touch down time
                    finalTrialTouchDownTimeStamp = currentTrialTouchDownTimeStamp;
                    finalTrialTouchDownTimeTaken = currentTrialTouchDownTimeTaken;
                    		
                    		
                    // If selection is inside the target area, i.e., a successful target selection, Then:
                    // Calculate the total time taken for touch down selection,
                    
                    if(attempt == 1){
                        firstTrialTouchDownTimeTaken = currentTrialTouchDownTimeTaken;  // 1 attempt means totally succeed, so first = final = this time;
                        firstTrialTouchDownTimeStamp = currentTrialTouchDownTimeStamp;
                    }
                }   // End of if (event.getAction() == MotionEvent.ACTION_DOWN)


                // Gather the Lift Up Data
            	/******************************************************
            	 * 
            	 * 
            	 * 
            	 * 
            	 ******************************************************/


                if (event.getAction() == MotionEvent.ACTION_UP){

                    currentTrialLiftUpTimeStamp = System.currentTimeMillis();  // Record the time stamp of lift up

                    // Record the Lift Up Locations
                    liftUpX = event.getX();
                    liftUpY = event.getY();

                    currentTrialLiftUpTimeTaken = currentTrialLiftUpTimeStamp - startTime;  
                    
                    if(attempt == 1){                   	
                        firstTrialLiftUpTimeStamp = currentTrialLiftUpTimeStamp;
                        firstTrialLiftUpTimeTaken = currentTrialLiftUpTimeTaken;

                    }
                     
                    getRelativeLocationfromTarget();
                    calculateErrors();  // Calculate the Error Type


                    /*****
                     * If lift up is inside the target area, Then:
                     * 
                     * 1. Stop the chronometer
                     * 2. Play the Right Audio
                     * 3. Update the time: current time stamp, current time taken, final time stamp, final time taken
                     * 4. Increment the score (if selected in the 1st attempt),
                     * 5. Write the final Trial data in the File
                     * 6. Display the trial # and score # on the screen, Disappear the target, 
                     * 7. Go to the next trial, or next block, or finish the task
                     * 
                     */
    
                    //  If liftUpX and liftUpY are inside the target, that is a successful target selection:
                    
                    if(isSelectionInsideTarget(liftUpX, liftUpY)){

                        chronoMeter.stop();   // Stop the timer
                        sp.play(rightSound,1,1,0,0,1); //play the right audio

                        touchDownAll += (finalTrialTouchDownTimeTaken/attempt) ; //add up all the average final time for each trial together, be used in the next activity to show the feedback of time to the participants
  
                        // if selected in the 1st sub-movement, then successful selection in the 1st attempt

                        if(attempt == 1){
                            select = 1;   // For a successful selection, select is true
                            score++;      // Increment the Score

                        }

                        // Update the Screen Properties
                        
                        imgRedCircle.setImageResource(R.drawable.white_background);  // Dissolve the Target
                        displayTrial.setText("Score: " + score + "/" + trial);   // Display the Trial and Score
 
                        finalTrialLiftUpTimeStamp = currentTrialLiftUpTimeStamp;
                        finalTrialLiftUpTimeTaken = currentTrialLiftUpTimeTaken;  // Calculate total time taken for target selection
                        liftUpAll += finalTrialLiftUpTimeTaken/attempt ;


                        writeTouchLocationDataInternal();  // Write the touch location (here target selection) data
                        writeTouchLocationDataExternal();  // Write the touch location (here target selection) data

                        // FINAL DATA OR FIRST DATA??????
                        
                        writeFinalDataInternal();  // Write the final trial data after selection on the "TwoDFittsData.csv" file
                        writeFinalDataExternal();  // Write the final trial data after selection on the "TwoDFittsData.csv" file
                        doAfterTargetSelection(event);  // Do a series of Tasks after the target selection

                    }

                    // After an unsuccessful target selection
                    else{

                        sp.play(wrongSound,1,1,0,0,1); //play the wrong audio
                        writeTouchLocationDataInternal();  // Write the touch location (here unsuccessful target selection) data
                        writeTouchLocationDataExternal();  // Write the touch location (here unsuccessful target selection) data

                    }
                    
                    
                }
                return true;
            }
        });
    }

   // Calculate Target Re-Entry
    
    public void calculateTRE(double corX, double corY) {
    
        
        CurrentX = corX;
        CurrentY = corY;

    	// if last position is outside, and current position is inside, entry ++
    	if(isSelectionInsideTarget(CurrentX, CurrentY)){

    		if(isSelectionInsideTarget(lastX,lastY)) {  // initially lastX and lastY is 0

    		}
    		else{
    			entry++;
    			//sp.play(right, 1, 1, 0, 0, 1); //play the right sound, used in debuging
    		}
    	}
   
        lastX = CurrentX;  // substitute last X
        lastY = CurrentY;  // substitute last Y


        // REQUIRED FOR TRE FROM ACTION_DOWN:
        
        entry = reEntry = 0;
        
        // REQUIRED FOR TRE FROM ACTION_UP:

        
        if(entry>0){
        reEntry = entry-1;}
        else{reEntry=0;}

        if(attempt == 1){

            firstreEntry = reEntry;}   // if entry=1,reEntry=0, means succeed
                                    // if entry =0, reEntry=-1, means unsuccessful, even didn't touch the boundary
                                    // if reEntry>0, can add it to the reEntryTotal, which records the total reEntry in all attempts for this trial

        /////////////////////// TRE END //////
        
        TRE = (float)firstreEntry/attempt; // TRE is the average reEntry times for each trial

       
    }   // END OF calculateTRE(double corX, double corY) 
    

    // Initialize the Trial properties

    public void initializeTrial() {
    
    	select = attempt = entry = reEntry = 0;      // Set everything back to 0
    	TRE = 0;  // Set TRE back to 0
    
    	trial++;      // Increase the trial number
    
    	chronoMeter.start();    // Start the timer
    	startTime = System.currentTimeMillis();  // start counting the time
    	chronoMeter.setBase(SystemClock.elapsedRealtime()+ startChronometer); // set the chrono meter from 0
    
    	ongoingTrial = true;   // Indicates an ongoing Trial
    
    }
    
    // Calculate ALL Types of Errors
    
    public void calculateErrors(){
    	
        error = 0;
        SlipError = NarrowSlipError = ModerateSlipError = LargeSlipError = VeryLargeSlipError = 0;
        MissError = NearMissError = NotSoNearMissError = OtherError = AccidentalTap = 0;
        AccidentalHit = 0;
        

    	
    	// if LiftUpX and LiftUpY inside the target, then it is not an Error
        // Set all the error = 0;
    	
        if(isSelectionInsideTarget(liftUpX, liftUpY)){
           
            error = 0;
            SlipError = NarrowSlipError = ModerateSlipError = LargeSlipError = VeryLargeSlipError = 0;
            MissError = NearMissError = NotSoNearMissError = OtherError = AccidentalTap = 0;
        }
        
        
        // Otherwise, it is an Error        
        // Determine if the Error is a Slip Error or a Miss Error
        
        else{
        	
        	error = 1;   // If selection outside the target, then it is an error
        	
        	// Determine if the Error is a Slip Error
            // If TouchDown inside the target, then it is a Slip Error
        	
            if(isSelectionInsideTarget(touchDownX,touchDownY)){
            	
            	SlipError = 1;           
                
        		// Determine the Sub-Category of the Slip Error
                
                if (isSelectionInsideTarget(liftUpX,liftUpY,0.5, 0.75))
                    NarrowSlipError = 1;
                 
                else if(isSelectionInsideTarget(liftUpX,liftUpY, 0.75, 1))
                	ModerateSlipError = 1;
                
                else if(isSelectionInsideTarget(liftUpX,liftUpY,1, 1.5))
                	LargeSlipError = 1;
               
                else
                	VeryLargeSlipError = 1;

            }  // END oF if(isSelectionInsideTarget(touchDownX,touchDownY)) i.e., Slip Error
              	
            // Determine if the Error is a Miss Error
            // If TouchDown is NOT inside the target, then it is a Miss Error
            
            else{
            	
                MissError = 1;
                
                // Determine the Sub-Category of the Miss Error
                
                if(isSelectionInsideTarget(liftUpX,liftUpY, 0.5, 0.75))
                	NearMissError = 1;
                
                else if (isSelectionInsideTarget(liftUpX,liftUpY, 0.75, 1))
                	NotSoNearMissError = 1;
                
                else if(isSelectionInsideTarget(liftUpX,liftUpY, 1, 1.5))
                	OtherError =1;
                
                else
                	AccidentalTap = 1;
            	
            }  // END OF ELSE For: if(isSelectionInsideTarget(touchDownX,touchDownY)), i.e., Miss Error
 
        }
        
        // Determine if Accidental Hit
        // When touch down is far away, but it succeed, we call it an accidental hit
        
        if(!(isSelectionInsideTarget(touchDownX,touchDownY)) && (isSelectionInsideTarget(liftUpX,liftUpY))){
            AccidentalHit = 1;
        }

      
        if(attempt == 1){
        	
        	
            // error 1 means the error for the first attempt, which we focus on;
            // error means the error of every trial, we might analyze them if needed
            // and so on
        	
            error1 = error;
            SlipError1 =SlipError;
            NarrowSlipError1 = NarrowSlipError;
            ModerateSlipError1 = ModerateSlipError;
            LargeSlipError1 = LargeSlipError;
            VeryLargeSlipError1 = VeryLargeSlipError;
            MissError1 =MissError;
            NearMissError1 = NearMissError;
            NotSoNearMissError1 = NotSoNearMissError;
            OtherError1 = OtherError;
            AccidentalTap1 =AccidentalTap;
            AccidentalHit1 =AccidentalHit;
       

        }
     

 	
    }
    
    // GET THE Relative Touch Down and Lift Up Location from the Target
    
    public void getRelativeLocationfromTarget(){
        
    	RelativeLiftUpXfromTarget = liftUpX - targetX;
    	RelativeLiftUpYfromTarget = liftUpY - targetY;
    	RelativeLiftUpXfromStart = liftUpX - startX;
    	RelativeLiftUpYfromStart = liftUpY - startY;
    	RelativeTouchDownXfromTarget = touchDownX - targetX;
    	RelativeTouchDownYfromTarget = touchDownY - targetY;

    	FinalTouchDownFromTarget = distance(touchDownX,touchDownY,targetX,targetY);
    	FinalLiftUpFromTarget = distance(liftUpX,liftUpY,targetX,targetY);
    
    }

    // Check if the touch point (selection) is inside the target, z is the percentage of the target width

    public boolean isSelectionInsideTarget(double x, double y){

        if(distance(x, y, targetX, targetY) <= targetWidth* 0.5)
            return true;

        return false;
    }
    
    // Check if the touch point (selection) is inside the target, z is the percentage of the target width

    public boolean isSelectionInsideTarget(double x, double y, double a, double b){

        if(distance(x, y, targetX, targetY) > targetWidth*a && distance(x, y, targetX, targetY) <= targetWidth*b)
            return true;

        return false;
    }


    // check if the touch point(selection) is outside the target, z as parameter
    public boolean isSelectionOutsideTarget(double x, double y, double z){

        if(distance(x, y, targetX, targetY) > targetWidth*z)
            return true;

        return false;
    }

    // Check the distance between the points (x1, y1) and (x2, y2)

    public double distance(double x1, double y1, double x2, double y2){

        return Math.pow((x2-x1)*(x2-x1) + (y2-y1) * (y2-y1), 0.5);

    }

    // Initialize Everything in the Layout and Activity

    public void initializeEverything(){


        block = trial = score = select = attempt = 0;
        targetWidth = targetDistance = pressure = targetAngle = 0;
        finalTrialLiftUpTimeStamp = finalTrialTouchDownTimeStamp = firstTrialTouchDownTimeStamp = firstTrialLiftUpTimeStamp =startChronometer = startTime = firstTrialTouchDownTimeTaken = firstTrialLiftUpTimeTaken = finalTrialLiftUpTimeTaken = finalTrialTouchDownTimeTaken = currentTrialTouchDownTimeStamp = currentTrialLiftUpTimeStamp = currentTrialLiftUpTimeTaken = currentTrialTouchDownTimeTaken = 0;
        screenWidth = screenHeight = 0;
        touchDownAll = liftUpAll  =0;
        CurrentX = CurrentY =lastY = lastX =0;
        RelativeLiftUpXfromTarget = RelativeLiftUpYfromTarget = RelativeLiftUpXfromStart = RelativeLiftUpYfromStart = RelativeTouchDownXfromTarget = RelativeTouchDownYfromTarget = FinalTouchDownFromTarget = FinalLiftUpFromTarget = 0;

        error = 0;
        SlipError = NarrowSlipError = ModerateSlipError = LargeSlipError = VeryLargeSlipError = MissError =0;
        MissError = NearMissError = NotSoNearMissError = OtherError = AccidentalTap =0;
        AccidentalHit =0;
        entry = reEntry  = firstreEntry =0;
        TRE = 0;
        
        getPid(); 		// Read the participant id from the "pid.txt" file        
        getGroup();

        // Linking with variables of the xml layout page

        frame = (FrameLayout) findViewById(R.id.frameLayout);
        relLayout = (RelativeLayout) findViewById(R.id.relLayoutTwoD);
        btnStart = (ImageButton) findViewById(R.id.btnStart); // setting up the Login button for start
        displayTrial = (TextView) findViewById(R.id.txtTrialNum);
        chronoMeter = (Chronometer) findViewById(R.id.chronoMeter); //chronometer

        // For playing the audio resources
        sp = new SoundPool(10, AudioManager.STREAM_ALARM,5);  //SoundPool(int maxStreams, int streamType, int srcQuality)
        wrongSound = sp.load(this,R.raw.wrong,1);  //load(String path, int priority)
        rightSound= sp.load(this,R.raw.right,1);

        createInitialTargetView();  // Create the initial View to draw the Target

        // Build and shuffle the Target Array
        targetArray = new TaskConditionsArray(targetAngles.length * targetDistances.length * targetWidths.length);
        targetArray.populateArray(targetAngles, targetDistances, targetWidths);  //Populate the target Array with given angles, distances and widths
        targetArray.shuffleArray();   // Shuffle the targetArray

        // Get all the necessary data before starting activity
        getBlock();  	// Read the block number from the "block.txt" file

        // set max_trial in different block
        if(block == 0){
            
            maxTrial = trialBlock_maxTrial;
        }
        else{
            
            maxTrial = fullBlock_maxTrial;
        }


        // For Sending Email
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    public void getGroup(){
    	
    
    	//getPid(); 		// Read the participant id from the "pid.txt" file
    	
    	if(Integer.parseInt(pid) > 100 && (Integer.parseInt(pid) < 200)){    		
    		group = 1;
    	}
    	
    	if(Integer.parseInt(pid) > 200 && (Integer.parseInt(pid) < 300)){    		
    		group = 2;
    	}
    }
    // Calculates the Width and Height of the target drawable area

    public void calculateScreenProperties(){

        screenWidth = relLayout.getRight() - relLayout.getLeft();
        screenHeight = relLayout.getBottom() - relLayout.getTop();

    }


    // Create the initial View of the Target with a White Background Image

    public void createInitialTargetView(){

        // add a new element to the FrameLayout

        imgRedCircle = new ImageView(this);
        imgRedCircle.setImageResource(R.drawable.white_background);
        frame.addView(imgRedCircle);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 0;
        lp.topMargin = 0;
        imgRedCircle.setLayoutParams(lp);

    }

    // Calculates the center coordinate of the Start Button

    public void calculateStartCenter(){
        startX = frame.getWidth()/2;
        startY = frame.getHeight()/2;
    }

    // Draw a target with given angle, distance and width

    public void drawTarget(){

        fetchTargetLocationData();  // Fetch the Target Location Data: Angle, Distance and Width
        calculateTargetCenter(targetAngle, targetDistance);  // Calculate the coordinate to load the target

        // Draw the Target on the Screen at a specified location

        Bitmap bmp = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);    // Create a bitmap
        canvas = new Canvas(bmp);    	         // create the canvas
        imgRedCircle.setImageBitmap(bmp);		 // associate imgRedCircle View with the bitmap
        paint = new Paint();					 // Initialize paint
        paint.setColor(Color.RED);				 // Set the color of the target
        canvas.drawCircle( (float) targetX,  (float) targetY, (float) targetWidth/2, paint);   // Draw the target on the canvas given the coordinates of the target center, target radius, and the color

    }

    // Calculate the Coordinates of the Target Centre


    public void calculateTargetCenter(double angl, double d){

        targetX = Math.cos(angl * Math.PI / 180) * d + startX;
        targetY = (Math.sin(angl * Math.PI / 180) * d + (startY) * -1) * -1 ;  	// Multiply with -1 as y coordinates increases in a different direction

        //Toast.makeText(TwoDFittsTask.this, "Start Coordinates: " + (int) startX + ", " + (int) startY, Toast.LENGTH_SHORT).show();

        //Toast.makeText(TwoDFittsTask.this, "Target Coordinates: " + (int) targetX + ", " + (int) targetY, Toast.LENGTH_SHORT).show();


    } // End of calculateTargetCenter


    // Fetch the Target Location Data: Angle and Distance

    public void fetchTargetLocationData(){

        targetAngle = targetArray.targetProperties[trial].angle;
        targetDistance = targetArray.targetProperties[trial].distance;
        targetWidth = targetArray.targetProperties[trial].width;

        //Toast.makeText(TwoDFittsTask.this, "Target Array[trial]: " + targetAngle + ", " + targetDistance + ", " + targetWidth , Toast.LENGTH_SHORT).show();

    }  // End of fetchTargetLocationData()


    // Write the Final Trial data on selection of the target in the "TwoDFittsData.csv" file

    public void writeFinalDataInternal(){

        try{

            FileOutputStream file = openFileOutput( "PId_" + pid + "_TwoDFittsData_Internal.csv", Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {
                out.write(group+","+pid + "," + block + "," + trial + "," +   targetDistance + ","+ targetWidth + ","  + targetAngle  + "," + select + "," + attempt + "," + error1+","+SlipError1+","+ NarrowSlipError1+","+ModerateSlipError1+","+LargeSlipError1+","+VeryLargeSlipError1+","+MissError1+","+NearMissError1+","+NotSoNearMissError1+","+OtherError1+","+AccidentalTap1+","+AccidentalHit1 + ","+pressure + "," + touchDownX + "," + touchDownY + "," + liftUpX + "," + liftUpY + "," + firstTrialTouchDownTimeStamp+","+ firstTrialTouchDownTimeTaken + ","+ firstTrialLiftUpTimeStamp+"," + firstTrialLiftUpTimeTaken + ","+finalTrialTouchDownTimeStamp +","+ finalTrialTouchDownTimeTaken + "," + finalTrialLiftUpTimeStamp +","+ finalTrialLiftUpTimeTaken+ "," +startTime +","+ firstreEntry +","+TRE );
                out.write('\n');
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }   // End of writeFinalData()



// Write the Final Trial data on selection of the target in the "TwoDFittsData.csv" file

    public void writeFinalDataExternal(){


        String fileName = "PId_" + pid + "_TwoDFittsData_External.csv";

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){ // If the External Storage is Mounted, then write on the file

            File Dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MotionTracAppFile");

            if(!Dir.exists()){    // If the Directory does not exist, make the directory

                Dir.mkdir();
            }

            File file = new File(Dir, fileName);

            try{

                BufferedWriter out = new BufferedWriter(new FileWriter(file, true));   //  FileWriter(file, true ) appends on the file

                out.write(group+","+pid + "," + block + "," + trial + "," +   targetDistance + ","+ targetWidth + ","  + targetAngle  + "," + select + "," + attempt + ","+error1+","+SlipError1+","+ NarrowSlipError1+","+ModerateSlipError1+","+LargeSlipError1+","+VeryLargeSlipError1+","+MissError1+","+NearMissError1+","+NotSoNearMissError1+","+OtherError1+","+AccidentalTap1+","+AccidentalHit1 + ","+ pressure + "," + touchDownX + "," + touchDownY + "," + liftUpX + "," + liftUpY + "," + firstTrialTouchDownTimeStamp+","+ firstTrialTouchDownTimeTaken + ","+ firstTrialLiftUpTimeStamp+"," + firstTrialLiftUpTimeTaken + ","+ finalTrialTouchDownTimeStamp + ","+ finalTrialTouchDownTimeTaken + "," + finalTrialLiftUpTimeStamp +","+ finalTrialLiftUpTimeTaken+ ","+startTime +","+firstreEntry +"," + TRE);
                out.write('\n');
                out.close();


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }    // End of if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))

        else{  // Otherwise, Toast a message
            Toast.makeText(TwoDFittsTask.this,"SD card Not Found",Toast.LENGTH_LONG).show(); // displays a message

        }

    }    // End of writeFinalDataExternal()


    // Write the data corresponding to unsuccessful Touch Location (unsuccessful target selection)

    public void writeTouchLocationDataInternal(){

        try{

            FileOutputStream file = openFileOutput( "PId_" + pid +  "_2D_Fitts_Detailed_Trial_Data_Internal.csv", Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {
               out.write(group+","+pid + "," + block + "," + trial + "," +   targetDistance + ","+ targetWidth + ","  + targetAngle + "," + attempt + ","+error+","+SlipError+","+ NarrowSlipError+","+ModerateSlipError+","+LargeSlipError+","+VeryLargeSlipError+","+MissError+","+NearMissError+","+NotSoNearMissError+","+OtherError+","+AccidentalTap+","+AccidentalHit +","+pressure + ","  + targetX+ ","  +targetY + ","  + touchDownX + ","  + RelativeTouchDownXfromTarget + ","  + touchDownY + ","  + RelativeTouchDownYfromTarget + ","  +liftUpX + ","  + RelativeLiftUpXfromStart + ","  + RelativeLiftUpXfromTarget + ","  + liftUpY + ","  + RelativeLiftUpYfromStart + ","  + RelativeLiftUpYfromTarget + ","  +FinalTouchDownFromTarget+ ","  +FinalLiftUpFromTarget+ ","  + currentTrialTouchDownTimeTaken + "," + currentTrialLiftUpTimeTaken + "," + currentTrialTouchDownTimeStamp + "," + currentTrialLiftUpTimeStamp+ "," + startTime + ","+ startX +"," + startY +"," + reEntry  );
                out.write('\n');
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e){

            e.printStackTrace();

        }

    }   // End of writeTouchLocationDataInternal

    // Write the data corresponding to unsuccessful Touch Location (unsuccessful target selection)

    public void writeTouchLocationDataExternal(){

        String fileName =  "PId_" + pid +  "_2D_FittsDetailedTrialData_External.csv";

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){   // If the External Storage is Mounted, then write on the file

            File Dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MotionTracAppFile");

            if(!Dir.exists())		// If the Directory does not exist, make the directory
                Dir.mkdir();

            File file = new File(Dir, fileName);

            try{

                BufferedWriter	out = new BufferedWriter(new FileWriter(file, true));   //  FileWriter(file, true ) appends on the file

                out.write(group+","+pid + "," + block + "," + trial + "," +   targetDistance + ","+ targetWidth + ","  + targetAngle  + ","  + attempt + ","+error+","+SlipError+","+ NarrowSlipError+","+ModerateSlipError+","+LargeSlipError+","+VeryLargeSlipError+","+MissError+","+NearMissError+","+NotSoNearMissError+","+OtherError+","+AccidentalTap+","+AccidentalHit +","+pressure + ","  + targetX+ ","  +targetY + ","  + touchDownX + ","  + RelativeTouchDownXfromTarget + ","  + touchDownY + ","  + RelativeTouchDownYfromTarget + ","  +liftUpX + ","  + RelativeLiftUpXfromStart + ","  + RelativeLiftUpXfromTarget + ","  + liftUpY + ","  + RelativeLiftUpYfromStart + ","  + RelativeLiftUpYfromTarget + ","  +FinalTouchDownFromTarget+ ","  +FinalLiftUpFromTarget+ ","  + currentTrialTouchDownTimeTaken + "," + currentTrialLiftUpTimeTaken + "," + currentTrialTouchDownTimeStamp + "," + currentTrialLiftUpTimeStamp+ "," + startTime + ","+ startX +"," + startY + "," + reEntry );
                out.write('\n');
                out.close();


            } catch (IOException e) {
                e.printStackTrace();
            }


        }    // END of if(Environment.MEDIA_MOUNTED.equals(state))

        else{  // Otherwise, Toast a message
            Toast.makeText(TwoDFittsTask.this,"SD card Not Found",Toast.LENGTH_LONG).show(); // displays a message
        }

    }   // End of writeTouchLocationDataExternal()


    // Reset the Block at 0

    public void resetBlock(){

        try{

            FileOutputStream file = openFileOutput( "block.txt", Context.MODE_PRIVATE | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {
                out.write("-1");
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }  // End of resetBlock()


    // Choose one: 1. Start the Next Trial 2. Go to the Next Block 3. Email the Results

    public void doAfterTargetSelection(MotionEvent event){

        // Choice 1: Start the Next Trial
        //If more Trials left: Enable the Start Button

        if(trial < maxTrial){
            // Initialize everything to start a new trial
            btnStart.setEnabled(true);
            btnStart.setImageResource(R.drawable.start_button_blue);
            ongoingTrial = false;

            // Setting everything back to 0
            targetX = targetY = touchDownX = touchDownY = liftUpX = liftUpY = 0;  // Set everything back to 0
            firstTrialTouchDownTimeTaken = firstTrialLiftUpTimeTaken = finalTrialTouchDownTimeTaken  = finalTrialLiftUpTimeTaken = currentTrialTouchDownTimeTaken  = currentTrialLiftUpTimeTaken = 0;;   // Set everything back to 0
            firstTrialTouchDownTimeStamp = firstTrialLiftUpTimeStamp = finalTrialTouchDownTimeStamp = finalTrialLiftUpTimeStamp = currentTrialTouchDownTimeStamp = currentTrialLiftUpTimeStamp = 0; 
        }

        // Choice 2: Go to the Next Block
        // No More Trials Left: Write the score in the score.txt file, then
        //If more blocks left, then go to the Next Block Screen, Otherwise Exit

        else{

            // Write the score in the score.txt file
            try{

                FileOutputStream file = openFileOutput( "score.txt", Context.MODE_PRIVATE | Context.MODE_WORLD_READABLE);
                OutputStreamWriter out = new OutputStreamWriter(file);

                try {

                    out.write(score + "/" + trial);
                    out.close();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e){

                e.printStackTrace();

            }


            // If More Block left: Go to the Next Block Screen

            if(block < maxBlock){
                startActivity(new Intent(TwoDFittsTask.this, NextBlockTwoD.class)); // Go to the Next Block Screen

            }

            // Choice 3: Email the Results, Go to the Login Screen
            // Otherwise, Reset the block to 0, Email Result, and Go to the Login Screen

            else{
                resetBlock();  // Reset the block to 0
                startActivity(new Intent(TwoDFittsTask.this, LastScreen.class)); // Go to the Next Block for 2-D Task sScreen
               
                /***********************************
                 * 
                 * FIX THE EMAIL PROBLEM
                 * 
                 ***********************************/

                //emailResult();  
            }
        }
    }   // ENd of doAfterTargetSelection(MotionEvent event)



    // Email ALL the trial data to the Researchers
    
    /***********************************
     * 
     * FIX THE EMAIL PROBLEM
     * 
     ***********************************/

   /*
    public void emailResult(){

        Mail email = new Mail("actlab.targetselection", "FittsTask3661", "2-D Fitts Task PId " + pid, "Results of 2-D Fitts Task from PId " + pid + ".");

        // Attach a file

        try {

            email.addAttachment(getFilesDir().getAbsolutePath() + "/PId_" + pid +  "_2D_Fitts_Detailed_Trial_Data_Internal.csv");
            email.addAttachment(getFilesDir().getAbsolutePath() + "/PId_" + pid + "_TwoDFittsData_Internal.csv");

        } catch (Exception e1) {
            e1.printStackTrace();
            Toast.makeText(TwoDFittsTask.this,"Cannot Attach a File ... ",Toast.LENGTH_LONG).show(); // displays a message
        }

        try {
            if(email.send())
                Toast.makeText(TwoDFittsTask.this,"Sending Results ... ",Toast.LENGTH_LONG).show(); // displays a message

            else
                Toast.makeText(TwoDFittsTask.this,"No Luck ... ",Toast.LENGTH_LONG).show(); // displays a message

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(TwoDFittsTask.this,"Cannot Send Results ... ",Toast.LENGTH_LONG).show(); // displays a message
        }

    }   // END of emailResult()
    
    */


// Reading the participant id (pid) from the "pid.txt" file

    public void getPid(){


        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("pid.txt")));
            String inputString;
            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append(inputReader.readLine());
            pid = stringBuffer.toString();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }   // End of getPid()

    // Reading the block number from the "block.txt" file

    public void getBlock(){


        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("block.txt")));
            String inputString;
            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append(inputReader.readLine());
            block = Integer.parseInt(stringBuffer.toString());


        } catch (IOException e) {
            e.printStackTrace();
        }

    }     // ENd of getBlock()

}
