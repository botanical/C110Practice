package com.example.jennifertran.cse110practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

public class SubjectNavActivity extends Activity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ProgressDialog pDialog;


    public final static String EXTRA_MESSAGE = "extra message?"; // for sending intent
    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Recieve data from LoginActivity
        Intent loginIntent = getIntent();
        username = loginIntent.getStringExtra("username");


        setContentView(R.layout.activity_subject_nav);
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        new AttemptLoadQuizzes().execute();


/*
        // Adding child data
        listDataHeader.add("Math");
        listDataHeader.add("History");
        listDataHeader.add("Science");

        // Adding child data
        List<String> subMath = new ArrayList<>();
        //subMath.add("Addition");
        //subMath.add("Subtraction");
        subMath.add("Multiplication and Division");
        /*subMath.add("Division");
        subMath.add("Negative Numbers");
        subMath.add("Algebraic Expressions");
        subMath.add("Fractions");

        List<String> subHistory = new ArrayList<>();
        //subHistory.add("Presidents");
        /*subHistory.add("Revolutonary War");
        subHistory.add("Spanish American War");
        subHistory.add("Civil War");
        subHistory.add("World War I");
        subHistory.add("World War II");

        List<String> subScience = new ArrayList<>();
        /*subScience.add("The Butterfly");
        subScience.add("Magnetism");
        subScience.add("Kinetic Forces");
        //subScience.add("Basic Chemistry");
        //subScience.add("Astronomy");

        listDataChild.put(listDataHeader.get(0), subMath); // Header, Child data
        listDataChild.put(listDataHeader.get(1), subHistory);
        listDataChild.put(listDataHeader.get(2), subScience); */
    }

    class AttemptLoadQuizzes extends AsyncTask<String,String,String> {
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

            //'ORDER BY' orders Quiz headers by the column 'indexer'
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
                            List<String> subList = new ArrayList<>();
                            //Iterate over remaining keys which at this point should be our subList
                            //entries.
                            while (keyIt.hasNext()) {
                                String k = (String) keyIt.next();
                                if (!jO.getString(k).equals(""))
                                    subList.add(jO.getString(k));
                            }
                            listDataChild.put(listDataHeader.get(i), subList);

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

            // Listview Group expanded listener
            expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
                    Toast.makeText(getApplicationContext(),
                            listDataHeader.get(groupPosition) + " Expanded",
                            Toast.LENGTH_SHORT).show();
                }
            });

            // Listview Group collasped listener
            expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

                @Override
                public void onGroupCollapse(int groupPosition) {
                    Toast.makeText(getApplicationContext(),
                            listDataHeader.get(groupPosition) + " Collapsed",
                            Toast.LENGTH_SHORT).show();

                }
            });

            // Listview on child click listener
            expListView.setOnChildClickListener(new OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    // TODO Auto-generated method stub
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
        }
    }

}