package com.example.jennifertran.cse110practice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class AdminStartupPageActivity extends AppCompatActivity {

    Button start_button;
    int testTimeSend = 70000;
    int numQuestions;
    TextView startTime;
    private String title;
    private String username;
    private ProgressDialog pDialog;
    private String loginUrl;
    private ArrayList<String> columns;
    public final static String EXTRA_TIME = "Time: ";
    public boolean isTaken;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_startup_page);
        loginUrl = getApplicationContext().getString(R.string.queryUrl);
        // displaying subject text
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        title = intent.getStringExtra("title");
        setTitle(title);
        new AttemptUpdateQuiz().execute();
    }

    private void startQuiz() {
        Intent intent = new Intent(this, EditQuizActivity.class);
        intent.putExtra(EXTRA_TIME, testTimeSend);
        intent.putExtra("title",title);
        intent.putExtra("username", username);
        //Can't pass object to a different activity so must pass object as a string
        JSONArray jA = new JSONArray(columns);
        intent.putExtra("columns", jA.toString());
        startActivity(intent);
        finish();
    }

    class AttemptUpdateQuiz extends AsyncTask<String,String,String> {

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(AdminStartupPageActivity.this);
            pDialog.setMessage("Attempting Update Quiz");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected String doInBackground(String... params) {
            RemoteDBHelper remDb = new RemoteDBHelper();
            String table = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                    "SELECT * FROM  " + "`"+title+"` ", loginUrl);//BACKTICKS ARE CRITICAL
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
                while(keyIt.hasNext()) //get column names for new local database
                {
                    String n = (String) keyIt.next();

                    //Add all child columns to columns
                    if((!n.equals("id")) && (!n.equals("question")) && (!n.equals("answer")) &&
                            !n.equals("marked") && !n.equals("solution"))
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
                    String solution = currRow.getString("solution");
                    currRow.remove("solution");
                    keyIt = currRow.keys();
                    while(keyIt.hasNext())
                    {
                        options.add(currRow.getString((String) keyIt.next()));

                    }
                    System.out.println("options "+options);
                    options.add(solution);
                    options.add(String.valueOf(marked)); //Put indexer as last child.
                    questOpPairs.put(id, new Pair<>(quesAns ,options));
                }

                /*
                    Create local adminClasses table to hold subjects and their respective
                    quizzes. Insert header and childList pairs into local database
                 */
                AdminStartupPageActivity.this.columns = columns;
                DbHelperQuiz db = new DbHelperQuiz(AdminStartupPageActivity.this, title,columns);
                db.createTable();
                db.upgradeQuiz(questOpPairs); //store subNav.db locally
                System.out.println("questOPPAIRS "+questOpPairs);

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(String message){
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            TextView numQ = (TextView) findViewById(R.id.num_of_questions);
            numQ.setText(String.valueOf(numQuestions));

            // set listener of start button to call startQuiz() on press
            findViewById(R.id.start_quiz_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
