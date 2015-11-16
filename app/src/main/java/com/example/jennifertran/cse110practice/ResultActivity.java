package com.example.jennifertran.cse110practice;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.HashMap;

public class ResultActivity extends AppCompatActivity{
    ELATest listAdapter;
    ExpandableListView expListView;
    ArrayList<ELAEntry> listDataHeader = new ArrayList<ELAEntry>();
    HashMap<String, ArrayList<ELAEntry>> listDataChild = new HashMap<String, ArrayList<ELAEntry>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //Recieve data from the quiz activity
        Bundle b = getIntent().getExtras();
        int questions = b.getInt("numOfQuestions"); // total number of textviews to add
        ArrayList<String> answers = b.getStringArrayList("correctAnswers");
        ArrayList<String> yourAnswers = b.getStringArrayList("yourAnswers");

        int score= b.getInt("score");
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        for(int i = 0; i < questions; ++i){
            ELAEntry entry = new ELAEntry();
            if(yourAnswers.get(i).equals("INCOMPLETE")){
                entry.color = Color.LTGRAY;
                entry.image = "quesmark";
            }
            else if(!answers.get(i).equals(yourAnswers.get(i))){
                entry.color = Color.LTGRAY;
                entry.image = "xmark";
            }
            else{
                entry.color = Color.LTGRAY;
                entry.image = "checkmark";
            }
            entry.TextEntry = "    Question " + (i+1);
            listDataHeader.add(entry);
        }

        for (int i = 0; i < questions; i++) {
            ELAEntry answerEntry = new ELAEntry();
            ELAEntry yourAnswerEntry = new ELAEntry();
            ELAEntry headerEntry = listDataHeader.get(i);
            answerEntry.color = headerEntry.color;
            yourAnswerEntry.color = headerEntry.color;
            answerEntry.image = headerEntry.image;
            yourAnswerEntry.image = headerEntry.image;

            answerEntry.TextEntry = " The answer was " + answers.get(i);
            yourAnswerEntry.TextEntry = " Your answer was " + yourAnswers.get(i);

            ArrayList<ELAEntry> childList = new ArrayList<ELAEntry>();
            childList.add(answerEntry);
            childList.add(yourAnswerEntry);
            listDataChild.put(headerEntry.TextEntry, childList);
        }
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