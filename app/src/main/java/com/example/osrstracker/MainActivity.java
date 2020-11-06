package com.example.osrstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnHttpRequestCompleteListener{

    private EditText userNameEdit;

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

                    new HttpRequestTask(MainActivity.this).execute(userNameEdit.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });

    }

    @Override
    public void OnHttpRequestComplete(String response, int responseCode) {
        if (responseCode != 200 || response == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("An Error occured");
            String dialogMessage;
            if (responseCode == 404)
                dialogMessage = "User has not been found.";
            else
                dialogMessage = "Please check if you have an active internet Connection.";
            alertDialog.setMessage(dialogMessage);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
            return;
        }
        Log.i("HTTP", response);
        //Response in DatabaseManager
        //Auf antwort warten
        //Umschalten
        setContentView(R.layout.activity_statistics);
    }
}