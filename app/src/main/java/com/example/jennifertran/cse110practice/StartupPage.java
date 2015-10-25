package com.example.jennifertran.cse110practice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class StartupPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_page);

        // displaying subject text
        Intent intent = getIntent();
        String subMessage = intent.getStringExtra(SubjectNavActivity.EXTRA_MESSAGE);
        TextView subText = (TextView) findViewById(R.id.subject_title_text);
        subText.setText(subMessage);
    }
}
