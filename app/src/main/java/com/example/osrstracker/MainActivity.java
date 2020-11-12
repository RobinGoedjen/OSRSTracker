package com.example.osrstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnHttpRequestCompleteListener{

    private EditText userNameEdit;
    private ProgressBar progressBar;

    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseManager databaseManager = new DatabaseManager(MainActivity.this);
        if (!(getIntent().getBooleanExtra("ADD_NEW_USER", false) || databaseManager.getAllUsernames() == null)) {
            databaseManager.close();
            loadStatisticsActivity();
            return;
        }
        progressBar = findViewById(R.id.progressBar);
        userNameEdit = (EditText) findViewById(R.id.editTextPersonName);
        userNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    currentUser = userNameEdit.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    new HttpRequestTask(MainActivity.this).execute(currentUser);
                }
                return handled;
            }
        });
    }

    @Override
    public void onHttpRequestComplete(String response, int responseCode) {
        progressBar.setVisibility(View.INVISIBLE);
        if (responseCode != 200 || response == null) {
            String dialogMessage;
            if (responseCode == 404)
                dialogMessage = "User has not been found";
            else
                dialogMessage = "Please check if you have an active internet Connection";
            Toast.makeText(MainActivity.this, dialogMessage, Toast.LENGTH_LONG).show();
            return;
        }
        PlayerStats newPlayer;
        try {
            newPlayer = new PlayerStats(response);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "The Service is temporary unavailable", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseManager databaseManager = new DatabaseManager(MainActivity.this);
        if (!databaseManager.registerUser(currentUser)) {
            Toast.makeText(MainActivity.this, "Failed to register new User", Toast.LENGTH_LONG).show();
            return;
        }
        if (!databaseManager.addTimestamp(currentUser, newPlayer)) {
            Toast.makeText(MainActivity.this, "Failed to create Timestamp", Toast.LENGTH_LONG).show();
            return;
        }
        databaseManager.close();
        loadStatisticsActivity();
    }

    private void loadStatisticsActivity() {
        Intent i = new Intent(MainActivity.this, StatisticsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

}