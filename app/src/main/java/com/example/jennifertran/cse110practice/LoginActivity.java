package com.example.jennifertran.cse110practice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

        private static final String LOGIN_FILE = "LOGIN_FILE";
        private static final String LOGGED_IN = "LOGGED_IN";
        private String message = "Default Message";
        private String loginUrl;
        private EditText usernameEdit, passwordEdit;
        private String username, password;
        private ProgressDialog pDialog;



    private SharedPreferences mLoginPreferences;

        private JSONParser jsonParser = new JSONParser();
        @Override
        public void onCreate(Bundle savedInstanceState) {
            // boilerplate...
            super.onCreate(savedInstanceState);
            loginUrl = getApplicationContext().getString(R.string.queryUrl);
            mLoginPreferences = getSharedPreferences(LOGIN_FILE, MODE_PRIVATE);


                setContentView(R.layout.activity_login);
                usernameEdit = ((EditText)findViewById(R.id.login_page_username));
                passwordEdit = ((EditText)findViewById(R.id.login_page_password));
                // set listener of login button to call login() on press
                findViewById(R.id.login_page_login_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        login();
                    }
                });
        }


        class AttemptLogin extends AsyncTask<String,String,String> {

            boolean failure = false;
            @Override
            protected String doInBackground(String... args) {
                RemoteDBHelper remDb = new RemoteDBHelper();
                String table = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                        "SELECT * FROM Users WHERE username='" + username + "'",
                        loginUrl);
                try {

                    return  new JSONArray(table).getString(0); //Get first valid username entry
                                                               //There should be only one.
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return null;

            }
            @Override
            protected void onPreExecute( ){
                super.onPreExecute();
                pDialog = new ProgressDialog(LoginActivity.this);
                pDialog.setMessage("Attempting login...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
            protected void onPostExecute(String message){

                if(pDialog != null)
                    pDialog.dismiss();
                try{
                    if(message == null)
                        return;
                    if(message.equals("")) {
                        Toast.makeText(LoginActivity.this, "Your credentials....failed.",
                                Toast.LENGTH_LONG).show();
                        this.failure = true;
                    }
                    else{

                        JSONObject args = new JSONObject(message);
                        if(args.getString("is_admin").equals(String.valueOf(1))) //isAdmin
                        {
                            Intent adminIntent = new Intent(LoginActivity.this, AdminActivity.class);
                            adminIntent.putExtra("username", username);
                            startActivity(adminIntent);

                        }
                        else if(args.getString("username").equals(username) &&
                                args.getString("password").equals(password))
                        {
                            Toast.makeText(LoginActivity.this,"You've logged in!",
                                    Toast.LENGTH_LONG).show();
                            //LOG IN: Start next activity.
                            Intent intent = new Intent(LoginActivity.this, SubjectNavActivity.class);
                            intent.putExtra("username", args.getString("username"));
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(LoginActivity.this,"Your credentials....failed.",
                                    Toast.LENGTH_LONG).show();
                        this.failure = true;

                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }


        }

        private void login() {
            // ...
            mLoginPreferences.edit()
                    .putBoolean(LOGGED_IN, true)
                    .apply();

            username = usernameEdit.getText().toString();
            password = passwordEdit.getText().toString();
            AttemptLogin log = new AttemptLogin();
            log.execute();






            /*
            Intent sendIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            String title = getResources().getString(R.string.hello_world);
// Create intent to show the chooser dialog
            Intent chooser = Intent.createChooser(sendIntent, title);

// Verify the original intent will resolve to at least one activity
            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
            else
                Toast.makeText(this, "No app with Action_send", Toast.LENGTH_SHORT).show();

            */
        }
        private void fetchServerSideData() {
        }

    }


