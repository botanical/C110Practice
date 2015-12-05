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


/*
 * Name: StartupPage
 * Parent Activity: SubjectNavActivity
 * Purpose: To provide information to the user before they start the quiz, such as the length of time
 * of the quiz.
 * Children Activity: QuizActivity
 */


public class StartupPage extends AppCompatActivity {
    //Time we use for the quiz length
    int testTimeSend = 70000;

    //Textview we list the time of the quiz
    TextView startTime;

    //title of the quiz
    private String title;

    //the users name
    private String username;
    //progress dialog for async tasks
    private ProgressDialog pDialog;

    //domain for db query
    private String loginUrl;
    //columns we have in our db
    private ArrayList<String> columns;
    //Time we have left
    public final static String EXTRA_TIME = "Time: ";
    //is the quiz taken
    public boolean isTaken;
    //questions in the quiz
    public int numQuestions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the layout
        setContentView(R.layout.activity_startup_page);
        //url for the query
        loginUrl = getApplicationContext().getString(R.string.queryUrl);
        Intent intent = getIntent();
        //title of quiz
        title = intent.getStringExtra(SubjectNavActivity.EXTRA_MESSAGE);
        //username
        username = intent.getStringExtra("username");
        setTitle(title); // display subject and title in bar
        title = intent.getStringExtra("title");

        //update the quize
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

    //Start the new quiz!
    private void startQuiz() {
        Intent intent = new Intent(this, QuizActivity.class);
        //put the time/title/username. Need all these fields to take quiz/update db.
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

    //Db call to update the quiz
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
                            !n.equals("marked") &&!n.equals("solution"))
                        columns.add(n);
                }

                //Make a map from questions to the options for said questions.
                HashMap<String, Pair<Pair<String, String>, ArrayList<String>>> questOpPairs =
                        new HashMap<>();
                //The purpose of this loop is to update the values of the quiz to reflect the values
                //in the db.
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
                if(!takenTable.equals("")) {

                    jTable = new JSONArray(takenTable);
                    HashMap<String, Integer> quizTakenPairs = new HashMap<>();
                    for (int i = 0; i < jTable.length(); i++) {
                        currRow = jTable.getJSONObject(i);
                        quizTakenPairs.put(currRow.getString("title"), currRow.getInt("taken"));
                    }
                    DbHelperTaken dbTaken = new DbHelperTaken(StartupPage.this, username);
                    dbTaken.createTable();
                    dbTaken.upgradeTaken(quizTakenPairs);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        //After updating the quiz set the time text and update isTaken fields
        protected void onPostExecute(String message){
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            TextView numQ = (TextView) findViewById(R.id.num_of_questions);
            numQ.setText(String.valueOf(numQuestions));
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
    //If we press back we are done
    @Override
    public void onBackPressed() {
        finish();
    }

}

