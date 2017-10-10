package com.example.precisionexperimentapp;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class LeapListener extends Listener{
	
	

	public void onInitialize(Controller controller){
		
		System.out.println("Initialized");
		
	}
	
	public void onConnect(Controller controller){
		
		System.out.println("Connected to Motion Sensor");
		
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
		
	}
	
	public void onDisconnect(Controller controller){
		
		//System.out.println("Motion Sensor Disconnected");
		
	}
	
	public void  onExit(Controller controller){
		
		//System.out.println("Exited");
		
	}
	
	public void onFrame(Controller controller){
		
		Frame frame = controller.frame();
		Hand hand = frame.hands().rightmost();   // Get the right hand
	    Finger index = frame.fingers().fingerType(Finger.Type.TYPE_INDEX).get(0); //(fingerType() returns an array since there could be more than one finger of a particular type in the list, which is why you need the get(0).)

		
	}
	
}
