package com.example.osrstracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

public class StatisticsActivity extends AppCompatActivity {
    private DatabaseManager db = null;
    private Spinner usersSpinner;
    private ImageButton buttonDelete;
    private ImageButton buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseManager(StatisticsActivity.this);
        setContentView(R.layout.activity_statistics);
        usersSpinner = findViewById(R.id.users_spinner);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonAdd = findViewById(R.id.buttonAdd);

        if (!fillComboBox()) {
            loadMainActivity();
            return;
        }

        buttonDelete.setOnClickListener(this::onButtonDeleteClick);
        buttonAdd.setOnClickListener(this::onButtonAddClick);
    }

    @Override
    protected void finalize() throws Throwable {
        if (db != null)
            db.close();
        super.finalize();
    }

    private boolean fillComboBox(){
        String[] users = db.getAllUsernames();
        if (users == null || users.length == 0)
            return false;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(StatisticsActivity.this, R.layout.support_simple_spinner_dropdown_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usersSpinner.setAdapter(adapter);
        return true;
    }

    private void loadMainActivity() {
        Intent i = new Intent(StatisticsActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }


    private void onButtonDeleteClick(View v) {
        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(StatisticsActivity.this);
        confirmationDialog.setTitle("Are you sure?");
        confirmationDialog.setMessage("This will delete all saved data for this user!");
        confirmationDialog.setNegativeButton("No", null);
        confirmationDialog.setPositiveButton("Yes", (view, which) -> {
            db.deleteUser(usersSpinner.getSelectedItem().toString());
            if (!fillComboBox())
                loadMainActivity();
        });
        confirmationDialog.show();
    }

    private void onButtonAddClick(View v) {
        Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
        intent.putExtra("ADD_NEW_USER", true);
        startActivity(intent);
    }

}