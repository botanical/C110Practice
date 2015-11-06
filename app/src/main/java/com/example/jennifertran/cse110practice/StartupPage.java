package com.example.jennifertran.cse110practice;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class StartupPage extends AppCompatActivity {
    Button start_button;
    int testTimeSend = 70000;
    TextView startTime;
    public final static String EXTRA_TIME = "Time: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_page);


        // displaying subject text
        Intent intent = getIntent();
        String subMessage = intent.getStringExtra(SubjectNavActivity.EXTRA_MESSAGE);
        TextView subText = (TextView) findViewById(R.id.subject_title_text);
        subText.setText(subMessage);

        // displaying time
        startTime = (TextView) findViewById(R.id.timer);
        String timeText = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(testTimeSend) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(testTimeSend)),
                TimeUnit.MILLISECONDS.toSeconds(testTimeSend) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(testTimeSend)));
        startTime.setText(timeText);

        // set listener of start button to call startQuiz() on press
        findViewById(R.id.start_quiz_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuiz();
            }
        });
    }

    private void startQuiz() {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra(EXTRA_TIME, testTimeSend);
        startActivity(intent);
    }
}

