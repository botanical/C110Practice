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

import org.json.JSONArray;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    static CountDownTimer timer;
    int numOfQuestions;
    ArrayList<String> yourAnswers;
    ArrayList<String> correctAnswers;

    List<Question> question_list;
    int[] answerScore;
    int score = 0;
    int question_id = 0;
    int answerCheck;
    int prev;
    Question current_question;
    TextView textQuestion;
    View submit, back_button;
    Button next_button;
    RadioButton answer;

    RadioButton rda, rdb, rdc;
    RadioGroup grp;
    DbHelperQuiz db;
    int marked;
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
    String title;
    Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewTime = (TextView) findViewById(R.id.textViewTimer);
        Intent intentReceived = getIntent();
        testTime = intentReceived.getIntExtra(StartupPage.EXTRA_TIME, 60);//TODO add time to Quiz class
        title = intentReceived.getStringExtra("title");

        /* Get list of columns from previous activity where the quiz was updated */
        String colsString  = intentReceived.getStringExtra("columns");
        ArrayList<String> cols = new ArrayList<>();
        try{
            JSONArray columns = new JSONArray(colsString);
            for(int i = 0; i < columns.length(); i++)
                cols.add(columns.getString(i));
        }catch(Exception e){
            e.printStackTrace();
        }
        /*******************************  Initialize Quiz Object ****************************/

        db =  new DbHelperQuiz(this,title,cols);
        quiz = new Quiz(title, db.getQuestionsAsQuestionArray(), db.rowcount());
        question_list = quiz.getQuestions();
        correctAnswers = quiz.getAnswers(); //Ex. Answer to question 1 = correctAnswers.get(0);
        numOfQuestions = quiz.getNumQuestions();
        current_question = question_list.get(question_id);
        answerScore = new int[numOfQuestions];

        /*^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  Initialize Quiz Object ^^^^^^^^^^^^^^^^^^^^^^^^^^^^*/

        yourAnswers = new ArrayList<>();
        for(int i = 0; i < numOfQuestions; i++)
            yourAnswers.add("INCOMPLETE");

        /***************************** Initialize Radio Buttons *****************************/

        grp = (RadioGroup)findViewById(R.id.radioGroup1);
        grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton r = (RadioButton) findViewById(checkedId);
                if( r == null)
                    return;

                System.out.println("RRRRRR " +r);
                System.out.println(checkedId);
                System.out.println(current_question);
                current_question.setMarked(checkedId);
                yourAnswers.set(question_id, r.getText().toString());
                if(current_question.getAnswer().equals(r.getText().toString()))
                {
                    answerScore[question_id] = 1;
                }
                else
                    answerScore[question_id] = 0;
            }
        });
        for(Question q : question_list)
        {
            ArrayList<RadioButton> btns = new ArrayList<>();
            ArrayList<String> opts = q.getOptions();
            for(int i = 0; i < opts.size(); i++)
            {
                RadioButton b = new RadioButton(this);
                b.setText(opts.get(i));
                b.setId(View.generateViewId()); //Generate id for the radioButton
                btns.add(b);
            }
            q.setRadioButtons(btns);
        }

        /* Set Radio Buttons for first page */
        System.out.println("RADIO BTNS: "+question_list.get(0).getRadioButtons());
        for(RadioButton r : question_list.get(0).getRadioButtons())
                grp.addView(r);

        /*^^^^^^^^^^^^^^^^^^^^^^^^^^^ Initialize Radio Buttons ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^*/

        /******************************** Create Hamburger  *********************************/
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.navList);  /* Set ListView for Fragment */

        addDrawerItems();         /* Add drawer items */
        setupDrawer();
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        /*^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ Create Hamburger  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^*/




        // Set up question on page
        textQuestion = (TextView)findViewById(R.id.textView1);
        next_button = (Button)findViewById(R.id.button_next);
        submit = findViewById(R.id.button_submit);
        if(numOfQuestions == 1)
            submit.setVisibility(View.VISIBLE);
        else
            submit.setVisibility(View.GONE);
        back_button = findViewById(R.id.button_back);
        back_button.setVisibility(View.GONE);


        // Set the questions on the page
        setQuestionView();

        // Call listener to check for next page request
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question_id++;
                current_question = question_list.get(question_id);
                //grp.clearCheck();
                grp.removeAllViews();
                for(RadioButton r : current_question.getRadioButtons())
                    grp.addView(r);

                if(numOfQuestions == 1)
                {
                    submit.setVisibility(View.VISIBLE);
                    next_button.setVisibility(View.GONE);
                }
                if (question_id == numOfQuestions - 1 ) {
                    submit.setVisibility(View.VISIBLE);
                    next_button.setVisibility(View.GONE);
                    back_button.setVisibility(View.VISIBLE);
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

                setQuestionView();

                if(current_question.getMarked() != -1)
                    grp.check(current_question.getMarked());

                /* Check the radioButton which was clicked previously */

                findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        back();
                    }
                });

                findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //RadioGroup grp = (RadioGroup) findViewById(R.id.radioGroup1);
                        // Save the user's answer
                        answer = (RadioButton) findViewById(grp.getCheckedRadioButtonId());

                        for (int i: answerScore) {
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
                b.putStringArrayList("correctAnswers", correctAnswers);
                b.putStringArrayList("yourAnswers", yourAnswers);

                intent.putExtras(b); //Put your score to your next Intent
                startActivity(intent);
                finish();

            }

            private void back() {
                question_id--;

                current_question = question_list.get(question_id);
                grp.removeAllViews();
                for(RadioButton r : current_question.getRadioButtons()) {
                    grp.addView(r);
                }

                if (question_id == 0) {
                    back_button.setVisibility(View.GONE);
                    submit.setVisibility(View.GONE);
                    next_button.setVisibility(View.VISIBLE);
                } else if ((question_id + 1) == numOfQuestions-1) { //numofq used to be 4
                    submit.setVisibility(View.GONE);
                    next_button.setVisibility(View.VISIBLE);
                }

                setQuestionView();
                if(current_question.getMarked() != -1 )
                grp.check(current_question.getMarked());

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

                b.putStringArrayList("correctAnswers", correctAnswers);
                b.putStringArrayList("yourAnswers", yourAnswers);
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

        b.putStringArrayList("correctAnswers", correctAnswers);
        b.putStringArrayList("yourAnswers", yourAnswers);

        intent.putExtras(b); //Put your score to your next Intent
        startActivity(intent);
        finish();
    }


    private void setQuestionView()
    {
        textQuestion.setText(current_question.getQuestion());
        qid = "Question: " + String.valueOf(question_id + 1) + "/" + String.valueOf(numOfQuestions);
        getSupportActionBar().setTitle(qid);
    }


    /* Helper method called by onCreate to add drawer items to Drawer */
    private void addDrawerItems() {

        ArrayList<String> row;
        Question currQuestion;
        ArrayList<Question> questionList = quiz.getQuestions();
        String[] questionNums = new String[questionList.size()]; //Used to hold question titles

        for (int i = 0; i < questionList.size(); i++) {
            currQuestion = questionList.get(i);
                                                            //Add 1 to zero indexed question number
            questionNums[i] = "Question " + String.valueOf(currQuestion.getId()+1);
        }

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questionNums);
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
