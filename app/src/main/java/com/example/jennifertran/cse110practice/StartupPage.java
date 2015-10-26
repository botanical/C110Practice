package com.example.jennifertran.cse110practice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartupPage extends AppCompatActivity {
    Button start_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_page);

        // displaying subject text
        Intent intent = getIntent();
        String subMessage = intent.getStringExtra(SubjectNavActivity.EXTRA_MESSAGE);
        TextView subText = (TextView) findViewById(R.id.subject_title_text);
        subText.setText(subMessage);

        // set listener of login button to call login() on press
        findViewById(R.id.start_quiz_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuiz();
            }
        });
    }

    private void startQuiz() {


        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }

}

