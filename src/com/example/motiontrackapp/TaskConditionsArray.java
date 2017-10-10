package com.example.precisionexperimentapp;

import java.util.Random;

/**
 * Created by carmen on 16-09-06.
 */

public class TaskConditionsArray {

    TaskConditions targetProperties [] ;
    public TaskConditionsArray(int i) {

        targetProperties = new TaskConditions[i];
    }

    // Populates the Target Array with the given angles, distances and widths

    public void populateArray(int angles[], int distances [], double widths[]){

        int arrayIndex = 0;

        for(int i = 0; i < angles.length; i++){   // all possible angles
            for(int j = 0; j < distances.length; j++){  // all possible distances
                for(int k = 0; k < widths.length; k++){  // all possible widths

                    targetProperties[arrayIndex] = new TaskConditions(angles[i], distances[j], widths[k]);
                    arrayIndex++;

                }
            }

        }

    }   // END OF buildArray(int angles[], int distances [], double widths[]


    // Shuffles the Array

    public void shuffleArray(){

        Random random = new Random();
        int j;
        TaskConditions temp;

        for (int i = targetProperties.length-1; i > 1; i--) {

            // Swaps two elements [i] and [j] of the target array
            j = random.nextInt(i);
            temp = targetProperties[i];
            targetProperties[i] = targetProperties[j];
            targetProperties[j] = temp;


        }
    }   // END OF shuffleArray()

}
