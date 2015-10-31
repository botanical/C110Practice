package com.example.jennifertran.cse110practice;

import android.app.Activity;
import android.content.Intent;
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

import java.util.List;

public class QuizActivity extends AppCompatActivity {

    List<Question> question_list;
    int[] answerScore = new int[5];
    int score = 0;
    int question_id = 0;
    Question current_question;
    TextView textQuestion;
    RadioButton rda, rdb, rdc;
    Button next_button;
    View submit, back_button;
    RadioButton answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Set up the database
        DbHelper db = new DbHelper(this);

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

        // Call listener to check for next page request
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup grp = (RadioGroup)findViewById(R.id.radioGroup1);

                // Save the user's answer
                answer = (RadioButton)findViewById(grp.getCheckedRadioButtonId());
                Log.d("your answer", current_question.getANSWER() + " " + answer.getText());

                if(current_question.getANSWER().equals(answer.getText()))
                {
                    answerScore[question_id] = 1;
                    //Log.d("score", "Your score" + answerScore[question_id]);

                }

                if(question_id < 3){
                    question_id++;
                    current_question = question_list.get(question_id);
                    back_button.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.GONE);

                    setQuestionView();

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
                    setQuestionView();


                    findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RadioGroup grp = (RadioGroup)findViewById(R.id.radioGroup1);

                            // Save the user's answer
                            answer = (RadioButton)findViewById(grp.getCheckedRadioButtonId());
                            Log.d("your answer", current_question.getANSWER() + " " + answer.getText());

                            if(current_question.getANSWER().equals(answer.getText()))
                            {
                                answerScore[question_id] = 1;
                                //Log.d("score", "Your score" + answerScore[question_id]);

                            }

                            for (int i = 0; i < (answerScore.length); i++) {
                                score = answerScore[i] + score;
                            }
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

                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                Bundle b = new Bundle();
                b.putInt("score", score); //Your score
                intent.putExtras(b); //Put your score to your next Intent
                startActivity(intent);
                finish();

            }

            private void back() {
                RadioGroup grp = (RadioGroup) findViewById(R.id.radioGroup1);
                question_id--;
                submit.setVisibility(View.GONE);
                next_button.setVisibility(View.VISIBLE);

                // Save the user's answer
                current_question = question_list.get(question_id);
                RadioButton answer = (RadioButton) findViewById(grp.getCheckedRadioButtonId());
                Log.d("your answer", current_question.getANSWER() + " " + answer.getText());

                if (question_id == 0) {
                    back_button.setVisibility(View.GONE);
                    setQuestionView();
                } else if (question_id < 5) {
                    setQuestionView();
                }
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
