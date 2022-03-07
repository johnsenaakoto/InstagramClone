package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";   // TAG holds the tag string for logcat
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // sets login activity view to xml layout files

        // Checks if there is a user already logged in, if yes then we go straight to the MainActivity
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        // find all xml items
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Listens to when the Login button is clicked and checks edit texts for username and password
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password); // Calls loginUser method to validate login
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick signup button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signupUser(username, password); // Calls loginUser method to validate login

            }
        });

    }

    // signupUser signs up using Parse
    private void signupUser(String username, String password) {
        Log.i(TAG, "Attempting to signup user " + username);

        // Create the ParseUser
        ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);

        // Invoke signUpInBackground
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Signup failed", e);
                    Toast.makeText(LoginActivity.this, "SignUp Failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO: navigate to the main activity if the user has signed up properly
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Signup Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // loginUser validates login using Parse
    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);

        // Call ParseUser to login user in background or on different thread
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO: navigate to the main activity if the user has signed in properly
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}