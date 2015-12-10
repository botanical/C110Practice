package com.example.jennifertran.cse110practice;

        import android.content.Context;
        import android.support.test.InstrumentationRegistry;
        import android.support.test.rule.ActivityTestRule;
        import android.support.test.runner.AndroidJUnit4;

        import org.hamcrest.Description;
        import org.hamcrest.Matcher;
        import org.junit.Rule;
        import org.junit.Test;
        import org.junit.rules.TestName;
        import org.junit.runner.RunWith;

        import static android.support.test.espresso.Espresso.onView;
        import static android.support.test.espresso.Espresso.*;
        import static android.support.test.espresso.action.ViewActions.click;
        import static android.support.test.espresso.action.ViewActions.typeText;
        import static android.support.test.espresso.assertion.ViewAssertions.matches;
        import static android.support.test.espresso.matcher.ViewMatchers.withChild;
        import static android.support.test.espresso.matcher.ViewMatchers.withId;
        import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AJScenarioTest {
    @Rule
    public ActivityTestRule<LoginActivity> lActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    /*
     * Given: I am a student attempting to log in
     * When: I type in the wrong credentials "a" with password "av"
     * Then: I am unable to proceed past the login activity
     */
    @Test
    public void failToLoginTest(){
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
     * Given: I am a student attempting to log in
     * When: I type in the correct credentials "a" with password "a"
     * Then: I am able to proceed past the login activity to the subject navigation activity.
     * When: I click the logout button in the subject navigation activity
     * Then: I am taken back to the login activity
     * When: I type in the correct credentials "a" with password "a"
     * Then: I am able to proceed past the login activity to the subject navigation activity.
     *
     *
     *
     */

    @Test
    public void succeedLoginTest(){
        //Given we start in login activity
        onView(withId(R.id.login_page_username)).perform(typeText("a"));//When I type "a" into the username EditText view
        onView(withId(R.id.login_page_password)).perform(typeText("a"));//and I type "a" into the password EditText view
        onView(withId(R.id.login_page_login_button)).perform(click()); //and I click the login button

        //Then I am taken to the subject navigation page, which has a (somewhat poorly named)
        //textview called textView1 with text "Bear's Subject Navigator!"
        onView(withId(R.id.textView1)).check(matches(withText("Bear's Subject Navigator!")));

        //When I click on the logout button
        onView(withId(R.id.logout)).perform(click());
        //Then I am taken back to the login page
        onView(withId(R.id.login_page_password_text)).check(matches(withText("password")));

        onView(withId(R.id.login_page_username)).perform(typeText("a"));//When I type "a" into the username EditText view
        onView(withId(R.id.login_page_password)).perform(typeText("a"));//and I type "a" into the password EditText view
        onView(withId(R.id.login_page_login_button)).perform(click()); //and I click the login button

        //Then I am taken to the subject navigation page, which has a (somewhat poorly named)
        //textview called textView1 with text "Bear's Subject Navigator!"
        onView(withId(R.id.textView1)).check(matches(withText("Bear's Subject Navigator!")));

        //onView(withId(R.id.lvExp)), withChild();

    }

}
