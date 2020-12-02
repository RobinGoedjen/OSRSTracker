package com.example.osrstracker;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity implements OnHttpRequestCompleteListener{
    private DatabaseManager db = null;
    private Spinner usersSpinner;
    private ImageButton buttonDelete;
    private ImageButton buttonAdd;
    private SwipeRefreshLayout swipeRefresh;
    private GraphView overallGraph;
    private PlayerStats.Skill currentGraphSkill = PlayerStats.Skill.Overall;
    private ArrayList<SkillView> allSkillViews = new ArrayList<SkillView>();

    private @DrawableRes int[] skillIcons = {R.drawable.skills_icon, R.drawable.attack_icon, R.drawable.defence_icon, R.drawable.strength_icon, R.drawable.hitpoints_icon, R.drawable.ranged_icon, R.drawable.prayer_icon, R.drawable.magic_icon, R.drawable.cooking_icon,
            R.drawable.woodcutting_icon, R.drawable.fletching_icon, R.drawable.fishing_icon, R.drawable.firemaking_icon, R.drawable.crafting_icon, R.drawable.smithing_icon, R.drawable.mining_icon, R.drawable.herblore_icon, R.drawable.agility_icon, R.drawable.thieving_icon,
            R.drawable.slayer_icon, R.drawable.farming_icon, R.drawable.runecraft_icon, R.drawable.hunter_icon, R.drawable.construction_icon};


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
        usersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentGraphSkill = PlayerStats.Skill.Overall;
                drawGraphRank(currentGraphSkill);
                setLevelSkills();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buttonDelete.setOnClickListener(this::onButtonDeleteClick);
        buttonAdd.setOnClickListener(this::onButtonAddClick);
        swipeRefresh.setOnRefreshListener(this::onRefresh);

        overallGraph.getViewport().setXAxisBoundsManual(true);
        overallGraph.getViewport().setMinX(0);
        drawGraphRank(currentGraphSkill);


        LinearLayout layout =  findViewById(R.id.skillLayout);
        for (PlayerStats.Skill currentSkill : PlayerStats.Skill.values()) {
            SkillView skillView = new SkillView(StatisticsActivity.this);
            skillView.fillView(skillIcons[currentSkill.ordinal()], currentSkill.ordinal(), currentSkill);
            layout.addView(skillView);
            skillView.findViewById(R.id.buttonRank).setOnClickListener((View v) -> drawGraphRank(skillView.selectedSkill));
            skillView.findViewById(R.id.buttonXP).setOnClickListener((View v) -> drawGraphXP(skillView.selectedSkill));
            allSkillViews.add(skillView);
        }
        setLevelSkills();
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

            drawGraphRank(currentGraphSkill);
        } finally {
            swipeRefresh.setRefreshing(false);
        }

    }

    private void drawGraphRank(PlayerStats.Skill skill) {
        overallGraph.removeAllSeries();
        overallGraph.setTitle(skill.name() + " Rank");
        LineGraphSeries<DataPoint> series = null;
        DataPoint[] data = db.getRankDataSet(skill, ((User)usersSpinner.getSelectedItem()).getUserID());
        overallGraph.getViewport().setMaxX(data[data.length - 1].getX());
        series = new LineGraphSeries<DataPoint>(data);
        series.setDrawDataPoints(true);
        overallGraph.addSeries(series);
    }

    private void drawGraphXP(PlayerStats.Skill skill) {
        overallGraph.removeAllSeries();
        overallGraph.setTitle(skill.name() + " XP");
        LineGraphSeries<DataPoint> series = null;
        DataPoint[] data = db.getXPDataSet(skill, ((User)usersSpinner.getSelectedItem()).getUserID());
        overallGraph.getViewport().setMaxX(data[data.length - 1].getX());
        series = new LineGraphSeries<DataPoint>(data);
        series.setDrawDataPoints(true);
        overallGraph.addSeries(series);
    }

    private void setLevelSkills() {
        PlayerStats stats = db.getLatestPlayerStats(((User)usersSpinner.getSelectedItem()).getUserID());
        for (PlayerStats.Skill currentSkill : PlayerStats.Skill.values()) {
            if (currentSkill == PlayerStats.Skill.Overall) {
                allSkillViews.get(currentSkill.ordinal()).setLevel(0);
                continue;
            }
            allSkillViews.get(currentSkill.ordinal()).setLevel(PlayerStats.getLevelAtExperience(stats.skillExperienceMap.get(currentSkill)));
        }
    }




}