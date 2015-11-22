package com.example.jennifertran.cse110practice;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.auth.Subject;

public class SubjectNavActivity extends Activity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ProgressDialog pDialog;
    private String loginUrl;
    private SQLiteDatabase loc;

    public final static String EXTRA_MESSAGE = "extra message?"; // for sending intent
    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginUrl = getApplicationContext().getString(R.string.queryUrl);
        //Receive data from LoginActivity
        Intent loginIntent = getIntent();
        username = loginIntent.getStringExtra("username");

        /*Register token with server, listen for updates */
        RegistrationIntentService regIntent= new RegistrationIntentService("debug");
        Intent intent = new Intent(SubjectNavActivity.this, RegistrationIntentService.class);
        intent.putExtra("username", username);
        //regIntent.startActivity(intent);


        setContentView(R.layout.activity_subject_nav);
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loggingOut = new Intent(SubjectNavActivity.this, LoginActivity.class);
                loggingOut.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(loggingOut);
                finish();
            }
        });
    }

    /*
     * Preparing the list data
     */
    //TODO call loadLocalQuizzes if AttemptUpdateQuizzes isn't needed
    private void prepareListData() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        new AttemptUpdateQuizzes().execute();

        /*
            Currently, the app always loads quizzes from the database.
            When I add timestamps on Quiz tables, I'll compare the server timestamps
            to the local timestamps and only update if they're different.
         */
        //if attemptUpdateQuizzess is not needed then
        //loadLocalQuizzes();


    }
    class AttemptUpdateQuizzes extends AsyncTask<String,String,String>{

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(SubjectNavActivity.this);
            pDialog.setMessage("Attempting Update");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected String doInBackground(String... params) {
            RemoteDBHelper remDb = new RemoteDBHelper();
            String table = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                    "SELECT * FROM  `" + username + "Quizzes` ORDER BY indexer ASC",
                    loginUrl);

            /*
                Update list of subjects and their associated quizzes to local database;
             */
            try {
                if(!table.equals("")) {
                    JSONArray jTable = new JSONArray(table);
                    JSONObject currRow;
                    List<String> columns = new ArrayList<>();
                    currRow = jTable.getJSONObject(0);
                    Iterator<?> keyIt = currRow.keys();
                    while (keyIt.hasNext()) //get column names for new local database
                    {
                        String n = (String) keyIt.next();

                        //Add all child columns to columns
                        if ((!n.equals("header")) && (!n.equals("indexer")))
                            columns.add(n);
                    }
                    Map<String, List<String>> headerChildPairs = new HashMap<>();

                    for (int i = 0; i < jTable.length(); i++) {
                        //
                        List<String> children = new ArrayList<>();
                        currRow = jTable.getJSONObject(i);
                        String header = currRow.getString("header");
                        String indexer = currRow.getString("indexer");
                        currRow.remove("header");
                        currRow.remove("indexer");
                        keyIt = currRow.keys();
                        while (keyIt.hasNext()) {
                            children.add(currRow.getString((String) keyIt.next()));
                        }
                        children.add(String.valueOf(indexer)); //Put indexer as last child.
                        headerChildPairs.put(header, children);
                    }

                /*
                    Create local subject nav table to hold subjects and their respective
                    quizzes. Insert header and childList pairs into local database

                 */

                    DbHelperSubNav db = new DbHelperSubNav(SubjectNavActivity.this, username, columns);
                    db.createTable();
                    db.upgradeSubNav(headerChildPairs); //store subNav.db locally

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;

        }

        protected void onPostExecute(String message){
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            loadLocalQuizzes();

        }
    }

    class AttemptGetClass extends AsyncTask<String,String,String>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(SubjectNavActivity.this);
            pDialog.setMessage("Getting Classes");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            RemoteDBHelper remDb = new RemoteDBHelper();
            String sqlSelect = "SELECT username FROM Users WHERE is_admin=1";
            String tableAdmins = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                    sqlSelect, loginUrl);
            try {
                if(!tableAdmins.equals("")) {
                    JSONArray jTable = new JSONArray(tableAdmins);
                    JSONObject currRow = jTable.getJSONObject(0);
                    ArrayList<String> adminNames = new ArrayList<>();
                    for(int i = 0; i < jTable.length(); i++){
                        adminNames.add(currRow.getString("username"));
                    }

                    ArrayList<Pair<String,String>> classes = new ArrayList<>();
                    for(String admin : adminNames)
                    {
                        String sqlSelectClasses = "SELECT class FROM "+admin+"Classes";
                        String tableClasses = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                                sqlSelectClasses, loginUrl);
                        if(!tableClasses.equals(""))
                        {
                            JSONArray jTableClasses = new JSONArray(tableClasses);
                            for (int i = 0; i < jTableClasses.length(); i++){

                                JSONObject j = jTableClasses.getJSONObject(i);
                                Pair<String,String> adminClassPair =
                                        new Pair<>(admin,j.getString("class"));
                                classes.add(adminClassPair);
                            }
                        }
                    }
                    //TODO save classes so register button can display them.


                }
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String message)
        {
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();


        }


    }

    private void loadLocalQuizzes() {
        DbHelperSubNav db = new DbHelperSubNav(this);
        Pair<ArrayList<String>, HashMap<String, List<String>> > pair = db.loadSubNav(username);

        //TODO add behavior for this user has no quizzes
        if(pair == null) {
            return;
        }
        listDataHeader = pair.first;
        listDataChild  = pair.second;

        listAdapter = new ExpandableListAdapter(SubjectNavActivity.this, listDataHeader,
                listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                // sending intent to Startup Page
                Intent intent = new Intent(getApplicationContext(), StartupPage.class);
                String subject = listDataHeader.get(groupPosition);
                String quizTitle = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                String message = subject+ " : "+ quizTitle;

                intent.putExtra(EXTRA_MESSAGE, message);
                intent.putExtra("title", quizTitle);
                intent.putExtra("username", username);
                startActivity(intent);
                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}