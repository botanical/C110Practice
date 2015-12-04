package com.example.jennifertran.cse110practice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Name: ResultActivity
 * Parent Activity: QuizActivity/AnalysisActivity
 * Purpose: Result activity is what QuizActivity/AnalysisActivity direct to after the quiz/analysis are
 * done. The result page tells the user what questions they missed, answered, and answered correctly as
 * well as providing information about what the correct answers should have been. The activity
 * uses a custom class CustomELA in order to save more than just strings.
 * Children Activity: None
 */

public class ResultActivity extends AppCompatActivity{
    CustomELA listAdapter; //The custom adapter we need, it functions as a standard ELAAdapter with
                           //the added capability of values being able to have colors and to a
                           //Limited extent images.
    ExpandableListView expListView; //The expandable list view we will place our listAdapter
    ArrayList<ELAEntry> listDataHeader = new ArrayList<>(); //The "parent" nodes of our ELA
    HashMap<String, ArrayList<ELAEntry>> listDataChild = new HashMap<>(); //The "child" nodes
                                                                          //Of our ELA

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //If we don't get anything we can't display anything;
        if( getIntent().getExtras() == null ) {
            setTitle("Results!");
            return;
        }
        //Set the layout. We pretty much just have an expandable list view and a logout button
        setContentView(R.layout.activity_result);
        //Recieve data from the quiz activity
        Bundle b = getIntent().getExtras();
        int questions = b.getInt("numOfQuestions");

        //Here we are passed all the information we need from quizActivity to inform the user
        //of their results, including the correct answers and the answers the user placed as
        //well as the original question text.
        ArrayList<String> answers = b.getStringArrayList("correctAnswers");
        ArrayList<String> yourAnswers = b.getStringArrayList("yourAnswers");
        ArrayList<String> questionText = b.getStringArrayList("questionText");

        int score= b.getInt("score");

        //Set the expandable list view to the ELA we made in the layout file.
        expListView = (ExpandableListView) findViewById(R.id.lvExp);


        //This loop creates all the parent nodes.
        for(int i = 0; i < questions; ++i){
            ELAEntry entry = new ELAEntry();//We always make a new entry for every question in the quiz.

            //This condition checks if you put anything at all
            if(yourAnswers.get(i).equals("INCOMPLETE")){
                entry.color = Color.GRAY;
                entry.image = "quesmark";
            }

            //This condition checks if the answer is not equal to the correct answer (and thus a
            //wrong answer).
            else if(!answers.get(i).equals(yourAnswers.get(i))){
                entry.color = Color.parseColor("#FFB2B2");
                entry.image = "xmark";
            }

            //The remaining condition is if its not wrong or simply not placed then it must be correct.
            else{
                entry.color = Color.parseColor("#FF41B124");
                entry.image = "checkmark";
            }
            //The text for each question should simply be the original quiz question text,
            //thus making it significantly easier to read the questions you got wrong/right.
            entry.TextEntry = "   "+questionText.get(i);
            //We add the entry to the list of parent nodes in our ela
            listDataHeader.add(entry);
        }

        //In this loop we set the children for our ELA
        //We don't care really about any extra ELAEntry features such as the image,
        //all we really want to do is display the answer the user gave and the correct answer.
        for (int i = 0; i < questions; i++) {
            //Two new entries represent these distinct entities.
            ELAEntry answerEntry = new ELAEntry();
            ELAEntry yourAnswerEntry = new ELAEntry();

            //Here we get plenty of info for the ELAEntry automatically.
            ELAEntry headerEntry = listDataHeader.get(i);
            answerEntry.color = Color.WHITE;
            yourAnswerEntry.color = Color.WHITE;
            answerEntry.image = headerEntry.image;
            yourAnswerEntry.image = headerEntry.image;

            //Setting the text to the Answer/YourAnswer format
            answerEntry.TextEntry = "Answer: " + answers.get(i);

            yourAnswerEntry.TextEntry = "Your Answer: " + yourAnswers.get(i);

            //Adding the entries back into our original ELA.
            ArrayList<ELAEntry> childList = new ArrayList<>();
            childList.add(answerEntry);
            childList.add(yourAnswerEntry);
            listDataChild.put(headerEntry.TextEntry, childList);
        }
        //Finally setting the adapter.
        listAdapter = new CustomELA(ResultActivity.this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);


        //The back to subject nav button just takes you back to the subject navigator - ending the
        //quiz.
        findViewById(R.id.button_back_to_subject_nav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToSubjectNavActivity= new Intent(ResultActivity.this, SubjectNavActivity.class);
                backToSubjectNavActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(backToSubjectNavActivity);
                finish();
            }
        });
    }

    //Inflates the hamburger. As of now this functionality is unused in the result page activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_result, menu);
        return true;
    }
}