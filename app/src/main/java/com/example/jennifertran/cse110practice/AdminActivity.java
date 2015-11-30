package com.example.jennifertran.cse110practice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

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
    String newQuiz;
    String newClass;
    String selectedClass;
    String selectedItem;
    Pair<String,String> quizParent;

    final String DEFAULT_TITLE = "Classes";
    boolean addQuizMode = false;
    boolean deleteMode  = false;
    boolean deleteClass = false;
    boolean deleteQuiz = false;


    final Context context = this;

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

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logingOut = new Intent(AdminActivity.this, LoginActivity.class);
                logingOut.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(logingOut);
                finish();
            }
        });

        findViewById(R.id.button_new_class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set title
                alertDialogBuilder.setTitle("Add New Class");
                final EditText input = new EditText(AdminActivity.this);
                input.setHint("Input the name of a new class");
                alertDialogBuilder.setView(input);

                // set dialog message
                alertDialogBuilder
                        .setMessage("Adding a new class.")
                        .setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                newClass = input.getText().toString();
                                newClass = newClass.replaceAll("[\n\r]", "");
                                new AttemptAddClass().execute();

                                //TODO add yes stuff
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }

                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();

                /*
                listAdapter.addHeader("TEST");
                listAdapter.notifyDataSetChanged(); */
            }
        });
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
                        if ((!n.equals("class")) && (!n.equals("indexer")))
                            columns.add(n);
                    }
                    Map<String, List<String>> classChildPairs = new HashMap<>();


                    //Handle Dynamic Number of Children
                    for (int i = 0; i < jTable.length(); i++) {
                        //
                        List<String> children = new ArrayList<>();
                        currRow = jTable.getJSONObject(i);
                        String aClass = currRow.getString("class");
                        String indexer = currRow.getString("indexer");
                        currRow.remove("class");
                        currRow.remove("indexer");
                        keyIt = currRow.keys();
                        while (keyIt.hasNext()) {
                            children.add(currRow.getString((String) keyIt.next()));
                        }
                        children.add(String.valueOf(indexer)); //Put indexer as last child.
                        classChildPairs.put(aClass, children);
                    }

                /*
                    Create local subject nav table to hold subjects and their respective
                    quizzes. Insert class and childList pairs into local database
                 */

                    DbHelperAdminClasses db = new DbHelperAdminClasses(AdminActivity.this, username, columns);
                    db.createTable();
                    db.upgradeAdminClasses(classChildPairs); //store subNav.db locally
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return String.valueOf(table.equals(""));
        }

        protected void onPostExecute(String message){
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            System.out.println("MESSAGE "+message);
            if(message.equals("false")) { // != ""
                loadClasses();
            }

        }
    }

    private void loadClasses() {

        DbHelperAdminClasses db = new DbHelperAdminClasses(this);
        Pair<ArrayList<String>, HashMap<String, List<String>> > pair = db.loadAdminClasses(username);
        System.out.println(pair.second);
        if(pair == null){
            return;
        }
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
                if (addQuizMode) {
                    addQuizMode = false;
                    ActionMenuItemView addQuizButton  = (ActionMenuItemView)findViewById(R.id.action_add_question);
                    addQuizButton.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_add_white_48dp, null));


                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set title
                    alertDialogBuilder.setTitle("Add New Quiz");
                    final EditText input = new EditText(AdminActivity.this);
                    selectedClass = (String) listAdapter.getGroup(groupPosition);
                    input.setHint("Input the name of a new quiz");
                    alertDialogBuilder.setView(input);

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Adding a new quiz to \"" +
                                    listAdapter.getGroup(groupPosition)+"\" .")
                            .setCancelable(false)
                            .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    newQuiz = input.getText().toString();
                                    newQuiz = newQuiz.replaceAll("[\n\r]", "");
                                new AttemptAddQuiz().execute();

                                    //TODO add yes stuff
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }

                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }else if( deleteMode)
                {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // set title
                    alertDialogBuilder.setTitle("Deleting Class");
                    selectedItem = (String) listAdapter.getGroup(groupPosition);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("You are about to delete: \"" +
                                    listAdapter.getGroup(groupPosition)+"\" ")
                            .setCancelable(false)
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteClass = true;
                                    new AttemptDeleteItem().execute();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }

                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }
                return false;

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if (deleteMode) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // set title
                    alertDialogBuilder.setTitle("Deleting Quiz");
                    quizParent = new Pair<>((String) listAdapter.getChild(groupPosition, childPosition)
                            ,(String) listAdapter.getGroup(groupPosition));
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("You are about to delete: \"" +
                                    listAdapter.getChild(groupPosition, childPosition) + "\" ")
                            .setCancelable(false)
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //deleteSelectedItem
                                    deleteQuiz = true;
                                    new AttemptDeleteItem().execute();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }

                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                } else {

                    // sending intent to Startup Page
                    Intent intent = new Intent(getApplicationContext(), AdminStartupPageActivity.class);
                    String subject = listDataClass.get(groupPosition);
                    String classTitle = listDataChild.get(listDataClass.get(groupPosition)).get(childPosition);

                    intent.putExtra("title", classTitle);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
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
               getSupportActionBar().setTitle(DEFAULT_TITLE);
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

        switch (item.getItemId()) {
            case R.id.action_add_question:
                addNewQuizDialogue();
                return true;
            case R.id.action_delete_question:
                deleteClassOrQuizDialogue();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
    public void addNewQuizDialogue(){
        if(!deleteMode) {
            addQuizMode = !addQuizMode;
        }
        else{
            Toast.makeText(this, "Tap the stop sign to exit delete mode.", Toast.LENGTH_SHORT).show();
        }
        if(!addQuizMode){
            ActionMenuItemView addQuizButton  = (ActionMenuItemView)findViewById(R.id.action_add_question);
            addQuizButton.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_add_white_48dp, null));
        }
        else{
            ActionMenuItemView addQuizButton  = (ActionMenuItemView)findViewById(R.id.action_add_question);
            addQuizButton.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_clear_white_48dp, null));
            Toast.makeText(this, "Tap a class you want to add a quiz to.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteClassOrQuizDialogue(){
        if(!addQuizMode) {
            deleteMode = !deleteMode;
        }
        else{
            Toast.makeText(this, "Tap the X to exit add quiz mode.", Toast.LENGTH_SHORT).show();
        }
        if(!deleteMode){
            ActionMenuItemView addQuizButton  = (ActionMenuItemView)findViewById(R.id.action_delete_question);
            addQuizButton.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_white_48dp, null));
        }
        else{
            ActionMenuItemView addQuizButton  = (ActionMenuItemView)findViewById(R.id.action_delete_question);
            addQuizButton.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_remove_circle_white_48dp, null));
            Toast.makeText(this, "Select a class or quiz you would like to delete.", Toast.LENGTH_SHORT).show();

        }

    }

    class AttemptAddQuiz extends AsyncTask<String, String, String>{
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(AdminActivity.this);
            pDialog.setMessage("Adding Quiz");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected String doInBackground(String... params) {
            RemoteDBHelper remDb = new RemoteDBHelper();
            String table = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                    "SELECT * FROM "+username+"Classes WHERE class='"+selectedClass+"'",
                    loginUrl);
            try {
                JSONArray jTable = new JSONArray(table);
                JSONObject currRow;
                List<String> columns = new ArrayList<>();
                currRow = jTable.getJSONObject(0);
                Iterator<?> keyIt = currRow.keys();
                String colString="";
                int numColsFilled = 0;
                while(keyIt.hasNext()) //get column names for new local database
                {
                    String n = (String) keyIt.next();
                    String nEntry = (String) currRow.get(n);
                    //Add all child columns to columns

                    if((!n.equals("class")) && (!n.equals("indexer"))) {
                        columns.add(n);
                        colString += "'',";
                        if(!nEntry.equals(""))
                        {
                            numColsFilled++;
                        }
                    }
                }

                int numColsLeft = columns.size() - numColsFilled;

                System.out.println("COLUMNS: "+columns.size());
                System.out.println("NUMCOLSLEFT: "+numColsLeft);
                System.out.println("NUMCOLSFILLED: "+numColsFilled);


                //If we need another column in order for the current row to hold a new quiz, add
                //a new column.
                if(numColsLeft == 0)
                {

                    //Add new Column
                    String addColQuery =
                            "ALTER TABLE `"+username+"Classes` ADD child"+
                                    columns.size()+" TEXT AFTER child"+(columns.size()-1);
                    remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                            addColQuery,
                            loginUrl);

                    //Update value of new column in selectedClass to newQuiz
                    String updateColQuery = "UPDATE `" + username
                            + "Classes` SET child"+columns.size()+"='"+newQuiz+
                            "' WHERE class='"+selectedClass+"'";
                    remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                            updateColQuery, loginUrl);

                    //Update value of new column in NOT selectedClass to ''
                    String updateRestOfColumnsToEmpty = "UPDATE `" + username
                            + "Classes` SET child"+columns.size()+ "='' " +
                            "WHERE class<>'"+selectedClass+"'"; //<> equals !=
                    remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                            updateRestOfColumnsToEmpty, loginUrl);


                }else
                {
                    //Update column which already exists in selectedClass to newQuiz
                    String addToColQuery = "UPDATE `" + username
                            + "Classes` SET child"+(numColsFilled)+"='"+newQuiz+"' " +
                            "WHERE class='"+selectedClass+"'";
                    remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                            addToColQuery, loginUrl);

                }

                String testIfExistsTable = "SELECT * FROM `"+newQuiz+"`";
                String newQuizExists = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        testIfExistsTable,
                        loginUrl);
                if(newQuizExists.equals("")) //newQuizExists == "" if newQuiz didn't exist
                {
                    //Create a quiz which supports by default, 3 radio buttons.
                    String createQuizStr = "CREATE TABLE IF NOT EXISTS `" + newQuiz + "` " +
                            "( id INTEGER, question TEXT, answer TEXT, " +
                            "option0 TEXT, solution TEXT, " +
                            "marked VARCHAR(50) )";
                    remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                            createQuizStr,
                            loginUrl);

                    //TODO Doesn't currently create default question
                    //Create a default question in the form:
                    //(id, question, answer, option0, option1, option2, solution, marked)
                    String createDefault = "INSERT INTO `"+newQuiz+"` VALUES ('0', 'Add a question!'," +
                            "'','Add an option!', 'Add a solution!', '')";
                    remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                            createDefault,
                            loginUrl);

                }

            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String message){
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            new AttemptUpdateClasses().execute();
        }


    }

    class AttemptAddClass extends AsyncTask<String, String, String>{
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(AdminActivity.this);
            pDialog.setMessage("Adding Quiz");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected String doInBackground(String... params) {
            RemoteDBHelper remDb = new RemoteDBHelper();
            String table = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                    "SELECT * FROM "+username+"Classes",
                    loginUrl);
            try {
                JSONArray jTable = new JSONArray(table);
                JSONObject currRow;
                List<String> columns = new ArrayList<>();
                currRow = jTable.getJSONObject(0);
                Iterator<?> keyIt = currRow.keys();
                String colString="";
                while(keyIt.hasNext()) //get column names for new local database
                {
                    String n = (String) keyIt.next();

                    //Add all child columns to columns
                    if((!n.equals("class")) && (!n.equals("indexer"))) {
                        columns.add(n);
                        colString += "'',";
                    }
                }
                remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        "INSERT INTO `" + username + "Classes` VALUES ( '"+newClass+"', "+colString+" '')",
                        loginUrl);


            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String message){
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            new AttemptUpdateClasses().execute();
        }
    }

    class AttemptDeleteItem extends AsyncTask<String, String, String>
    {
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(AdminActivity.this);
            if(deleteClass)
                pDialog.setMessage("Deleting Class....");
            else
                pDialog.setMessage("Deleting Quiz....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }
        @Override
        protected String doInBackground(String... params) {
            RemoteDBHelper remDb = new RemoteDBHelper();
            if(deleteClass) {
                /*Delete quiz from current admin */
                String adminTable = username+"Classes";
                String delQuizAdmin = "DELETE FROM `"+adminTable+"` WHERE class='"+selectedItem+"'";
                remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        delQuizAdmin, loginUrl);

                /* For each user, delete the entire class col name is "header" */
                String selUsernames = "SELECT username FROM Users";
                String table = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        selUsernames,
                        loginUrl);
                try{
                    JSONArray jTable = new JSONArray(table);
                    for(int i = 0; i < jTable.length(); i++)
                    {
                        String userQuizTable = jTable.getJSONObject(i).getString("username")+"Quizzes";
                        String delQuizUser = "DELETE FROM `"+userQuizTable+"` WHERE header='"+selectedItem+"'";
                        remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                                delQuizUser, loginUrl);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
            else if(deleteQuiz) //Delete
            {
                /*Delete quiz from current admin */
                String classTable = username+"Classes";
                int indexOfSel =
                        remDb.findIndexOfEntry(classTable, quizParent.first, quizParent.second,
                                "class","child",
                                getApplicationContext().getString(R.string.remotePass),loginUrl );
                String delQuiz = "UPDATE `"+classTable+"` SET child"+indexOfSel+
                        "='' WHERE class='"+quizParent.second+"'";
                remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        delQuiz, loginUrl);

                /* Delete quiz from other users */

                String selUsernames = "SELECT username FROM Users";
                String table = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        selUsernames,
                        loginUrl);
                try{
                    JSONArray jTable = new JSONArray(table);
                    for(int i = 0; i < jTable.length(); i++)
                    {
                        String quizTable = jTable.getJSONObject(i).getString("username")+"Quizzes";
                        int indexOfSelUser =
                                remDb.findIndexOfEntry(quizTable, quizParent.first, quizParent.second,
                                        "header","child",
                                getApplicationContext().getString(R.string.remotePass),loginUrl );
                        String delQuizUser = "UPDATE `"+quizTable+"` SET child"+indexOfSelUser+
                                "='' WHERE header='"+quizParent.second+"'";
                        remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                                delQuizUser, loginUrl);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                /* Delete actual quiz */
                String deleteQuiz = "DROP TABLE `"+quizParent.first+"`";
                remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        deleteQuiz, loginUrl);

            }

            return null;
        }

        protected void onPostExecute(String message){
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            deleteClass = false;
            deleteQuiz  = false;
            new AttemptUpdateClasses().execute();
        }
    }

}
