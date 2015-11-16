package com.example.jennifertran.cse110practice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by emd_000 on 11/15/2015.
 */
public class DbHelperTaken extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "taken.db";
    private static final String KEY_QUIZ = "title";
    private static final String KEY_TAKEN = "taken";
    private String table;

        public DbHelperTaken(Context context, String table) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.table = "`"+table+"`";

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS " + table + " ( " +
                    KEY_QUIZ + " TEXT, " + KEY_TAKEN + " INTEGER)";
            db.execSQL(sql);
        }
        public void createTable()
        {
            String sql = "CREATE TABLE IF NOT EXISTS " + table + " ( " +
                    KEY_QUIZ + " TEXT, " + KEY_TAKEN + " INTEGER)";
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(sql);

        }
        /* upgrades a user's list of taken quizzes. */
        public void upgradeTaken(HashMap<String, Integer> QTPairs)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(table, null, null); //Delete entries in old table

            Iterator<?> it = QTPairs.entrySet().iterator();
            ContentValues colValuePairs = new ContentValues();

            for(Map.Entry<String, Integer> next : QTPairs.entrySet()){
                colValuePairs.put(KEY_QUIZ, next.getKey());
                colValuePairs.put(KEY_TAKEN, next.getValue());
            }
            db.insert(table, null, colValuePairs);
        }

        public int getIsTaken(String title)
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "SELECT * FROM "+this.table+" WHERE title=\""+title+"\"";
            Cursor dataCurs = db.rawQuery(sql, null);
            dataCurs.moveToFirst();
            int result = dataCurs.getInt(dataCurs.getColumnIndex(KEY_TAKEN)); //HardCoded location of 'taken' column
            dataCurs.close();
            return result;
        }
        public void setTaken(int n, String title)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "UPDATE "+table+" SET "+KEY_TAKEN+"="+String.valueOf(n)+" WHERE "+KEY_QUIZ+"=\""+title+"\"";
            db.execSQL(sql);
            /*ContentValues colVal = new ContentValues();
            colVal.put(KEY_TAKEN, n);
            String where = KEY_QUIZ+"=\""+title+"\"";
            String[] whereArgs = {};
            db.update(table,colVal, where, whereArgs);
            */
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
}

