package com.example.jennifertran.cse110practice;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class StartupPage extends AppCompatActivity {
    Button start_button;
    //Button btnStart, btnStop;
    //public TextView textViewTime;

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
                //timer.start();
            }
        });

        //textViewTime = (TextView) findViewById(R.id.timer);
        /*btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);

        textViewTime.setText("00:30");
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.start();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
            }
        });*/
    }

    private void startQuiz() {
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }

    /*public class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO Auto-generated constructor stub
        }

        public void onTick
    }*/

    /*CountDownTimer timer = new CountDownTimer(30000, 1000) {

        public void onTick(long millisUntilFinished) {
            textViewTime.setText("seconds remaining: " + millisUntilFinished / 1000);
        }

        public void onFinish() {
            textViewTime.setText("done!");
        }
    }.start();*/

}

