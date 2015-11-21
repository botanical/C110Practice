package com.example.jennifertran.cse110practice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    /* Adding member variables, strings, and booleans for fragments */
    private ListView mDrawerList;
    //private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    String open_drawer = "Current Section";
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<String> listDataClass;
    HashMap<String, List<String>> listDataChild;
    ProgressDialog pDialog;
    String username;
    String loginUrl;
    final String DEFAULT_TITLE = "Classes";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setTitle(DEFAULT_TITLE);
        expListView = (ExpandableListView) findViewById(R.id.expListAdmin);
        Intent received = getIntent();
        username = received.getStringExtra("username");
        loginUrl = getApplicationContext().getString(R.string.queryUrl);
        /******************************** Create Hamburger  *********************************/
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.navList);  /* Set ListView for Fragment */

        addDrawerItems();         /* Add drawer items */
        setupDrawer();
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        /*^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ Create Hamburger  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^*/


        if(username != null)
            new AttemptUpdateClasses().execute();

    }

    class AttemptUpdateClasses extends AsyncTask<String,String,String> {

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(AdminActivity.this);
            pDialog.setMessage("Updating Classes");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected String doInBackground(String... params) {
            RemoteDBHelper remDb = new RemoteDBHelper();
            String table = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                    "SELECT * FROM  `" + username + "Classes` ORDER BY indexer ASC",
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
                    if((!n.equals("class")) && (!n.equals("indexer")))
                        columns.add(n);
                }
                Map<String,List<String>> classChildPairs = new HashMap<>();

                for (int i = 0; i < jTable.length(); i++) {
                    //
                    List<String> children = new ArrayList<>();
                    currRow = jTable.getJSONObject(i);
                    String aClass = currRow.getString("class");
                    String indexer = currRow.getString("indexer");
                    currRow.remove("class");
                    currRow.remove("indexer");
                    keyIt = currRow.keys();
                    while(keyIt.hasNext())
                    {
                        children.add(currRow.getString((String) keyIt.next()));
                    }
                    children.add(String.valueOf(indexer)); //Put indexer as last child.
                    classChildPairs.put(aClass, children);
                }

                /*
                    Create local subject nav table to hold subjects and their respective
                    quizzes. Insert class and childList pairs into local database

                 */

                DbHelperAdminClasses db = new DbHelperAdminClasses(AdminActivity.this,username,columns);
                db.createTable();
                db.upgradeAdminClasses(classChildPairs); //store subNav.db locally

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String message){
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            loadClasses();

        }
    }

    private void loadClasses() {

        DbHelperAdminClasses db = new DbHelperAdminClasses(this);
        Pair<ArrayList<String>, HashMap<String, List<String>> > pair = db.loadAdminClasses(username);

        listDataClass = pair.first;
        listDataChild = pair.second;
        listAdapter = new ExpandableListAdapter(AdminActivity.this, listDataClass,
                listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                // sending intent to Startup Page
                Intent intent = new Intent(getApplicationContext(), AdminStartupPageActivity.class);
                String subject = listDataClass.get(groupPosition);
                String classTitle = listDataChild.get(listDataClass.get(groupPosition)).get(childPosition);

                intent.putExtra("title", classTitle);
                intent.putExtra("username", username);
                startActivity(intent);
                return false;
            }
        });
    }

    /* Helper method called by onCreate to add drawer items to Drawer */
    private void addDrawerItems() {

        //ArrayList<String> row;
        //Question currQuestion;
        //ArrayList<Question> questionList = quiz.getQuestions();
        FragmentNavigationAdapter mAdapter;
        FragmentNavigationTitle navTitle[] = new FragmentNavigationTitle[1];
                navTitle[0] = new FragmentNavigationTitle (R.drawable.ic_answered_question_24px
                        , R.drawable.ic_unviewed_question_24px, "TEST");
        //String[] questionNums = new String[questionList.size()]; //Used to hold question titles

        /*
        for (int i = 0; i < questionList.size(); i++) {
            currQuestion = questionList.get(i);
            //Add 1 to zero indexed question number
            //questionNums[i] = "Question " + String.valueOf(currQuestion.getId()+1);


            if (currQuestion.getViewed() == false && (currQuestion.getMarked() == -1)) {
                navTitle[i] = new FragmentNavigationTitle(R.drawable.ic_unanswered_question_24px,
                        R.drawable.ic_unviewed_question_24px,
                        "Question " + String.valueOf(currQuestion.getId() + 1));
            }
            else if (currQuestion.getViewed() == false && (currQuestion.getMarked() != -1)){
                navTitle[i] = new FragmentNavigationTitle(R.drawable.ic_answered_question_24px,
                        R.drawable.ic_unviewed_question_24px,
                        "Question " + String.valueOf(currQuestion.getId() + 1));
            }
            else if (currQuestion.getViewed() == true && (currQuestion.getMarked() == -1)){
                navTitle[i] = new FragmentNavigationTitle(R.drawable.ic_unanswered_question_24px,
                        R.drawable.ic_viewed_question_24px,
                        "Question " + String.valueOf(currQuestion.getId() + 1));
            }

            else if (currQuestion.getViewed() == true && (currQuestion.getMarked() != -1)){
                navTitle[i] = new FragmentNavigationTitle(R.drawable.ic_answered_question_24px,
                        R.drawable.ic_viewed_question_24px,
                        "Question " + String.valueOf(currQuestion.getId() + 1));
            }
        }
       */
        mAdapter =
                new FragmentNavigationAdapter(this, R.layout.fragment_navigation_titles, navTitle);

        //mAdapter = new ArrayAdapter<>(this, R.layout.fragment_navigation_titles, questionNums);

        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //goToQuestion(position);
                addDrawerItems();
            }
        });
    }
    /* Helper method called by onCreate to set up drawer items to Drawer */
    private void setupDrawer() {

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(open_drawer);
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if(getSupportActionBar() != null) {
                   // getSupportActionBar().setTitle("Question: " + String.valueOf(question_id + 1)
                     //       + "/" + String.valueOf(numOfQuestions));
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                setTitle(DEFAULT_TITLE);
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    /* Method is used for Fragment. Syncs the indicator to match current state */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    /* Method is used for Fragment. Makes smooth transitioning for orientation change */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    /* Used for Inflating Activity Bar if Items are present */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_quiz, menu);
        return true;
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

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
