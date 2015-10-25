package com.example.jennifertran.cse110practice;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RatingBar;
import android.widget.TextView;

public class ResultActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //get rating bar object
        RatingBar bar=(RatingBar)findViewById(R.id.ratingBar1);
        bar.setNumStars(5);

        bar.setStepSize(0.5f);
        //get text view
        TextView t =(TextView)findViewById(R.id.textResult);
        //get score
        Bundle b = getIntent().getExtras();
        int score= b.getInt("score");
        //display score
        bar.setRating(score);
        switch (score)
        {
            case 1:
                t.setText("1 out of 5");
                break;
            case 2:
                t.setText("2 out of 5");
                break;
            case 3:
                t.setText("3 out of 5");
                break;
            case 4:
                t.setText("4 out of 5");
                break;
            case 5:
                t.setText("5 out of 5!!!");
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_result, menu);
        return true;
    }
}