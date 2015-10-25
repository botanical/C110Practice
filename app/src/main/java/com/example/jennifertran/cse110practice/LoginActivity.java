package com.example.jennifertran.cse110practice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

        private static final String LOGIN_FILE = "LOGIN_FILE";

        private static final String LOGGED_IN = "LOGGED_IN";

        private SharedPreferences mLoginPreferences;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            // boilerplate...
            super.onCreate(savedInstanceState);

            mLoginPreferences = getSharedPreferences(LOGIN_FILE, MODE_PRIVATE);


                setContentView(R.layout.activity_login);

                // set listener of login button to call login() on press
                findViewById(R.id.login_page_login_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        login();
                    }
                });

        }

        private void login() {
            // ...
            mLoginPreferences.edit()
                    .putBoolean(LOGGED_IN, true)
                    .apply();


        //Intent intent = new Intent(this, MainActivity.class);

        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);

            /*
            Intent sendIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
// Always use string resources for UI text.
// This says something like "Share this photo with"
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


