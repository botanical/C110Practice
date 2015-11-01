package com.example.jennifertran.cse110practice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

import java.util.List;

public class QuizActivity extends AppCompatActivity {
    static CountDownTimer timer;
    List<Question> question_list;
    int score=0;
    int question_id=0;
    Question current_question;
    TextView textQuestion;
    RadioButton rda, rdb, rdc;
    Button next_button;
    View submit;
    TextView textViewTime;
    int testTime = 30000; // 30 seconds by default for test

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Set up the database
        DbHelper db = new DbHelper(this);

        // for timer stuff
        Intent intentReceived = getIntent();
        textViewTime = (TextView) findViewById(R.id.textViewTimer);
        testTime = intentReceived.getIntExtra(StartupPage.EXTRA_TIME, 60);

        question_list = db.getAllQuestions();
        current_question = question_list.get(question_id);

        // Set up question on page
        textQuestion = (TextView)findViewById(R.id.textView1);
        rda = (RadioButton)findViewById(R.id.radio0);
        rdb = (RadioButton)findViewById(R.id.radio1);
        rdc = (RadioButton)findViewById(R.id.radio2);
        next_button = (Button)findViewById(R.id.button_next);
        submit = findViewById(R.id.button_submit);
        submit.setVisibility(View.GONE);

        // Set the question son the page
        setQuestionView();

        // Call listener to check for next page request
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup grp = (RadioGroup)findViewById(R.id.radioGroup1);

                // Save the user's answer
                RadioButton answer = (RadioButton)findViewById(grp.getCheckedRadioButtonId());
                Log.d("your answer", current_question.getANSWER() + " " + answer.getText());

                if(current_question.getANSWER().equals(answer.getText()))
                {
                    score++;
                    Log.d("score", "Your score" + score);
                }

                if(question_id < 5){
                    current_question = question_list.get(question_id);
                    setQuestionView();
                } else if (question_id == 5){
                    submit.setVisibility(View.VISIBLE);
                    next_button.setVisibility(View.GONE);
                    findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            submit();
                        }
                    });

                    /*Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("score", score); //Your score
                    intent.putExtras(b); //Put your score to your next Intent
                    startActivity(intent);
                    finish();*/
                }
            }

            private void submit() {
                timer.cancel();
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                Bundle b = new Bundle();
                b.putInt("score", score); //Your score
                intent.putExtras(b); //Put your score to your next Intent
                startActivity(intent);
                finish();
            }
        });

        // much timer stuff
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
                }
            }

            public void onFinish() {
                textViewTime.setText("Time's up!");
                // submit quiz when time's up; just copied code
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                Bundle b = new Bundle();
                b.putInt("score", score); //Your score
                intent.putExtras(b); //Put your score to your next Intent
                startActivity(intent);
                finish();
            }
        }.start();
    }
    @Override
    public void onBackPressed() {
        timer.cancel();
        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        Bundle b = new Bundle();
        b.putInt("score", score); //Your score
        intent.putExtras(b); //Put your score to your next Intent
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_quiz, menu);
        return true;
    }
    private void setQuestionView()
    {
        // TODO: Randomize questions
        textQuestion.setText(current_question.getQUESTION());
        rda.setText(current_question.getOPTA());
        rdb.setText(current_question.getOPTB());
        rdc.setText(current_question.getOPTC());
        question_id++;
    }
}
