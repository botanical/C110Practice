package com.example.jennifertran.cse110practice;

/**
 * Created by Jennifer on 12/3/2015.
 */
// MainActivityInstrumentationTest.java

import android.app.Application;
import android.support.test.rule.ActivityTestRule;
import android.test.ApplicationTestCase;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

// Tests for MainActivity
public class MainActivityInstrumentationTest extends ApplicationTestCase<Application> {
    public MainActivityInstrumentationTest() {
        super(Application.class);
    }

    // Preferred JUnit 4 mechanism of specifying the activity to be launched before each test
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    
    // Looks for an EditText with id = "R.id.etInput"
    // Types the text "Hello" into the EditText
    // Verifies the EditText has text "Hello"
    @Test
    public void validateEditText() {
        onView(withId(R.id.login_page_username))
                .perform(typeText("a"));
        onView(withId(R.id.login_page_password))
                .perform(typeText("a"));
        onView(withId(R.id.login_page_login_button))
                .perform(click());    }
}
