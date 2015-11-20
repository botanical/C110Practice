package com.example.jennifertran.cse110practice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by emd_000 on 11/19/2015.
 */
public class DbHelperQuizResponse extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "quizResponses.db";
    private String table;
    private static final String KEY_ID = "id";
    private static final String KEY_QUES = "question";
    private static final String KEY_ANSWER = "answer"; //correct option
    private static final String KEY_CORRECT = "correct"; //marked answer by user

    private SQLiteDatabase dbase;
    public DbHelperQuizResponse(Context context, String tableName) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.table = "`"+tableName+"`"; //need backticks in case table name has spaces

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_QUES
                + " TEXT, " + KEY_ANSWER+ " TEXT, " + KEY_CORRECT +" TEXT )";

        db.execSQL(sql);
        //db.close()
        //Sets question id's to start from 0 instead of 1
        String subtraction = "UPDATE " + table +  " SET " + KEY_ID + "=" + KEY_ID + " - 1";
        db.execSQL(subtraction);
    }

    public void createTable()
    {
        String colQuery = "";
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_QUES
                + " TEXT, " + KEY_ANSWER+ " TEXT, "+KEY_CORRECT+" TEXT)";

        System.out.println("CREATE TABLE QUERY " + sql);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        String subtraction = "UPDATE " + table +  " SET " + KEY_ID + "=" + KEY_ID + " - 1";
        db.execSQL(subtraction);
    }

    //Upgrades the table representing a user's responses to a quiz.
    public void upgradeResponse(ArrayList<ArrayList<String>> responses)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, null, null); //Delete entries in old table

        for(int i = 0; i < responses.size(); i++)
        {
            ArrayList<String> currRow = responses.get(i);
            ContentValues colValuePairs = new ContentValues();

            //TODO Make magic numbers into final constants
            colValuePairs.put("id",currRow.get(0)); //key represents column names
            colValuePairs.put("question", currRow.get(1));
            colValuePairs.put("answer", currRow.get(2));
            colValuePairs.put("correct",currRow.get(3));

            db.insert(table, null, colValuePairs);
        }
        //db.close();
    }

    ArrayList<ArrayList<String>> getResponses()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+this.table;
        Cursor resp = db.rawQuery(sql, null);

        if(!resp.moveToFirst())
            return null;

        ArrayList<String> row = new ArrayList<>();
        ArrayList<ArrayList<String>> table = new ArrayList<>();

        //TODO make magic numbers into final constants
        row.add(resp.getString(0));
        row.add(resp.getString(1));
        row.add(resp.getString(2));
        row.add(resp.getString(3));
        table.add(row);

        while(resp.moveToNext())
        {
            row = new ArrayList<>();
            row.add(resp.getString(0));
            row.add(resp.getString(1));
            row.add(resp.getString(2));
            row.add(resp.getString(3));
            table.add(row);
        }
        System.out.println("RESPONSES: "+table);

        return table;
    }





    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
