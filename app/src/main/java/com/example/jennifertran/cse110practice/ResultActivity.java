package com.example.jennifertran.cse110practice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.HashMap;

public class ResultActivity extends AppCompatActivity{
    ELATest listAdapter;
    ExpandableListView expListView;
    ArrayList<ELAEntry> listDataHeader = new ArrayList<>();
    HashMap<String, ArrayList<ELAEntry>> listDataChild = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( getIntent().getExtras() == null ) {
            setTitle("Results!");

            return;
        }
        setContentView(R.layout.activity_result);
        //Recieve data from the quiz activity
        Bundle b = getIntent().getExtras();
        int questions = b.getInt("numOfQuestions"); // total number of textviews to add

        ArrayList<String> answers = b.getStringArrayList("correctAnswers");
        ArrayList<String> yourAnswers = b.getStringArrayList("yourAnswers");
        ArrayList<String> questionText = b.getStringArrayList("questionText");

        int score= b.getInt("score");
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        for(int i = 0; i < questions; ++i){
            ELAEntry entry = new ELAEntry();
            if(yourAnswers.get(i).equals("INCOMPLETE")){
                entry.color = Color.GRAY;
                entry.image = "quesmark";
            }
            else if(!answers.get(i).equals(yourAnswers.get(i))){
                entry.color = Color.parseColor("#FFB2B2");
                entry.image = "xmark";
            }
            else{
                entry.color = Color.parseColor("#CCFFCC");
                entry.image = "checkmark";
            }
            entry.TextEntry = "   "+questionText.get(i);
            listDataHeader.add(entry);
        }

        for (int i = 0; i < questions; i++) {
            ELAEntry answerEntry = new ELAEntry();
            ELAEntry yourAnswerEntry = new ELAEntry();
            ELAEntry headerEntry = listDataHeader.get(i);
            answerEntry.color = Color.WHITE;
            yourAnswerEntry.color = Color.WHITE;
            answerEntry.image = headerEntry.image;
            yourAnswerEntry.image = headerEntry.image;

            answerEntry.TextEntry = "Answer: " + answers.get(i);
            yourAnswerEntry.TextEntry = "Your Answer: " + yourAnswers.get(i);

            ArrayList<ELAEntry> childList = new ArrayList<>();
            childList.add(answerEntry);
            childList.add(yourAnswerEntry);
            listDataChild.put(headerEntry.TextEntry, childList);
        }
        listAdapter = new ELATest(ResultActivity.this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_result, menu);
        return true;
    }
}