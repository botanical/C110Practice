package com.example.jennifertran.cse110practice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultActivity extends AppCompatActivity{
    ELATest listAdapter;
    ExpandableListView expListView;
    ArrayList<ELAEntry> listDataHeader = new ArrayList<ELAEntry>();
    HashMap<String, ArrayList<ELAEntry>> listDataChild = new HashMap<String, ArrayList<ELAEntry>>();

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
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_example);
        Bundle b = getIntent().getExtras();

        int questions = b.getInt("numOfQuestions"); // total number of textviews to add

        String[] answers = b.getStringArray("correctAnswers");
        String[] yourAnswers = b.getStringArray("yourAnswers");

        //final TextView[] questionNumView = new TextView[questions]; // create an empty array;
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
            if(answers != null) {
                correctAnswerView.setText(" The correct answer was: " + answers[i]);
                correctAnswerView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            }
            else
                correctAnswerView.setText(" No answers for the question were found in the DB");

            linearLayout.addView(correctAnswerView);

            final TextView yourAnswerView = new TextView(this);
            if(yourAnswers != null) {
                yourAnswerView.setText(" Your answer was: " + yourAnswers[i]);
                yourAnswerView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            }
            else
                yourAnswerView.setText("Your answers didn't push properly to our DB");

            linearLayout.addView(yourAnswerView);

            // save a reference to the textview for later
            //  questionNumView[i] = quesTextView;
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
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //Recieve data from the quiz activity
        Bundle b = getIntent().getExtras();
        int questions = b.getInt("numOfQuestions"); // total number of textviews to add
        //ArrayList<String> answers = b.getStringArrayList("correctAnswers");
        //ArrayList<String> yourAnswers = b.getStringArrayList("yourAnswers");

        //HARDCODE
        ArrayList<String> answers = new ArrayList<>();
        ArrayList<String> yourAnswers = new ArrayList<>();
        answers.add("36");
        yourAnswers.add("INCOMPLETE");
        answers.add("10");
        yourAnswers.add("10");

        int score= b.getInt("score");
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        for(int i = 0; i < questions; ++i){
            ELAEntry entry = new ELAEntry();
            if(yourAnswers.get(i) == "INCOMPLETE"){
                entry.color = Color.BLUE;
                entry.image = "quesmark";
            }
            else if(answers.get(i) != yourAnswers.get(i)){
                entry.color = Color.RED;
                entry.image = "xmark";
            }
            else{
                entry.color = Color.GREEN;
                entry.image = "checkmark";
            }
            entry.TextEntry = "Question " + (i+1);
            listDataHeader.add(entry);
        }
/*
        for (int i = 1; i <= questions; i++) {
            ELAEntry headerEntry = listDataHeader.get(i-1);
            ELAEntry yourAnswerEntry = headerEntry;
            ELAEntry answerEntry = headerEntry;

            answerEntry.TextEntry = "The answer was " + answers.get(i-1);
            yourAnswerEntry.TextEntry = "Your answer was " + yourAnswers.get(i-1);

            ArrayList<ELAEntry> childList = new ArrayList<ELAEntry>();
            childList.add(answerEntry);
            childList.add(yourAnswerEntry);
            listDataChild.put(headerEntry.TextEntry, childList);
        }
*/
        listAdapter = new ELATest(ResultActivity.this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_result, menu);
        return true;
    }
}