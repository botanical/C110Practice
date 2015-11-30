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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.auth.Subject;

public class SubjectNavActivity extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    HashMap<String,String> classPairs;
    ArrayList<String> classes;
    ProgressDialog pDialog;
    private String loginUrl;
    private SQLiteDatabase loc;
    String selectedClass;
    String selectForDelete;
    View removeClass;
    Boolean remClass = false;

    public final static String EXTRA_MESSAGE = "extra message?"; // for sending intent
    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
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

        findViewById(R.id.addClass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AttemptGetClass().execute();
            }
        });


        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loggingOut = new Intent(SubjectNavActivity.this, LoginActivity.class);
                loggingOut.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(loggingOut);
                finish();
            }
        });
        findViewById(R.id.removeClass).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                remClass = !remClass;
                Toast.makeText(SubjectNavActivity.this,
                        "Tap on a class to remove it from your list of classes.",
                        Toast.LENGTH_SHORT).show();
                if(remClass)
                    findViewById(R.id.removeClass).setBackgroundColor(Color.RED);
                else
                    findViewById(R.id.removeClass).setBackgroundColor(Color.LTGRAY);
            }
        });
        findViewById(R.id.removeClass).setBackgroundColor(Color.LTGRAY);
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
                else{
                    DbHelperSubNav db = new DbHelperSubNav(SubjectNavActivity.this, username, null);
                    db.createTable();
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
            System.out.println("tableAdmins: " + tableAdmins);
            try {
                if(!tableAdmins.equals("")) {
                    JSONArray jTable = new JSONArray(tableAdmins);
                    ArrayList<String> adminNames = new ArrayList<>();
                    for(int i = 0; i < jTable.length(); i++) {

                        JSONObject currRow = jTable.getJSONObject(i);
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
                    ArrayList<String> c = new ArrayList<>();

                    //Store pairs as key=Class value=teacher
                    classPairs = new HashMap<>();
                    for(Pair p : classes) {
                        c.add((String) p.second);
                        classPairs.put((String) p.second,(String) p.first);
                    }
                    SubjectNavActivity.this.classes = c;
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String message) {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            final ListPopupWindow listPopupWindow = new ListPopupWindow(
                    SubjectNavActivity.this);

            System.out.println(classes);
            listPopupWindow.setAdapter(new ArrayAdapter(
                    SubjectNavActivity.this,
                    R.layout.class_list_item, classes.toArray()));
            listPopupWindow.setAnchorView(findViewById(R.id.addClass));
            View v = findViewById(R.id.mainSubNav);
            listPopupWindow.setWidth(v.getWidth());
            listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
            listPopupWindow.setModal(true);
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedClass = (String) parent.getItemAtPosition(position);
                    listPopupWindow.dismiss();
                    new AttemptAddClass().execute();
                }
            });
            listPopupWindow.show();


        }
    }


    class AttemptAddClass extends AsyncTask<String,String,String>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(SubjectNavActivity.this);
            pDialog.setMessage("Adding Class");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            RemoteDBHelper remDb = new RemoteDBHelper();

            //Get new quizzes to add
            String sqlSelect = "SELECT * FROM `"+classPairs.get(selectedClass)+"Classes` WHERE " +
                    "class='"+selectedClass+"'";
            String newQuizzes = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                    sqlSelect, loginUrl);



            try {
                if(!newQuizzes.equals("")) {
                    JSONArray jTable = new JSONArray(newQuizzes);
                    JSONObject currRow = jTable.getJSONObject(0);
                    String header = currRow.getString("class");
                    currRow.remove("class");
                    String indexer = currRow.getString("indexer");
                    currRow.remove("indexer");
                    ArrayList<String> cols = new ArrayList<>();
                    for(int i = 0; i < currRow.length(); i++){
                        cols.add(currRow.getString("child"+i));
                    }

                    String addColumnsQuery ="SELECT * FROM `"+username+"Quizzes` ";
                    String detectCols = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                            addColumnsQuery, loginUrl);
                    System.out.println("DETECT: "+detectCols);
                    //TODO if user doesn't have at least 1 entry, they have by default 2 entries
                    if(!detectCols.equals(""))
                    {
                        JSONArray jTableNew = new JSONArray(detectCols);
                        JSONObject currRowNew = jTableNew.getJSONObject(0);
                        currRowNew.remove("header");
                        currRowNew.remove("indexer");
                        int userColSize = currRowNew.length();
                        System.out.println("USERCOLSIZE: "+userColSize);
                        System.out.println("COLSIZE: "+cols.size());
                        System.out.println("COLS: "+cols);


                        if(cols.size() > userColSize)
                        {

                            for(int i = userColSize; i < cols.size(); i++)
                            {
                                String increaseColsNoQuizzes = "ALTER TABLE `"+username+"Quizzes` ADD " +
                                        "child"+i+" TEXT AFTER child"+(i-1);
                                remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                                        increaseColsNoQuizzes, loginUrl);
                            }
                        }

                    }else {
                        if(cols.size() > 2){

                            for(int i = 2; i < cols.size(); i++){
                                String increaseColsNoQuizzes = "ALTER TABLE `"+username+"Quizzes` ADD " +
                                        "child"+i+" TEXT AFTER child"+(i-1);
                                remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                                        increaseColsNoQuizzes, loginUrl);

                            }

                        }
                    }


                    String colQuery = "";
                    for(int i = 0; i < cols.size(); i++)
                    {
                            colQuery += "'"+cols.get(i)+"', ";
                    }

                    String insert = "INSERT INTO `"+username+"Quizzes` VALUES ('"+header+"', " +
                            colQuery+" '"+indexer+"')";
                    remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                            insert, loginUrl);

                    for(int i = 0; i < cols.size(); i++)
                    {
                        String insertTaken= "INSERT INTO `"+username+"Taken` VALUES ('"+cols.get(i)+"', '0')";
                        remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                                insertTaken, loginUrl);
                    }


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
            new AttemptUpdateQuizzes().execute();



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
                if(remClass)
                {
                    selectForDelete = (String) listAdapter.getGroup(groupPosition);
                    remClass = !remClass;
                    findViewById(R.id.removeClass).setBackgroundColor(Color.LTGRAY);
                    new AttemptRemoveClass().execute();
                }



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
                String message = subject + " : " + quizTitle;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onPrepareOptionsMenu(Menu menu){
        menu.clear();
        getMenuInflater().inflate(R.menu.activity_edit_quiz, menu);
        return super.onPrepareOptionsMenu(menu);
    }
    /* Used for Inflating Activity Bar if Items are present */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_edit_quiz, menu);
        return true;
    }

    class AttemptRemoveClass extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(SubjectNavActivity.this);
            pDialog.setMessage("Adding Class");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            RemoteDBHelper remDb = new RemoteDBHelper();
            //Get new quizzes to add
            String sqlSelect = "DELETE FROM `"+username+"Quizzes` WHERE " +
                    "header='"+selectForDelete+"'";
            remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                    sqlSelect, loginUrl);

            String selectQuizzesTaken = "SELECT * FROM `"+username+"Quizzes` WHERE "+
                    "header='"+selectForDelete+"'";
            String selected = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                    selectQuizzesTaken, loginUrl);

            try{
                if(!selected.equals("")){
                    JSONArray select = new JSONArray(selected);
                    JSONObject currRow = select.getJSONObject(0);
                    currRow.remove("header");
                    currRow.remove("indexer");
                    ArrayList<String> quizzes = new ArrayList<>();
                    for(int i = 0; i < currRow.length(); i++){
                        quizzes.add(currRow.getString("child"+i));
                    }
                    String delQuery = "DELETE FROM `"+username+"Taken` WHERE title=";
                    for(int i = 0; i < quizzes.size(); i++)
                    {
                        String tmp = delQuery + "'"+quizzes.get(i)+"'";
                        remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                                tmp, loginUrl);
                    }

                }
            }catch(Exception e ){
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(String message)
        {
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            new AttemptUpdateQuizzes().execute();

        }


    }
}