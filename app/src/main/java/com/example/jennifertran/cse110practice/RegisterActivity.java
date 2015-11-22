package com.example.jennifertran.cse110practice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    ProgressDialog pDialog;
    EditText usernameE;
    EditText passwordE;
    EditText companyE;
    EditText emailE;
    String username;
    String password;
    String company;
    String email;
    String loginUrl;
    //EditText is_admin;
    //Last login is final entry
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final EditText usernameE = (EditText) findViewById(R.id.register_page_username);
        final EditText passwordE = (EditText) findViewById(R.id.register_page_password);
        final EditText companyE  = (EditText) findViewById(R.id.register_page_company);
        final EditText emailE    = (EditText) findViewById(R.id.register_page_email);
        loginUrl = getApplicationContext().getString(R.string.queryUrl);
        findViewById(R.id.login_page_register_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                username = usernameE.getText().toString();
                password = passwordE.getText().toString();
                company  = companyE.getText().toString();
                email    = emailE.getText().toString();
                new AttemptRegister().execute();
            }
        });
     }

    class AttemptRegister extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute( ){
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Attempting to Register...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            RemoteDBHelper remDb = new RemoteDBHelper();
            String table = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                    "SELECT * FROM Users WHERE username='" + username + "'",
                    loginUrl);
            if(table.equals(""))
            {
                remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        "INSERT INTO Users VALUES ('" + username + "', '" +
                                password + "' , '" + company + "', '" + email + "', '0', '')",
                        loginUrl);
                remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        "CREATE TABLE IF NOT EXISTS " + username + "Quizzes ( header TEXT, " +
                                "child0 TEXT, child1 TEXT, indexer INTEGER )",
                        loginUrl); //Create remote
                ArrayList<String> cols = new ArrayList<>();
                cols.add("child0");
                cols.add("child1");
                DbHelperSubNav dbSub =
                        new DbHelperSubNav(RegisterActivity.this, username, cols); //Create local
                dbSub.createTable();


                remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        "CREATE TABLE IF NOT EXISTS " + username +
                                "Taken ( title TEXT, " + "taken INTEGER )",
                        loginUrl);
                DbHelperTaken dbTaken =
                        new DbHelperTaken(RegisterActivity.this, username);
                dbTaken.createTable();

                remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        "CREATE TABLE IF NOT EXISTS "+username+"Answers ( id INTEGER, " +
                                "question TEXT,  answer TEXT, correct INTEGER)",
                        loginUrl);
                DbHelperQuizResponse dbQuiz =
                        new DbHelperQuizResponse(RegisterActivity.this, username);
                dbQuiz.createTable();
            }
            try {
                if(table.equals(""))
                    return "";
                else
                    return  new JSONArray(table).getString(0); //Get first valid username entry
                //There should be only one.
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        protected void onPostExecute(String message){

            if(pDialog != null)
                pDialog.dismiss();
            try{
                if(message == null)
                    return;
                if(!message.equals("") || username.equals("")) {
                    Toast.makeText(RegisterActivity.this, "That username is taken!",
                            Toast.LENGTH_LONG).show();
                }
                else{
                        Toast.makeText(RegisterActivity.this,"You've registered!",
                                Toast.LENGTH_LONG).show();
                        //LOG IN: Start next activity.
                        Intent intent = new Intent(RegisterActivity.this, SubjectNavActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }


            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    /*********** Hide keyboard and unfocus currently focused EditText ********************/
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View mEditText = getCurrentFocus();
            if (mEditText != null) {
                Rect outRect = new Rect();
                mEditText.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    mEditText.clearFocus();
                    //
                    // Hide keyboard
                    //
                    InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


}
