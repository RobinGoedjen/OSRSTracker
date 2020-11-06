package com.example.osrstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnHttpRequestCompleteListener{

    private EditText userNameEdit;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameEdit = (EditText) findViewById(R.id.editTextPersonName);
        userNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    currentUser = userNameEdit.getText().toString();
                    new HttpRequestTask(MainActivity.this).execute(currentUser);
                    handled = true;
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
                dialogMessage = "User has not been found.";
            else
                dialogMessage = "Please check if you have an active internet Connection.";
            createDialog("An Error occured", dialogMessage).show();
            return;
        }
        Log.i("HTTP", response);
        if (!(DatabaseManager.registerUser(currentUser) && DatabaseManager.addTimestamp(currentUser, response))) {
            createDialog("An unexpected Error occured", "The response content is erroneous").show();
            return;
        }
        //Auf antwort warten
        //Umschalten
        setContentView(R.layout.activity_statistics);
    }

    private AlertDialog createDialog(String title, String content) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(content);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        return alertDialog;
    }
}