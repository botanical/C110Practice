package com.example.jennifertran.cse110practice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    static CountDownTimer timer;
    int numOfQuestions = 5;
    String[] yourAnswers = new String[numOfQuestions];
    String[] correctAnswers = new String[numOfQuestions];

    List<Question> question_list;
    int[] answerScore = new int[numOfQuestions];
    int score = 0;
    int question_id = 0;
    int answerCheck;
    int prev_qid;
    Question current_question;
    TextView textQuestion;
    View submit, back_button;
    Button next_button;
    RadioButton answer;

    RadioButton rda, rdb, rdc;
    RadioGroup grp;
    DbHelperQuiz db;
    String marked;
    String colName = "marked";
    String qid;
    TextView textViewTime;
    final Animation anim = new AlphaAnimation(1, 0);
    int testTime = 20000; // 30 seconds by default for test

    /* Adding member variables, strings, and booleans for fragments */
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    String open_drawer = "Question Navigation";
    Boolean backClick = false;
    Boolean nextClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        /* Set ListView for Fragment */
        mDrawerList = (ListView)findViewById(R.id.navList);

        /* Add drawer items */
        addDrawerItems();
        setupDrawer();
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        for(int i = 0; i < numOfQuestions; i++)
            yourAnswers[i] = "INCOMPLETE";
        // Set up the database
        db = new DbHelperQuiz(this);

        // for timer stuff
        Intent intentReceived = getIntent();
        textViewTime = (TextView) findViewById(R.id.textViewTimer);

        //Update question number in ActionBar
        qid = "Question: "+String.valueOf(question_id + 1)+"/"+String.valueOf(numOfQuestions);
        getSupportActionBar().setTitle(qid);

        testTime = intentReceived.getIntExtra(StartupPage.EXTRA_TIME, 60);

        question_list = db.getAllQuestions();

        //This code generates an array of strings in which each index corresponds to
        //the correct Answer in the quiz.
        for(int i = 0; i < correctAnswers.length; ++i){
            correctAnswers[i] = question_list.get(i).getANSWER();
        }

        current_question = question_list.get(question_id);
        rda = (RadioButton)findViewById(R.id.radio0);
        rdb = (RadioButton)findViewById(R.id.radio1);
        rdc = (RadioButton)findViewById(R.id.radio2);
        // Set up question on page
        textQuestion = (TextView)findViewById(R.id.textView1);
        next_button = (Button)findViewById(R.id.button_next);
        submit = findViewById(R.id.button_submit);
        submit.setVisibility(View.GONE);
        back_button = findViewById(R.id.button_back);
        back_button.setVisibility(View.GONE);
        grp = (RadioGroup)findViewById(R.id.radioGroup1);



        // Set the questions on the page
        setQuestionView();




        // Call listener to check for next page request
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup grp = (RadioGroup) findViewById(R.id.radioGroup1);

                question_id++;
                qid = "Question: " + String.valueOf(question_id + 1) + "/" + String.valueOf(numOfQuestions);
                getSupportActionBar().setTitle(qid);

                current_question = question_list.get(question_id);
                grp.clearCheck();
                if (question_id == 4) {
                    submit.setVisibility(View.VISIBLE);
                    next_button.setVisibility(View.GONE);
                } else if (question_id != 0) {
                    back_button.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.GONE);
                } else {
                    back_button.setVisibility(View.GONE);
                }
                //Update question number in ActionBar





                    /* Check to see if previous question had radio buttons checked
                     * and resets new question to have no buttons checked.
                     */

                Log.d("QUESTION", String.valueOf(question_id));


                    /* Increments question_id to show new question
                     * and set view accordingly
                     */

                    /* Uncheck all buttons so new page has no checked answer */

                marked = db.getQuestionEntry(colName, question_id);

                //Log.d("THIS", marked);

                setQuestionView();
                if (((rda.getText()).toString()).equals(marked)) {
                    grp.check(R.id.radio0);

                    Log.d("Banana1", rda.getText().toString());
                }
                if (((rdb.getText().toString()).equals(marked))) {
                    grp.check(R.id.radio1);
                    Log.d("Banana420 ", String.valueOf(rdb.isChecked()));
                    Log.d("Banana2", rdb.getText().toString());

                }
                if (((rdc.getText()).toString()).equals(marked)) {

                    grp.check(R.id.radio2);
                    Log.d("Banana3", rdc.getText().toString());

                }

                findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        back();
                    }
                });

                findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RadioGroup grp = (RadioGroup) findViewById(R.id.radioGroup1);
                        // Save the user's answer
                        answer = (RadioButton) findViewById(grp.getCheckedRadioButtonId());

                        for (int i = 0; i < (answerScore.length); i++) {
                            score = answerScore[i] + score;
                        }
                        submit();
                    }
                });
            }


            private void submit() {
                timer.cancel();
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                Bundle b = new Bundle();
                b.putInt("score", score); //Your score
                b.putInt("numOfQuestions", numOfQuestions);

                b.putStringArray("correctAnswers", correctAnswers);
                b.putStringArray("yourAnswers", yourAnswers);

                intent.putExtras(b); //Put your score to your next Intent
                startActivity(intent);
                finish();

            }

            private void back() {
                RadioGroup grp = (RadioGroup) findViewById(R.id.radioGroup1);
                question_id--;
                qid = "Question: "+String.valueOf(question_id + 1)+"/"+String.valueOf(numOfQuestions);
                getSupportActionBar().setTitle(qid);
                Log.d("newqid", String.valueOf(question_id));
                marked = db.getQuestionEntry(colName, question_id);
                Log.d("marked", "---:" + marked + ":---");
                current_question = question_list.get(question_id);
                grp.clearCheck();

                if ((question_id + 1) == 4) {
                    submit.setVisibility(View.GONE);
                    next_button.setVisibility(View.VISIBLE);
                } else if (question_id == 0) {
                    back_button.setVisibility(View.GONE);
                }

                setQuestionView();
                if (((rda.getText()).toString()).equals(marked)) {
                    grp.check(R.id.radio0);

                    Log.d("Checked", rda.getText().toString());

                }
                if (((rdb.getText()).toString()).equals(marked)) {
                    grp.check(R.id.radio1);

                    Log.d("Checked", rdb.getText().toString());

                }
                if (((rdc.getText()).toString()).equals(marked)) {
                    grp.check(R.id.radio2);

                    Log.d("Checked", rdc.getText().toString());

                }

                Log.d("BUTTON1", rda.getText().toString());
                Log.d("BUTTON2", rdb.getText().toString());
                Log.d("BUTTON3", rdc.getText().toString());
            }
        });

        // setting up blinking timer animation
        anim.setDuration(150);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        // Instantiate quiz timer.
        timer = new CountDownTimer(testTime, 1000) {
            public void onTick(long millisUntilFinished) {
                //textViewTime.setText("Time Remaining: " + millisUntilFinished/60000
                //+ ":" + (millisUntilFinished/1000) % 60 );

                String timeText = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                textViewTime.setText(timeText);

                // 1 minute left warning
                if( millisUntilFinished <= 60000 && millisUntilFinished > 57000 ) {
                    Toast.makeText(getApplicationContext(),
                            "1 minute left!",
                            Toast.LENGTH_SHORT).show();
                }

                // 30 seconds left warning
                if( millisUntilFinished <= 30000 && millisUntilFinished > 27000 ) {
                    Toast.makeText(getApplicationContext(),
                            "30 seconds left!",
                            Toast.LENGTH_SHORT).show();
                }

                // 10 seconds left warning
                if( millisUntilFinished <= 10000 && millisUntilFinished > 7000 ) {
                    Toast.makeText(getApplicationContext(),
                            "10 seconds left!",
                            Toast.LENGTH_SHORT).show();
                    textViewTime.setTextColor(Color.RED);
                    textViewTime.startAnimation(anim);
                }
            }


            public void onFinish() {
                textViewTime.setText("Time's up!");
                textViewTime.clearAnimation();
                for (int i = 0; i < (answerScore.length); i++) {
                    score = answerScore[i] + score;
                }
                // submit quiz when time's up; just copied code
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                Bundle b = new Bundle();

                b.putInt("score", score); //Your score
                b.putInt("numOfQuestions", numOfQuestions);

                b.putStringArray("correctAnswers", correctAnswers);
                b.putStringArray("yourAnswers", yourAnswers);
                intent.putExtras(b); //Put your score to your next Intent
                // submit quiz when time's up; just copied code
                intent.putExtras(b); //Put your score to your next Intent
                startActivity(intent);
                finish();
            }
        }.start();

    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        for (int i = 0; i < (answerScore.length); i++) {
            score = answerScore[i] + score;
        }
        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        Bundle b = new Bundle();

        b.putInt("score", score); //Your score
        b.putInt("numOfQuestions", numOfQuestions);

        b.putStringArray("correctAnswers", correctAnswers);
        b.putStringArray("yourAnswers", yourAnswers);

        intent.putExtras(b); //Put your score to your next Intent
        startActivity(intent);
        finish();
    }


    private void setQuestionView()
    {
        textQuestion.setText(current_question.getQUESTION());
        rda.setText(current_question.getOPTA());
        rdb.setText(current_question.getOPTB());
        rdc.setText(current_question.getOPTC());
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        ContentValues values = new ContentValues();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio0:
                yourAnswers[question_id] = rda.getText().toString();
                if (checked)
                    values.put(colName, rda.getText().toString());
                if(current_question.getANSWER().equals(rda.getText().toString()))
                {
                    answerScore[question_id] = 1;
                }
                db.insertIntoDb(String.valueOf(question_id), values);
                Log.d("inserted", rda.getText().toString());
                break;
            case R.id.radio1:
                yourAnswers[question_id] = rdb.getText().toString();
                if (checked)
                    values.put(colName, rdb.getText().toString());
                if(current_question.getANSWER().equals(rdb.getText().toString()))
                {
                    answerScore[question_id] = 1;
                }
                db.insertIntoDb(String.valueOf(question_id), values);
                Log.d("inserted", rdb.getText().toString());

                break;
            case R.id.radio2:
                yourAnswers[question_id] = rdc.getText().toString();
                if (checked)
                    values.put(colName, rdc.getText().toString());
                if(current_question.getANSWER().equals(rdc.getText().toString()))
                {
                    answerScore[question_id] = 1;
                }
                db.insertIntoDb(String.valueOf(question_id), values);
                Log.d("inserted", rdc.getText().toString());

                break;
        }
    }

    /* Helper method called by onCreate to add drawer items to Drawer */
    private void addDrawerItems() {
        String[] osArray = { "Question 1", "Question 2", "Question 3", "Question 4", "Question 5" };
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(QuizActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /* Helper method called by onCreate to set up drawer items to Drawer */
    private void setupDrawer() {

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(open_drawer);
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Question: " + String.valueOf(question_id + 1)
                            + "/" + String.valueOf(numOfQuestions));
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    /* Method is used for Fragment. Syncs the indicator to match current state */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    /* Method is used for Fragment. Makes smooth transitioning for orientation change */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    /* Used for Inflating Activity Bar if Items are present */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_quiz, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
