package com.example.jennifertran.cse110practice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {

        private static final String LOGIN_FILE = "LOGIN_FILE";

        private static final String LOGGED_IN = "LOGGED_IN";

        private SharedPreferences mLoginPreferences;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            // boilerplate...
            super.onCreate(savedInstanceState);

            mLoginPreferences = getSharedPreferences(LOGIN_FILE, MODE_PRIVATE);

            if (mLoginPreferences.getBoolean(LOGGED_IN, true)) {
                Intent dashboardIntent = new Intent(this, MainActivity.class);
                startActivity(dashboardIntent);

                //finish();
            } else {
                setContentView(R.layout.activity_login);

                // set listener of login button to call login() on press
                findViewById(R.id.login_page_login_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        login();
                    }
                });
            }

        }

        private void login() {
            // ...
            mLoginPreferences.edit()
                    .putBoolean(LOGGED_IN, true)
                    .apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        }

        private void fetchServerSideData() {
        }

    }


