package com.example.jennifertran.cse110practice;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;

/**
 * Created by Jennifer on 12/4/2015.
 */

/* Name: MyViewActions
 * Function: Allows clicks without constraints for testing purposes.
 */
public class MyViewActions {
    public static ViewAction click() {
        return new GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER, Press.FINGER);
    }
}
