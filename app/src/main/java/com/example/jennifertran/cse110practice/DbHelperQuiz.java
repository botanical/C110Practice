package com.example.jennifertran.cse110practice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DbHelperQuiz extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "triviaQuiz";
    // tasks table name
    private static final String TABLE_QUEST = "quest";
    // tasks Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_QUES = "question";
    private static final String KEY_ANSWER = "answer"; //correct option
    private static final String KEY_OPTA= "opta"; //option a
    private static final String KEY_OPTB= "optb"; //option b
    private static final String KEY_OPTC= "optc"; //option c
    private static final String KEY_MARKED = "marked"; //marked answer by user

    private SQLiteDatabase dbase;
    public DbHelperQuiz(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        dbase=db;
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_QUEST + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_QUES
                + " TEXT, " + KEY_ANSWER+ " TEXT, "+KEY_OPTA +" TEXT, "
                +KEY_OPTB +" TEXT, "+KEY_OPTC+" TEXT, "+KEY_MARKED+" TEXT)";
        db.execSQL(sql);
        addQuestions();
        //db.close()
        //Sets question id's to start from 0 instead of 1
        String subtraction = "UPDATE " + TABLE_QUEST +  " SET " + KEY_ID + "=" + KEY_ID + " - 1";
        db.execSQL(subtraction);
    }
    private void addQuestions()
    {

        Question q1=new Question("What is 12*3?" ,"36", "15", "52", "36", "");
        this.addQuestion(q1);
        Question q2=new Question("What is 4*80?", "120", "420", "320", "320", "");
        this.addQuestion(q2);
        Question q3=new Question("What is 15/3?","5", "12","7","5", "");
        this.addQuestion(q3);
        Question q4=new Question("What is 64/8?", "8", "3", "11","8", "");
        this.addQuestion(q4);
        Question q5=new Question("What is 55/5?","11","50","14","11", "");
        this.addQuestion(q5);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUEST);
        // Create tables again
        onCreate(db);
    }

    // Adding new question
    public void addQuestion(Question quest) {
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUES, quest.getQUESTION());
        values.put(KEY_ANSWER, quest.getANSWER());
        values.put(KEY_OPTA, quest.getOPTA());
        values.put(KEY_OPTB, quest.getOPTB());
        values.put(KEY_OPTC, quest.getOPTC());
        values.put(KEY_MARKED, quest.getMARKED());
        //values.put(KEY_MARKED, quest.getMARKED());
        // Inserting Row
        dbase.insert(TABLE_QUEST, null, values);
    }
    public List<Question> getAllQuestions() {
        List<Question> quesList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_QUEST;
        dbase=this.getReadableDatabase();
        Cursor cursor = dbase.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Question quest = new Question();
                quest.setID(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                quest.setQUESTION(cursor.getString((cursor.getColumnIndex(KEY_QUES))));
                quest.setANSWER(cursor.getString(cursor.getColumnIndex(KEY_ANSWER)));
                quest.setOPTA(cursor.getString(cursor.getColumnIndex(KEY_OPTA)));
                quest.setOPTB(cursor.getString(cursor.getColumnIndex(KEY_OPTB)));
                quest.setOPTC(cursor.getString(cursor.getColumnIndex(KEY_OPTC)));
                quest.setMARKED(cursor.getString(cursor.getColumnIndex(KEY_MARKED)));
                quesList.add(quest);
            } while (cursor.moveToNext());
        }
        // return quest list
        return quesList;
    }
    public int rowcount()
    {
        int row;
        String selectQuery = "SELECT  * FROM " + TABLE_QUEST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        row=cursor.getCount();
        return row;
    }
    public void insertIntoDb(String id, ContentValues value) {
        int x = dbase.update(TABLE_QUEST, value, KEY_ID+"="+id, null);
        Log.d("UPDATED???", String.valueOf(x));
        Log.d("VAL", value.toString());
        Log.d("QID", id);
    }
    public String queryMarkedAnswers( int id ) {
        String marked;
        String selectQuery = "SELECT " + KEY_MARKED + " FROM " + TABLE_QUEST
                + " WHERE " + KEY_ID + "=" + id;
        Log.d("QUERY", selectQuery);
        Cursor cursor = dbase.rawQuery(selectQuery, null);

        if  (cursor.moveToFirst()) {
            marked = cursor.getString(cursor.getColumnIndex(KEY_MARKED));
            Log.d("moved", "cursor moved!!");

            return marked;
        }
        else {
            return "help";
        }
    }
}
