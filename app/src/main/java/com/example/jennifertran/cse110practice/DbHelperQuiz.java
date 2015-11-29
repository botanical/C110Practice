package com.example.jennifertran.cse110practice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DbHelperQuiz extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "triviaQuiz.db";
    // tasks table name
    private String table;
    // tasks Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_QUES = "question";
    private static final String KEY_ANSWER = "answer"; //correct option
    private static final String KEY_MARKED = "marked"; //marked answer by user
    private static final String KEY_SOLUTION = "solution";
    private ArrayList<String> optionCols;

    private SQLiteDatabase dbase;
    public DbHelperQuiz(Context context, String tableName, ArrayList<String> optionCols) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.table = "`"+tableName+"`"; //need backticks in case table name has spaces
                                        //SINGLE QUOTES DO NOT WORK, MUST USE BACKTICKS
        this.optionCols = optionCols;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        dbase=db;
        String colQuery = "";
        for(int i = 0; i < optionCols.size(); i++)
        {
            colQuery += optionCols.get(i)+" TEXT, ";
        }
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_QUES
                + " TEXT, " + KEY_ANSWER+ " TEXT, "+colQuery+KEY_SOLUTION+" TEXT, "+KEY_MARKED+" TEXT)";
        //SOLUTION Column is currently included with colQuery
        System.out.println("ONCREATE QUERY: "+sql);

        //TODO remove solution from colQuery

        db.execSQL(sql);
        //db.close()
        //Sets question id's to start from 0 instead of 1
        String subtraction = "UPDATE " + table +  " SET " + KEY_ID + "=" + KEY_ID + " - 1";
        db.execSQL(subtraction);


    }
    /* createSubNav (Map<String, List<String>> headerChildPairs
     *  headerChildPairs: Contains key value pairs where the key is the header of a set of quizzes
     *  and the value is a list of that header's children (actual quizzes
     *
     * CreateSubNav fills the suBnav database from headerChildPairs in the format :
     * Row: header child0 child1 ....
     */
    public void upgradeQuiz(HashMap<String,Pair<Pair<String,String>, ArrayList<String>>>
                                    questOpPairs)
    {
        //The last child in the list of children for each header is actually that row's index

        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(table, null, null); //Delete entries in old table
        db.execSQL("DROP TABLE " + table);
        createTable();
        System.out.println("TABLE: " + table);
        System.out.println("DATABASE: ");
        Cursor cur = db.rawQuery("SELECT * FROM "+table,null);
        DatabaseUtils.dumpCursor(cur);


        Iterator<?> keyIt = questOpPairs.keySet().iterator();
        String currRow;
        ArrayList<String> currOptions;
        while(keyIt.hasNext())
        {

            currRow = (String) keyIt.next(); //current id
            ContentValues colValuePairs = new ContentValues();
            colValuePairs.put("id", currRow); //key represents column names

            currOptions =  questOpPairs.get(currRow).second;
            colValuePairs.put("question", questOpPairs.get(currRow).first.first);
            colValuePairs.put("answer", questOpPairs.get(currRow).first.second);

            //Iterate until last child. The last child is actually the index of the row.
            for(int i = 0; i < currOptions.size()-2; i++) //TODO make final instance const
            {
                colValuePairs.put(optionCols.get(i),  currOptions.get(i));
            }
            String solution = currOptions.get(currOptions.size() -2); //gets Solution
            String marked = currOptions.get(currOptions.size() - 1);  //gets Marked
            colValuePairs.put("marked", marked);
            colValuePairs.put("solution", solution);
            db.insert(table, null, colValuePairs);
        }
        System.out.println("TABLE: "+table);
        System.out.println("DATABASE AFTER: ");
        cur = db.rawQuery("SELECT * FROM "+table,null);
        DatabaseUtils.dumpCursor(cur);
        //db.close();
    }

    public void createTable()
    {
        System.out.println("CREATE TABLE " + table);
        String colQuery = "";
        for(int i = 0; i < optionCols.size(); i++)
        {
            colQuery += optionCols.get(i)+" TEXT, ";
        }
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_QUES
                + " TEXT, " + KEY_ANSWER+ " TEXT, "+colQuery+KEY_SOLUTION+" TEXT, "+KEY_MARKED+" TEXT )";

        System.out.println("CREATE TABLE QUERY "+sql);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + table);
        // Create tables again
        onCreate(db);
    }

    // Adding new question
    public void addQuestion(Question quest) {
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUES, quest.getQuestion());
        values.put(KEY_ANSWER, quest.getAnswer());
        values.put(KEY_MARKED, quest.getMarked());
        //values.put(KEY_MARKED, quest.getMARKED());
        // Inserting Row
        dbase.insert(table, null, values);
    }

    ArrayList<ArrayList<String>> getQuestions (String tableName)
    {
        ArrayList<String> row;
        ArrayList<ArrayList<String>> table = new ArrayList<>();
        for(int i = 0 ; (row = getQuestionAsArray(i)) != null ; i++) {
            table.add(row);
        }
        return table;
    }

    public int rowcount()
    {
        int row;
        String selectQuery = "SELECT  * FROM " + table;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        row=cursor.getCount();
        cursor.close();
        return row;
    }
    public void insertIntoDb(String id, ContentValues value) {
        int x = this.getWritableDatabase().update(table, value, KEY_ID + "=" + id, null);
        Log.d("UPDATED???", String.valueOf(x));
        Log.d("VAL", value.toString());
        Log.d("QID", id);
    }
    public String getQuestionEntry(String colName, int id ) {
        String marked;
        String selectQuery = "SELECT " + colName + " FROM " + table
                + " WHERE " + KEY_ID + "=" + id;
        Log.d("QUERY", selectQuery);
        Cursor cursor = this.getWritableDatabase().rawQuery(selectQuery, null);

        if  (cursor.moveToFirst()) {
            marked = cursor.getString(cursor.getColumnIndex(colName));
            Log.d("moved", "cursor moved!!");

            cursor.close();
            return marked;
        }
        else {
            cursor.close();
            return "null";
        }
    }


    /************************* Methods for adding questions to quiz object ***********************/


    public ArrayList<Question> getQuestionsAsQuestionArray(){
        ArrayList<ArrayList<String>> questions = getQuestionTable();
        ArrayList<Question> quests = new ArrayList<>();
        for (ArrayList<String> quest : questions) {
            quests.add(Question.arrayListToQuestion(quest));
        }

        return quests;
    }
    private ArrayList<ArrayList<String>> getQuestionTable() {
        ArrayList<ArrayList<String>> table = new ArrayList<>();
        ArrayList<String> row;
        for (int i = 0; ((row = getQuestionAsArray(i)) != null); i++) {
            table.add(row);
        }
        return table;
    }

    /*
     * getQuestion (int id)
     * getQuestion retrieves the a quiz question from the local db and returns it as an ArrayList
     * on our remote database.
     * Parameters: int id - question number of the desired question
     * Returns: ArrayList<String> - ArrayList representing the SQL row of the desired question
     *
     */
    private ArrayList<String> getQuestionAsArray(int id)
    {
        String selectQuery = "SELECT * FROM "+table+" WHERE "+KEY_ID+"="+id;
        Log.d("QUERY", selectQuery);

        Cursor cursor = this.getWritableDatabase().rawQuery(selectQuery, null);

        if(!cursor.moveToFirst()) //moveToFirst returns false when cursor is empty
            return null;

        ArrayList<String> row = new ArrayList<>();
        for(int i = 0; i < cursor.getColumnCount(); i++)
        {
            row.add(cursor.getString(i));
        }
        return row;

    }
}
