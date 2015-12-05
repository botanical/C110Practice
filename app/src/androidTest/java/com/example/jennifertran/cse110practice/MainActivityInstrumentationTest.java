package com.example.jennifertran.cse110practice;

/**
 * Created by Jennifer on 12/3/2015.
 */
// MainActivityInstrumentationTest.java

import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

// Tests for MainActivity
public class MainActivityInstrumentationTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    public MainActivityInstrumentationTest() {
        super(LoginActivity.class);
    }

    // Preferred JUnit 4 mechanism of specifying the activity to be launched before each test

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }


    // Looks for an EditText with id = "R.id.etInput"
    // Types the text "Hello" into the EditText
    // Verifies the EditText has text "Hello"
    public void testValidateEditText() {
        onView(withId(R.id.login_page_username))
                .perform(typeText("a"));
        onView(withId(R.id.login_page_password))
                .perform(typeText("a"));
        onView(withId(R.id.login_page_login_button))
                .perform(click());    }
}
