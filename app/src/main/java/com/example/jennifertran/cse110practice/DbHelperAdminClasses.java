package com.example.jennifertran.cse110practice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by emd_000 on 11/16/2015.
 */
public class DbHelperAdminClasses extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "AdminClasses.db";
    // tasks table name
    // tasks Table Columns names
    private static final String KEY_CLASS= "class";
    private static final String KEY_INDEXER = "indexer";
    private List<String> childrenCols;
    private String table;
    private static final int CLASS_INDEX = 0;


    public DbHelperAdminClasses(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public DbHelperAdminClasses(Context context, String tableName, List<String> columns)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.childrenCols = columns;
        this.table = "`"+tableName+"Classes`";
    }
    public void createTable(){
        String colQuery = "";
        for(int i = 0; i < childrenCols.size(); i++)
        {
            colQuery += childrenCols.get(i)+" TEXT, ";
        }
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " ( " +
                KEY_CLASS + " TEXT, " + colQuery + KEY_INDEXER + " INTEGER)";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create sqlQuery in format col1 TEXT, col2 TEXT, col3 TEXT
        //to add dynamic number of columns to database
        String colQuery = "";
        for(int i = 0; i < childrenCols.size(); i++)
        {
            colQuery += childrenCols.get(i)+" TEXT, ";
        }
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " ( " +
                KEY_CLASS + " TEXT, " + colQuery + KEY_INDEXER + " INTEGER)";
        db.execSQL(sql);

    }

    @Override
    public void onOpen (SQLiteDatabase db){

    }

    /* createSubNav (Map<String, List<String>> headerChildPairs
     *  headerChildPairs: Contains key value pairs where the key is the header of a set of quizzes
     *  and the value is a list of that header's children (actual quizzes
     *
     * CreateSubNav fills the suBnav database from headerChildPairs in the format :
     * Row: header child0 child1 ....
     */
    public void upgradeAdminClasses(Map<String,List<String>> headerChildPairs)
    {
        //The last child in the list of children for each header is actually that row's index

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, null, null); //Delete entries in old table
        db.execSQL("DROP TABLE IF EXISTS "+table);
        createTable();
        Iterator<?> keyIt = headerChildPairs.keySet().iterator();
        String currRow;
        List<String> currChildList;
        while(keyIt.hasNext())
        {

            currRow = (String) keyIt.next(); //current header
            ContentValues colValuePairs = new ContentValues();
            colValuePairs.put("class", currRow); //key represents column names
            currChildList =  headerChildPairs.get(currRow);

            //Iterate until last child. The last child is actually the index of the row.
            for(int i = 0; i < currChildList.size()-1; i++)
            {
                colValuePairs.put(childrenCols.get(i),  currChildList.get(i));
            }
            int indexer = Integer.valueOf(currChildList.get(currChildList.size()-1));
            colValuePairs.put("indexer",indexer);
            db.insert(table, null, colValuePairs);

        }
        db.close();
    }


    /*
     * loadAdminClasses (String table)
     * dbHelperAdminClasses is a helper class which mimics the SQL format of quizzes that is used
     * on our remote database.
     * Parameters: String username - 'username' should be the username of the currently logged user
     * Returns: Pair<ArrayList<String>, HashMap<String,List<String>>> - Returns
     *              1. An ArrayList representing the headers of each quiz topic
     *              2. A HashMap that that pairs header keys with a List of that header's children
     *          The header and child information is used in SubjectNavActivity to create
     *          an expandable list where the unexpanded tab is a quiz topic, and the expanded tabs
     *          are specific quizzes belonging to that topic.
     *
     */
    public Pair<ArrayList<String>, HashMap<String,List<String>>> loadAdminClasses (String username) {

        this.table = "`"+username + "Classes`";

        String selectionQuery = "SELECT * FROM "+this.table+" ORDER BY indexer";
        try {
            Cursor dataCurs = this.getWritableDatabase().rawQuery(selectionQuery, null);
            dataCurs.moveToFirst();
            HashMap<String, List<String>> headerChildPairs = new HashMap<>();
            ArrayList<String> headers = new ArrayList<>();
            do{
                List<String> children = new ArrayList<>();
                String header = dataCurs.getString(CLASS_INDEX);//Get Header
                headers.add(header);

                //Iterate starts after header and ends before indexer.
                for(int i = CLASS_INDEX + 1; i < dataCurs.getColumnCount() -1; i++) {
                    if(dataCurs.getString(i) != null)  //If child isn't null, or "" add to list of child
                        if(!dataCurs.getString(i).equals(""))
                            children.add(dataCurs.getString(i));
                }
                headerChildPairs.put(header, children);
            }
            while(dataCurs.moveToNext());
            dataCurs.close();

            return new Pair<>(headers,headerChildPairs);
        }catch(SQLiteException e) {
            e.printStackTrace();
        }

        return null;

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + table);
        // Create tables again
        onCreate(db);
    }
}
