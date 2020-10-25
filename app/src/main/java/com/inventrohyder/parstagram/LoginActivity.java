package com.inventrohyder.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private EditText mEtUsername;
    private EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) {
            goToMainActivity();
        }

        mEtUsername = findViewById(R.id.username);
        mEtPassword = findViewById(R.id.password);

        Button btLogin = findViewById(R.id.login);
        btLogin.setOnClickListener(
                view -> loginUser(
                        mEtUsername.getText().toString().trim(),
                        mEtPassword.getText().toString().trim()
                )
        );

    }

    public void loginUser(String username, String password) {
        Log.i(TAG, "loginUser: ");
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                Log.e(TAG, "done: Issue with login", e);
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to MainActivity if the user signed in properly
            goToMainActivity();
            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
        });
    }

    private void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}