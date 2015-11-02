package com.example.jennifertran.cse110practice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    List<Question> question_list;
    int[] answerScore = new int[5];
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
    ContentValues values = new ContentValues();
    String marked;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Set up the database
        db = new DbHelperQuiz(this);

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
        back_button = findViewById(R.id.button_back);
        back_button.setVisibility(View.GONE);

        // Set the question son the page
        setQuestionView();
        grp = (RadioGroup)findViewById(R.id.radioGroup1);
        answer = (RadioButton)findViewById(grp.getCheckedRadioButtonId());
        answerCheck = grp.getCheckedRadioButtonId();
        if(!(answerCheck == -1)) {
            if(current_question.getANSWER().equals(answer.getText()))
            {
                Log.d("DEBUG", "here");
                answerScore[question_id] = 1;
                values.put("marked", answer.getText().toString());
                db.insertIntoDb(String.valueOf(question_id), values);

            }
        }
        else {
            // Else, put empty string into database for no answer
            values.put("marked", "");
            db.insertIntoDb(String.valueOf(question_id), values);
        }

        // Call listener to check for next page request
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(question_id < 3){
                    answer = (RadioButton)findViewById(grp.getCheckedRadioButtonId());
                    grp = (RadioGroup)findViewById(R.id.radioGroup1);

                    /* Check to see if previous question had radio buttons checked
                     * and resets new question to have no buttons checked.
                     */
                    if (grp.getCheckedRadioButtonId() != -1) {
                        answer.setChecked(false);

                    }

                    /* Increments question_id to show new question
                     * and set view accordingly
                     */
                    question_id++;
                    current_question = question_list.get(question_id);

                    back_button.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.GONE);

                    int next_qid = question_id;
                    marked = db.queryMarkedAnswers(next_qid);

                    setQuestionView();

                    if (((rda.getText()).toString()).equals(marked)) {
                        rda.setChecked(true);
                        rdb.setChecked(false);
                        rdc.setChecked(false);
                        Log.d("Banana", rda.getText().toString());

                    }
                    if (((rdb.getText()).toString()).equals(marked)) {
                        rda.setChecked(false);
                        rdb.setChecked(true);
                        rdc.setChecked(false);
                        Log.d("Banana", rdb.getText().toString());

                    }
                    if (((rdc.getText()).toString()).equals(marked)) {
                        rda.setChecked(false);
                        rdb.setChecked(false);
                        rdc.setChecked(true);
                        Log.d("Banana", rdc.getText().toString());

                    }

                    grp = (RadioGroup)findViewById(R.id.radioGroup1);
                    answerCheck = grp.getCheckedRadioButtonId();
                    if(!(answerCheck == -1)) {
                        answer = (RadioButton)findViewById(grp.getCheckedRadioButtonId());
                        if(current_question.getANSWER().equals(answer.getText()))
                        {
                            Log.d("DEBUG", "here");
                            answerScore[question_id] = 1;
                            values.put("marked", answer.getText().toString());
                            db.insertIntoDb(String.valueOf(question_id), values);

                        }
                    }
                    else {
                        values.put("marked", "");
                        db.insertIntoDb(String.valueOf(question_id), values);
                    }


                    findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            back();
                        }
                    });

                } else {

                    submit.setVisibility(View.VISIBLE);
                    back_button.setVisibility(View.VISIBLE);
                    next_button.setVisibility(View.GONE);
                    question_id++;
                    current_question = question_list.get(question_id);

                    if(!(answerCheck == -1)) {
                        answer = (RadioButton)findViewById(grp.getCheckedRadioButtonId());
                        if(current_question.getANSWER().equals(answer.getText()))
                        {
                            Log.d("DEBUG", "here");
                            answerScore[question_id] = 1;
                            values.put("marked", answer.getText().toString());
                            db.insertIntoDb(String.valueOf(question_id), values);

                        }
                    }
                    else {
                        values.put("marked", "");
                        db.insertIntoDb(String.valueOf(question_id), values);
                    }

                    setQuestionView();


                    findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RadioGroup grp = (RadioGroup)findViewById(R.id.radioGroup1);

                            // Save the user's answer
                            answer = (RadioButton)findViewById(grp.getCheckedRadioButtonId());
                            Log.d("your answer", current_question.getANSWER() + " " + answer.getText());

                            if(!(answerCheck == -1)) {
                                if(current_question.getANSWER().equals(answer.getText()))
                                {
                                    Log.d("DEBUG", "here");
                                    answerScore[question_id] = 1;
                                    values.put("marked", answer.getText().toString());
                                    db.insertIntoDb(String.valueOf(question_id), values);

                                    for (int i = 0; i < (answerScore.length); i++) {
                                        score = answerScore[i] + score;
                                    }
                                    submit();
                                }
                            }
                            else {
                                values.put("marked", "");
                                db.insertIntoDb(String.valueOf(question_id), values);
                            }


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

                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                Bundle b = new Bundle();
                b.putInt("score", score); //Your score
                intent.putExtras(b); //Put your score to your next Intent
                startActivity(intent);
                finish();

            }

            private void back() {
                answer.setChecked(false);
                question_id--;
                prev_qid = question_id;
                marked = db.queryMarkedAnswers(prev_qid);
                current_question = question_list.get(question_id);

                submit.setVisibility(View.GONE);
                next_button.setVisibility(View.VISIBLE);

                if (question_id == 0) {
                    back_button.setVisibility(View.GONE);
                    setQuestionView();
                } else if (question_id < 5) {
                    setQuestionView();
                }


                if (((rda.getText()).toString()).equals(marked)) {
                    rda.setChecked(true);
                    rdb.setChecked(false);
                    rdc.setChecked(false);
                    Log.d("Checked", rda.getText().toString());

                }
                if (((rdb.getText()).toString()).equals(marked)) {
                    rda.setChecked(false);
                    rdb.setChecked(true);
                    rdc.setChecked(false);
                    Log.d("Checked", rdb.getText().toString());

                }
                if (((rdc.getText()).toString()).equals(marked)) {
                    rda.setChecked(false);
                    rdb.setChecked(false);
                    rdc.setChecked(true);
                    Log.d("Checked", rdc.getText().toString());

                }

                grp = (RadioGroup)findViewById(R.id.radioGroup1);
                answerCheck = grp.getCheckedRadioButtonId();


                ContentValues values = new ContentValues();
                if(!(answerCheck == -1)) {
                    answer = (RadioButton)findViewById(grp.getCheckedRadioButtonId());
                    if(current_question.getANSWER().equals(answer.getText()))
                    {
                        Log.d("DEBUG", "here");
                        answerScore[question_id] = 1;
                        values.put("marked", answer.getText().toString());
                        db.insertIntoDb(String.valueOf(question_id), values);

                    }
                }
                else {
                    // Else, put empty string into database for no answer
                    values.put("marked", "");
                    db.insertIntoDb(String.valueOf(question_id), values);
                }
                // Save the user's answer



                Log.d("BUTTON1", rda.getText().toString());
                Log.d("BUTTON2", rdb.getText().toString());
                Log.d("BUTTON3", rdc.getText().toString());
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_quiz, menu);
        return true;
    }
    private void setQuestionView()
    {
        textQuestion.setText(current_question.getQUESTION());
        rda.setText(current_question.getOPTA());
        rdb.setText(current_question.getOPTB());
        rdc.setText(current_question.getOPTC());
    }
}
