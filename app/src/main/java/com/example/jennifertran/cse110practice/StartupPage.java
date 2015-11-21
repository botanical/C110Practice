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
import android.widget.Toast;

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
    int testTimeSend = 70000;
    TextView startTime;
    private String title;
    private String username;
    private ProgressDialog pDialog;
    private String loginUrl;
    private ArrayList<String> columns;
    public final static String EXTRA_TIME = "Time: ";
    public boolean isTaken;
    public int numQuestions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_page);
        loginUrl = getApplicationContext().getString(R.string.queryUrl);
        // displaying subject text
        Intent intent = getIntent();
        title = intent.getStringExtra(SubjectNavActivity.EXTRA_MESSAGE);
        username = intent.getStringExtra("username");
        TextView subText = (TextView) findViewById(R.id.subject_title_id);
        subText.setText(title);
        title = intent.getStringExtra("title");
        new AttemptUpdateQuiz().execute();
    }

    /* Check if quiz has been taken when this activity is restarted */
    @Override
    protected void onRestart(){
        super.onRestart();
        DbHelperTaken dbTaken = new DbHelperTaken(StartupPage.this, username);
        int taken = dbTaken.getIsTaken(title);
        if(taken == 1) {
            isTaken = true;
            Button b = (Button) findViewById(R.id.start_quiz_button);
            b.setText("Results");
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StartupPage.this, QuizActivity.class);
                    intent.putExtra("isTaken", isTaken);
                    intent.putExtra(EXTRA_TIME, testTimeSend);
                    intent.putExtra("title",title);
                    intent.putExtra("username", username);
                    //Can't pass object to a different activity so must pass object as a string
                    JSONArray jA = new JSONArray(columns);
                    intent.putExtra("columns", jA.toString());

                    startActivity(intent);
                }
            });
        }
        else {
            isTaken = false;

        }
    }

    private void startQuiz() {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra(EXTRA_TIME, testTimeSend);
        intent.putExtra("title",title);
        intent.putExtra("username", username);
        //Can't pass object to a different activity so must pass object as a string
        JSONArray jA = new JSONArray(columns);
        try {
            System.out.println("STARTUP: " + jA.get(0));
        }catch(Exception e){
            e.printStackTrace();
        }
        intent.putExtra("columns", jA.toString());
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
                    "SELECT * FROM  " + "`" + title + "` ", loginUrl);//BACKTICKS ARE CRITICAL OMG
            //SAME KEY AS ~
            /*
                Update quiz to local quiz;
             */
            try {
                JSONArray jTable = new JSONArray(table);
                numQuestions = jTable.length();
                JSONObject currRow;
                ArrayList<String> columns = new ArrayList<>();
                currRow = jTable.getJSONObject(0);
                Iterator<?> keyIt = currRow.keys();
                while (keyIt.hasNext()) //get column names for new local database
                {
                    String n = (String) keyIt.next();

                    //Add all child columns to columns
                    if ((!n.equals("id")) && (!n.equals("question")) && (!n.equals("answer")) &&
                            !n.equals("marked"))
                        columns.add(n);
                }

                /* q*/

                HashMap<String, Pair<Pair<String, String>, ArrayList<String>>> questOpPairs =
                        new HashMap<>();
                for (int i = 0; i < jTable.length(); i++) {

                    ArrayList<String> options = new ArrayList<>();
                    currRow = jTable.getJSONObject(i);
                    System.out.println("CURR ROW: "+currRow);
                    String id = currRow.getString("id");
                    String question = currRow.getString("question");
                    String answer = currRow.getString("answer");
                    Pair<String, String> quesAns = new Pair<>(question, answer);
                    String marked = currRow.getString("marked");
                    String solution = currRow.getString("solution");
                    currRow.remove("id");
                    currRow.remove("question");
                    currRow.remove("answer");
                    currRow.remove("marked");
                    currRow.remove("solution");
                    keyIt = currRow.keys();
                    while (keyIt.hasNext()) {
                        options.add(currRow.getString((String) keyIt.next()));
                    }
                    options.add(solution);
                    options.add(String.valueOf(marked)); //Put indexer as last child.
                    questOpPairs.put(id, new Pair<>(quesAns, options));
                }

                /*
                    Create local subject nav table to hold subjects and their respective
                    quizzes. Insert header and childList pairs into local database

                 */

                StartupPage.this.columns = columns;
                DbHelperQuiz db = new DbHelperQuiz(StartupPage.this, title, columns);
                db.createTable();
                db.upgradeQuiz(questOpPairs); //store subNav.db locally


                /*************** Get list of taken quizzes ********************************/


                String takenTable = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        "SELECT * FROM  " + "`" + username + "Taken` ", loginUrl);//BACKTICKS
                // CRITICAL OMG

                jTable = new JSONArray(takenTable);
                HashMap<String, Integer> quizTakenPairs = new HashMap<>();
                for (int i = 0; i < jTable.length(); i++) {
                    currRow = jTable.getJSONObject(i);
                    quizTakenPairs.put(currRow.getString("title"), currRow.getInt("taken"));
                }
                DbHelperTaken dbTaken = new DbHelperTaken(StartupPage.this, username);
                dbTaken.createTable();
                dbTaken.upgradeTaken(quizTakenPairs);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String message){
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            TextView numQ = (TextView) findViewById(R.id.num_of_questions_text);
            numQ.setText("Number of Questions: " +String.valueOf(numQuestions));

            DbHelperTaken dbTaken = new DbHelperTaken(StartupPage.this, username);
            int taken = dbTaken.getIsTaken(title);
            if(taken == 1)
                isTaken = true;


            //displaying time
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
                    if(isTaken)
                    {
                        Toast.makeText(getApplicationContext(),
                                "You've taken this quiz already!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                        startQuiz();
                }
            });

        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }

}

