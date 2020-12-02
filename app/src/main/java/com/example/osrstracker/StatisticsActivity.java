package com.example.osrstracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;

public class StatisticsActivity extends AppCompatActivity implements OnHttpRequestCompleteListener{
    private DatabaseManager db = null;
    private Spinner usersSpinner;
    private ImageButton buttonDelete;
    private ImageButton buttonAdd;
    private SwipeRefreshLayout swipeRefresh;
    private GraphView overallGraph;

    private PlayerStats.Skill currentGraphSkill = PlayerStats.Skill.Overall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseManager(StatisticsActivity.this);
        setContentView(R.layout.activity_statistics);
        usersSpinner = findViewById(R.id.users_spinner);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonAdd = findViewById(R.id.buttonAdd);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        overallGraph = findViewById(R.id.overall_graph);

        if (!fillComboBox()) {
            loadMainActivity();
            return;
        }
        buttonDelete.setOnClickListener(this::onButtonDeleteClick);
        buttonAdd.setOnClickListener(this::onButtonAddClick);
        swipeRefresh.setOnRefreshListener(this::onRefresh);

        overallGraph.getViewport().setXAxisBoundsManual(true);
        overallGraph.getViewport().setMinX(0);
        drawGraph(PlayerStats.Skill.Overall);


        //TODO Work in Progess
        LinearLayout layout =  findViewById(R.id.skillLayout);
        SkillView test = new SkillView(StatisticsActivity.this);
        layout.addView(test);
        test = new SkillView(StatisticsActivity.this);
        test.fillView(R.drawable.agility_icon, 99);
        layout.addView(test);
        test = new SkillView(StatisticsActivity.this);
        layout.addView(test);
        test = new SkillView(StatisticsActivity.this);
        layout.addView(test);
        test = new SkillView(StatisticsActivity.this);
        layout.addView(test);
        test = new SkillView(StatisticsActivity.this);
        layout.addView(test);
        test = new SkillView(StatisticsActivity.this);
        layout.addView(test);
        test = new SkillView(StatisticsActivity.this);
        layout.addView(test);
        test = new SkillView(StatisticsActivity.this);
        layout.addView(test);
    }

    @Override
    protected void finalize() throws Throwable {
        if (db != null)
            db.close();
        super.finalize();
    }

    private boolean fillComboBox(){
        User[] users = db.getAllUsers();
        if (users == null || users.length == 0)
            return false;
        ArrayAdapter<User> adapter = new ArrayAdapter<User>(StatisticsActivity.this, R.layout.support_simple_spinner_dropdown_item, users);
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
            db.deleteUser(((User)usersSpinner.getSelectedItem()).getUserID());
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

    private void onRefresh() {
        new HttpRequestTask(StatisticsActivity.this).execute(((User)usersSpinner.getSelectedItem()).getUserName());
    }

    @Override
    public void onHttpRequestComplete(String response, int responseCode) {
        try {
            if (responseCode != 200 || response == null) {
                String dialogMessage;
                if (responseCode == 404)
                    dialogMessage = "User has not been found";
                else
                    dialogMessage = "Please check if you have an active internet Connection";
                Toast.makeText(StatisticsActivity.this, dialogMessage, Toast.LENGTH_LONG).show();
                return;
            }
            PlayerStats newPlayer;
            try {
                newPlayer = new PlayerStats(response);
            } catch (Exception e) {
                Toast.makeText(StatisticsActivity.this, "The Service is temporary unavailable", Toast.LENGTH_LONG).show();
                return;
            }
            db.addTimestamp(((User)usersSpinner.getSelectedItem()).getUserID(), new PlayerStats(response));
            Toast.makeText(StatisticsActivity.this, "Success", Toast.LENGTH_SHORT).show();

            drawGraph(currentGraphSkill);
        } finally {
            swipeRefresh.setRefreshing(false);
        }

    }

    private void drawGraph(PlayerStats.Skill skill) {
        overallGraph.removeAllSeries();
        overallGraph.setTitle(skill.name() + " Rank");
        LineGraphSeries<DataPoint> series = null;
        DataPoint[] data = db.getRankDataSet(skill, ((User)usersSpinner.getSelectedItem()).getUserID());
        overallGraph.getViewport().setMaxX(data[data.length - 1].getX());
        series = new LineGraphSeries<DataPoint>(data);
        series.setDrawDataPoints(true);
        overallGraph.addSeries(series);
    }
}