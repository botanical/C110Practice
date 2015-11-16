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
    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginUrl = getApplicationContext().getString(R.string.queryUrl);
        //Recieve data from LoginActivity
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
    //TODO Implemet loadSubNav in dbhelper sub nav
    private void loadLocalQuizzes() {
        DbHelperSubNav db = new DbHelperSubNav(this);
        Pair<ArrayList<String>, HashMap<String, List<String>> > pair = db.loadSubNav(username);
        listDataHeader = pair.first;
        listDataChild  = pair.second;
        System.out.println(listDataChild);

        listAdapter = new ExpandableListAdapter(SubjectNavActivity.this, listDataHeader,
                listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();

                // sending intent to Startup Page
                Intent intent = new Intent(getApplicationContext(), StartupPage.class);
                String subjectMessage = listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition);
                intent.putExtra("Subject", subjectMessage);
                intent.putExtra("Source", "SubjectNavActivity");
                startActivity(intent);
                return false;
            }
        });


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
                    "SELECT * FROM  " + username + "Quizzes ORDER BY indexer ASC",
                    loginUrl);

            /*
                Update list of subjects and their associated quizzes to local database;
             */
            try {
                JSONArray jTable = new JSONArray(table);
                JSONObject currRow;
                List<String> columns = new ArrayList<>();
                currRow = jTable.getJSONObject(0);
                Iterator<?> keyIt = currRow.keys();
                while(keyIt.hasNext()) //get column names for new local database
                {
                    String n = (String) keyIt.next();

                    //Add all child columns to columns
                    if((!n.equals("header")) && (!n.equals("indexer")))
                        columns.add(n);
                }
                Map<String,List<String>> headerChildPairs = new HashMap<>();

                for (int i = 0; i < jTable.length(); i++) {
                    //
                    List<String> children = new ArrayList<>();
                    currRow = jTable.getJSONObject(i);
                    String header = currRow.getString("header");
                    String indexer = currRow.getString("indexer");
                    currRow.remove("header");
                    currRow.remove("indexer");
                    keyIt = currRow.keys();
                    while(keyIt.hasNext())
                    {
                        children.add(currRow.getString((String) keyIt.next()));
                    }
                    children.add(String.valueOf(indexer)); //Put indexer as last child.
                    headerChildPairs.put(header, children);
                }

                /*
                    Create local subject nav table to hold subjects and their respective
                    quizzes. Insert header and childList pairs into local database

                 */

                DbHelperSubNav db = new DbHelperSubNav(SubjectNavActivity.this,username,columns);
                db.upgradeSubNav(headerChildPairs); //store subnav db locally


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

    /*class AttemptLoadQuizzes extends AsyncTask<String,String,String> {
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(SubjectNavActivity.this);
            pDialog.setMessage("Loading Quizzes...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            //Query for headers and children
            Map<String,String> params = new HashMap<>();
            params.put("auth","qwepoi12332191827364");
            //Sql queries quizes for a particular user. The sql database names the user
            // specific list of headers and quizzes: usernameQuizzes
            params.put("query", "SELECT * FROM "+username+"Quizzes ORDER BY indexer ASC");
            JSONParser p = new JSONParser();
            JSONArray j = p.makeHttpRequest(getApplicationContext().getString(R.string.queryUrl),
                    "POST", params);
            return j.toString();



        }
        protected void onPostExecute(String message) {
            if (pDialog != null)
                pDialog.dismiss();

            try {
                JSONArray j = new JSONArray(message);
                    for (int i = 0; i < j.length(); i++) {
                        try {
                            JSONObject jO = j.getJSONObject(i); //jO represents a row
                            //Add headers to header list then remove from keys
                            listDataHeader.add(jO.getString("header"));
                            jO.remove("header");
                            jO.remove("indexer");
                            Iterator<?> keyIt = jO.keys();

                            //Create subList to store children of current header
                            List<String> childList = new ArrayList<>();

                            //Iterate over remaining keys which at this point should be our childList
                            //entries.
                            while (keyIt.hasNext()) {
                                String k = (String) keyIt.next();
                                if (!jO.getString(k).equals(""))
                                    childList.add(jO.getString(k));
                            }
                            listDataChild.put(listDataHeader.get(i), childList);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
            }catch(Exception e){
                e.printStackTrace();
            }

            listAdapter = new ExpandableListAdapter(SubjectNavActivity.this, listDataHeader,
                    listDataChild);

            // setting list adapter
            expListView.setAdapter(listAdapter);

            // Listview Group click listener
            expListView.setOnGroupClickListener(new OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    // Toast.makeText(getApplicationContext(),
                    // "Group Clicked " + listDataHeader.get(groupPosition),
                    // Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            // Listview on child click listener
            expListView.setOnChildClickListener(new OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    /*
                    Toast.makeText(
                            getApplicationContext(),
                            listDataHeader.get(groupPosition)
                                    + " : "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition), Toast.LENGTH_SHORT)
                            .show();
                    *
                    // sending intent to Startup Page
                    Intent intent = new Intent(getApplicationContext(), StartupPage.class);
                    String subjectMessage = listDataHeader.get(groupPosition)
                            + " : "
                            + listDataChild.get(
                            listDataHeader.get(groupPosition)).get(
                            childPosition);
                    intent.putExtra(EXTRA_MESSAGE, subjectMessage);
                    startActivity(intent);
                    return false;
                }
            });
            System.out.println(listDataChild.toString());
        }*/


}