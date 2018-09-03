package com.toyota.assetcare.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.toyota.assetcare.R;
import com.toyota.assetcare.utils.CommonConstants;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private EditText mEmailView;
    private EditText mPasswordView;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email_field);
        mPasswordView = (EditText) findViewById(R.id.password_field);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEmailView.getText().toString().trim().equals("") || mPasswordView.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter jira credentials", Toast.LENGTH_LONG).show();
                    return;
                }

                prefs.edit().putString(CommonConstants.EMAIL_SP,mEmailView.getText().toString()).commit();
                prefs.edit().putString(CommonConstants.TOKEN_SP,mPasswordView.getText().toString()).commit();
                Intent intent = new Intent(LoginActivity.this, AssetTypeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public void getTokenClick(View v) {
        Intent intent = new Intent(LoginActivity.this, HowToGetTokenActivity.class);
        startActivity(intent);
    }

}

