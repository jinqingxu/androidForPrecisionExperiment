package com.example.precisionexperimentapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
//import android.support.v7.app.AppCompatActivity;
import android.provider.DocumentsContract;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.common.api.GoogleApiClient;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * Created by carmen on 16-11-03.
 */


public class TwoDCalibTask extends Activity  {
    String ipAddress="142.157.179.219"; //the ip address of the server
    double firstSuccessTouchTimeStamp,firstSuccessLiftUpTimeStamp;
    boolean restart=false;
    int experimentTime=0;
    double sumx=0.0;
    double sumy=0.0;
    int j=0;
    ImageView imgRedCross;        // Displays the Target
    FrameLayout frame;        // The Total Screen Space
    LinearLayout linLayout;   // Touchable Screen Space
    int block,trial=0;
    private SoundPool sp;  // plays audio resources
    private int right, wrong, count ; // audio resources
    private MediaPlayer mp = new MediaPlayer();

    // int streamID; // to stop the soundpool
    double pressure = 0;
    double length = 2500; // the half length of the cross

    long touchDownTimeStamp, liftUpTimeStamp;
    double touchDownX, touchDownY, liftUpX, liftUpY, targetX, targetY,RelativeLiftUpXfromTarget,RelativeLiftUpYfromTarget,RelativeTouchDownXfromTarget,RelativeTouchDownYfromTarget;
    int select = 0;
    int i=0;

    //double [] targetWidths = {9.2, 7.2, 4.8};
    //double [] targetWidths = {174, 136, 91};   // Nexus: mm to px in xxhdpi
    double[] targetWidths = {270, 378};   // Nexus: mm to px in xxhdpi - Take only The Smallest Target
    //double [] targetWidths = {110, 86, 58};   // Samsung: mm to px in xxhdpi
    //double [] targetWidths = {100, 100, 100};   // TEST
    double[][] targetCor = {{0,0},{0,-1},{0,1},{-1,0},{1,0}};


    String pid;

    int screenWidth;
    int screenHeight;

    Canvas canvas;
    Paint paint;
    private static final Object TAG = new Object(); //used for volley network request
    long diffTimestamp=0; // represent the difference between the timestamp
    long beginClock=0; // record the begin time of the request
    long endClock=0; // record the end time of the request



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_d_calib_task);

        initializeEverything();    // Initialize all the necessary variables and prepare the screen
        drawTarget();   // Draw the target on the screen


        // Tap on the Red Circle: Successful Target Selection
        imgRedCross.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {  //A pressed gesture has started, the motion contains the initial starting location.

                    // Get the screenWidth and screenHeight of the Touchable Area of the screen
                    screenWidth = linLayout.getRight() - linLayout.getLeft();
                    screenHeight = linLayout.getBottom() - linLayout.getTop();

                    // Calculate the targetX and targetY

                    /*targetX = screenWidth / 2;
                    targetY = screenHeight / 2;
                    // target position
                    // todo
                    if (i < 3) {
                        targetY += targetCor[i][1] * targetWidths[0];
                    } else {
                        targetX += targetCor[i][0] * targetWidths[1];
                    }*/

                    touchDownX = motionEvent.getX();   // Record the Touch Down x- coordinate Location
                    touchDownY = motionEvent.getY();   // Record the Touch Down y- coordinate Location

                    pressure = motionEvent.getPressure();
                    touchDownTimeStamp = System.currentTimeMillis();
                    // Record the time stamp of touch down


                    // when the touchDown point is located inside the specific cross, begins to count from 1 to 3
                    if (isSelectionHit(touchDownX, touchDownY)) {
                        //streamID= sp.play(count, 1, 1, 0, 0, 1);  //streamID is used for stopping the count tone
                        try {
                            //mp.setDataSource("/Users/carmen/Documents/CarmenGoing/FFittsAndroidApp/app/src/main/res/raw/count");

                            mp = MediaPlayer.create(TwoDCalibTask.this, R.raw.count);
                            //mp.prepare();
                            mp.start();
                            adjustTimeStamp(); //adjust the timestamp of tablet and laptop to be the same
                            firstSuccessTouchTimeStamp=touchDownTimeStamp;


                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }

                        // when the count tone is done, the calibration duration time has reached, so play the right tone "bling"
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                select = 1;
                                //mp.stop();
                                //mp.release();


                                //sp.play(right, 1, 1, 0, 0, 1);


                            }
                        });


                    } // end of isSelectionHit


                }  // end of touch down
                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    if (!(isSelectionHit(motionEvent.getX(), motionEvent.getY()))) {
                        mp.pause();// pause the count media player


                    }

                }
                // when the finger lifts up
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {  //A pressed gesture has finished, the motion contains the final release location
                    // as well as any intermediate points since the last down or move event.


                    liftUpX = motionEvent.getX();   // Record the Lift Up x- coordinate Location
                    liftUpY = motionEvent.getY();   // Record the Lift Up y- coordinate Location



                    RelativeLiftUpXfromTarget = liftUpX - targetX;
                    RelativeLiftUpYfromTarget = liftUpY - targetY;
                    RelativeTouchDownXfromTarget = touchDownX - targetX;
                    RelativeTouchDownYfromTarget = touchDownY - targetY;

                    liftUpTimeStamp = System.currentTimeMillis();
                    // Record the time stamp of Lift Up
                    firstSuccessLiftUpTimeStamp=liftUpTimeStamp;
                    // after selecting the center and then lifting up, we record the timestamp
                    // if the participant fails to select the center and then lift up, we can not record the tiemstamp
                    // the duration must be above 1s, otherwise the participant may make a mistake of lift up too soon
                    if (firstSuccessTouchTimeStamp>0.0&&firstSuccessLiftUpTimeStamp-firstSuccessTouchTimeStamp>1000) {
                        writeTimeStampFile();
                    }
                    // when lifts up, if not successful maintains three seconds, the count tone would stop and play the wrong tone instead
                    if (select == 0) {
                        mp.pause();
                        sp.play(wrong, 1, 1, 0, 0, 1);
                    } else if (select == 1)  // if successful, start next cross calibration or start 2D task

                    {

                        select = 0;


                        // different i corresponds to different cross locations
                        if (!(i < 100)) {// after 100 times, release the mediaplayer and restart the activity
                            mp.release();
                            startActivity(new Intent(TwoDCalibTask.this, TwoDCalibToTwoDTask.class));
                        } else {
                            doAfterTouch();
                            i++;
                            drawTarget();
                        } //after the target selection, do other things

                    }
                if(restart==true){
                    startActivity(new Intent(TwoDCalibTask.this, LoginScreen.class));
                }
                }  // end of lift up

                return true;
            }  // end of on touch


        });
    } // end of on create

    public void adjustTimeStamp(){
        String url="http://"+ipAddress+":8080/api/getTime";  // the url of the request
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext()); //volley request queue
        // construct a volley network GET request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>(){
            public void onResponse(JSONObject result){ // the response function is asynchronous from the request
                try {
                    endClock=System.currentTimeMillis(); // the end timestamp of receiving respond from the server
                    long rtt=endClock-beginClock; // Round-Trip Time of a network request
                    long delay=Math.round(rtt*0.5); //the delay should be half of the rtt
                    long laptopTime=(long)result.get("time")+delay; //get the current timestamp from laptop. It should add the delay time
                    long systemTime=System.currentTimeMillis(); //get the current timestamp from android system
                    diffTimestamp=laptopTime-systemTime; // get the difference. After that,each time we record timestamp in android,the difference should be added.

                }
                catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
            }
        });
        request.setTag(TAG);
        requestQueue.add(request); // add the request to the queue,then it will be sended
        beginClock=System.currentTimeMillis(); // the begin timestamp of sending network request
    }


    public void writeTimeStampFile(){




        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){ // If the External Storage is Mounted, then write on the file

            File Root = Environment.getExternalStorageDirectory();  // Return the primary shared/external storage directory
            //File Dir = new File(Root.getAbsolutePath() + "/MotionCaptureFile");
            File Dir = new File(Root.getAbsolutePath() + "/MotionTracAppFile/Presion Experiment/");

            if(!Dir.exists()){
                Dir.mkdir();   // Creates the directory named by this abstract pathname if not existed
            }
            String filename="PID_"+pid+"_Precision_Experiment_TimeStamp.csv";
            File file = new File(Dir, filename);


            try{

                BufferedWriter out = new BufferedWriter(new FileWriter(file, true));   //  FileWriter(file, true ) appends on the file
                firstSuccessLiftUpTimeStamp+=diffTimestamp;
                firstSuccessTouchTimeStamp+=diffTimestamp;
                String data=firstSuccessTouchTimeStamp+","+firstSuccessLiftUpTimeStamp;
                out.write(data);
                out.write('\n');
                out.close();
                firstSuccessTouchTimeStamp=0.0;
                firstSuccessLiftUpTimeStamp=0.0;
                experimentTime++;
                if(experimentTime==2) restart=true;


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }    // End of if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))

        else{  // Otherwise, Toast a message
            Toast.makeText(TwoDCalibTask.this,"SD card Not Found",Toast.LENGTH_LONG).show(); // displays a message

        }

    }    // End of writeFinalDataExternal()

    // Do these common series of tasks after a successful or unsuccessful target selection
    private void doAfterTouch() {

            trial++;
            // mark down touchDown liftUp locations and target X Y
            writeDataInternal();  // Write Trial data in the internal "FingerCalibData.csv" file
            writeDataExternal();  // // Write Trial data in the External "FingerCalibData_External.csv" file


        }


    // Write Trial data in the Internal "FingerCalibData.csv" file
    private void writeDataInternal() {

        try {

            //FileOutputStream file = openFileOutput( "PId_" + pid +  "_2D_Fitts_Detailed_Trial_Data_Internal.csv", Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
            FileOutputStream file = openFileOutput( "PId_" + pid +  "_FingerCalibData_Internal.csv", Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
            OutputStreamWriter out = new OutputStreamWriter(file);

            try {

                // some data do not include here so " " is used to fill in the blank
                out.write(pid + "," + block + "," + trial + "," +   " " + ","+ " "+ ","  + " " + "," +pressure + ","  + targetX+ ","  +targetY + ","  + touchDownX + ","  + RelativeTouchDownXfromTarget + ","  + touchDownY + ","  + RelativeTouchDownYfromTarget + ","  +liftUpX + ","  + " " + ","  + RelativeLiftUpXfromTarget + ","  + liftUpY + ","  + " " + ","  + RelativeLiftUpYfromTarget + ","  + " "+ ","  +" "+ ","  + " " + "," + " " + "," + touchDownTimeStamp + "," + liftUpTimeStamp+ "," + " " + ","+ " " +"," + " " +"," + " " );
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

        String fileName =  "PId_" + pid +  "_FingerCalibData_External.csv";

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {   // If the External Storage is Mounted, then write on the file

            File Dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MotionTracAppFile");

            if (!Dir.exists()) {  // If the Directory does not exist, make the directory

                Dir.mkdir();
            }

            File file = new File(Dir, fileName);

            try {

                BufferedWriter out = new BufferedWriter(new FileWriter(file, true));  //  FileWriter(file, true ) appends on the file

                // some data do not include here so " " is used to fill in the blank
                out.write(pid + "," + block + "," + trial + "," +" " + ","+ " "+ ","  + " " + "," +pressure + ","  + targetX+ ","  +targetY + ","  + touchDownX + ","  + RelativeTouchDownXfromTarget + ","  + touchDownY + ","  + RelativeTouchDownYfromTarget + ","  +liftUpX + ","   + RelativeLiftUpXfromTarget + ","  + liftUpY + ","    + RelativeLiftUpYfromTarget + ","  + touchDownTimeStamp + "," + liftUpTimeStamp+ "," );
                out.write('\n');
                out.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }   // End of if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))

        else {  // Otherwise, Toast a message
            Toast.makeText(TwoDCalibTask.this, "SD card Not Found", Toast.LENGTH_LONG).show(); // displays a message
        }

    }



    // Check if the touch point (selection) is inside the target
    private boolean isSelectionHit(double X, double Y) {

        if (distance(X, Y, targetX, targetY) <= 15 )
        //if (distance(X, Y, targetX, targetY) <= length/2 )
            return true;

        return false;
    }

    private double distance(double X, double Y, double targetX, double targetY) {

        return Math.pow((targetX - X) * (targetX - X) + (targetY - Y) * (targetY - Y), 0.5);
    }

    // draw the target to touch
    private void drawTarget() {

//        // Get the screenWidth and screenHeight of the Touchable Area of the screen
//        screenWidth = linLayout.getRight() - linLayout.getLeft();
//        screenHeight = linLayout.getBottom() - linLayout.getTop();

        targetX = screenWidth / 2;
        targetY = screenHeight / 2;
        /*
        int width=504;//40mm
        // draw different targets (according to the correspondent target X Y in 2D Fitts Task)
        if(i<3){
            targetY += targetCor[i][1] * targetWidths[0];}
        else{
            targetX += targetCor[i][0] * targetWidths[1];}*/




        //the center point
        /*if(i==0){
            targetX=screenWidth/2;
            targetY=screenHeight/2;
        }
        //some edge points
        if(i==1){
            targetX=screenWidth/2+width;
            targetY=screenHeight/2;
        }
        if(i==2){
            targetX=screenWidth/2;
            targetY=screenHeight/2-width;
        }
        if(i==3){
            targetX=screenWidth/2-width;
            targetY=screenHeight/2;
        }
        if(i==4){
            targetX=screenWidth/2;
            targetY=screenHeight/2+width;
        }
        //the center point
        if(i==5){
            targetX=screenWidth/2;
            targetY=screenHeight/2;
        }*/



        // Draw the Target
        Bitmap bmp = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);    // Create a bitmap
        canvas = new Canvas(bmp);                 // create the canvas
        imgRedCross.setImageBitmap(bmp);         // associate imgRedCross View with the bitmap
        paint = new Paint();                     // Initialize paint
        paint.setColor(Color.RED);                 // Set the color of the target
        paint.setStrokeWidth(10 );
        canvas.drawLine((float) (targetX-length), (float) targetY, (float)(targetX+length), (float) targetY,paint);   // Draw the target on the canvas given the coordinates of the target center, target radius, and the color
        canvas.drawLine((float) targetX, (float) (targetY-length), (float) targetX, (float) (targetY+length),paint);
    }


    // initialize everything
    private void initializeEverything() {

        // Linking with variables of the xml layout page
        frame = (FrameLayout) findViewById(R.id.frameLayoutFinger2DCalib);
        linLayout = (LinearLayout) findViewById(R.id.linLayoutFinger2DCalib);
        // displayTrial = (TextView) findViewById(R.id.txtTrial2DNum);

        createInitialTargetView();

        // Get Screen Resolution
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;    // Screen Width in pixel
        screenHeight = metrics.heightPixels;  // Screen Height in pixel
        int densityDpi = metrics.densityDpi; // 320
        System.out.println(densityDpi);
        select = 0;

        mp = MediaPlayer.create(TwoDCalibTask.this, R.raw.count);

        // For Sending Email
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // For playing the audio resources
        sp = new SoundPool(10, AudioManager.STREAM_ALARM, 5);  //SoundPool(int maxStreams, int streamType, int srcQuality)
        wrong = sp.load(this, R.raw.wrong, 1);  //load(String path, int priority)
        right = sp.load(this, R.raw.right, 1);
        count = sp.load(this, R.raw.count,1);



        getPid();       // Read the participant id (pid) from the "pid.txt" file
    }

    // Create the initial View of the Target with a White Background Image
    private void createInitialTargetView() {

        // add a new element to the FrameLayout
        imgRedCross = new ImageView(this);
        imgRedCross.setImageResource(R.drawable.white_background);  //Sets a drawable as the content of this ImageView.
        frame.addView(imgRedCross);


        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 0;
        lp.topMargin = 0;
        imgRedCross.setLayoutParams(lp);
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



    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.connect();
        /*
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "FingerCalibTask Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.ACTlab.FFittsAndroidApp/http/host/path")
        );
        //AppIndex.AppIndexApi.start(client, viewAction);
         
         */
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "FingerCalibTask Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.ACTlab.FFittsAndroidApp/http/host/path")
        );
        //AppIndex.AppIndexApi.end(client, viewAction);
        //client.disconnect(); */
    }
} // end of the class

