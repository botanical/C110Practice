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
    String marked;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Set up the database
        db = new DbHelperQuiz(this);

        question_list = db.getAllQuestions();
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

        // Set the question son the page
        setQuestionView();




        // Call listener to check for next page request
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                question_id++;
                Log.d("QuestionList", String.valueOf(question_list.get(0)));
                current_question = question_list.get(question_id);
                grp.clearCheck();
                if(question_id == 4){
                    submit.setVisibility(View.VISIBLE);
                    next_button.setVisibility(View.GONE);
                }
                else if (question_id != 0){
                    back_button.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.GONE);
                }
                else {
                    back_button.setVisibility(View.GONE);
                }




                    /* Check to see if previous question had radio buttons checked
                     * and resets new question to have no buttons checked.
                     */

                    Log.d("QUESTION", String.valueOf(question_id));


                    /* Increments question_id to show new question
                     * and set view accordingly
                     */

                    /* Uncheck all buttons so new page has no checked answer */

                    marked = db.queryMarkedAnswers(question_id);

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

                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                Bundle b = new Bundle();
                b.putInt("score", score); //Your score
                intent.putExtras(b); //Put your score to your next Intent
                startActivity(intent);
                finish();

            }

            private void back() {

                question_id--;
                Log.d("newqid", String.valueOf(question_id));
                marked = db.queryMarkedAnswers(question_id);
                Log.d("marked", "---:"+marked+":---");
                current_question = question_list.get(question_id);
                grp.clearCheck();

                if ((question_id + 1) == 4) {
                    submit.setVisibility(View.GONE);
                    next_button.setVisibility(View.VISIBLE);
                } else if (question_id == 0){
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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        ContentValues values = new ContentValues();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio0:
                if (checked)
                    values.put("marked", rda.getText().toString());
                if(current_question.getANSWER().equals(rda.getText().toString()))
                {
                    answerScore[question_id] = 1;
                }
                db.insertIntoDb(String.valueOf(question_id), values);
                Log.d("inserted", rda.getText().toString());
                break;
            case R.id.radio1:
                if (checked)
                    values.put("marked", rdb.getText().toString());
                if(current_question.getANSWER().equals(rdb.getText().toString()))
                {
                    answerScore[question_id] = 1;
                }
                db.insertIntoDb(String.valueOf(question_id), values);
                Log.d("inserted", rdb.getText().toString());

                break;
            case R.id.radio2:
                if (checked)
                    values.put("marked", rdc.getText().toString());
                if(current_question.getANSWER().equals(rdc.getText().toString()))
                {
                    answerScore[question_id] = 1;
                }
                db.insertIntoDb(String.valueOf(question_id), values);
                Log.d("inserted", rdc.getText().toString());

                break;
        }
    }

}
