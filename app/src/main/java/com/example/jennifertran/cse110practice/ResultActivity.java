package com.example.jennifertran.cse110practice;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity{
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        TextView t =(TextView)findViewById(R.id.textResult);
        Bundle b = getIntent().getExtras();
        int score= b.getInt("score");
        double percentage = (((double)score)/5);
        setTitle("Result: "+String.valueOf(score)+"/"+String.valueOf(5));
        t.setText("Your score is %" + (percentage)*100);
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_example);
        Bundle b = getIntent().getExtras();

/*
        // Add textview 1
        TextView textView1 = new TextView(this);
        textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView1.setText("programmatically created TextView1");
        textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
        textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
        linearLayout.addView(textView1);

        // Add textview 2
        TextView textView2 = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        layoutParams.setMargins(10, 10, 10, 10); // (left, top, right, bottom)
        textView2.setLayoutParams(layoutParams);
        textView2.setText("programmatically created TextView2");
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView2.setBackgroundColor(0xffffdbdb); // hex color 0xAARRGGBB
        linearLayout.addView(textView2);
*/
        int questions = b.getInt("numOfQuestions"); // total number of textviews to add

        String[] answers = b.getStringArray("correctAnswers");
        String[] yourAnswers = b.getStringArray("yourAnswers");

        final TextView[] questionNumView = new TextView[questions]; // create an empty array;
        final TextView[] correctAnswersView = new TextView[questions];
        final TextView[] yourAnswersView = new TextView[questions];


        for (int i = 0; i < questions; i++) {
            // create a new textview
            final TextView quesTextView = new TextView(this);
            // set some properties of rowTextView or something
            quesTextView.setText("Question: " + (i+1));
            quesTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            // add the textview to the linearlayout
            linearLayout.addView(quesTextView);

            final TextView correctAnswerView = new TextView(this);
            correctAnswerView.setText(" The correct answer was: " + answers[i]);
            correctAnswerView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            linearLayout.addView(correctAnswerView);

            final TextView yourAnswerView = new TextView(this);
            yourAnswerView.setText(" Your answer was: " + yourAnswers[i]);
            yourAnswerView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            linearLayout.addView(yourAnswerView);

            // save a reference to the textview for later
            questionNumView[i] = quesTextView;
        }
        int score= b.getInt("score");
        double percentage = (((double)score)/5);
        setTitle("Result: " + String.valueOf(score) + "/" + String.valueOf(5));

        final TextView scoreResult = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        scoreResult.setLayoutParams(layoutParams);

        scoreResult.setText("Your score is %" + (percentage) * 100);
        scoreResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        linearLayout.addView(scoreResult);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_result, menu);
        return true;
    }
}