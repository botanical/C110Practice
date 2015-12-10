package com.example.jennifertran.cse110practice;

/**
 * Created by Jennifer on 12/3/2015.
 */
// MainActivityInstrumentationTest.java

import android.support.test.espresso.Espresso;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.onData;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.anything;


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

    /*
     * Given: I am a student with 2 usernames attempting to log in
     * When: I type in the correct credentials "a" with password "a"
     * Then: I am able to proceed past the login activity and see the Subject Navigation
     * When: I click the logout button
     * Then: I will be returned to the login page
     * When: I type in the correct credentials "jim" with password "jimmers"
     * Then: I am able to proceed to see the Subject Navigation with my specific classes
     */
    public void testSucceedLogin(){
        //Given we start in login activity
        //When I type "a" into the username EditText view (when I login as user "a")
        onView(withId(R.id.login_page_username)).perform(typeText("a"));
        //and I type "a" into the password EditText view
        onView(withId(R.id.login_page_password)).perform(typeText("a"));
        //and I click the login button
        onView(withId(R.id.login_page_login_button)).perform(click());

        //Then I am taken to the subject navigation page called "Bear's Subject Navigator!"
        onView(withId(R.id.textView1)).check(matches(withText("Bear's Subject Navigator!")));

        //When I click on the logout button
        onView(withId(R.id.logout)).perform(click());
        //Then I am taken back to the login page
        onView(withId(R.id.login_page_password_text)).check(matches(withText("password")));
        //When I type "a" into the username EditText view (when I login as user "a")
        onView(withId(R.id.login_page_username)).perform(typeText("jim"));
        //and I type "a" into the password EditText view
        onView(withId(R.id.login_page_password)).perform(typeText("jimmers"));
        //and I click the login button
        onView(withId(R.id.login_page_login_button)).perform(click());

        //Then I am taken to the subject navigation page called "Bear's Subject Navigator!"
        onView(withId(R.id.textView1)).check(matches(withText("Bear's Subject Navigator!")));


    }
    /*
    * Given: I am a student attempting to log in
    * When: I type in the incorrect credentials "a" with password "av"
    * Then: I am unable to proceed past the login activity and see the Subject Navigation
    */
    public void testFailToLoginTest(){
        //Given we start in login activity
        onView(withId(R.id.login_page_username)).perform(typeText("a"));
        onView(withId(R.id.login_page_password)).perform(typeText("av"));
        onView(withId(R.id.login_page_login_button)).perform(click());

        //Checking the current activity is difficult in espresso, but luckily we can get the
        //same behavior by checking if the login_page_password_text still exists. If the textview exists,
        //we know that we are still on the login activity.
        onView(withId(R.id.login_page_password_text)).check(matches(withText("password")));
    }
    /*
     * Given: I am an admin attempting to log in
     * When: I type in the correct credentials "b" with password "b"
     * Then: I am able to proceed past the login activity and see the Admin Subject Navigation
     * When: I click the logout button
     * Then: I will be returned to the login page
     */
    public void testLogoutAdmin() {
        //Given we start in login activity
        //When I type "a" into the username EditText view (when I login as user "a")
        onView(withId(R.id.login_page_username)).perform(typeText("b"));
        //and I type "a" into the password EditText view
        onView(withId(R.id.login_page_password)).perform(typeText("b"));
        //and I click the login button
        onView(withId(R.id.login_page_login_button)).perform(click());

        //Then I am taken to the Admin subject navigation page called "Admin's Subject Navigator!"
        onView(withId(R.id.textView1)).check(matches(withText("Admin's Subject Navigator!")));

        //When I click the logout button
        onView(withId(R.id.logout)).perform(click());
        //Then I should be taken to the login page
        onView(withId(R.id.login_page_password_text)).check(matches(withText("password")));


    }
    /*
    * Given: I am a student attempting to log in
    * When: I type in the correct credentials "a" with password "a"
    * Then: I am able to proceed past the login activity and see the Subject Navigation
    * When: I click the first expandable list item (a subject I am enrolled in)
    * Then: I will see the expanded list (the available quizzes)
    */
    public void testExpandList() {
        //Given we start in login activity
        //When I type "a" into the username EditText view (when I login as user "a")
        onView(withId(R.id.login_page_username)).perform(typeText("a"));
        //and I type "a" into the password EditText view
        onView(withId(R.id.login_page_password)).perform(typeText("a"));
        //and I click the login button
        onView(withId(R.id.login_page_login_button)).perform(click());

        //Then I am taken to the subject navigation page called "Bear's Subject Navigator!"
        onView(withId(R.id.textView1)).check(matches(withText("Bear's Subject Navigator!")));

        //When I click on a class item (a subject)
        onData(anything()).inAdapterView(withId(R.id.lvExp)).atPosition(0).perform(click());
        //Then the class item should expand
        onData(anything()).inAdapterView(withId(R.id.lvExp)).atPosition(0).check(matches(isDisplayed()));

    }
    /*
     * Given: I am on the login page
    * When: I click on the register button
    * Then: I will be redirected to the registration page
    * When: I fill in the registration information such as username, password, company
    *       with "testUser", "pass", and "UCSD"
    * Then: I will not be successfully registered because "testUser" is already registered
    */
    public void testRegistrationTaken() {
        //Given we start on the login page
        onView(withId(R.id.login_page_username)).check(matches(withHint("Enter id here")));
        //When I click on the register button
        onView(withId(R.id.login_page_register_button))
                .perform(click());
        //Then I am taken to the registration page which has a editText fields where I
        //can fill in my registration information
        onView(withId(R.id.register_page_username)).check(matches(withHint("Enter id here")));
        //When I type "testUser" into the username EditText view, which is already a registered user
        onView(withId(R.id.register_page_username)).perform(typeText("testUser"));
        onView(withId(R.id.register_page_password)).check(matches(withHint("Enter Password")));
        //and when I type pass for my password
        onView(withId(R.id.register_page_password)).perform(typeText("pass"));
        onView(withId(R.id.register_page_company)).check(matches(withHint("Enter Company")));
        //and when I type UCSD for my company
        onView(withId(R.id.register_page_company)).perform(typeText("UCSD"));
        //and when I click the register button
        onView(withId(R.id.login_page_register_button)).perform(click());
        //then I won't be able successfully registered because the username is already taken
        onView(withId(R.id.register_page_username)).check(matches(withHint("Enter id here")));


    }
}
