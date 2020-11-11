package com.example.osrstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnHttpRequestCompleteListener{

    private EditText userNameEdit;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseManager databaseManager = new DatabaseManager(MainActivity.this);
        if (databaseManager.getAllUsernames() != null) {
            databaseManager.close();
            loadStatisticsActivity();
            return;
        }

        userNameEdit = (EditText) findViewById(R.id.editTextPersonName);
        userNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    currentUser = userNameEdit.getText().toString();
                    new HttpRequestTask(MainActivity.this).execute(currentUser);
                }
                return handled;
            }
        });
    }

    @Override
    public void onHttpRequestComplete(String response, int responseCode) {
        if (responseCode != 200 || response == null) {
            String dialogMessage;
            if (responseCode == 404)
                dialogMessage = "User has not been found";
            else
                dialogMessage = "Please check if you have an active internet Connection";
            Toast.makeText(MainActivity.this, dialogMessage, Toast.LENGTH_LONG).show();
            return;
        }
        PlayerStats newPlayer = new PlayerStats(response);


        DatabaseManager databaseManager = new DatabaseManager(MainActivity.this);
        if (!databaseManager.registerUser(currentUser)) {
            Toast.makeText(MainActivity.this, "Failed to register User", Toast.LENGTH_LONG).show();
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
        Intent i = new Intent(MainActivity.this, Statistics.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

}