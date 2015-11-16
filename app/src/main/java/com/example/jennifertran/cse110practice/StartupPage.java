package com.example.jennifertran.cse110practice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StartupPage extends AppCompatActivity {
    Button start_button;
    boolean quizTaken = false;
    int testTimeSend = 70000;
    TextView startTime;
    private String title;
    private ProgressDialog pDialog;
    private String loginUrl;
    private ArrayList<String> columns;
    public final static String EXTRA_TIME = "Time: ";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_page);
        loginUrl = getApplicationContext().getString(R.string.queryUrl);
        // displaying subject text
        Intent intent = getIntent();
        title = intent.getStringExtra(SubjectNavActivity.EXTRA_MESSAGE);
        //TextView subText = (TextView) findViewById(R.id.subject_title_text);
        //subText.setText(title);
        title = intent.getStringExtra("title");
        setTitle("Quiz: " + title); // display subject in title bar
        new AttemptUpdateQuiz().execute();
    }

    private void startQuiz() {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra(EXTRA_TIME, testTimeSend);
        intent.putExtra("title",title);
        //Can't pass object to a different activity so must pass object as a string
        JSONArray jA = new JSONArray(columns);
        try {
            System.out.println("STARTUP: " + jA.get(0));
        }catch(Exception e){
            e.printStackTrace();
        }
        intent.putExtra("columns",jA.toString());
        startActivity(intent);
    }

    private void seeResults() {
        Intent intent = new Intent(this, ResultActivity.class);

        // dummy info with null data
        Bundle b = new Bundle();
        int numOfQuestions = 1;
        String[] stringArray = new String[numOfQuestions];
        b.putInt("score", 0); //Your score
        b.putInt("numOfQuestions", numOfQuestions);
        b.putStringArray("correctAnswers", stringArray);
        b.putStringArray("yourAnswers", stringArray);
        intent.putExtras(b);

        startActivity(intent);
    }

    class AttemptUpdateQuiz extends AsyncTask<String,String,String> {

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(StartupPage.this);
            pDialog.setMessage("Attempting Update Quiz");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected String doInBackground(String... params) {
            RemoteDBHelper remDb = new RemoteDBHelper();
            String table = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                    "SELECT * FROM  " + "`"+title+"` ", loginUrl);//BACKTICKS ARE CRITICAL OMG
                                                                  //SAME KEY AS ~
            System.out.println("TITLE: ---'"+title+"'----");
            System.out.println("ATTEMPT UPDATE TABLE: "+table);

            /*
                Update quiz to local quiz;
             */
            try {
                JSONArray jTable = new JSONArray(table);
                JSONObject currRow;
                ArrayList<String> columns = new ArrayList<>();
                currRow = jTable.getJSONObject(0);
                Iterator<?> keyIt = currRow.keys();
                while(keyIt.hasNext()) //get column names for new local database
                {
                    String n = (String) keyIt.next();

                    //Add all child columns to columns
                    if((!n.equals("id")) && (!n.equals("question")) && (!n.equals("answer")) &&
                                                                        !n.equals("marked"))
                        columns.add(n);
                }

                /* q*/

                HashMap<String, Pair<Pair<String,String>, ArrayList<String>>> questOpPairs =
                        new HashMap<>();
                for (int i = 0; i < jTable.length(); i++) {

                    ArrayList<String> options = new ArrayList<>();
                    currRow = jTable.getJSONObject(i);
                    String id = currRow.getString("id");
                    String question = currRow.getString("question");
                    String answer   = currRow.getString("answer");
                    Pair<String, String> quesAns = new Pair<>(question, answer);
                    String marked   = currRow.getString("marked");
                    currRow.remove("id");
                    currRow.remove("question");
                    currRow.remove("answer");
                    currRow.remove("marked");
                    keyIt = currRow.keys();
                    while(keyIt.hasNext())
                    {
                        options.add(currRow.getString((String) keyIt.next()));
                    }
                    options.add(String.valueOf(marked)); //Put indexer as last child.
                    questOpPairs.put(id, new Pair<>(quesAns ,options));
                }

                /*
                    Create local subject nav table to hold subjects and their respective
                    quizzes. Insert header and childList pairs into local database

                 */


                StartupPage.this.columns = columns;
                DbHelperQuiz db = new DbHelperQuiz(StartupPage.this, title,columns);
                db.createTable();
                db.upgradeQuiz(questOpPairs); //store subNav.db locally


            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String message){
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            //displaying time
            startTime = (TextView) findViewById(R.id.timer);
            String timeText = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(testTimeSend) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(testTimeSend)),
                    TimeUnit.MILLISECONDS.toSeconds(testTimeSend) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(testTimeSend)));
            startTime.setText(timeText);

            // adjusting Start Button
            start_button = (Button)findViewById(R.id.start_quiz_button);

            if( quizTaken == false ) {
                start_button.setText("START");
                start_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startQuiz();
                    }
                });
            }
            else {
                start_button.setText("SEE RESULTS");
                TextView instrucText = (TextView) findViewById(R.id.instruc_id);
                instrucText.setText("You have already taken this quiz. You may view your results.");
                start_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        seeResults();
                    }
                });

            }

        }
    }
}

