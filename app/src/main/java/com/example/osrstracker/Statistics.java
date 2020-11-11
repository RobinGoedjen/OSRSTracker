package com.example.osrstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Statistics extends AppCompatActivity {
    private DatabaseManager db = null;
    private Spinner usersSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseManager(Statistics.this);
        setContentView(R.layout.activity_statistics);
        usersSpinner = findViewById(R.id.users_spinner);

        fillComboBox();
    }

    @Override
    protected void finalize() throws Throwable {
        if (db != null)
            db.close();
        super.finalize();
    }

    private boolean fillComboBox(){
        String[] users = db.getAllUsernames();
        if (users == null)
            return false;
        //CREATE ARRAY ADAPTER

        for (int i = 0; i < users.length; i++) {
            //FILL ARRAY ADAPTER
        }
        return true;
    }


}